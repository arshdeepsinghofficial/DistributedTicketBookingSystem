package replicamanager;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class UDPRMListner implements Runnable
{

	public RMInstance rmInstance;
	
	public UDPRMListner(RMInstance rmInstance)
	{
		this.rmInstance = rmInstance;
	}
	@Override
	public void run() 
	{
		String RMInput = "";
		String RMOutput = "";
		
		try
		{
			DatagramSocket receiverSocket = new DatagramSocket(8087);
//			DatagramSocket receiverSocket = new DatagramSocket(8088);
//			DatagramSocket receiverSocket = new DatagramSocket(8089);
			
			while(true)
			{
				byte[] buffer = new byte[1024];
				DatagramPacket input = new DatagramPacket(buffer, buffer.length);
				System.out.println("RM UDP Listner Started");
				receiverSocket.receive(input);
				System.out.println("rECEIVED BY THE RM Listner .. !!!");
				RMInput = new String(input.getData());
				System.out.println("RMInput : "+RMInput);
				String splitted = RMInput.split("-_-")[0];
				
				
				if(splitted.equals("RMIshan"))
				{
					rmInstance.RM2Request_I = RMInput.substring(12);
				}
				else if(splitted.equals("RMSaryu"))
				{
					rmInstance.RM3Request_S = RMInput.substring(12);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}		
	}

}
