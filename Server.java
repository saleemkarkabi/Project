/**
 * Assignment 1
 * SYSC 3303 
 * Andrew Ward
 * 100898624
 * September 2016
 * 
 * Receives a request from an intermediate host and parses the request for valid format. If the packet is invalid, throws an exception and quits. If the packet is a read or write request sends back 0301 or 0400 respectively.
 */
import java.io.IOException;
import java.net.*;
import java.util.Arrays;


public class Server 
{

	private DatagramSocket sendSocket,receiveSocket;
	private DatagramPacket sendPacket,receivePacket;
	
	public Server()
	{
		try
		{
			// Datagram socket bound to port 69 on the local host machine
			// This socket will be used to receive UDP Datagram packets
			receiveSocket = new DatagramSocket(69);	
			
		}
		catch(SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
	}
	
	/**
	 * Waits for 5 seconds to receive a packet, parses received data for validity, then prints received info. Constructs a send packet and sends it back from where it came from.
	 */
	public void serverAlgorithm()
	{
		while(true)
		{
			// Byte array for received datagram packet
			byte[] msg = new byte[50];
			// Response data
			byte[] data = new byte[4];
			// Packet in which the request will be stored
			receivePacket = new DatagramPacket(msg, msg.length);
			System.out.println("Server is waiting for a packet");
			
			try
			{
				System.out.println("Waiting...");
				
				try
				{
					// Wait for 5 seconds
					Thread.sleep(5000);
				}
				
				catch(InterruptedException e)
				{
					e.printStackTrace();
					System.exit(1);
				}
				// Store request data in receive packet
				receiveSocket.receive(receivePacket);
			}
			
			catch(IOException e)
			{
				System.out.println("Error\n" + e);
				e.printStackTrace();
				System.exit(1);
			}
		
			System.out.println("Server has received a packet");
			
			// Type of request received
			String request = "";
			
			try
			{
				// Invalid opcode
				if(msg[1] == 0)
				{
					throw new NoSuchFieldException();
				}
				// Read request
				if (msg[1] == 1)
				{
					request = "Read";
					data[1] = 3;
					data[3] = 1;
				}
				// Write request
				if(msg[1] == 2)
				{
					request = "Write";
					data[1] = 4;
				}
			}
			
			catch(NoSuchFieldException e)
			{
				System.out.println("Invalid request... Quitting");
				System.exit(1);
			}
			
			// Print type of request received
			System.out.println(request + " request received");
			
			// Parse received packet for validity
			
			// File name
			byte[] file = new byte[1];
			// Transfer mode
			byte[] mode = new byte[1];
			byte[] msgBytes = new byte[1];
			// Counts amount of zero bytes so far
			int count = 0;
			for(int i = 2; i < msg.length; i++)
			{				
				if(msg[i] == 0)
				{
					count++;
					if (count == 1)
					{
						// Copy requested file name
						file = Arrays.copyOfRange(msg, 2, i);
					}
					if(count == 2)
					{
						// Copy requested transfer mode
						mode = Arrays.copyOfRange(msg, 3 + file.length, i);
						break;
					}
				}	
			}
			
			// Print information of received packet
			String fileName = new String(file);
			System.out.println("File Name: " + fileName);
			
			String mode2 = new String(mode);
			System.out.println("Mode: " + mode2);
			
			int len = file.length + mode.length + 4;
			System.out.println("Length: " + len);
			
			String infoString = new String(msg, 0, fileName.length() + mode2.length() + 4);
			System.out.println("Information as String: " + infoString);

			msgBytes = Arrays.copyOfRange(msg, 0, len);
			System.out.println("Information as Bytes: "+ Arrays.toString(msgBytes) + "\n");
			
			
			sendPacket = new DatagramPacket(data,data.length, receivePacket.getAddress(),receivePacket.getPort());
			
			// Printing out the information of the packet being sent
			System.out.println("Server sent packet");
			System.out.println("To Host: " + sendPacket.getAddress());
			System.out.println("Destination host port: " + sendPacket.getPort());
			System.out.print("Response Packet: ");
			
			for(int k = 0; k < data.length; k++)
			{
				System.out.print(" " + data[k]);
			}
			
			System.out.println("\n");
			
			try
			{
				// Datagram socket bound to any available port on the local host machine.
				// This socket will be used to send UDP Datagram packets
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
	
	public static void main(String[] args)
	{
		Server server = new Server();
		server.serverAlgorithm();
	}
}
