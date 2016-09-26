import java.net.*;
import java.util.Arrays;
import java.io.*;



public class Client 
{
	
	private DatagramSocket sendReceiveSocket;
	private DatagramPacket sendPacket, receivePacket;
	private String fileName;
	private String mode;
	private byte[] message;
	
	public Client()
	{
		// create DatagramSocket to use to both send and receive
		try
		{
			sendReceiveSocket = new DatagramSocket();
		}
		catch(SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
		fileName = "test.txt";
		mode = "ocTEt".toLowerCase();
		
		byte[] data = new byte[4];
		receivePacket = new DatagramPacket(data,data.length);
	}
	/**
	 * Algorithm for Client 
	 * @return 
	 */
	public void createPack(byte[] packet){
		try{
			sendPacket = new DatagramPacket(packet,packet.length,InetAddress.getLocalHost(),23);
		}
		catch(UnknownHostException e){
			e.printStackTrace();
			System.exit(1);
		}
    	
	}

	public void ClientAlgorithm()
	{
		
		String request;
		
		for(int i = 0; i < 11; i++)
		{
			message = new byte[4 + fileName.length() + mode.length()];
			message[0] = 0;
			
			if(i == 10)
			{
				message[1] = 0;
				request = "Error"; // #11 invalid request
			}
			else if(i%2 == 0)
			{
				message[1] =1;
				request = "Read"; // read request
			}
			else
			{
				message[1]=2;
				request = "Write"; // write request
			}
			
			byte[] fileNameToBytes = fileName.getBytes();
			int os1 = fileNameToBytes.length;
			
			try{
				File file = new File(fileName);
				
				if(file.createNewFile()){
					System.out.println("File is created");
				}else{
					System.out.println("File already exists.");
				}
				
			}catch (IOException e){
				e.printStackTrace();
			}
			
			System.arraycopy(fileNameToBytes, 0, message, 2, os1);
			message[os1 + 2] = 0;
			
			byte[] modeToBytes = mode.getBytes();
			int os2 = modeToBytes.length;
			
			System.arraycopy(modeToBytes, 0, message, os1 + 3, os2);
			int os3 = os1 + os2 + 3;
			message[os3] = 0;
			
			// forming Packet that will be sent to server but first the intermediate host at port 23
			try
			{
				sendPacket = new DatagramPacket(message,message.length,InetAddress.getLocalHost(),23);
			}
			catch(UnknownHostException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			
			
			send(sendPacket);
			
			
			
			//Send Packet info
			
			
			System.out.println("Sending: " + request + " Request");
			System.out.println("Host: " + sendPacket.getAddress());
			System.out.println("Destination Port: " + sendPacket.getPort());
			System.out.println("File Name: " + fileName);
			System.out.println("Mode: " + mode);
			
			int length1 = os3 + 1;
			System.out.println("Length: " + length1);
			String info = new String(message,0,length1);
			System.out.println("String : " + info);
			System.out.println("Bytes : " + Arrays.toString(message));
			
			// receive packet
			
			
			
			try
			{
				sendReceiveSocket.receive(receivePacket);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			// Receive Packet info
			System.out.println("Client received packet");
			System.out.println("Sent from Host: " + receivePacket.getAddress());
			System.out.println(" using port: " + receivePacket.getPort());
			int length2 = receivePacket.getLength();
			System.out.println("Length: " + length2);
			System.out.println("Packet: ");
			
			for(int k = 0; k<receivePacket.getData().length;k++)
			{
				System.out.print(" " + receivePacket.getData()[k]);
			}
			
			byte[] compWrite = new byte[] {0,3,0,1};
			
			if(receivePacket.getData() == compWrite){
				System.out.println("files match");
				
			}
		}
		
	}
	public void send(DatagramPacket sendPacket){
		
		try{
			sendReceiveSocket.send(sendPacket);
		}
		catch(IOException e){
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Client has sent packet\n");
		
	}
	public void receive (){
		try
		{
			sendReceiveSocket.receive(receivePacket);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
    public void sendData(String name)
    	throws FileNotFoundException, IOException
        {
    		int packNum = 0;
        	BufferedInputStream in = new BufferedInputStream(new FileInputStream("test.txt"));
        	//bytes from file
            byte[] fdata = new byte[512];
            //op code and block # + fdata
            byte[] pack = new byte[516];
            // used for cycling through file
            int n;
            // a and b used for printing packet number without negatives
            int a;
            int b;
            // data requests are op code 0 3
            pack[0] = 0;
            pack[1] = 3; 
            // while loop cycles through data in file 512 bytes at a time
            while ((n = in.read(fdata)) != -1) {
            	//setting bytes for packet number converting from int to 2 bytes
            	pack[3] = (byte) (packNum & 0xFF);
            	pack[2] = (byte) ((packNum >> 8) & 0xFF); 
            	packNum ++;
          // if end of data from file is null then the remaining part of the file was under 512 bytes
            	if (fdata[511] == 0x00){
            		// resized array to match the remaining bytes in file (from 512 to < 512)
            	    byte[] lastData = resize(fdata);
            	    System.out.println(lastData[3]);
            		System.out.println("data not 512 bytes");
            		System.out.println("Size of this is array is: " + lastData.length);
            		// copies file data behind opcode and packet number
            		System.arraycopy(lastData, 0, pack, 4, lastData.length);
            		// resizes final array from 516 to 4 + remaining data from file
            		byte[] lastPack = resize(pack);
            		// creates final packet
            		createPack(lastPack);
            		 a = lastPack[2];
                	 b = lastPack[3];
                	a &= 0xFF;
                	b &= 0xFF;
                	System.out.println(lastPack[4]);
            	}
            	else{
            	// if file is sending 512 bytes for data
            	System.arraycopy(fdata, 0, pack, 4, fdata.length);
            	createPack(fdata);
            	
            	 a = pack[2];
            	 b = pack[3];
            	a &= 0xFF;
            	b &= 0xFF;
            	}
            	System.out.println(a + ", " + b);

            	
            	
            	send(sendPacket);
            	re(fdata);
            	//receive();
            	
            }

            in.close();
        }
		// wipes array replacing all elements with null
    public byte [] re (byte[] data){
    	for (int i = 0; i < data.length; i++){
    		data[i] = 0x00;
    	}
    	return data;
    }
    // resizes arrays to remove null at the end
    public byte[] resize (byte[] data){
    	int i;
    	for(i = 4; i < data.length; i++){
    		if(data[i] == 0x00){
    			break;
    			
    		}
    	}
    	
    	data = Arrays.copyOf(data, i+1);
    	
    	return data;
    }
    
	public static void main(String[] args) 
	{

		Client c = new Client();
		//c.ClientAlgorithm();
		try {
			c.sendData("test.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
