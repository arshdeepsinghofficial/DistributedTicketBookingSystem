package replicamanager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import Client.Client;

public class RMInstance implements Runnable
{	
	public static final String RM1_NAME = "RMArsh";
	public static final String RM2_NAME = "RMIshan";
	public static final String RM3_NAME = "RMSaryu"; 
	public UDPRMListner udpRMListner;
	public int previousRequestSequence;
	public int requestSequenceNumber;
	public String serverIPAddress = "";
	public String serverUDPPort = "";
	public String backupServerIPAddress = "";
	public String backupServerUDPPort = "";
	public String RM1Request_A = "";
	public String RM2Request_I = "";
	public String RM3Request_S = "";
	public String RM1Response_A = "";
	public String RM2Response_I = "";
	public String RM3Response_S = "";
	public String serverResponse = "";
	public String RMName;
	public String clientServer = "";
	public static boolean RM_ServerFailedFlag = false;

	
	
	
	public RMInstance(String RMName)
	{
		previousRequestSequence = 0;
		this.RMName = RMName;
		udpRMListner = new UDPRMListner(this);
	}
	
	@Override
	public void run() 
	{
		try 
		{
			// create a multicast socket
			MulticastSocket multicastSocket = new MulticastSocket(8090);
			InetAddress receivedGroup = InetAddress.getByName("224.0.0.7");
			multicastSocket.joinGroup(receivedGroup);
			System.out.println("Entered RM1");
			while(true)
			{
				byte[] buffer = new byte[1024];
				DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
				System.out.println("Entered RM2");
				multicastSocket.receive(packet);
				System.out.println("Entered RM3 : "  );
				String requestData = new String(packet.getData(), 0, packet.getLength());
				RM1Request_A = requestData.trim();
				System.out.println("Received multicast request: " + requestData);
				System.out.println("Received multicast request on RM: " + RMName);
				processRequest(RM1Request_A);
				if(!RM1Request_A.split("-_-")[0].equals("2"))
				{
				sendReplyToFE();
				}
			}

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
	}
	
	public String processRequest(String requestData)
	{
		System.out.println("Inside Process Request");
//		setClientServer();
		int requestType = Integer.parseInt(requestData.split("-_-")[0]);

		System.out.println("Request Type : " + requestType);
		System.out.println("Request Data : " + requestData.split("-_-")[1].trim());
		System.out.println("RMName : " + RMName);
		if(requestType == 2 && requestData.split("-_-")[1].trim().equals(RMName))
		{
			RM_ServerFailedFlag = true;
			startServerRecoveryProcess();
			return "1";
		}
		else if (requestType == 2)
		{
			return "1";
		}
		setClientServer();
		requestSequenceNumber = Integer.parseInt(requestData.split("-_-")[1]);
		System.out.println("sendUDPMulticastToRMs()");
		sendUDPMulticastToRMs();
		System.out.println("requestSequenceNumber : "+ requestSequenceNumber);
		System.out.println("previousRequestSequence"+ previousRequestSequence);

		if(requestSequenceNumber == previousRequestSequence+1)
		{	
			System.out.println("Forwarding request from RM to Server");
			forwaredRequestToServer();
		}
		else
		{
			RM1Request_A = RM2Request_I;
			forwaredRequestToServer();
		}
		
		previousRequestSequence++;
		System.out.println("processRequest ENDED");
		return "1";
	}
	
	public void sendUDPMulticastToRMs()
	{
		System.out.println("Sending : sendUDPMulticastToRMs()");
		try 
		{
			MulticastSocket multicastSocket = new MulticastSocket();
			String request = "";
			byte[] input = RM1Request_A.getBytes();
			
			InetAddress hostname = InetAddress.getByName("224.0.0.3");
			DatagramPacket packet = new DatagramPacket(input, input.length, hostname, 8084);
//			DatagramPacket packet = new DatagramPacket(input, input.length, hostname, 8085);
//			DatagramPacket packet = new DatagramPacket(input, input.length, hostname, 8086);

			multicastSocket.send(packet);
			System.out.println("Send Completed : sendUDPMulticastToRMs()");
			multicastSocket.close();

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
			System.out.println("sendUDPMulticastToRMs EXCEPTION");

		}
		System.out.println("sendUDPMulticastToRMs ENDED");

	}
	
	public void startServerRecoveryProcess()
	{
		System.out.println("Inside startServerRecoveryProcess Now");
		System.out.println("INITIATING RECOVERY");
		
		
		//System.exit(0);
		
		//Make new instance of current server on new thread.
	}
	
	public boolean checkWithOtherReplicas()
	{
		if(((RM1Request_A.split("-_-")[0].equals(RM2Request_I.split("-_-")[0]))) && ((RM1Request_A.split("-_-")[0].equals(RM3Request_S.split("-_-")[0]))))
		{
			return true;
		}
		
		return false;
	}
	
	public void forwaredRequestToServer()
	{
		int serverUDPPort = 0;
		int serverBackupUDPPort = 0;

		
			if(clientServer.equals("ATW"))
			{
				serverBackupUDPPort = 9345;
			}
			else if(clientServer.equals("VER"))
			{
				serverBackupUDPPort = 9346;
			}
			else if(clientServer.equals("OUT"))
			{
				serverBackupUDPPort = 9347;
			}
		
			if(clientServer.equals("ATW"))
			{
				serverUDPPort = 2345;
			}
			else if(clientServer.equals("VER"))
			{
				serverUDPPort = 2346;
			}
			else if(clientServer.equals("OUT"))
			{
				serverUDPPort = 2347;
			}
		
		DatagramSocket socket = null;
		DatagramSocket socketDummy = null;
		try
		{
			System.out.println("Server UDP port : " + serverUDPPort);
			socket = new DatagramSocket();
			socketDummy = new DatagramSocket();
			System.out.println("RM1Request_A on RM1 is :" + RM1Request_A);
			RM1Request_A = RM1Request_A.substring(RM1Request_A.indexOf("-_-")+3);
			RM1Request_A = RM1Request_A.substring(RM1Request_A.indexOf("-_-")+3);
			RM1Request_A = RM1Request_A.substring(RM1Request_A.indexOf("-_-")+3);
			RM1Request_A = RM1Request_A.substring(RM1Request_A.indexOf("-_-")+3);
			System.out.println("RM1Request_A on RM1 is :" + RM1Request_A);
			byte [] input = RM1Request_A.getBytes();
			InetAddress hostname = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(input, input.length, hostname, serverUDPPort);

			socket.send(request);
			
			
			//Sending Request to Backup Server - Start
			DatagramPacket requestDummy = new DatagramPacket(input, input.length, hostname, serverBackupUDPPort);
			socketDummy.send(requestDummy);
			//Sending Request to Backup Server - End
			
			byte [] output = new byte[1000];
			DatagramPacket reply = new DatagramPacket(output, output.length);
			
			System.out.println("RM_ServerFailedFlag : "+RM_ServerFailedFlag);
			if(RM_ServerFailedFlag)
			{
				System.out.println("Returning from Dummy Server");

				
				socketDummy.receive(reply);
				
				System.out.println("Down server response"+reply.getData());
			}
			else
			{
				try
				{
				socket.setSoTimeout(5000);
				System.out.println("Waiting");
				socket.receive(reply);
				}
				catch(Exception e)
				{
					
				}
			}
			
			System.out.println(" RETURNING FROM forwaredRequestToServer TRY : "+ reply.getData() );
			System.out.println(" RETURNING FROM forwaredRequestToServer TRY");

			RM1Response_A =  new String(reply.getData());
		}
		catch(Exception e)
		{
			//e.printStackTrace();
			//return Client.OPERATION_FAIL;
		}
		finally 
		{
			if(socket != null)
			{System.out.println("forwaredRequestToServer ENDED");
				socket.close();
			}
			System.out.println("forwaredRequestToServer ENDED :OUT1");
		}
	
	}
	
	public void sendReplyToFE()
	{
		DatagramSocket socket = null;
		try
		{
			socket = new DatagramSocket();
			//RM1Response_A = RM1Response_A.trim() + "Test from Arsh";
			RM1Response_A = RM1Response_A.trim();
			byte [] input = RM1Response_A.getBytes();
			InetAddress hostname = InetAddress.getByName("192.168.189.181");
			DatagramPacket request = new DatagramPacket(input, input.length, hostname, 7777);
//			DatagramPacket request = new DatagramPacket(input, input.length, hostname, 8888);
//			DatagramPacket request = new DatagramPacket(input, input.length, hostname, 9999);
			System.out.println("Sending to FE : "+ RM1Response_A);
			socket.send(request);
			

		}
		catch(Exception e)
		{
			e.printStackTrace();
			//return Client.OPERATION_FAIL;
		}
		finally 
		{
			if(socket != null)
			{
				socket.close();
			}
		}
	}
	
	public void setClientServer()
	{
		String temp;
		
		temp = RM1Request_A.substring(RM1Request_A.indexOf("-_-")+3);
		System.out.println("temp : "+temp);
		temp = temp.substring(temp.indexOf("-_-")+3);
		System.out.println("temp : "+temp);
		temp = temp.substring(temp.indexOf("-_-")+3);
		System.out.println("temp : "+temp);
		clientServer = temp.substring(0,3);
		System.out.println("client Server is :"+clientServer );
	}
}
