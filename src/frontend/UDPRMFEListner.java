package frontend;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class UDPRMFEListner implements Runnable
	{
		byte[] response = new byte[1000];
		String RMresponse = "";
		String RMListnerName;
		int port;
		FrontEnd frontEnd;
		
		public static final String RM1LISTNER_NAME = "RMListnerArsh";
		public static final String RM2LISTNER_NAME = "RMListnerIshan";
		public static final String RM3LISTNER_NAME = "RMListnerSaryu";
		
		public UDPRMFEListner(String RMListnerName, FrontEnd frontEnd)
		{
			// TODO Auto-generated constructor stub
			this.RMListnerName = RMListnerName;
			this.frontEnd = frontEnd;
		}
		
		@Override
		public void run() 
		{
			if(RMListnerName.equals(RM1LISTNER_NAME))
			{
				port = 7777;
			}
			else if(RMListnerName.equals(RM2LISTNER_NAME))
			{
				port = 8888;
			}
			else if(RMListnerName.equals(RM3LISTNER_NAME))
			{
				port = 9999;
			}
			
			try
			{
				DatagramSocket socket = new DatagramSocket(port);
				
				while(true)
				{
					byte[] buffer = new byte[1000];
					DatagramPacket request = new DatagramPacket(buffer, buffer.length);
					System.out.println("RM Front End UDP Listner Started for : " + RMListnerName);
					socket.receive(request);
					
					if(RMListnerName.equals(RM1LISTNER_NAME))
					{
						frontEnd.RM1Response_A = new String(request.getData());
						System.out.println("FrontEnd : "+FrontEnd.RM1Response_A);
						System.out.println(port+"frontEnd.RM1Response_A : "+frontEnd.RM1Response_A);
					}
					else if(RMListnerName.equals(RM2LISTNER_NAME))
					{
						frontEnd.RM2Response_I = new String(request.getData());
						System.out.println(port+"frontEnd.RM2Response_I : "+frontEnd.RM2Response_I);
					}
					else if(RMListnerName.equals(RM3LISTNER_NAME))
					{
						frontEnd.RM3Response_S = new String(request.getData());
						System.out.println("frontEnd.RM3Response_S : "+frontEnd.RM3Response_S);
					}
					
//					frontEnd.RM1Response_A = new String(request.getData());
					System.out.println(port+" : port ,frontEnd.RM1Response : "+new String(request.getData()));
//					frontEnd.RM2Response_I = new String(request.getData());
//					System.out.println(port+" : port ,frontEnd.RM2Response_I : "+frontEnd.RM2Response_I);
//					frontEnd.RM3Response_S = new String(request.getData());
//					System.out.println(port+" : port ,frontEnd.RM3Response_S : "+frontEnd.RM3Response_S);
					
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			
		}
}
