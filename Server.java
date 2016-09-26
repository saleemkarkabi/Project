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
public class Server 
{
	// Instance Variables 
	private DatagramSocket sendSocket, receiveSocket;
	private DatagramPacket sendPacket, receivePacket;
	private File serverDir;
	
	public Server()
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
	 */
	public void serverAlgorithm()
	{
		while(true)
		{
			// Byte array to contain client request
			byte[] request = new byte[50];
			// Byte array to respond to request
			byte[] response = new byte[4];
			// Packet to be received from client
			receivePacket = new DatagramPacket(request, request.length);
			
			/*
			 * Attempt to receive a packet from the client
			 */
			
			System.out.println("Server is waiting for a packet");
			
			try
			{
				System.out.println("Waiting...\n");
				
				// Slow the program down to simulate wait time
				try
				{
					Thread.sleep(5000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
					System.exit(1);
				}
				
				// Receive the packet
				receiveSocket.receive(receivePacket);
			}
			catch(IOException e)
			{
				System.out.println("Error\n" + e);
				e.printStackTrace();
				System.exit(1);
			}
		
			System.out.println("Server has received a packet");
			
			/*
			 * IMPLEMENT THE DATA AND ACK PACKET FORMATION HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
			 * 
			 */
			
			// Read or write
			String requestType = "";
			
			try
			{
				// DATA packet formed if READ
				if (request[0] == 0 && request[1] == 1)
				{
					requestType = "Read";
					response[1] = 3;
					response[3] = 1;
				}
				// ACK packet formed if WRITE
				else if(request[0] == 0 && request[1] == 2)
				{
					requestType = "Write";
					response[1] = 4;
				}
				// Invalid request
				else
				{
					throw new NoSuchFieldException();
				}
			}
			catch(NoSuchFieldException e)
			{
				System.out.println("Invalid request... Quitting");
				System.exit(1);
			}
			
			System.out.println(requestType + " request received");
			
			/*
			 * Parse received packet for proper format and extract file name and transfer mode
			 */
			 
			// File name
			byte[] file = new byte[0];
			// Transfer mode
			byte[] mode = new byte[0];
			byte[] msgBytes = new byte[0];
			Boolean isValidRequest = false;
			// Amount of zero bytes in the request
			int count = 0;
			for(int i = 2; i < request.length; i++)
			{				
				if(request[i] == 0)
				{
					// Extract data
					count++;
					if (count == 1)
					{
						file = Arrays.copyOfRange(request, 2, i);
					}
					if(count == 2)
					{
						mode = Arrays.copyOfRange(request, 3 + file.length, i);
						isValidRequest = true;
						break;
					}
				}	
			}
			
			try
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
			
			// Convert the received file name back into a string
			String fileName = new String(file);
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
					System.out.println("Received file successfully added to the server directory.");
				}
			}
			
			/*
			 * Print received packet information
			 */
			
			String fileStr = new String(file);
			System.out.println("File Name: " + fileStr);
			
			String modeStr = new String(mode);
			System.out.println("Mode: " + modeStr);
			
			int len = file.length + mode.length + 4;
			System.out.println("Length: " + len);
			
			String infoStr = new String(request, 0, fileStr.length() + modeStr.length() + 4);
			System.out.println("Information as String: " + infoStr);

			String requestStr = Arrays.toString(Arrays.copyOfRange(request, 0, len));
			System.out.println("Information as Bytes: "+ requestStr + "\n");
			
			// Create response packet to be sent back to client
			sendPacket = new DatagramPacket(response, response.length, receivePacket.getAddress(), receivePacket.getPort());
			
			/*
			 * Print sent packet information
			 */
			 
			System.out.println("Server has sent packet");
			System.out.println("To Host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			System.out.print("Response Packet: ");
			for(int k = 0; k < response.length; k++)
			{
				System.out.print(" " + response[k]);
			}
			System.out.println();
			
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
		        sendSocket.send(sendPacket);
		    }
		    catch(IOException e)
		    {
		        e.printStackTrace();
		        System.exit(1);
		    }	
		    sendSocket.close();
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
	
	public static void main(String[] args)
	{
		Server server = new Server();
		server.serverAlgorithm();
	}
}
