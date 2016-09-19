/**
 * Assignment #1
 * SYSC 3303
 * Andrew Ward
 * 100898624
 * September 2016
 * 
 * Client Class
 */
import java.io.IOException;
import java.net.*;
import java.util.Arrays;



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
		
	}
	/**
	 * Algorithm for Client 
	 */

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
			
			
			//sending packet
			
			try
			{
				sendReceiveSocket.send(sendPacket);
			}
			catch(IOException e)
			{
				e.printStackTrace();
				System.exit(1);
			}
			
			System.out.println("Client has sent packet\n");
			
			// receive packet
			
			byte[] data = new byte[4];
			receivePacket = new DatagramPacket(data,data.length);
			
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
			
			for(int k = 0; k<data.length;k++)
			{
				System.out.print(" " + data[k]);
			}
		}
		
	}
	public static void main(String[] args) 
	{
		Client c = new Client();
		c.ClientAlgorithm();

	}

}
