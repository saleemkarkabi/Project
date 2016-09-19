/**
 * Assignment 1
 * SYSC 3303 
 * Andrew Ward
 * 100898624
 * September 2016
 * 
 * Server Class
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
			receiveSocket = new DatagramSocket(69);	
			// DatagramSocket to use to receive(port 69)
			
		}
		catch(SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
	}
	/**
	 * Algorithm for Server
	 */
	public void serverAlgorithm()
	{
		while(true)
		{
			byte[] msg = new byte[50];
			byte[] data = new byte[4];
			
			receivePacket = new DatagramPacket(msg,msg.length);
			System.out.println("Server is waiting for a packet");
			
			try
			{
				System.out.println("Waiting.....\n");
				
				try
				{
					Thread.sleep(5000);
				}
				
				catch(InterruptedException e)
				{
					e.printStackTrace();
					System.exit(1);
				}
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
				if(msg[1] == 0)
				{
					throw new NoSuchFieldException();
				}
				if (msg[1] == 1)
				{
					request = "Read";
					data[1] = 3;
					data[3] = 1;
				}
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
						file = Arrays.copyOfRange(msg, 2, i);
					}
					if(count == 2)
					{
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
			
			System.out.println("\n");
			
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
