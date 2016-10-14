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
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	private static String filePath = "M:/workspace/SYSC3303_CLIENTSERVER/Client Directory/";
	
	/////////////////////////////////////////////////////////////////////////////////////////////////////////////
	// Datagrams to be used in the client
	private DatagramSocket sendReceiveSocket;
	private DatagramPacket sendPacket, receivePacket;
	
	// User input file name and transfer mode
	private String fileName;	
	private String mode;
	
	// Size of the Packet being sent back to the client
	private byte[] sendPacketSize;
	private byte[] receivePacketSize;
	// The data of the request packet
	private byte[] message;
	private byte[] opNum = new byte[4];
	
	// User input mode of the TFTP
	private boolean quiet;
	private boolean normal;
	
	// The client directory
	private File clientDir;
	private int portNum;
	
	
	/*
	 * The main constructor for class Client
	 * When the client is initialized it creates both a Datagram socket and a 'Client Directory'
	 * to aid in file transfer.
	 */
	public Client()
	{
		// create DatagramSocket to both send and receive packets
		try
		{
			sendReceiveSocket = new DatagramSocket();
		}
		catch(SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
		
		// The mode of each request will always be octet
		mode = "ocTEt".toLowerCase();
		
		// Client Directory creation
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
	
	
	/*
	 * The ClientAlgorithm() method defines the core behavior of the TFTP Client.
	 * The Client's current state is initialized by user input (test/normal and quiet/verbose)
	 * The Client loops HERE until killed:
	 * 	- The Client is told by user input which type of request packet it should form.
	 *  - The user tells the Client which file it should be transferring.
	 *  - The Client packages the desired bytes into the sendPacket DatagramPacket.
	 *  - The request packet is then sent to a specific port (test mode = port 23, normal mode = port 69).
	 *  - The Client then waits for the DatagramSocket to receive a packet
	 *  	- If the Client sent a read request the received packet will receive a DATA packet
	 *  	- If the Client sent a write request the received packet will receive a ACK packet
	 */
	public void ClientAlgorithm()
	{
		// We want our Client to run until we say so
		boolean running = true;		
		while (running)
		{
			// The user input request
			String request = null;
	
			//keeps track between test or normal mode
			int tOrN = 0;		
			//keeps track between quiet and verbose 
			int qOrV = 0;		
    
			// USER INPUT 1: Test mode (Uses ErrorSimulator) or Normal mode (Doesn't uses the ErrorSimulator)
			System.out.print("Would you like to enter test mode, or normal mode? (1 for test 2 for normal): ");
			try
			{
				BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
				String inputString = bufferRead.readLine();
				
				while(tOrN == 0)
				{
					// If the user input 1
					if(inputString.equals("1"))
					{
						System.out.println("\nWe are now in test mode!");
						tOrN = 1;
						normal = false;
						portNum = 23;		// The request packets will be sent to the Error Simulator
					}
					
					// If the user input 2
					else if(inputString.equals("2"))
					{
						System.out.println("\nWe are now in normal mode!");
						tOrN = 2;
						normal = true;
						portNum = 69;		// The request packets will be sent to the Server
					}
					
					// If the user input was invalid
					else
					{
						System.out.print("Invalid option, please enter 1 for test mode and 2 for normal mode: ");
						inputString = bufferRead.readLine();
					}
				}
          
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
				System.exit(0);
			}
        
			// USER INPUT 2: Quiet mode (Minimal information displayed) or Verbose mode (Displays detailed information)
			System.out.print("Would you like to enter quiet mode, or verbose mode? (1 for quiet 2 for verbose): \n");
			try
			{
				BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
				String inputString = bufferRead.readLine();
      
				while(qOrV == 0)
				{
					// If the user input 1
					if(inputString.equals("1"))
					{
						System.out.println("\nWe are now in quiet mode");
						qOrV = 1;
						quiet = true;
					}
					
					// If the user input 1
					else if(inputString.equals("2"))
					{
						System.out.println("\nWe are now in verbose mode");
						qOrV = 2;
						quiet = false;
					}
					
					// If the user input is invalid
					else
					{
						System.out.print("Invalid option, please enter 1 for quiet mode and 2 for verbose mode: \n");
						inputString = bufferRead.readLine();
					}
				}
			}
			catch(IOException ex)
			{
              ex.printStackTrace();
              System.exit(0);
         	}
	
			// HERE!!!!!! The Client will stay in this loop until killed
			while(true)
			{
				// USER INPUT 3: What file will we be accessing?
				System.out.print("What file are we going to be transferring?\n");
				try
				{
					BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
					String inputString = bufferRead.readLine();
			        
					// The user input file name
					fileName = inputString;
			        
				}
				catch(IOException ex)
				{
					ex.printStackTrace();
		        }
				
				// Create an empty byte array for the request packet
				message = new byte[4 + fileName.length() + mode.length()];
				message[0] = 0;
		
				// USER INPUT 4: What are we doing to the user specified file
				System.out.print("Would you like to read a file (read) or write to a file (write)? ");
				try
				{
					BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
					String inputString = bufferRead.readLine();

					// If the user wants to read the file from the server
					if(inputString.equals("read"))
					{
						// If we give a Read request, we will send ACK packets and receive DATA packets
						sendPacketSize = new byte[4];
						receivePacketSize = new byte[516];
						message[1] = 1;		// 01 is the opcode for read
						request = "Read";
					}
					
					// If the user wants to write the file to the server
					else if(inputString.equals("write"))
					{
						// If we give a Write request, we will send DATA packets and receive ACK packets
					    sendPacketSize = new byte[516];
						receivePacketSize = new byte[4];
						message[1] = 2;		// 02 is the opcode for write
						request = "Write"; 
					}
					
					// If the user wants an invalid request
					else
					{
						message[1] = 0;		// 00 is the opcode for error
						request = "Error"; // #11 invalid request
					}
					
				}
				catch(IOException ex)
				{
                  ex.printStackTrace();
				}
		
				// CREATING THE REQUESET PACKET!
				// Data in packets must be in byte form
				byte[] fileNameToBytes = fileName.getBytes();
				int os1 = fileNameToBytes.length;
		  
				byte[] modeToBytes = mode.getBytes();
				int os2 = modeToBytes.length;
				
				// Copying data into 'message' byte array
				System.arraycopy(fileNameToBytes, 0, message, 2, os1);
				message[os1 + 2] = 0;

				System.arraycopy(modeToBytes, 0, message, os1 + 3, os2);
				int os3 = os1 + os2 + 3;
				message[os3] = 0;
				
				// CREATING THE SEND DATAGRAMPACKET
				try
				{	
					// If we are operating in Test mode, Send to the Error Simulator
					if(normal == false)
					{
						sendPacket = new DatagramPacket(message,message.length,InetAddress.getLocalHost(),23);
					}
					// If we are operating in Normal mode, Send to the Server 
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

				// Send the packet
				send(sendPacket);

				// If we are operating in verbose mode, Print what we sent
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

				
				// RECEIVING PACKETS!
				// We initialize the DatagramPacket that we receive into
				receivePacket = new DatagramPacket(receivePacketSize, receivePacketSize.length);
				try
				{
					sendReceiveSocket.receive(receivePacket);
				}
				catch(IOException e)
				{
					e.printStackTrace();
					System.exit(1);
				}
		
				// If we are operating in verbose mode, Print what we receive
				if(quiet == false)
				{
					// Receive Packet info
					System.out.println("Client received packet");
					System.out.println("Sent from Host: " + receivePacket.getAddress());
					System.out.println(" using port: " + receivePacket.getPort());
					int length2 = receivePacket.getLength();
					System.out.println("Length: " + length2);
					System.out.println("Packet: ");

					for(int k = 0; k < receivePacket.getData().length; k++)
					{
						System.out.print(" " + receivePacket.getData()[k]);
					}
					System.out.println();
				}
		
				// If we have received an ACK packet
				if(receivePacket.getData()[1] == 0x04)
				{
					try 
					{
						// Start writing data to the server directory
						sendData(fileName);
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
				
				// If we have received a DATA packet
				else if(receivePacket.getData()[1] == 0x03)
				{
					waitForData();
				}
				else
				{
				//////////////////////////////////////////////////////////////
				//	CLIENT BEHAVIOUR IF IT RECEIVES AN ERROR FROM THE SERVER//
				//////////////////////////////////////////////////////////////
				}
				
				// Last step of the loop is to ask the user if they want to kill the client
				System.out.println("Would you like to kill the client? (k to kill, any other key to keep running)");
				try
				{
					BufferedReader bufferRead = new BufferedReader(new InputStreamReader(System.in));
					String inputString = bufferRead.readLine();
		        
					// If the user wanted to kil the client
					if(inputString.equals("k"))
					{
						System.exit(0);
					}
				}
				catch(IOException ex)
				{
		        ex.printStackTrace();
		        }
				//END OF LOOP!
			}
			// END OF CLIENT ALGORITHM
		}
	}


	/*
	 * A method creates a new file in the client directory
	 */
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
				if(quiet == false)
				{
					System.out.println("Received file created in the server directory.");
					System.out.println("File: " + receivedFile.toString());
				}
			}
		}
		
	}
	
	/*
	 * A method that when called, properly formats the passed data and then writes the data into the passed file
	 */
	public void appendToFile(File f, byte[] byteData)
	{
		try
		{
			// Properly formatting data to be written
			String stringData = new String(resize(byteData));
			
			// Where are we writing to?
			File file = new File(filePath + f);

			FileWriter fw = new FileWriter(file.getAbsolutePath(),false);
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(stringData);
			bw.close();
			
			//System.out.println(file.getAbsolutePath().toString());
			System.out.println("Write successful!");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	/*
	 * A method that when called, it initializes the send DatagramPacket to the proper address and port
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
	
	/*
	 * A method that simply sends the passed packet through the Datagram Socket
	 */
	public void send(DatagramPacket sP)
	{
		if(quiet == false)
		{
			// Where we are sending the packet
			System.out.println("Client is sending to port: " + receivePacket.getPort());
		}
		
		try
		{
			// Send
			sendReceiveSocket.send(sP);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		System.out.println("Client has sent packet!");
	}
	
	/*
	 * A method that simply receives an incoming Datagram Packet
	 */
	public void receive ()
	{
		if(quiet == false)
		{
			// Where we are receiving the packet
			System.out.println("Client is receiving at " + sendReceiveSocket.getLocalPort());
		}
		try
		{
			sendReceiveSocket.receive(receivePacket);
		}
		catch(IOException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		System.out.println("We have received");
	}
	
	
	/*
	 * A method that handles what the client does when a read request is send and a DATA packet is received.
	 */
	public void handleRead(byte[] data) 
	{
		// Save the first 4 bytes of the DATA packet as 'opNum'
		System.arraycopy(receivePacket.getData(), 0, opNum, 0, 4);
		
		File tempFile = new File(fileName);
		
		// Cut the first 4 bytes off the DATA packet
		byte[] writeData = cutOP(receivePacket.getData());
		// Write data to file
		appendToFile(tempFile, writeData);
		
		// When finished writing, Send ACK
		sendAck(opNum);
	}
	
	/*
	 * A method that handles what the Client does when it receives a DATA packet
	 */
	public void waitForData()
	{
		// While the DATA packet contains 512 bytes
		while(receivePacket.getData()[515] != (byte) 0)
		{
			// Write to file
			handleRead(receivePacket.getData());
			System.out.println("Data has been writen to the file!");
			
			// Send ACK
			sendAck(opNum);
			receive();
		}
		
		// When the DATA packet contains less then 512 bytes
		handleRead(receivePacket.getData());
		System.out.println("The final data packet has been writen to the file!");
	}
	
	
	/*
	 * A method that handles how the Client sends
	 */
    public void sendData(String name) throws FileNotFoundException, IOException
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
