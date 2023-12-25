package server;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

//import com.webservice.TicketBooking;

public class UDPConnectionListner extends Thread
{
	public static final int METHODTYPE_ADDMOVIESLOT = 1;
	public static final int METHODTYPE_REMOVEMOVIESLOT = 2;
	public static final int METHODTYPE_LISTMOVIESHOWSAVAILABILITY = 3;
	public static final int METHODTYPE_BOOKMOVIETICKET = 4;
	public static final int METHODTYPE_GETBOOKINGSCHEDULE = 5;
	public static final int METHODTYPE_CANCELMOVIE = 6;
	
	private TicketBooking ticketBooking ;
	private int serverUDPPort;
	
	public UDPConnectionListner(TicketBooking ticketBooking,int serverUDPPort) 
	{
		this.ticketBooking = ticketBooking;
		this.serverUDPPort = serverUDPPort;
		//new Thread(this).start();
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public void run() 
	{
		int operationCalled = 0;
		String clientID = "";
		String movieShowID  = "";
		String movieName = "";
		int movieCapacity = 0;
		int numberOfTickets = 0;																	
		String []receivedCommand = new String [6];
		byte[] response = new byte[1000];
		
		try
		{
			DatagramSocket socket = new DatagramSocket(this.serverUDPPort);
			byte[] buffer = new byte[1000];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			while(true)
			{
				System.out.println("UDPConnection Handler Started for "+ serverUDPPort);
				socket.receive(request);
				System.out.println("Reached UDPConnection Handler");
				receivedCommand = (new String(request.getData())).split(",");
				System.out.println("UDP Handler DATA IS : "+ new String(request.getData()));
				System.out.println("*****Just in UDPConnectionListner : Number of Tickets :"+numberOfTickets);
				operationCalled = Integer.parseInt(receivedCommand[0]);
				
				clientID = receivedCommand[1];
				if(clientID.equals("null"))
					clientID = null;
				
				movieShowID  = receivedCommand[2];
				if(movieShowID.equals("null"))
					movieShowID = null;
				
				movieName = receivedCommand[3];
				if(movieName.equals("null"))
					movieName = null;
				
				if(!(receivedCommand[4].equals("null"))&&(!receivedCommand[4].contains("null")))
				movieCapacity = Integer.parseInt(receivedCommand[4]);
				
				if(!(receivedCommand[5].trim().equals("null"))&&(!receivedCommand[5].contains("null")))
				numberOfTickets = Integer.parseInt(receivedCommand[5].trim());

				switch(operationCalled)
				{
				case 1:
					response = this.ticketBooking.addMovieSlot(movieShowID, movieName, movieCapacity).getBytes();
					break;
				case 2 :
					//
					break;
				case 3 :
					//response = this.ticketBooking.listMovieShowsAvailability(movieName).getBytes();
					response = this.ticketBooking.singleServerAvailability(movieName).getBytes();
					break;
				case 4 : 
					//response = this.ticketBooking.bookMovieTickets(clientID, movieShowID, movieName, numberOfTickets).getBytes();
					response = this.ticketBooking.bookSingleServerMovie(clientID, movieShowID, movieName, numberOfTickets).getBytes();
					break;
				case 5 :
					response = this.ticketBooking.getSingleServerBookingSchedule(clientID).getBytes();
					break;
				case 6 : 
					response = this.ticketBooking.cancelMovieTickets(clientID, movieShowID, movieName, numberOfTickets).getBytes();
					break;
				}
				
				DatagramPacket reply = new DatagramPacket(response,response.length,request.getAddress(),request.getPort());
				socket.send(reply);

			}
			
			

			// TODO Auto-generated method stub

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

	}




}
