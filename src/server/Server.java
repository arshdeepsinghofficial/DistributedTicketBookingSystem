package server;

import server.UDPServer;

public class Server 
{
	public int SERVERPORT_ATWATER = 2345;
	public int SERVERPORT_VERDUN = 2346;
	public int SERVERPORT_OUTREMONT = 2347;
	
	public static void main(String areg[])throws Exception
	{
		
		ServerInstance atwater = new ServerInstance(2345);
		ServerInstance verdun = new ServerInstance(2346);
		ServerInstance outremont = new ServerInstance(2347);

		Thread thread1 = new Thread(atwater);
		thread1.start();
		Thread thread2 = new Thread(verdun);
		thread2.start();
		Thread thread3 = new Thread(outremont);
		thread3.start();
		
		UDPServer udpServer = new UDPServer();
		
	}
}
