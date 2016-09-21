/**
 * Project Server class
 * SYSC 3303 
 * Andrew Ward, Alex Hoecht, Connor Emery, Robert Graham, Saleem Karkabi
 * 100898624,   100933730,   100980809,    100981086,     100944655
 * Fall Semester 2016
 * 
 * Server Class
 */
import java.io.IOException;
import java.net.*;
import java.util.Arrays;


public class Server 
{
	//Instance Variables 
	private DatagramSocket sendSocket, receiveSocket;
	private DatagramPacket sendPacket, receivePacket;
	
	public Server()
	{
		try
		{
			receiveSocket = new DatagramSocket(69);	
			// DatagramSocket created to receive(port 69)
			
		}
		catch(SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Algorithm for Server:
	 * Repeat forever
	 * 	Wait to receive a request from the server.
	 * 	The request will be deturmined to be either read or write.
	 * 	The format of the packet is parsed to insure valid data was received.
	 * 	The information contained in the packet is printed to the console.
	 * 	If the request is a read, send back the message 0301
	 * 	If the request is a write, send back the message 0400	 * 
	 * 	The message being sent back to the client it printed to the console.
	 * 	A socket is created to send the new packet.
	 * 	Close the socket
	 * 
	 */
	public void serverAlgorithm()
	{
		while(true)
		{
			//byte arrays created to pack and unpacked data
			byte[] msg = new byte[50];
			byte[] data = new byte[4];
			
			//The packet that is received from the client
			receivePacket = new DatagramPacket(msg,msg.length);
			System.out.println("Server is waiting for a packet");
			
			try
			{
				System.out.println("Waiting.....\n");
				
				//Slow the program down to simulate wait time
				try
				{
					Thread.sleep(5000);
				}
				catch(InterruptedException e)
				{
					e.printStackTrace();
					System.exit(1);
				}
				
				//Receive the packet
				receiveSocket.receive(receivePacket);
			}
			catch(IOException e)
			{
				System.out.println("Error\n" + e);
				e.printStackTrace();
				System.exit(1);
			}
		
			System.out.println("Server has received a packet");
			
			String request = "";
			
			try
			{
				//Invalid request receive
				if(msg[1] == 0)
				{
					throw new NoSuchFieldException();
				}
				//Read request receive
				if (msg[1] == 1)
				{
					request = "Read";
					data[1] = 3;
					data[3] = 1;
				}
				//Write request receive
				if(msg[1] == 2)
				{
					request = "Write";
					data[1] = 4;
				}
			}
			
			catch(NoSuchFieldException e)
			{
				System.out.println("Invalid Request..... Quitting");
				System.exit(1);
			}
			
			//Parsing the packet received
			System.out.println(request + " Request received");
			byte[] file = new byte[1];
			byte[] mode = new byte[1];
			byte[] msgBytes = new byte[1];
			int count = 0;
			for(int i = 2; i < msg.length; i++)
			{				
				if(msg[i] == 0)
				{
					count++;
					if (count == 1)
					{
						// get the file name
						file = Arrays.copyOfRange(msg, 2, i);
					}
					if(count == 2)
					{
						// get the mode
						mode = Arrays.copyOfRange(msg, 3 + file.length, i);
						break;
					}
				}	
			}
			//Printing the information of the received packet
			String fileName = new String(file);
			System.out.println("File Name: " + fileName);
			
			String mode2 = new String(mode);
			System.out.println("Mode: " + mode2);
			
			int len = file.length + mode.length + 4;
			System.out.println("Length: " + len);
			
			String infoString = new String(msg,0,fileName.length() + mode2.length() + 4);
			System.out.println("Information as String: " + infoString);

			msgBytes = Arrays.copyOfRange(msg, 0, len);
			System.out.println("Information as Bytes: "+ Arrays.toString(msgBytes) + "\n");
			
			//Creating the new packet to be sent back to the Client
			sendPacket = new DatagramPacket(data,data.length,
					receivePacket.getAddress(),receivePacket.getPort());
			
			//Printing out the information of the packet being sent
			System.out.println("Server sent packet");
			System.out.println("To Host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			System.out.print("Response Packet: ");
			
			for(int k = 0; k<data.length;k++)
			{
				System.out.print(" " + data[k]);
			}
			
			System.out.println();
			
			try{
				sendSocket = new DatagramSocket();
			}
			catch(SocketException se)
			{
				se.printStackTrace();
				System.exit(1);
			}
			
		    try {
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
	
	public static void main(String[] args)
	{
		Server server = new Server();
		server.serverAlgorithm();
	}
}
