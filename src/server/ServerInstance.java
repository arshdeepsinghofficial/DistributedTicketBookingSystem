package server;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import javax.xml.ws.Endpoint;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import Client.Client;
import server.UDPConnectionListner;
//import TicketBookingInterfaceApp.TicketBookingInterface;
//import TicketBookingInterfaceApp.TicketBookingInterfaceHelper;
//import client.Client;

public class ServerInstance implements Runnable
{
	String serverID;
	String serverName;
	int serverPort;
	int serverUDPPort;
	String serverInput;
	String serverOutput;
	TicketBooking ticketBooking;
	
	public ServerInstance(int serverPort) throws Exception
	{
		try
		{
			if(serverPort == 2345)
			{
				serverID = Client.CLIENTSERVER_ATWATER;
				serverName = "ATWATER";
				serverUDPPort = 4444;
			}
			else if(serverPort == 2346)
			{
				serverID = Client.CLIENTSERVER_VERDUN;
				serverName = "VERDUN";
				serverUDPPort = 5555;
			}
			else if(serverPort == 2347)
			{
				serverID = Client.CLIENTSERVER_OUTREMONT;
				serverName = "OUTREMONT";
				serverUDPPort = 6666;
			}
			
			else if(serverPort == 9345)
			{
				serverID = Client.CLIENTSERVER_ATWATER;
				serverName = "ATWATER";
				serverUDPPort = 7878;
			}
			else if(serverPort == 9346)
			{
				serverID = Client.CLIENTSERVER_VERDUN;
				serverName = "VERDUN";
				serverUDPPort = 8989;
			}
			else if(serverPort == 9347)
			{
				serverID = Client.CLIENTSERVER_OUTREMONT;
				serverName = "OUTREMONT";
				serverUDPPort = 9090;
			}
			
			this.serverPort = serverPort;
			this.serverInput = "";
			this.serverOutput = "";
			ticketBooking = new TicketBooking(serverName);
//		    orb = ORB.init(args, null);
//			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
//			rootpoa.the_POAManager().activate();
//			
//			TicketBooking ticketBooking = new TicketBooking(serverName);
//			ticketBooking.setOrb(orb);
//			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(ticketBooking);
//			TicketBookingInterface href = TicketBookingInterfaceHelper.narrow(ref);
//			
//			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
//			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
//			NameComponent path[] = ncRef.to_name("TICKET_BOOKING_"+serverID);
//			ncRef.rebind(path,href);
//			//System.out.println("Addition Server ready and waiting");
//			System.out.println(serverName + " Server Started ");
			//for(;;)
			{
				//System.out.println("Output from port:" + args[1]);
				//orb.run();
			}
			
//			Endpoint endpoint = Endpoint.publish("htttps://localhost:8080/TicketBooking/Atwater", new TicketBooking(serverName));
//			System.out.println(serverName + " Server Started ");

			UDPConnectionListner UDPConnectionListner = new UDPConnectionListner(ticketBooking,serverUDPPort); 	
			UDPConnectionListner.start();

			
		}
		catch(Exception e)
		{
			e.printStackTrace();
			System.out.println("Exception Occured ! Exiting");
		}
	}

