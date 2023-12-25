package replicamanager;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class RM 
{
	//public static final String NETWORK_IPADDRESS_LOCALHOST = "localhost";
	public static final String SEQUENCER_RM_NETWORK_IPADDRESS_LAN = "230.0.0.1";
	public static final String RM_RM_NETWORK_IPADDRESS_LAN = "230.0.0.2";
	public static final int SEQUENCER_RM_MULTICAST_UDPPORT = 8083;
	public static final String RM1_NAME = "RMArsh";
	public static final String RM2_NAME = "RMIshan";
	public static final String RM3_NAME = "RMSaryu"; 
	public static final int RM_RM_MULTICAST_PORT = 8084;
	public static final int RM1_SERVER1_UNICASTUDPPORT = 4444;
	public static final int RM2_SERVER2_UNICASTUDPPORT = 5555;
	public static final int RM3_SERVER3_UNICASTUDPPORT = 6666;
	public static final int RM1_FE_UNICASTUDPPORT = 7777;
	public static final int RM2_FE_UNICASTUDPPORT = 8888;
	public static final int RM3_FE_UNICASTUDPPORT = 9999;
	public static final int UDPSERVERPORT = 2348;
	public static final int SERVERPORT_ATWATER = 2345;
	public static final int SERVERPORT_VERDUN = 2346;
	public static final int SERVERPORT_OUTREMONT = 2347;
	
	public static final String RM4_NAME = "RMBackup";
	
	public static void main(String areg[])
	{
		
		RMInstance rm1 = new RMInstance(RM1_NAME);
//		RMInstance rm2 = new RMInstance(RM2_NAME);
//		RMInstance rm3 = new RMInstance(RM3_NAME);
//		RMInstance rm4 = new RMInstance(RM4_NAME);
		
		Thread thread1 = new Thread(rm1);
		thread1.start();
//		Thread thread2 = new Thread(rm2);
//		thread2.start();
//		Thread thread3 = new Thread(rm3);
//		thread3.start();
//		Thread thread4 = new Thread(rm4);
//		thread4.start();
		
		
	}
	
	public void setVariables()
	{
		
	}
	
	
}
