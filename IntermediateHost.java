/**
 * Assignment #1
 * SYSC 3303 
 * Andrew Ward
 * 100898624
 * September 2016
 * 
 * Intermediate Host Class
 */

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class IntermediateHost 
{
	
	private DatagramSocket sendSocket, receiveSocket, sendReceiveSocket;
	private DatagramPacket sendPacketServer, sendPacketClient, receivePacketServer, receivePacketClient;
	
	public IntermediateHost()
	{
		try
		{
			receiveSocket = new DatagramSocket(23);
			sendReceiveSocket = new DatagramSocket();
		}
		catch(SocketException e)
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 *Algorithm for Intermediate Host
	 */
	public void intermediateHostAlgorithm()
	{
		String request = "";
		while(true)
		{
			byte[] message = new byte[50];
			byte[] data = new byte[4];
			
			//Receive Packet
			int msglength = message.length;
			receivePacketClient = new DatagramPacket(message,msglength);
			System.out.println("Intermediate Host waiting for packet");
			
			//Packet from Client
			try
			{
				System.out.println("Waiting on Packet");
				receiveSocket.receive(receivePacketClient);
			}			
			catch(IOException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			System.out.println("Intermediate Host has received a packet");
			
			if(message[1] == 0)
			{
				request = "Invalid";
			}
			if(message[1] == 1)
			{
				request = "Read";
			}
			if(message[1] == 2)
			{
				request = "Write";
			}
			
			System.out.println(request + " Request received");
			String fileName = "";
			String mode2 = "";
			String Stringinfo = "";
			byte[] Bytesinfo = new byte[2];
			
			//Parsing
			if(!request.equals("Invalid"))
			{
				byte[] file = new byte[1];
				byte[] mode = new byte[1];
				int count = 0;
				for(int i = 2; i < msglength; i++)
				{				
					if(message[i] == 0)
					{
						count++;
						if (count == 1)
						{
							file = Arrays.copyOfRange(message, 2, i);
						}
						if(count == 2)
						{
							mode = Arrays.copyOfRange(message, 3 + file.length, i);
							break;
						}
					}	
				}
				//Printing Received Packet
				fileName = new String(file);
				System.out.println("File Name: " + fileName);
				
				mode2 = new String(mode);
				System.out.println("Mode: " + mode2);
				
				int msgLength = 4 +fileName.length() + mode2.length();
				Stringinfo = new String(message,0,msgLength);
				System.out.println("Information as String: " + Stringinfo);
				Bytesinfo = Arrays.copyOfRange(message, 0, msgLength);
				System.out.println("Information as Bytes: "+ Arrays.toString(Bytesinfo) + "\n");
			}
			
			//Send Packet
			try
			{
				sendPacketServer = new DatagramPacket(message,message.length,
						InetAddress.getLocalHost(),69);
			}
			
			catch(UnknownHostException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			System.out.println("Intermediate Host is sending a packet");
			System.out.println("Sending " + request + " Request");
			
			if(!request.equals("Invalid"))
			{
				System.out.println("File Name: " + fileName);
				System.out.println("Mode: " + mode2);
				System.out.println("Information as String: " + Stringinfo);
				int msgLength = fileName.length() + mode2.length() + 4;
				Bytesinfo = Arrays.copyOfRange(message, 0, msgLength);
				System.out.println("Information as Bytes: "+ Arrays.toString(Bytesinfo) + "\n");
			}
			
			//Packet to Server
			try
			{
				sendReceiveSocket.send(sendPacketServer);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			//Receive Packet from Server
			receivePacketServer = new DatagramPacket(data,data.length);
			
			//Receive Packet
			try
			{
				sendReceiveSocket.receive(receivePacketServer);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			//Print Receive
			System.out.println("Intermediate Host received a packet");
			System.out.println("From Server " + receivePacketServer.getAddress());
			System.out.println("With port: " + receivePacketServer.getPort());
			
			int len = receivePacketServer.getLength();
			System.out.println("Length: " + len);
			
			System.out.print("Info: ");
			
			for(int k = 0; k<data.length;k++)
			{
				System.out.print(" " + data[k]);
			}
			
			System.out.println("\n");
			
			//Send packet for Client 
			sendPacketClient = new DatagramPacket(data,data.length,receivePacketClient.getAddress(),receivePacketClient.getPort());
			
			//Create Socket
			try
			{
				sendSocket = new DatagramSocket();
			}
			
			catch(SocketException se)
			{
				se.printStackTrace();
				System.exit(1);
			}
			
			//printing information to be sent to client
			System.out.println("Intermediate Host sent packet");
			System.out.println("To Host: " + sendPacketClient.getAddress());
			System.out.println("Destination host port: " + sendPacketClient.getPort());
			System.out.print("Response Packet: ");
			
			for(int k = 0; k<data.length;k++)
			{
				System.out.print(" " + data[k]);
			}
			
			System.out.println("\n");
			
			//Send Packet to Client 
		    try 
		    {
		        sendSocket.send(sendPacketClient);
		    }
		    catch(IOException e)
		    {
		        e.printStackTrace();
		        System.exit(1);
		    }
		}
	}
	
	public static void main(String args[])
	{
		IntermediateHost i = new IntermediateHost();
		i.intermediateHostAlgorithm();
	}
}

