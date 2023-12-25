package sequencer;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;

import frontend.FrontEnd;

public class Sequencer implements Runnable
{
	
	public static final int FRONTEND_UDPPORT = 8081;
	public static final int SEQUENCER_UDPPORT = 8082;
	public String RMOutput = "";
	public String RMInput = "";
	public int sequenceNumber = 1;
	
	public static void main(String areg[])
	{
		Sequencer sequencer = new Sequencer();
		Thread sequencerThread = new Thread(sequencer);
		sequencerThread.start();
	}

	@Override
	public void run() 
	{
		try
		{
			DatagramSocket receiverSocket = new DatagramSocket(SEQUENCER_UDPPORT);
			
			while(true)
			{
				byte[] buffer = new byte[1024];
				DatagramPacket input = new DatagramPacket(buffer, buffer.length);
				System.out.println("Sequencer UDP Listner Started");
				receiverSocket.receive(input);
				System.out.println("rECEIVED BY THE SEQUENCER .. !!!");
				System.out.println("RMInput : "+ RMInput);
				RMInput = new String(input.getData()).trim();
				System.out.println("RMInput : "+RMInput);
				String splitted = RMInput.split("-_-")[0];
				if (splitted.equals("0"))
				{
					setSequenceNumber();
					DatagramSocket senderSocket = new DatagramSocket();
					byte [] output = RMOutput.getBytes();
					InetAddress hostname = InetAddress.getByName("localhost");
					DatagramPacket response = new DatagramPacket(output, output.length, hostname, FRONTEND_UDPPORT);
					senderSocket.send(response);
					sendUDPMulticast();
				}
				else if (splitted.equals("2"))
				{
					RMOutput = RMInput;
					System.out.println("Sending Recovery Message");
					sendUDPMulticast();
					
				}
				else if(splitted.equals("2")) 
				{
					// TO-DO .... SendForSwitchThePort()
				}
				
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public void setSequenceNumber()
	{
		RMOutput = new String(RMInput.substring(0,1)+"-_-"+sequenceNumber +"-_-"+ RMInput.substring(4));
		sequenceNumber++;
		System.out.println("In Sequencer : " + sequenceNumber);
		System.out.println("In Sequencer : " + RMOutput);
		System.out.println("Sequencer Response : "+ RMOutput);
	}
	
	private void broadcastMessageToRm(List<InetAddress> listAllBroadcastAddresses, byte[] sequencedData, int multicastPort) {
        for(InetAddress addr: listAllBroadcastAddresses) {
        	System.out.println("Inside Loop");
        	System.out.println(addr.getHostAddress());
            DatagramSocket socketInner;
            try {
                socketInner = new DatagramSocket();
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }
            try {
                socketInner.setBroadcast(true);
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

           // System.out.println("Sequencer sending request to RM : MulticastAddress - "+multicastAddress);
            System.out.println("Sequencer sending request to RM : MulticastPort - "+multicastPort);

            DatagramPacket packet
                    = new DatagramPacket(sequencedData, sequencedData.length, addr, multicastPort);
            try {
                socketInner.send(packet);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            socketInner.close();
        }
    }

    private List<InetAddress> listAllBroadcastAddresses() {
        List<InetAddress> broadcastList = new ArrayList<>();
        Enumeration<NetworkInterface> interfaces
                = null;
        try {
            interfaces = NetworkInterface.getNetworkInterfaces();
        } catch (SocketException e) {
            throw new RuntimeException(e);
        }
        while (interfaces.hasMoreElements()) {
            NetworkInterface networkInterface = interfaces.nextElement();

            try {
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue;
                }
            } catch (SocketException e) {
                throw new RuntimeException(e);
            }

            networkInterface.getInterfaceAddresses().stream()
                    .map(a -> a.getBroadcast())
                    .filter(Objects::nonNull)
                    .forEach(broadcastList::add);
        }
        return broadcastList;
    }
	
	public void sendUDPMulticast()
	{
		try {
		    MulticastSocket multicastSocket = new MulticastSocket();

		    // create a datagram packet containing the request data
		    String request = "";
		    byte[] input = RMOutput.getBytes();
		   // InetAddress hostname = InetAddress.getByName("230.0.0.1");
		   // DatagramPacket packet = new DatagramPacket(input, input.length, hostname, 8083);
		    List<InetAddress> listAllBroadcastAddresses = listAllBroadcastAddresses();
            broadcastMessageToRm(listAllBroadcastAddresses,input,8090);
		   // multicastSocket.send(packet);
		    //System.out.println("UDP Multicast Done with data : "+ RMOutput);
		   // multicastSocket.close();
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
		

	}
}
