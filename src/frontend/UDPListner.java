package frontend;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPListner implements Runnable
{
	byte[] response = new byte[1000];
	String RMresponse = "";
	
	@Override
	public void run() 
	{
		try
		{
			DatagramSocket socket = new DatagramSocket(8081);
			byte[] buffer = new byte[1000];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			while(true)
			{
				System.out.println("Front End UDP Listner Started");
				socket.receive(request);
				RMresponse = new String(request.getData());
			}
		}
		catch(Exception e)
		{
			
		}
		
	}

}
