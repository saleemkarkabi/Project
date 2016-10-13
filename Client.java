/**
 * Project Client class
 * SYSC 3303 L2
 * Andrew Ward, Alex Hoecht, Connor Emery, Robert Graham, Saleem Karkabi
 * 100898624,   100933730,   100980809,    100981086,     100944655
 * Fall Semester 2016
 * 
 * Client Class
 */
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
	private byte[] opNum = new byte[4];
	private boolean quiet;
	private boolean normal;
	private File clientDir;
	private int portNum;
	
	// Size of the Packet being sent back to the client
	private byte[] sendPacketSize;
	private byte[] receivePacketSize;
	
	public Client()
	{
		// create DatagramSocket to use to both send and receive
		try
		{
			sendReceiveSocket = new DatagramSocket();
			System.out.println(sendReceiveSocket.getLocalPort());
		}
		catch(SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
		
		mode = "ocTEt".toLowerCase();
		fileName = "stupud.txt";		// LMAO
		
		clientDir = new File("Client Directory");
		// If the directory doesnt already exist, create it
		if(!clientDir.exists())
		{
			try
			{
			//If the directory is successfully created
				clientDir.mkdir();
			}
			catch(SecurityException se)
			{
				System.exit(1);
			}
		}
	}
	//}
		//System.out.print("Leaving data loop");
	
	public void ClientAlgorithm()
	{
		  boolean running = true;
		  while (running){
		  String request = null;
		
		  int tOrN = 0;//keeps track between test or normal mode
	      int qOrV = 0;//keeps track between quiet and verbose 
	    
	      System.out.print("Would you like to enter test mode, or normal mode? (1 for test 2 for normal): ");
	      try{
	    	
	        BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	        String inputString = bufferRead.readLine();
	          
	          while(tOrN == 0){
	        	  
	              if(inputString.equals("1")){
	            	
	                 System.out.println("\nWe are now in test mode\n");
	                 tOrN = 1;
	                 normal = false;
	                 portNum = 23;
	              
	              }else if(inputString.equals("2")){
	            	
	        	     System.out.println("\nWe are now in normal mode\n");
	        	     tOrN = 2;
	        	     normal = true;
	        	     portNum = 69;
	        	  
	              }else{
	            	
	        	     System.out.print("Invalid option, please enter 1 for test mode and 2 for normal mode: ");
	        	     inputString = bufferRead.readLine();
	        	  
	              }
	            
	          }
	          
	      }catch(IOException ex){
	        	
	          ex.printStackTrace();
	          
	      }
	        
	      System.out.print("Would you like to enter quiet mode, or verbose mode? (1 for quiet 2 for verbose): ");
	      try{
	    	
	          BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	          String inputString = bufferRead.readLine();
	      
	          while(qOrV == 0){
	        	  
	              if(inputString.equals("1")){
	            	
	                  System.out.println("\nWe are now in quiet mode\n");
	                  qOrV = 1;
	                  quiet = true;
	          
	              }else if(inputString.equals("2")){
	            	
	    	          System.out.println("\nWe are now in verbose mode\n");
	    	          qOrV = 2;
	    	          quiet = false;
	    	  
	              }else{
	            	
	    	          System.out.print("Invalid option, please enter 1 for quiet mode and 2 for verbose mode: ");
	    	          inputString = bufferRead.readLine();
	    	      
	              }
	        
	          }
	      
	          }catch(IOException ex){
	        	
	              ex.printStackTrace();
	      
	          }
		
		  int w = 0;
		  while(w == 0)
		  {
			  message = new byte[4 + fileName.length() + mode.length()];
			  message[0] = 0;
			
			  System.out.print("Would you like to read a file (read) or write to a file (write)? ");
			  try{
			    BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	            String inputString = bufferRead.readLine();
	            
	            
	            
			    if(inputString.equals("read"))
			    {
				    message[1] =1;
				    request = "Read"; // read request
					// If we give a Read request, we will send ACK packets and receive DATA packets
					sendPacketSize = new byte[4];
					receivePacketSize = new byte[516];
			    }
			    else if(inputString.equals("write"))
			    {
				    message[1]=2;
				    request = "Write"; // write request
				 // If we give a Write request, we will send DATA packets and receive ACK packets
				    sendPacketSize = new byte[516];
					receivePacketSize = new byte[4];  
			    }
			    else
			    {
				    message[1] = 0;
				    request = "Error"; // #11 invalid request
			    }
			
			
			   }catch(IOException ex){
	            	
	                  ex.printStackTrace();
	          
	           }
			  
			  receivePacket = new DatagramPacket(receivePacketSize, receivePacketSize.length);
			  
	
			  System.out.print("what is the name of your file? ");
			
			  try{
				  BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
		          String inputString = bufferRead.readLine();
		        
		          fileName = inputString;
		        
			  }catch(IOException ex){
	        	
	              ex.printStackTrace();
	      
	          }
		
			
			  byte[] fileNameToBytes = fileName.getBytes();
			  int os1 = fileNameToBytes.length;
			  
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
				  if(normal == false){
					
					  sendPacket = new DatagramPacket(message,message.length,InetAddress.getLocalHost(),23);
					  //System.out.println("we are here");
				  }
				  else
				  {
					  sendPacket = new DatagramPacket(message,message.length,InetAddress.getLocalHost(),69);
					
				  }
			  }
			  catch(UnknownHostException e)
			  {
				  e.printStackTrace();
				  System.exit(1);
			  }
			
			
			  send(sendPacket);
			
			
			
			  //Send Packet info
			
			  if(quiet == false)
			  {
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
			  }
			
			  
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
			
			  if(quiet == false){
				
				  // Receive Packet info
				  System.out.println("Client received packet");
				  System.out.println("Sent from Host: " + receivePacket.getAddress());
				  System.out.println(" using port: " + receivePacket.getPort());
				  int length2 = receivePacket.getLength();
				  System.out.println("Length: " + length2);
				  System.out.println("Packet: ");
			  }
			
			  for(int k = 0; k<receivePacket.getData().length;k++)
			  {
				  System.out.print(" " + receivePacket.getData()[k]);
			  }
			  
			  System.out.println();
			
			  if(receivePacket.getData()[1] == 0x04)
			  {
				  try 
				  {
					  sendData(fileName);
					  System.out.println("we are here");
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
			  else if(receivePacket.getData()[1] == 0x03)
			  {
				  waitForData();
			  }
		  }
		
		}
		System.out.println("Would you like to kill the client? (k to kill, any other key to keep running)");
		try{
			BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
	        String inputString = bufferRead.readLine();
	        
	        if(inputString.equals("k"))
	        	System.exit(0);
	        
		}catch(IOException ex){
			
	    	
	        ex.printStackTrace();
	
	    }
	}
	

	public void fileCreation(byte[] f)
	{
		// Convert the received file name back into a string
		String fileName = new String(f);
		// Create the file to be added to the directory
		File receivedFile = new File(clientDir,fileName);
		
		// If the file does not exist in the client directory
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
				System.out.println("File: " + receivedFile.toString());
			}
		}
		
		//appendToFile(receivedFile, receivePacket.getData());
	}
	public void appendToFile(File f, byte[] byteData)
	{
		//////////////////////////////////////////////////////////////////////////
		String filePath = "M:/workspace/SYSC3303_CLIENTSERVER/Client Directory/";
		try
		{
			String stringData = new String(resize(byteData));
	
			File file = new File(filePath + f);
	
			System.out.println(file.getAbsolutePath().toString());
	
			FileWriter fw = new FileWriter(file.getAbsolutePath(),true);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(stringData);
			bw.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Algorithm for Client 
	 * 
	 */
	public void createPack(byte[] packet)
	{
		try
		{
			sendPacket = new DatagramPacket(packet,packet.length,InetAddress.getLocalHost(),portNum);
		}
		catch(UnknownHostException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
    	
	}
	
	 public void send(DatagramPacket sP)
	{
		
		System.out.println(receivePacket.getPort()+ " port we r sending to");
		
		try
		{
			sendReceiveSocket.send(sP);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("Client has sent packet\n");
		
	}
	public void receive ()
	{
		try
		{
			System.out.println("we are receiving at \n\n" + sendReceiveSocket.getLocalPort() + "\n\n");
			sendReceiveSocket.receive(receivePacket);
			System.out.println("We have received");
			
			
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
	}
	
	public void handleRead(byte[] data) 
	{
		
		System.arraycopy(receivePacket.getData(), 0, opNum, 0, 4);
		File tempFile = new File(fileName);
		byte[] writeData = cutOP(receivePacket.getData());
		appendToFile(tempFile, writeData);
		sendAck(opNum);
	}
	
	public void waitForData()
	{
		System.out.println("Client is waiting to receive a data packet");
		
		//receive();
		
		while(receivePacket.getData()[515] != (byte) 0)
		{
			handleRead(receivePacket.getData());
			System.out.println("data packet has been writen to file");
			sendAck(opNum);
			receive();
		}
		
		handleRead(receivePacket.getData());
		System.out.println("final data packet has been writen to file");
	}
	
    public void sendData(String name)
	throws FileNotFoundException, IOException
	{
    	portNum = receivePacket.getPort();
	    sendPacket.setPort(portNum);
	    System.out.println("Sending data to: " + sendPacket.getPort());
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
	    		createPack(lastPack);
	    		a = lastPack[2];
	        	b = lastPack[3];
	        	a &= 0xFF;
	        	b &= 0xFF;
	    	}
	    	else
	    	{
	    		System.out.println("\n" + fdata[511] + "\n");
	    	// if file is sending 512 bytes for data
	    	System.arraycopy(fdata, 0, pack, 4, fdata.length);
	    	createPack(pack);
	    	
	    	a = pack[2];
	    	b = pack[3];
	    	a &= 0xFF;
	    	b &= 0xFF;
	    	}
	    	//System.out.println(a + ", " + b);
	
	    	for (int i = 0; i < pack.length; i++){
	    		System.out.print(" " + pack[i]);
	    	}
	    	System.out.println( "\n \n" + sendPacket.getData()[1] + " 2nd byte of data being sent");
	    	send(sendPacket);
	    	re(fdata);
	    	System.out.println("Reaching receive");
	    	receive();
	    	System.out.println( "\n \n" + receivePacket.getData()[1] + " 2nd byte of data being sent");
	    	
	    }
	    System.out.println("Leaving Send Data");
	    in.close();
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
    
 // removes opcode from datagram packet
    public byte[] cutOP (byte[] data)
    {
    	byte[] temp = new byte[data.length];
    	int j = 0;
    	for(int i = 4; i < data.length; i++)
    	{
    		temp[j] = data[i];
    		j++;
    	}
    	return temp;
    } 
    
    public void sendAck (byte[] code)
	{
    	code[1] = (byte) 4;
    	sendPacket.setPort(receivePacket.getPort());
		sendPacket.setData(code);
		System.out.println("Server sent ACK packet");
		System.out.println("To Host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		System.out.print("Response Packet: ");
		
		for(int k = 0; k<opNum.length;k++)
		{
			System.out.print(" " + opNum[k]);
		}
		
		System.out.println();
		
		try 
		{
			sendReceiveSocket.send(sendPacket);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
    
	public static void main(String[] args) 
	{

		Client c = new Client();
		c.ClientAlgorithm();


	}
}
