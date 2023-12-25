package server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

import Client.Client;

public class UDPServer
{
	public static final int METHODTYPE_ADDMOVIESLOT = 1;
	public static final int METHODTYPE_REMOVEMOVIESLOT = 2;
	public static final int METHODTYPE_LISTMOVIESHOWSAVAILABILITY = 3;
	public static final int METHODTYPE_BOOKMOVIETICKET = 4;
	public static final int METHODTYPE_GETBOOKINGSCHEDULE = 5;
	public static final int METHODTYPE_CANCELMOVIE = 6;
	
	public int requestedServerPort;
	public int UDPServerPort = 2348;
	
//	UDPServer(int requestedServerPort)
//	{
//		//new Thread(new Server())).put
//		new Thread(new receiver()).start();
//	}
	
	public UDPServer() 
	{
		// TODO Auto-generated constructor stub
		new Thread(new InterServerCommunication()).start();
		System.out.println("UDP Server Started");
	}
}

class InterServerCommunication implements Runnable
{

	public static final int UDPPORTID_ATWATER = 4444;
	public static final int UDPPORTID_VERDUN = 5555;
	public static final int UDPPORTID_OUTREMONT = 6666;
	public static final int UDPSERVERPORT = 2348;
	int receiverPort;
	String message;
	byte [] response;
	@Override
	public void run() 
	{
		
		receiver();
				
	}
	
	public void receiver()
	{
		int operationCalled = 0;
		String clientID = "";
		String movieShowID  = "";
		String movieName = "";
		int movieCapacity = 0;
		int numberOfTickets = 0;																	
		String []receivedCommand = new String [7];
		
		try
		{
			// TODO Auto-generated method stub
			DatagramSocket socket = new DatagramSocket(UDPSERVERPORT);
			byte[] buffer = new byte[1000];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			while(true)
			{
				socket.receive(request);
				System.out.println("Request Received on UDP Server");
				
				receivedCommand = (new String(request.getData())).split(",");
				System.out.println("DATA IS : "+ new String(request.getData()));
				System.out.println("DATA IS : "+ receivedCommand);
				System.out.println("*****receiver() Number of Tickets :"+numberOfTickets);

				receiverPort = Integer.parseInt(receivedCommand[0]);
				operationCalled = Integer.parseInt(receivedCommand[1]);
				
				
				clientID = receivedCommand[2];
				if(clientID.equals("null"))
					clientID = null;
						
				movieShowID  = receivedCommand[3];
				if(movieShowID.equals("null"))
					movieShowID = null;
				
				movieName = receivedCommand[4];
				if(movieName.equals("null"))
					movieName = null;
				
				if((!(receivedCommand[5].equals("null"))&&(!receivedCommand[5].contains("null"))))
					movieCapacity = Integer.parseInt(receivedCommand[5]);
				
				System.out.println("NOOFTICKER:"+receivedCommand[6]+":");
				if(!(receivedCommand[6].trim().equals("null"))&&(!receivedCommand[6].contains("null")))
					numberOfTickets = Integer.parseInt(receivedCommand[6].trim());
				System.out.println("*****3Just before receive to send to client listner Number of Tickets :"+numberOfTickets);
				
				//message = operationCalled +","+ clientID +","+ movieShowID +","+ movieName +","+ movieCapacity +","+ numberOfTickets;
				message = new String(request.getData()).substring(5);
				sender();
				DatagramPacket reply = new DatagramPacket(response,response.length,request.getAddress(),request.getPort());
				socket.send(reply);
			}
		}
			
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	
	public String sender()
	{
		DatagramSocket socket = null;
		System.out.println("Reached Sender");
		try
		{
			socket = new DatagramSocket();
			byte [] input = message.getBytes();
			InetAddress hostname = InetAddress.getLocalHost();
			DatagramPacket request = new DatagramPacket(input, message.length(), hostname, receiverPort);
			System.out.println("receiverPort in UDPServer:"+receiverPort);
			System.out.println("*****Just before UDP Server .send(request) Number of Tickets :"+request);
			socket.send(request);
			System.out.println("receiverPort in UDPServer:"+receiverPort+":Sent");
			
			byte [] output = new byte[1000];
			DatagramPacket reply = new DatagramPacket(output, output.length);
			socket.receive(reply);
			//return new String(reply.getData());
			response = reply.getData();
			return Client.OPERATION_SUCCESS;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return Client.OPERATION_FAIL;
		}
		finally 
		{
			if(socket != null)
			{
				socket.close();
			}
		}

	}
	
	

	
}
