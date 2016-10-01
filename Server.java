/**
 * Project Server class
 * SYSC 3303 L2
 * Andrew Ward, Alex Hoecht, Connor Emery, Robert Graham, Saleem Karkabi
 * 100898624,   100933730,   100980809,    100981086,     100944655
 * Fall Semester 2016
 * 
 * Server Class
 */
 
import java.io.*;
import java.net.*;
import java.util.Arrays;

/**
 * Receives a datagram packet from a client and, if it contains the proper format, responds with an acknowledgement or "data".
 */
public class Server implements Runnable 
{
	// Instance Variables 
	private DatagramSocket sendSocket, receiveSocket;
	private DatagramPacket sendPacket, receivePacket;
	private File serverDir;
	private InputChecker inuptChecker;
    private Thread inuptCheckerThread;
    
    private byte[] request = new byte[512];
    private byte[] pack = new byte[4];
    
    
	public Server(boolean b)
	{
		this(b,null);
	}
	public Server(boolean b, DatagramPacket rP)
	{
		
		inuptChecker = new InputChecker();
	    inuptCheckerThread = new Thread(inuptChecker, "Server Input Checker");

		if(!b)
		{
			try
			{
				// Datagram socket to receive UDP packets
				receiveSocket = new DatagramSocket(69);	
			}
			catch(SocketException se)
			{
				se.printStackTrace();
				System.exit(1);
			}
		}
		else
		{
			this.receivePacket = rP;
			// File directory to store any received files
			serverDir = new File("Server Directory");
			// If the directory doesnt already exist, create it
			if(!serverDir.exists())
			{
				try
				{
				//If the directory is successfully created
					serverDir.mkdir();
				}
				catch(SecurityException se)
				{
					System.exit(1);
				}
			}
		}
	}
	
	/**
	 * Algorithm for Server:
	 * Repeat forever
	 * 	Wait to receive a request from the server
	 * 	The request will be determined to be either read or write
	 * 	The format of the packet is parsed to insure valid data was received
	 * 	The information contained in the packet is printed to the console
	 * 	If the request is a read, send back the message 0301
	 * 	If the request is a write, send back the message 0400
	 * 	The message being sent back to the client it printed to the console
	 * 	A socket is created to send the new packet
	 * 	Close the socket
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public void serverAlgorithm() throws FileNotFoundException, IOException
	{
		inuptCheckerThread.start();
		while(!inuptChecker.kill)
		{
			// Byte array to contain client request
			//byte[] request = new byte[512];
			// Byte array to respond to request
			//byte[] response = new byte[4];
			// Packet to be received from client
			receivePacket = new DatagramPacket(request, request.length);
			
			/*
			 * Attempt to receive a packet from the client
			 */
			System.out.println("Server is waiting for a packet");
			System.out.println("Waiting...\n");
				
			// Slow the program down to simulate wait time
			/*try
			{
				Thread.sleep(5000);
			}
			catch(InterruptedException e)
			{
				e.printStackTrace();
				System.exit(1);
			}*/
			
