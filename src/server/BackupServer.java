package server;

public class BackupServer 
{
	public static void main(String areg[])throws Exception
	{
		ServerInstance atwaterDummy = new ServerInstance(9345);
		ServerInstance verdunDummy = new ServerInstance(9346);
		ServerInstance outremontDummy = new ServerInstance(9347);
		
		Thread thread1Dummy = new Thread(atwaterDummy);
		thread1Dummy.start();
		Thread thread2Dummy = new Thread(verdunDummy);
		thread2Dummy.start();
		Thread thread3Dummy = new Thread(outremontDummy);
		thread3Dummy.start();
				
	}
}
