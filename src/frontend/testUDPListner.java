package frontend;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class testUDPListner 
{
	public static void main(String areg[])
	{
		try
		{
		DatagramSocket socket = new DatagramSocket(8888);
		byte[] buffer = new byte[1000];
		DatagramPacket request = new DatagramPacket(buffer, buffer.length);
		socket.receive(request);
		System.out.println(new String(request.getData()));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}
	

}