	@Override
	public void run() 
	{
		try
		{
			System.out.println("serverPort : " + serverPort);
			DatagramSocket receiverSocket = new DatagramSocket(serverPort);
			System.out.println(serverName + " ------------------Server Started");
			byte[] buffer = new byte[1024];
			while(true)
			{
				DatagramPacket input = new DatagramPacket(buffer, buffer.length);
				
				receiverSocket.receive(input);
				serverOutput = new String();
				serverInput = new String(input.getData());
				System.out.println("serverInput :---------------- "+serverInput);
				callServerOperations();
				
				DatagramSocket senderSocket = new DatagramSocket();
				byte [] output = serverOutput.getBytes();
				DatagramPacket response = new DatagramPacket(output, output.length, input.getAddress(), input.getPort());
				senderSocket.send(response);
				
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public String callServerOperations()
	{
		System.out.println("Reached "+serverName+" Server.");
		System.out.println("Operation Request is : " + serverInput);
		

		String operationCalled = "";
		String clientID = "";
		String movieShowID  = "";
		String movieName = "";
		int movieCapacity = 0;
		int numberOfTickets = 0;	
		String oldMovieName = "";
		String oldMovieShowID  = "";
		String []receivedCommand = new String [6];
		byte[] response = new byte[1000];

		receivedCommand = serverInput.split("-_-");
		operationCalled = receivedCommand[0];

		clientID = receivedCommand[4];
		if(clientID.equals("null"))
			clientID = null;

		movieShowID  = receivedCommand[2];
		if(movieShowID.equals("null"))
			movieShowID = null;

		movieName = receivedCommand[1];
		if(movieName.equals("null"))
			movieName = null;

		if(!(receivedCommand[3].equals("null"))&&(!receivedCommand[3].contains("null")))
			movieCapacity = Integer.parseInt(receivedCommand[3]);

		if(!(receivedCommand[5].trim().equals("null"))&&(!receivedCommand[5].contains("null")))
			numberOfTickets = Integer.parseInt(receivedCommand[5].trim());

		if(!((receivedCommand[6]).trim().equals("null"))&&(!receivedCommand[6].contains("null")))
			oldMovieName = receivedCommand[6].trim();

		if(!((receivedCommand[7]).trim().equals("null"))&&(!receivedCommand[7].contains("null")))
			oldMovieShowID = receivedCommand[7].trim();

		System.out.println("operationCalled : "+operationCalled);
		System.out.println("clientID : "+clientID);
		System.out.println("movieShowID : "+movieShowID);
		System.out.println("movieName : "+ movieName);
		System.out.println("movieCapacity : "+ movieCapacity);
		System.out.println("oldMovieName : "+oldMovieName);
		System.out.println("numberOfTickets : "+numberOfTickets);
		System.out.println("oldMovieShowID : "+oldMovieShowID);

		switch(operationCalled)
		{
		case "addMovieSlot":
			serverOutput = ticketBooking.addMovieSlot(movieShowID, movieName, movieCapacity);
			break;
		case "removeMovieSlots" :
			serverOutput = ticketBooking.removeMovieSlots(movieShowID,movieName);
			break;
		case "listMovieShowsAvailability" :
			serverOutput = ticketBooking.listMovieShowsAvailability(movieName);
			//response = this.ticketBooking.singleServerAvailability(movieName).getBytes();
			break;
		case "bookMovieTickets" : 
			System.out.println("SERVER INSTNCE : "+clientID);
			serverOutput = ticketBooking.bookMovieTickets(clientID, movieShowID, movieName, numberOfTickets);
			//response = this.ticketBooking.bookSingleServerMovie(clientID, movieShowID, movieName, numberOfTickets).getBytes();
			break;
		case "getBookingSchedule" :
			serverOutput = ticketBooking.getBookingSchedule(clientID);
			break;
		case "cancelMovieTickets" : 
			serverOutput = ticketBooking.cancelMovieTickets(clientID, movieShowID, movieName, numberOfTickets);
			break;
		case "exchangeTickets" : 
			serverOutput = ticketBooking.exchangeTickets(clientID, oldMovieName, oldMovieShowID, movieShowID, movieName, numberOfTickets);
			break;
		}

		System.out.println("serverOutput : "+serverOutput);
		System.out.println("serverPort : "+serverPort);
		if(serverPort == 9345 || serverPort == 9346 || serverPort == 9347)
		{
			System.out.println("Backup Server is UP and responding");
			return serverOutput;
		}
		else 
		{
			System.out.println("Server Output . invalid : -----------------------------------");
			serverOutput = serverOutput+" Invalid ";
			return serverOutput;
		}
	}

//	@Override
//	public void run() {
//		// TODO Auto-generated method stub
//		try
//		{
//			//ticketBooking = new TicketBooking(serverName); 
//		//Endpoint endpoint = Endpoint.publish("http://localhost:8080/TicketBooking/"+serverID, new TicketBooking(serverName));
//		Endpoint endpoint = Endpoint.publish("http://localhost:8080/TicketBooking/"+serverID, ticketBooking);
//		if(endpoint.isPublished())
//		{
//			System.out.println(serverName + " Server Started ");
//		}
//		else
//		{
//			System.out.println(serverName + " Server is down!Please try later");
//		}
//		
////		
////		orb = ORB.init(args, null);
////		POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
////		rootpoa.the_POAManager().activate();
////		
////		//TicketBooking ticketBooking = new TicketBooking(serverName);
////		ticketBooking.setOrb(orb);
////		org.omg.CORBA.Object ref = rootpoa.servant_to_reference(ticketBooking);
////		TicketBookingInterface href = TicketBookingInterfaceHelper.narrow(ref);
////		
////		org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
////		NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
////		NameComponent path[] = ncRef.to_name("TICKET_BOOKING_"+serverID);
////		ncRef.rebind(path,href);
////		//System.out.println("Addition Server ready and waiting");
////		System.out.println(serverName + " Server Started ");
////		for(;;)
////		{
////			orb.run();
////		}
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//			System.out.println("Exception Occured ! Exiting");
//		}
//		
//	}
	
	
}
