	import java.io.BufferedWriter;
	import java.io.File;
	import java.io.FileWriter;
	import java.io.IOException;
	import java.net.*;
	import java.util.Arrays;
public class SubServer implements Runnable {
	private DatagramSocket sendSocket;
	private DatagramPacket sendDataPacket, sendPacket;
	byte[] data = new byte[516];
	byte[] ack = new byte[4];
	private File serverDir;
	private File receivedFile;
	private String fileName;
	
	public SubServer ( int target, byte[] d, String fN){
		fileName = fN;
		try
		{
			sendSocket = new DatagramSocket();	
		}
		catch(SocketException se)
		{
			se.printStackTrace();
			System.exit(1);
		}
		try {
			sendPacket = new DatagramPacket (ack, ack.length-1,InetAddress.getLocalHost(), target);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
			try {
				sendDataPacket = new DatagramPacket (d, d.length, InetAddress.getLocalHost(), target);
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		data = d;
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
		
		
	

	@Override
	public void run() {
		System.out.println("data " + data[1]);
		if (data[1] == 2){
			sendAck(2, true);
			System.out.println(fileName + " is being created");
			receivedFile = new File(serverDir,fileName);
			waitForData();
		}
		sendSocket.close();		
	}
	
	public void waitForData(){
		System.out.println("Server is waiting for data packet");
		receive();
		while(sendDataPacket.getData()[515] != 0x00){
			handleWrite(sendDataPacket.getData());
			System.out.println("data packet has been writen to file");
			receive();
		}
		handleWrite(sendDataPacket.getData());
		System.out.println("final data packet has been writen to file");
	}
	
	public void receive(){
		try {
			sendSocket.receive(sendDataPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendPacket(){
		try {
			sendSocket.send(sendDataPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void appendToFile(File f, byte[] byteData)
    {
    	String filePath = "C:/Users/alexh/Desktop/school/Carleton/Year 4/Sysc 3303/GroupProject/";
    	try
    	{
    		String stringData = new String(byteData);

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
	
	public void fileCreation(byte[] f){
		// Convert the received file name back into a string
    	fileName = new String(f);
    	
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
    			System.out.println("File: " + receivedFile.toString());
    		}
    	}
	}
	public void handleWrite(byte[] f)
    {
    	
    	byte[] opNum = new byte[4];
    	System.arraycopy(f, 0, opNum, 0, 4);

    	appendToFile(receivedFile, sendDataPacket.getData());
    	sendAck(opNum);
    	sendPacket();

	
    }
	public void sendAck (int code, boolean start){
		sendPacket.getData()[1] = 0x04;
		System.out.println("Sending ACK to client " + sendPacket.getPort());
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void sendAck (byte[] code){
		sendPacket.setData(code);
		try {
			sendSocket.send(sendPacket);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
