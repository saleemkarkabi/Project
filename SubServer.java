import java.io.*;
import java.net.*;
import java.util.Arrays;

public class SubServer implements Runnable {
	
////////////////////////////////////////////////////////////////////////////////////////
/*
 * IMPORTANT!!!!!!!
 * SET THE FILE PATH TO THE SERVER DIRECTORY!!!!!!!!!!!!!!!!!!!!!
 * IF COPYING PATH FROM 'FILE EXPLORER': -ALL '\' MUST BE CHANGED TO '/'
 *									     -THE PATH SHOULD ALSO END WITH A '/'
 */
	private static String filePath = "M:/workspace/SYSC3303_CLIENTSERVER/Server Directory/";
////////////////////////////////////////////////////////////////////////////////////////
	
	private DatagramSocket subServerSocket;
	private DatagramPacket receivePacket, sendPacket;
	
	// Size of the Packet being sent back to the client
	byte[] sendPacketSize;
	byte[] receivePacketSize;
	
	private byte[] data = new byte[516];
	private byte[] ack = new byte[4];
	private byte[] opNum = new byte[4];
	
	// Integer representation of the packet number
	private int packetCounter = 0;
	
	// The file being accessed by the client request
	private File receivedFile;
	private String fileName;
	
	public SubServer ( int target, byte[] d, String fN, byte[] requestOp)
	{
		// Save the name of the requested file
		fileName = fN;
		
		// Create Socket
		try
		{
			subServerSocket = new DatagramSocket();	
		}
		catch(SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
		
		// Create packet that will be sent back to the client
		// If we received a Read request, we will send DATA packets and receive ACK packets
		if(requestOp[1] == 3)
		{
			sendPacketSize = new byte[516];
			receivePacketSize = new byte[4];
		}
		// If we received a Write request, we will send ACK packets and receive DATA packets
		else
		{
			sendPacketSize = new byte[4];
			receivePacketSize = new byte[516];
		}
		
		// Creating Datagram Packets!
		try 
		{
			// Create the packet of designated size, and points to the address and port of
			// the client
			sendPacket = new DatagramPacket (sendPacketSize, sendPacketSize.length-1,
													InetAddress.getLocalHost(), target);
		} 
		catch (UnknownHostException e) 
		{
			e.printStackTrace();
		}

		// Create the packet of designated size, and points to the address and port of
		// the client
		receivePacket = new DatagramPacket (receivePacketSize, receivePacketSize.length);
		
		data = d;
		ack = requestOp;
	}
		
	
	@Override
	public void run() 
	{
		//System.out.println("data " + data[1]);
		
		// If write request
		if (data[1] == 2)
		{
			// File creation
			System.out.println(fileName + " is being created");
			fileCreation(fileName);
			
			sendAck(2, true);
			waitForData();
		}
		// If read request
		else if(data[1] == 1)
		{
			// HANDLE READ REQUEST STUFFFFF
		}
		else
		{
			// ERROR REQUEST REACHED
		}
		subServerSocket.close();		
	}
	
	
	public void fileCreation(String f)
	{
		// Convert the received file name back into a string
    	//String filePath = "C:/Users/alexh/Desktop/school/Carleton/Year 4/Sysc 3303/GroupProject/";
    	
    	// Create the file to be added to the directory
    	receivedFile = new File(f);
    	
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
	}
	
	
	public void appendToFile(File f, byte[] byteData)
    {
    	try
    	{
    		String stringData = new String(cutEnd(byteData));

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
	
	
	public void sendAck (int code, boolean start)
	{
		//Printing out the information of the packet being sent
		System.out.println("Server sent ACK packet");
		System.out.println("To Host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		System.out.print("Packet: ");
		
		for(int k = 0; k<ack.length;k++)
		{
			System.out.print(" " + ack[k]);
		}
		// sendPacket data is set to the ACK packet
		sendPacket.setData(ack);
		
		System.out.println();
		try
		{
			subServerSocket.send(sendPacket);
		} 
		
		catch (IOException e)
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	public void sendAck (byte[] code)
	{
		sendPacket.setData(code);
		System.out.println("Server sent ACK packet");
		System.out.println("To Host: " + sendPacket.getAddress());
		System.out.println("Destination host port: " + sendPacket.getPort());
		System.out.print("Response Packet: ");
		
		for(int k = 0; k<ack.length;k++)
		{
			System.out.print(" " + ack[k]);
		}
		
		System.out.println();
		
		try 
		{
			subServerSocket.send(sendPacket);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	public void waitForData()
	{
		System.out.println("Server is waiting to receive a data packet");
		
		receive();
		
		while(receivePacket.getData()[515] != (byte) 0)
		{
			handleWrite(receivePacket.getData());
			System.out.println("data packet has been writen to file");
			receive();
		}
		
		handleWrite(receivePacket.getData());
		System.out.println("final data packet has been writen to file");
		// Reset the packet counter
		packetCounter = 0;
	}
	
	
	public void receive(){
		try 
		{
			subServerSocket.receive(receivePacket);
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	
	public void sendPacket(){
		try {
			subServerSocket.send(receivePacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void handleWrite(byte[] f)
    {
    	packetCounter++;
    	System.arraycopy(f, 0, opNum, 0, 4);
    	opNum[3] = (byte) packetCounter;
    	byte[] writeData = resize(receivePacket.getData());
    	appendToFile(receivedFile, writeData);
    	sendAck(opNum);
    }
	
	
	// resizes arrays to remove null at the end
    public byte[] resize (byte[] data)
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
    public byte[] cutEnd (byte[] data)
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
    

}