			receive();
	

//////////////////////////////////////////////////////////////////////////
			new Thread(new Server(true, receivePacket)).start();
/////////////////////////////////////////////////////////////////////////

		}
		System.out.println("\nServer killed");
	    sendSocket.close();
	}
	
    public void sendPackData(String name) throws FileNotFoundException, IOException
    {
    	int packNum = 0;
    	
    	BufferedInputStream in = new BufferedInputStream(new FileInputStream(name));
    		
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
        while ((n = in.read(fdata)) != -1)
        {
        	//setting bytes for packet number converting from int to 2 bytes
            pack[3] = (byte) (packNum & 0xFF);
            pack[2] = (byte) ((packNum >> 8) & 0xFF); 
            packNum ++;
    		    
            // if end of data from file is null then the remaining part of the file was under 512 bytes
            if (fdata[511] == 0x00)
    		{
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
                sendPacket = new DatagramPacket(lastPack,lastPack.length,receivePacket.getAddress(), receivePacket.getPort());
                
                a = lastPack[2];
                b = lastPack[3];
                a &= 0xFF;
                b &= 0xFF;
                System.out.println(lastPack[4]);
            }
            else
    		{
                // if file is sending 512 bytes for data
                System.arraycopy(fdata, 0, pack, 4, fdata.length);
                
                sendPacket = new DatagramPacket(fdata,fdata.length,receivePacket.getAddress(), receivePacket.getPort());
                	
                a = pack[2];
                b = pack[3];
                a &= 0xFF;
                b &= 0xFF;
            }
            System.out.println(a + ", " + b);
         	re(fdata);
         	
         	receive();
        }
        in.close();
    }
    
    public void sendAck()
    {
    	
    	//HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
    	pack[0] = 0;
    	pack[3] = (byte) (receivePacket.getData()[3] & 0xFF);
        pack[2] = (byte) ((receivePacket.getData()[2] >> 8) & 0xFF);

        sendPacket = new DatagramPacket(pack,pack.length,receivePacket.getAddress(), receivePacket.getPort());
    }
    	
        // wipes array replacing all elements with null
        public byte [] re (byte[] data)
        {
        	for (int i = 0; i < data.length; i++)
    	{
        		data[i] = 0x00;
        	}
        	return data;
        }
    	
        // resizes arrays to remove null at the end
        public byte[] resize (byte[] data)
        {
        	int i;
        	for(i = 4; i < data.length; i++)
    	{
        		if(data[i] == 0x00)
    		{
        			break;
        			
        		}
        	}
        	
        	data = Arrays.copyOf(data, i+1);
        	
        	return data;
        }
        
    public void run()
    {
		// Once a packet has been received
		System.out.println("Server has received a packet");
		
		// Determine Read or write
		String requestType = "";
		
		System.out.println(receivePacket.getData()[1]);
		
			// DATA packet formed if READ
			if (receivePacket.getData()[1] == 0x01)
			{
				requestType = "Read request";
			}
			// ACK packet formed if WRITE
			else if(receivePacket.getData()[1] ==  0x02)
			{
				requestType = "Write request";
			}
			// If a data packet is received
			else if(receivePacket.getData()[1] ==  0x03)
			{
				requestType = "Data packet";
			}
			// Invalid request
			else
			{
				System.out.println("Invalid request... Quitting");
				System.exit(1);
			}
		
		System.out.println(requestType + " received");
		
		/*
		 * Parse received packet for proper format and extract file name and transfer mode
		 */
		 
		// File name
		byte[] file = new byte[0];
		// Transfer mode
		byte[] mode = new byte[0];
		Boolean isValidRequest = false;
		// Amount of zero bytes in the request
		int count = 0;
		
		for(int i = 2; i < request.length; i++)
		{				
			if(receivePacket.getData()[i] == 0)
			{
				// Extract data
				count++;
				if (count == 1)
				{
					file = Arrays.copyOfRange(receivePacket.getData(), 2, i);
				}
				if(count == 2)
				{
					mode = Arrays.copyOfRange(receivePacket.getData(), 3 + file.length, i);
					isValidRequest = true;
					break;
				}
			}	
		}
		
		/*try
		{
			if(!isValidRequest)
			{
				throw new NoSuchFieldException();
			}
		}
		catch(NoSuchFieldException e)
		{
			System.out.println("Invalid request... Quitting");
			System.exit(1);
		}
		*/
		/*
		 * Print received packet information
		 */
		String fileStr = new String(file);
		System.out.println("File Name: " + fileStr);
		
		String modeStr = new String(mode);
		System.out.println("Mode: " + modeStr);
		
		int len = file.length + mode.length + 4;
		System.out.println("Length: " + len);
		
		String infoStr = new String(receivePacket.getData(), 0, fileStr.length() + modeStr.length() + 4);
		System.out.println("Information as String: " + infoStr);

		String requestStr = Arrays.toString(Arrays.copyOfRange(receivePacket.getData(), 0, len));
		System.out.println("Information as Bytes: "+ requestStr + "\n");
		
		// form the packet to be sent back to the client
		// DATA packet formed if READ request received
		if (requestType.equals("Read request"))
		{
			try 
			{
				pack[1] = (byte) 3;
				sendPackData(fileStr);

			} 
			catch (FileNotFoundException e) 
			{
				e.printStackTrace();
			} 
			catch (IOException e) 
			{
				e.printStackTrace();
			}
		}
		// ACK packet formed if WRITE
		if(requestType.equals("Write request"))
		{
			pack[1] = (byte) 4;
			handleWrite(file);
		}
		// If a Data packet is received
		if(requestType.equals("Data packet"))
		{
			pack[1] = 4;
			sendAck();
			send(sendPacket);
		}
		// Invalid request
		else
		{
			System.out.println("Invalid request... Quitting");
			System.exit(1);
		}

		/*
		 * Print sent packet information
		 */
		System.out.println("Server has sent packet");
		System.out.println("To Host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		System.out.print("Response Packet: ");
		for(int k = 0; k < pack.length; k++)
		{
			System.out.print(" " + pack[k]);
		}
		
    }
    
    public void handleWrite(byte[] f)
    {
    	// 
    	boolean fileEnd = false;
    	// Convert the received file name back into a string
    	String fileName = new String(f);
    	// Create the file to be added to the directory
    	File receivedFile = new File(serverDir,fileName);
    	if(!receivedFile.exists())
    	{
    		boolean fileAdded = false;
    		try
    		{
    			// Create if the file doesn't already exist
    			receivedFile.createNewFile();
    			fileAdded = true;
    		} 
    		catch (IOException e) 
    		{
    			e.printStackTrace();
    			System.exit(1);
    		}
    		if(fileAdded)
    		{
    			System.out.println("Received file created in the server directory.");
    		}
    	}
    	
    	sendAck();
    	send(sendPacket);
    	while(!fileEnd)
    	{
    		try
    		{
    			sendSocket.receive(receivePacket);
    		}
    		catch(IOException e)
    		{
    			e.printStackTrace();
    			System.exit(1);
    		}
    	
    		byte[] temp = new byte[516];
    		receivePacket = new DatagramPacket(temp, temp.length);
    		
    		appendToFile(fileName, receivePacket.getData());
    		
    		fileEnd = true;
    		
    	}
    	System.out.print("Leaving data loop");
    	
    }
	
    /**
	 * Appends data to the specified text file. If the file does not exist then create it and append.
	 */
    public void appendToFile(String name, byte[] byteData)
    {
    	//////////////////////////////////////////////////////////////////////////
    	String filePath = "C:/Users/alexhoecht/workspace/GroupProject/";
    		
    	try
    		{
    			String stringData = new String(byteData);

    			File file = new File(filePath + name);

    			// If file does not exists, then create it
    			if (!file.exists())
    			{
    				file.createNewFile();
    			}

    			FileWriter fw = new FileWriter(file.getAbsoluteFile(), true);
    			BufferedWriter bw = new BufferedWriter(fw);
    			bw.write(stringData);
    			bw.close();
    		}
    		catch (IOException e)
    		{
    			e.printStackTrace();
    		}
    	}
    
	public void printServerDir()
	{
		int count = 1;
		// Retrieve files in the directory
		File[] tempList = serverDir.listFiles();
		
		System.out.println("The Server directory now contains: ");
		
		// Print and number each file
		for(File f : tempList)
		{
			if(f.isFile())
			{
				System.out.println(count + ". " + f.getName());
				count++;
			}
		}
	}
	
	public void send(DatagramPacket sendPacket)
	{
		/*
		 * Attempt to send response data to client
		 */
		try
		{
			sendSocket = new DatagramSocket();
		}
		catch(SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
		
		try
		{
			System.out.println("\n\n\n" + sendPacket.getPort() + "\n\n\n");
			sendSocket.send(sendPacket);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Server has sent packet\n");
		
		for(int i = 0; i < sendPacket.getData().length; i++)
		{
			System.out.print(sendPacket.getData()[i]);
		}
		
	}
	public void receive ()
	{
		try
		{
			receiveSocket.receive(receivePacket);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void createPack(byte[] packet)
	{
		sendPacket = new DatagramPacket(packet,packet.length,receivePacket.getAddress(), receivePacket.getPort());
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		Server server = new Server(false);
		server.serverAlgorithm();
	}
}
