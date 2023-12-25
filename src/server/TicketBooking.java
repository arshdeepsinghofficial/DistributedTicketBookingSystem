package server;

import javax.jws.WebService;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.omg.CORBA.ORB;

//import TicketBookingInterfaceApp.*;
import Utilities.CustomerBooking;
//import logs.Logs;
import Utilities.*;
import Client.Client;
import logs.Logs;

@WebService(endpointInterface = "com.webservice.TicketBookingInterface")
public class TicketBooking implements TicketBookingInterface
{
	public String serverName;
	public Map<String,Map<String,MovieShow>> database ;
	public String serverResponse;
	public static Logs serverLogs;
	public CustomerBooking client;
	public ORB orb;
	
	protected TicketBooking(String serverName) throws RemoteException,IOException
	{
		this.serverName = serverName;
		database = new ConcurrentHashMap<>();
		database.put("AVATAR", new ConcurrentHashMap<>());
		database.put("AVENGERS", new ConcurrentHashMap<>());
		database.put("TITANIC", new ConcurrentHashMap<>());
		// TODO Auto-generated constructor stub
		
		serverLogs = new Logs(Client.SERVERLOGSPATH+serverName+".txt");//sampleData();
	}
	
	public ORB getOrb() {
		return orb;
	}

	public void setOrb(ORB orb) {
		this.orb = orb;
	}

	public String addMovieSlot (String movieID, String movieName, int movieCapacity)
	{
		//System.out.println("Entered addMovieSlot with Server Name : " + serverName);
		//return "0";
		

		try
		{
			Date currentDate = new Date();
			//SimpleDateFormat currentDateFormat = new SimpleDateFormat("ddMMyy");
			Date movieDate = new SimpleDateFormat("ddMMyy").parse(extractMovieDate(movieID));
			Calendar cal = Calendar.getInstance();
			cal.setTime(currentDate);
			cal.add(Calendar.DATE,7);
			Date afterWeek = cal.getTime();
			
			if((movieDate.after(afterWeek)))
			{
				serverResponse = movieID + " : Movie Slot cannot be booked. Movieslot Date is after 7 days";
				try
				{
					serverLogs.input = new Date() + " : bookMovieTickets ; ServerName : "+serverName+";MovieID : "+movieID+";MovieName : "+movieName+"; Server Response :("+serverResponse+")";
					serverLogs.createServerLogs(serverName);
				}
				catch(IOException e)
				{
					////e.printStackTrace();
				}
				return serverResponse;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "Date Exception";
		}
		if(!database.get(movieName).containsKey(movieID))
		{
			database.get(movieName).put(movieID, new MovieShow(movieID, movieName, movieCapacity));

			/**********Return Statements - Start**********/
			serverResponse = "Movie Slot added for "+movieID+" with MovieName : "+ movieName+ " and Capacity "+movieCapacity;
			
			try
			{
				serverLogs.input = new Date() + " : addMovieSlot ; ServerName : "+serverName+";MovieShowID : "+movieID+";MovieName : "+movieName+";MovieCapacity : "+movieCapacity+"; Server Response :("+serverResponse+")";
				serverLogs.createServerLogs(serverName);
			}
			catch(IOException e)
			{
				////e.printStackTrace();
			}
			
			return serverResponse;
			/**********Return Statements - End**********/
		}
		else
		{
			database.get(movieName).get(movieID).movieShowCapacity = movieCapacity;

			/**********Return Statements - Start**********/
			serverResponse = "Movie Capacity updated for "+movieID+" with MovieName : "+ movieName+ " and updated Capacity "+movieCapacity;
			
			try
			{
				serverLogs.input = new Date() + " : addMovieSlot ; ServerName : "+serverName+";MovieShowID : "+movieID+";MovieName : "+movieName+";MovieCapacity : "+movieCapacity+"; Server Response :("+serverResponse+")";
				serverLogs.createServerLogs(serverName);
			}
			catch(IOException e)
			{
				////e.printStackTrace();
			}
			
			return serverResponse;
			/**********Return Statements - End**********/
		}
	}
	
	public String removeMovieSlots (String movieID, String movieName)
	{
//		System.out.println("SERVER SAME : "+serverName);
//		return "0";
		

		ArrayList<CustomerBooking> customerBookingList = new ArrayList<>();
		if(database.containsKey(movieName) && database.get(movieName).containsKey(movieID))
		{
			customerBookingList = database.get(movieName).get(movieID).customerBookings;
		}
		else
		{
			/**********Return Statements - Start**********/
			serverResponse = "MovieName or MovieSlot do not exists";
			
			try
			{
				serverLogs.input = new Date() + " : removeMovieSlot ; ServerName : "+serverName+";MovieShowID : "+movieID+";MovieName : "+movieName+"; Server Response :("+serverResponse+")";
				serverLogs.createServerLogs(serverName);
			}
			catch(IOException e)
			{
				////e.printStackTrace();
			}
			
			return serverResponse;
			/**********Return Statements - End**********/
		}
		
		String slotRemoveOutput = removerSingleServerMovieSlot(movieName,movieID);
		if(slotRemoveOutput.equals("1"))
		{
			bookNextMovieSlot(movieID, movieName,customerBookingList);
						
			/**********Return Statements - Start**********/
			serverResponse = "Slot Remove and ticket booked for next possible show";
			
			try
			{
				serverLogs.input = new Date() + " : removeMovieSlot ; ServerName : "+serverName+";MovieShowID : "+movieID+";MovieName : "+movieName+"; Server Response :("+serverResponse+")";
				serverLogs.createServerLogs(serverName);
			}
			catch(IOException e)
			{
				////e.printStackTrace();
			}
			
			return serverResponse;
			/**********Return Statements - End**********/
		}
		else if(slotRemoveOutput.equals("0"))
		{			
			/**********Return Statements - Start**********/
			serverResponse = movieID + " movie slot is on past date. Cannot be removed";
			
			try
			{
				serverLogs.input = new Date() + " : removeMovieSlot ; ServerName : "+serverName+";MovieShowID : "+movieID+";MovieName : "+movieName+"; Server Response :("+serverResponse+")";
				serverLogs.createServerLogs(serverName);
			}
			catch(IOException e)
			{
				////e.printStackTrace();
			}
			
			return serverResponse;
			/**********Return Statements - End**********/
		}
		else
		{
			/**********Return Statements - Start**********/
			serverResponse = slotRemoveOutput;
			
			try
			{
				serverLogs.input = new Date() + " : removeMovieSlot ; ServerName : "+serverName+";MovieShowID : "+movieID+";MovieName : "+movieName+"; Server Response :("+serverResponse+")";
				serverLogs.createServerLogs(serverName);
			}
			catch(IOException e)
			{
				////e.printStackTrace();
			}
			
			return serverResponse;
			/**********Return Statements - End**********/
		}
	}
	
	public String listMovieShowsAvailability (String movieName)
	{
		//return "0";
		

		try
		{
			String availibilityReturningString = "";
			availibilityReturningString = movieName + ":(";
			availibilityReturningString = availibilityReturningString + singleServerAvailability(movieName).trim();

			if(serverName.equals(Client.CLIENTSERVERNAME_ATWATER))
			{
				availibilityReturningString = availibilityReturningString + receiveUDPData(Client.UDPPORTID_VERDUN, Client.METHODTYPE_LISTMOVIESHOWSAVAILABILITY,"null","null",movieName,"null","null").trim();
				availibilityReturningString = availibilityReturningString + receiveUDPData(Client.UDPPORTID_OUTREMONT, Client.METHODTYPE_LISTMOVIESHOWSAVAILABILITY,"null","null",movieName,"null","null").trim();
			}
			else if(serverName.equals(Client.CLIENTSERVERNAME_VERDUN))
			{
				availibilityReturningString = availibilityReturningString + receiveUDPData(Client.UDPPORTID_ATWATER, Client.METHODTYPE_LISTMOVIESHOWSAVAILABILITY,"null","null",movieName,"null","null").trim();
				availibilityReturningString = availibilityReturningString + receiveUDPData(Client.UDPPORTID_OUTREMONT, Client.METHODTYPE_LISTMOVIESHOWSAVAILABILITY,"null","null",movieName,"null","null").trim();
			}
			else
			{
				availibilityReturningString = availibilityReturningString + receiveUDPData(Client.UDPPORTID_VERDUN, Client.METHODTYPE_LISTMOVIESHOWSAVAILABILITY,"null","null",movieName,"null","null").trim();
				availibilityReturningString = availibilityReturningString + receiveUDPData(Client.UDPPORTID_ATWATER, Client.METHODTYPE_LISTMOVIESHOWSAVAILABILITY,"null","null",movieName,"null","null").trim();
			}
			
			availibilityReturningString = (availibilityReturningString.substring(0,availibilityReturningString.length()-1))+ ")";
			
			/**********Return Statements - Start**********/
			serverResponse = availibilityReturningString;
			
			try
			{
				serverLogs.input = new Date() + " : listMovieShowsAvailability ; ServerName : "+serverName+";MovieName : "+movieName+"; Server Response :("+serverResponse+")";
				serverLogs.createServerLogs(serverName);
			}
			catch(IOException e)
			{
				////e.printStackTrace();
			}
			
			return serverResponse;
			/**********Return Statements - End**********/
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			try
			{
				serverLogs.input = new Date() + " : listMovieShowsAvailability ; ServerName : "+serverName+";MovieName : "+movieName+"; Server Response :(OPERATION_FAILED)";
				serverLogs.createServerLogs(serverName);
			}
			catch(IOException ee)
			{
				////ee.printStackTrace();
			}
			
			return Client.OPERATION_FAIL;
		}
	}
	
	public String bookMovieTickets (String customerID,String movieID,String movieName,int numberOfTickets)
	{


		MovieShow movieShow ;
		CustomerBooking customerBooking;
		int otherServerTicketCount = 0;
		
		try
		{
			
			if(checkSameMovieSlots(movieID, movieName, customerID))
			{
				serverResponse = "Client cannot book tickets for same Movie and same Time Shows in 2 different Theaters";
				
				try
				{
					serverLogs.input = new Date() + " : bookMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
					serverLogs.createServerLogs(serverName);
				}
				catch(IOException e)
				{
					////e.printStackTrace();
				}
				
				return serverResponse.trim();
			}
			
			
			
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		
		if(!(serverName.equals(getMovieServer(movieID))))
		{			
			otherServerTicketCount = (getBookedMovieCount(customerID,serverName));
			if((numberOfTickets + otherServerTicketCount) > 3)
			{
				serverResponse = "Client cannot book more than 3 tickets in other theaters";
				
				try
				{
					serverLogs.input = new Date() + " : bookMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
					serverLogs.createServerLogs(serverName);
				}
				catch(IOException e)
				{
					////e.printStackTrace();
				}
				
				return serverResponse.trim();
			}
		}
		if(serverName.equals(getMovieServer(movieID)))
		{
			/**********Return Statements - Start**********/
			serverResponse = bookSingleServerMovie(customerID,movieID,movieName,numberOfTickets);
			return serverResponse.trim();
			/**********Return Statements - End**********/
		}
		else if (getMovieServer(movieID).equals(Client.CLIENTSERVERNAME_ATWATER))
		{
			
			/**********Return Statements - Start**********/
			serverResponse = receiveUDPData(Client.UDPPORTID_ATWATER, Client.METHODTYPE_BOOKMOVIETICKET, customerID, movieID, movieName,"null", Integer.toString(numberOfTickets));
			return serverResponse.trim();
			/**********Return Statements - End**********/
			
		}
		else if (getMovieServer(movieID).equals(Client.CLIENTSERVERNAME_VERDUN))
		{
			/**********Return Statements - Start**********/
			serverResponse = receiveUDPData(Client.UDPPORTID_VERDUN, Client.METHODTYPE_BOOKMOVIETICKET, customerID, movieID, movieName,"null", Integer.toString(numberOfTickets));
			
			return serverResponse.trim();
			/**********Return Statements - End**********/
		}
		else
		{		
			/**********Return Statements - Start**********/
			serverResponse = receiveUDPData(Client.UDPPORTID_OUTREMONT, Client.METHODTYPE_BOOKMOVIETICKET, customerID, movieID, movieName,"null", Integer.toString(numberOfTickets));
			System.out.println("OUPPUT :"+  serverResponse);
			return serverResponse.trim();
			/**********Return Statements - End**********/
		}
		//return "0";
	}
	
	public String getBookingSchedule (String customerID)
	{
		System.out.println("In side getBookingSchedule : "+customerID);
		String bookingReturnString = "";
		
		if(serverName.equals(Client.CLIENTSERVERNAME_ATWATER))
		{
			bookingReturnString = bookingReturnString + getSingleServerBookingSchedule(customerID);
			bookingReturnString = bookingReturnString + receiveUDPData(Client.UDPPORTID_VERDUN, Client.METHODTYPE_GETBOOKINGSCHEDULE, customerID, "null", "null","null","null").trim()+"\n";
			bookingReturnString = bookingReturnString + receiveUDPData(Client.UDPPORTID_OUTREMONT, Client.METHODTYPE_GETBOOKINGSCHEDULE, customerID, "null", "null","null","null").trim();
		}
		else if(serverName.equals(Client.CLIENTSERVERNAME_VERDUN))
		{
			bookingReturnString = bookingReturnString + receiveUDPData(Client.UDPPORTID_ATWATER, Client.METHODTYPE_GETBOOKINGSCHEDULE, customerID, "null", "null","null","null").trim()+"\n";
			bookingReturnString = bookingReturnString + getSingleServerBookingSchedule(customerID);
			bookingReturnString = bookingReturnString + receiveUDPData(Client.UDPPORTID_OUTREMONT, Client.METHODTYPE_GETBOOKINGSCHEDULE, customerID, "null", "null","null","null").trim();
		}
		else
		{
			bookingReturnString = bookingReturnString + receiveUDPData(Client.UDPPORTID_ATWATER, Client.METHODTYPE_GETBOOKINGSCHEDULE, customerID, "null", "null","null","null").trim()+"\n";
			bookingReturnString = bookingReturnString + receiveUDPData(Client.UDPPORTID_VERDUN, Client.METHODTYPE_GETBOOKINGSCHEDULE, customerID, "null", "null","null","null").trim()+"\n";
			bookingReturnString = bookingReturnString + getSingleServerBookingSchedule(customerID);
		}
		
		/**********Return Statements - Start**********/
		serverResponse = bookingReturnString;
		
		try
		{
			serverLogs.input = new Date() + " : getBookingSchedule ; ServerName : "+serverName+";CustomerID : "+customerID+"; Server Response :("+serverResponse+")";
			serverLogs.createServerLogs(serverName);
		}
		catch(IOException e)
		{
			////e.printStackTrace();
		}
		
		return serverResponse;
		/**********Return Statements - End**********/
		//return "0";
	}
	
	public String cancelMovieTickets (String customerID, String movieID, String movieName, int numberOfTickets)
	{System.out.println("Reached in "+serverName+" : Movie Server is :  "+(getMovieServer(movieID)+""));

		MovieShow movieShow ;
		ArrayList <CustomerBooking> customerBookingsList ;

		try
		{
			if(serverName.equals(getMovieServer(movieID)))
			{
				if(database.get(movieName).containsKey(movieID))
				{
					customerBookingsList = getClientBookingList(movieName, movieID);
					int customerFlag = 0;
					
					if(customerBookingsList.size()!=0)
					{
						for(CustomerBooking c : customerBookingsList)
						{
							if(c.customerID.equals(customerID))
							{
								customerFlag = 1;
								break;
							}
						}

						if(customerFlag == 0)
						{
							/**********Return Statements - Start**********/
							serverResponse = "No ticket available for "+customerID+ "in movieslot "+ movieID;
							
							try
							{
								serverLogs.input = new Date() + " : cancelMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
								serverLogs.createServerLogs(serverName);
							}
							catch(IOException e)
							{
								////e.printStackTrace();
							}
							
							return serverResponse.trim();
							/**********Return Statements - End**********/ 
						}
						else
						{


							for(CustomerBooking customer : customerBookingsList)
							{
								if(customer.customerID.equals(customerID))
								{
									client = customer;
									if(client.ticketCount<numberOfTickets)
									{

										/**********Return Statements - Start**********/
										serverResponse = "Tickets booked by :"+customerID + " for movieslot "+ movieID +" are less than tickets to cancel";
										
										try
										{
											serverLogs.input = new Date() + " : cancelMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
											serverLogs.createServerLogs(serverName);
										}
										catch(IOException e)
										{
											////e.printStackTrace();
										}
										
										return serverResponse.trim();
										/**********Return Statements - End**********/
									}
								}
							}
						}
					}
					else
					{
						/**********Return Statements - Start**********/
						serverResponse = "No ticket available for :"+customerID;
						
						try
						{
							serverLogs.input = new Date() + " : cancelMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
							serverLogs.createServerLogs(serverName);
						}
						catch(IOException e)
						{
							////e.printStackTrace();
						}
						
						return serverResponse.trim();
						/**********Return Statements - End**********/
					}
				}
				else
				{

					/**********Return Statements - Start**********/
					serverResponse = "Specified Movie Slot is not available";
					
					try
					{
						serverLogs.input = new Date() + " : cancelMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
						serverLogs.createServerLogs(serverName);
					}
					catch(IOException e)
					{
						////e.printStackTrace();
					}
					
					return serverResponse.trim();
					/**********Return Statements - End**********/
				}

				movieShow = database.get(movieName).get(movieID);
				movieShow.movieShowBookingCount = movieShow.movieShowBookingCount - numberOfTickets;
				client.ticketCount = client.ticketCount - numberOfTickets;

				/**********Return Statements - Start**********/
				serverResponse = (numberOfTickets  +" tickets cancelled for "+movieName+" for client "+ customerID);
				
				try
				{
					serverLogs.input = new Date() + " : cancelMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
					serverLogs.createServerLogs(serverName);
				}
				catch(IOException e)
				{
					////e.printStackTrace();
				}
				
				return serverResponse.trim();
				/**********Return Statements - End**********/

			}

			else if (getMovieServer(movieID).equals(Client.CLIENTSERVERNAME_ATWATER))
			{System.out.println("In "+serverName+" : Now calling the "+(getMovieServer(movieID)+""));
				/**********Return Statements - Start**********/
				serverResponse = receiveUDPData(Client.UDPPORTID_ATWATER, Client.METHODTYPE_CANCELMOVIE, customerID, movieID, movieName,"null", Integer.toString(numberOfTickets));
				
				return serverResponse.trim();
				/**********Return Statements - End**********/
			}

			else if (getMovieServer(movieID).equals(Client.CLIENTSERVERNAME_VERDUN))
			{System.out.println("In "+serverName+" : Now calling the "+(getMovieServer(movieID)+""));
				/**********Return Statements - Start**********/
				serverResponse = receiveUDPData(Client.UDPPORTID_VERDUN, Client.METHODTYPE_CANCELMOVIE, customerID, movieID, movieName,"null", Integer.toString(numberOfTickets));
				
				return serverResponse.trim();
				/**********Return Statements - End**********/
			}
			else
			{System.out.println("In "+serverName+" : Now calling the "+(getMovieServer(movieID)+""));
				/**********Return Statements - Start**********/
				serverResponse = receiveUDPData(Client.UDPPORTID_OUTREMONT, Client.METHODTYPE_CANCELMOVIE, customerID, movieID, movieName,"null", Integer.toString(numberOfTickets));
				
				return serverResponse.trim();
				/**********Return Statements - End**********/
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			
			try
			{
				serverLogs.input = new Date() + " : cancelMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :(OPERATION FAILED)";
				serverLogs.createServerLogs(serverName);
			}
			catch(IOException ee)
			{
				////ee.printStackTrace();
			}
			
			return Client.OPERATION_FAIL;
		}
		//return "0";
	}
	
	public String exchangeTickets(String customerID, String old_movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets)
	{
			
			try
			{
				int ticketCount = 0;
				
				Map<String,Map<String,CustomerBooking>> customerBookingList = getCustomerBooking(customerID);
				
				CustomerBooking cb = (CustomerBooking)customerBookingList.get(old_movieName).get(movieID);
				ticketCount = customerBookingList.get(old_movieName).get(movieID).ticketCount;
								
				if(ticketCount>=numberOfTickets)
				{
					serverResponse = cancelMovieTickets(customerID, movieID, old_movieName, numberOfTickets);
					
					if(serverResponse.contains("tickets cancelled for"))
					{
						String response = bookMovieTickets(customerID, new_movieID, new_movieName, numberOfTickets);

						if(serverResponse.contains(": Tickets Booked for"))
						{
							serverResponse = "Ticket Exchange Successfull";
							
							try
							{
								serverLogs.input = new Date() + " : exchangeTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";Old MovieID : "+movieID+";Old MovieName : "+old_movieName+";New MovieID : "+new_movieID+";New MovieName : "+new_movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
								serverLogs.createServerLogs(serverName);
							}
							catch(IOException e)
							{
								////e.printStackTrace();
							}
							
							return serverResponse; 
						}
						else
						{
							bookMovieTickets(customerID, movieID, old_movieName, numberOfTickets);
							
							try
							{
								serverLogs.input = new Date() + " : exchangeTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";Old MovieID : "+movieID+";Old MovieName : "+old_movieName+";New MovieID : "+new_movieID+";New MovieName : "+new_movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
								serverLogs.createServerLogs(serverName);
							}
							catch(IOException e)
							{
								////e.printStackTrace();
							}
							
							return response;
						}
					}
					else
					{
						try
						{
							serverLogs.input = new Date() + " : exchangeTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";Old MovieID : "+movieID+";Old MovieName : "+old_movieName+";New MovieID : "+new_movieID+";New MovieName : "+new_movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
							serverLogs.createServerLogs(serverName);
						}
						catch(IOException e)
						{
							////e.printStackTrace();
						}
						return serverResponse;
					}
				}
				else
				{
					serverResponse = "Number of Tickets booked are less than number of tickets to exchange";
					return serverResponse;
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			return "Ticket Exchange Unsuccessful ! Movie Name and Movie ID details you have entered do not matches any record";

	}
	
	public Map<String,Map<String,CustomerBooking>> getCustomerBooking(String customerID)
	{
		try
		{
			String allCustomerBookings = getBookingSchedule(customerID);
			String eachServerBooking[] = allCustomerBookings.split("\n");
			String eachMovie [] = new String [3];
			String eachSlot[] = new String[3];
			int ticketCount = 0;
			
			
			System.out.println("***********-----------------Entered getCustomerBooking : allCustomerBookings"+ allCustomerBookings );
			
			Map<String,Map<String,CustomerBooking>> customerBookingList = new ConcurrentHashMap<>();
			customerBookingList.put(Client.MOVIENAME_AVATAR, new ConcurrentHashMap<String,CustomerBooking>());
			customerBookingList.put(Client.MOVIENAME_AVENGERS, new ConcurrentHashMap<String,CustomerBooking>());
			customerBookingList.put(Client.MOVIENAME_TITANIC, new ConcurrentHashMap<String,CustomerBooking>());
			
			//String eachMovie [] = new String [3];
			
			eachServerBooking[0] = eachServerBooking[0].substring(eachServerBooking[0].indexOf('(')+1,eachServerBooking[0].length()-1);
    		eachServerBooking[1] = eachServerBooking[1].substring(eachServerBooking[1].indexOf('(')+1,eachServerBooking[1].length()-1);
    		eachServerBooking[2] = eachServerBooking[2].substring(eachServerBooking[2].indexOf('(')+1,eachServerBooking[2].length()-1);

	    	
	    	//int ticketCount = 0;
	    	for(int j =0;j<=2;j++)
	    	{
	    		eachMovie = eachServerBooking[j].split(";");
	    		System.out.println("eachMovie.lenght()"+eachMovie.length);
	    		System.out.println("Each Movie : eachServerBooking[j] :"+eachServerBooking[j]);
	    		for(int i = 0;i<=eachMovie.length-1;i++)
	    		{
	    			System.out.println(" each Movie i : "+ i+ ";eachmovie" +eachMovie[i] );
	    			if(eachMovie[i].trim().indexOf("()") == -1)
	    			{
	    				System.out.println("()()()()()()()()()()()()()()()()()()");
	    				eachMovie[i] = eachMovie[i].substring(eachMovie[i].indexOf("(")+1,eachMovie[i].length()-1);
	    				System.out.println("***---eachMovie[i] :"+ eachMovie[i]);
	    				//eachMovie = eachMovie[i].split(",");
	    				eachSlot = eachMovie[i].split(",");
	    				for(String slot : eachSlot)
	    				{
	    					System.out.println("***---SLOT :"+ slot);
	    					//ticketCount = ticketCount + Integer.parseInt(slot.substring(slot.indexOf('>')+1));
	    					if(i==0)
	    					{
	    						System.out.println("Entered 0");
	    						CustomerBooking customerBooking = new CustomerBooking();
	    						customerBooking.bookedMovieName = Client.MOVIENAME_AVATAR;
	    						System.out.println("customerBooking.bookedMovieName : "+customerBooking.bookedMovieName);
	    						customerBooking.bookedMovieID = slot.substring(0,10);
	    						System.out.println("customerBooking.bookedMovieID : "+customerBooking.bookedMovieID);
	    						customerBooking.customerID = customerID;
	    						System.out.println("customerBooking.customerID : "+customerBooking.customerID);
	    						customerBooking.ticketCount = Integer.parseInt(slot.substring(12,slot.length()));
	    						System.out.println("customerBooking.ticketCount : "+customerBooking.ticketCount);
	    						customerBookingList.get(Client.MOVIENAME_AVATAR).put(customerBooking.bookedMovieID,customerBooking);
	    						//System.out.println("Inside : -----------------:"+customerBookingList.get(Client.MOVIENAME_AVATAR).get(customerBooking.bookedMovieName).ticketCount);
	    					}
	    					else if(i==1)
	    					{
	    						System.out.println("Entered 1");
	    						CustomerBooking customerBooking = new CustomerBooking();
	    						customerBooking.bookedMovieName = Client.MOVIENAME_AVENGERS;
	    						customerBooking.bookedMovieID = slot.substring(0,10);
	    						customerBooking.customerID = customerID;
	    						customerBooking.ticketCount = Integer.parseInt(slot.substring(12,slot.length()));
	    						customerBookingList.get(Client.MOVIENAME_AVENGERS).put(customerBooking.bookedMovieID,customerBooking);
	    					}
	    					else
	    					{
	    						System.out.println("Entered 2");
	    						CustomerBooking customerBooking = new CustomerBooking();
	    						customerBooking.bookedMovieName = Client.MOVIENAME_TITANIC;
	    						customerBooking.bookedMovieID = slot.substring(0,10);
	    						customerBooking.customerID = customerID;
	    						customerBooking.ticketCount = Integer.parseInt(slot.substring(12,slot.length()));
	    						customerBookingList.get(Client.MOVIENAME_TITANIC).put(customerBooking.bookedMovieID,customerBooking);
	    					}
	    				}
	    			}
	    		}
	    	}
	    	System.out.println("Database : " + customerBookingList);
	    	//return ticketCount;
			
			//for(int i = 0 ;i<=2; i++)
//			{
//				String serverBooking = eachServerBooking[i];
//				System.out.println("***********-----------------Entered serverBooking : serverBooking"+ serverBooking );
//
//				serverBooking = serverBooking.substring(serverBooking.indexOf('(')+1,serverBooking.length()-1);
//				eachMovie = serverBooking.split(";");
//				
//				eachMovie[0] = eachMovie[0].substring(eachMovie[0].indexOf("(")+1, eachMovie[0].indexOf(")"));
//				eachMovie[1] = eachMovie[1].substring(eachMovie[1].indexOf("(")+1, eachMovie[1].indexOf(")"));
//				eachMovie[2] = eachMovie[2].substring(eachMovie[2].indexOf("(")+1, eachMovie[2].indexOf(")"));
//				for (String slot : eachMovie)
//				{
//					
//					System.out.println("***********-----------------Entered slot : slot"+ slot );
//
//					if(i==0)
//					{
//						customerBooking.bookedMovieName = Client.MOVIENAME_AVATAR;
//						customerBooking.bookedMovieID = slot.substring(0,9);
//						customerBooking.customerID = customerID;
//						customerBooking.ticketCount = Integer.parseInt(slot.substring(12,slot.length()));
//						customerBookingList.get(Client.MOVIENAME_AVATAR).put(customerBooking.bookedMovieID,customerBooking);
//					}
//					else if(i==1)
//					{
//						customerBooking.bookedMovieName = Client.MOVIENAME_AVENGERS;
//						customerBooking.bookedMovieID = slot.substring(0,9);
//						customerBooking.customerID = customerID;
//						customerBooking.ticketCount = Integer.parseInt(slot.substring(12,slot.length()));
//						customerBookingList.get(Client.MOVIENAME_AVENGERS).put(customerBooking.bookedMovieID,customerBooking);
//					}
//					else
//					{
//						customerBooking.bookedMovieName = Client.MOVIENAME_TITANIC;
//						customerBooking.bookedMovieID = slot.substring(0,9);
//						customerBooking.customerID = customerID;
//						customerBooking.ticketCount = Integer.parseInt(slot.substring(12,slot.length()));
//						customerBookingList.get(Client.MOVIENAME_TITANIC).put(customerBooking.bookedMovieID,customerBooking);
//					}
//					
//				}
//			}
			
			return customerBookingList;
		}
		catch(Exception e)
		{
			System.out.println("@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@Eception occured in getCustomer@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@");
			e.printStackTrace();
			return new ConcurrentHashMap<>();
		}
	}

	public void shutdown()
	{
		orb.shutdown(false);
	}
	
	public String getMovieServer(String movieID)
	{		
		if(movieID.substring(0,3).equals(Client.CLIENTSERVER_ATWATER))
		{
			return Client.CLIENTSERVERNAME_ATWATER;
		}
		else if(movieID.substring(0,3).equals(Client.CLIENTSERVER_VERDUN))
		{
			return Client.CLIENTSERVERNAME_VERDUN;
		}
		else
		{
			return Client.CLIENTSERVERNAME_OUTREMONT;
		}
	}
	
	public String getMovieName(String movieID)
	{
		String avatarShows = listMovieShowsAvailability("AVATAR");
		String avengersShows = listMovieShowsAvailability("AVENGERS");
		String titanicShows = listMovieShowsAvailability("TITANIC");
		
		if(avatarShows.contains(movieID))
		{
			return Client.MOVIENAME_AVATAR;
		}
		else if(avengersShows.contains(movieID))
		{
			return Client.MOVIENAME_AVENGERS;
		}
		else if (titanicShows.contains(movieID))
		{
			return Client.MOVIENAME_TITANIC;
		}
		else
		{
			return Client.OPERATION_FAIL;
			//return "Invalid Movie Slot ! Movie Slot is not present";
		}
	}
	
    public int getSingleServerBookedMovieCount(String input)
    {
       	String eachMovie [] = new String [3];
    	eachMovie = input.split(";");
    	int ticketCount = 0;
    	
    	for(int i = 0;i<=2;i++)
    	{
    		if(eachMovie[i].trim().indexOf("()") == -1)
    		{
    			eachMovie[i] = eachMovie[i].substring(9,eachMovie[i].length()-1);//BugFound
				String eachSlotAvtar [] = eachMovie[i].split(",");
				for(String slot : eachSlotAvtar)
				{
					ticketCount = ticketCount + Integer.parseInt(slot.substring(slot.indexOf('>')+1));
				}
    		}
    	}
    	return ticketCount;
    }
    
    public String getClientServer()
	{
		return this.serverName;
	}
	
	public int getShowVacancy(String movieName, String movieID)
	{
		int capacity = database.get(movieName).get(movieID).movieShowCapacity;
		int bookingCount = database.get(movieName).get(movieID).movieShowBookingCount;
		int vacancy = capacity - bookingCount;
		return vacancy;
	}
	
	public boolean isClientPresent(String movieName, String movieID,String customerID)
	{
		ArrayList <CustomerBooking> clientList = database.get(movieName).get(movieID).customerBookings;
		
		for(CustomerBooking client : clientList)
		{
			if(client.customerID.equals(customerID))
			{
				this.client = client;
				return true;
			}
		}
		return false;
	}
	
	public String receiveUDPData(int UDPPortID,int methodType, String clientID, String movieID, String movieName, String movieCapacity, String noOfTickets)
	{
		String message = UDPPortID +","+ methodType +","+ clientID +","+ movieID +","+ movieName +","+ movieCapacity +","+ noOfTickets+",";
		DatagramSocket socket = null;
		
		try
		{
			socket = new DatagramSocket();
			byte [] input = message.getBytes();
			InetAddress hostname = InetAddress.getByName("localhost");
			DatagramPacket request = new DatagramPacket(input, input.length, hostname, Client.UDPSERVERPORT);
			System.out.println("*****Just before .send(request) in receiveUDPData Number of Tickets :"+noOfTickets);
			socket.send(request);
			
			byte [] output = new byte[1000];
			DatagramPacket reply = new DatagramPacket(output, output.length);
			socket.receive(reply);
			return new String(reply.getData());
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return Client.OPERATION_FAIL;
		}
		finally 
		{
			if(socket != null)
			{
				socket.close();
			}
		}
	}
	
	public String singleServerAvailability (String movieName) throws RemoteException
	{
		
		String availibilityReturningString = "";
		try
		{
			Map<String, MovieShow> availabilityList = database.get(movieName);
			if(availabilityList.size() != 0)
			{
				for(MovieShow movieShow :availabilityList.values())
				{
					availibilityReturningString = availibilityReturningString + movieShow.movieID + " - " + (movieShow.movieShowCapacity-movieShow.movieShowBookingCount) + "," ;
				}
			}
			return availibilityReturningString.trim();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return Client.OPERATION_FAIL;
		}
		
	}
	
	public Boolean verifyFutureDate(String movieID)
	{
		try
		{
			Date currentDate = new Date();
			SimpleDateFormat currentDateFormat = new SimpleDateFormat("ddMMyy");
			Date movieDate = new SimpleDateFormat("ddMMyy").parse(extractMovieDate(movieID));

			if((currentDate.before(movieDate)))
			{
				return true;
			}
			else
			{
				return false;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		
	}
	
	public String extractMovieDate(String movieID)
	{
		return movieID.substring(4);
	}
	
	
	public String removerSingleServerMovieSlot(String movieName, String movieID)
	{
		
		if(database.get(movieName).containsKey(movieID))
		{
			if(verifyFutureDate(movieID))
			{
				database.get(movieName).remove(movieID);
				return Client.OPERATION_SUCCESS;
			}
			else
			{
				return Client.OPERATION_FAIL;
			}
		}
		else
		{
			return "Movieslot " + movieID + " is not present";
		}
		
	}
	
	public void bookNextMovieSlot(String movieID, String movieName, ArrayList<CustomerBooking> customerBookingList)
	{
		try
		{
			for (CustomerBooking cusotmerBooking:customerBookingList)
			{
				String nextMovieID = getNextMovieSlot(movieID, movieName,cusotmerBooking.ticketCount);
				if(!(nextMovieID.equals("0")))
				{
					bookMovieTickets(cusotmerBooking.customerID, nextMovieID, movieName, cusotmerBooking.ticketCount);
				}
				else
				{

				}
			}

		}
		catch(Exception e)
		{
			e.printStackTrace();
			//return Client.OPERATION_FAIL;
		}
				
	}
	
	public String getNextMovieSlot(String movieID, String movieName,int ticketCount)
	{	
		try
		{
			String availability = this.listMovieShowsAvailability(movieName);
			availability = availability.substring((availability.indexOf(':')+1));
			availability = availability.substring(1,availability.length()-1);
			String[] listAvailability = availability.split(",");
			for(String shows:listAvailability)
			{
				String[] showDetails = shows.split("-");
				if((Integer.parseInt(showDetails[1].trim()) > ticketCount)&&(verifyFutureDate(showDetails[0].trim())))
				{
					return showDetails[0].trim();
				}
				
			}
		return "0";
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return "0";
		}
		
		
	}
	
	public String bookSingleServerMovie(String customerID,String movieID,String movieName,int numberOfTickets)
	{
		MovieShow movieShow ;
		CustomerBooking customerBooking;
		System.out.println("Number of Tickets : "+numberOfTickets);
		
		if(database.get(movieName).containsKey(movieID.trim()))
		{
			if(getShowVacancy(movieName, movieID)>=numberOfTickets)
			{
				movieShow = database.get(movieName).get(movieID);
				movieShow.movieShowBookingCount = movieShow.movieShowBookingCount + numberOfTickets;
				
				if(this.isClientPresent(movieName,movieID,customerID))
				{
					client.ticketCount = client.ticketCount + numberOfTickets;
					
					serverResponse = numberOfTickets  +" : Tickets Booked for "+movieName ;
					
					try
					{
						serverLogs.input = new Date() + " : bookMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
						serverLogs.createServerLogs(serverName);
					}
					catch(IOException e)
					{
						////e.printStackTrace();
					}
					
					return serverResponse;
				}
				else
				{
					
					customerBooking = new CustomerBooking(customerID, movieName, movieID, numberOfTickets);
					database.get(movieName).get(movieID).customerBookings.add(customerBooking);
					
					serverResponse = numberOfTickets  +" : Tickets Booked for "+movieName ;
				
					try
					{
						serverLogs.input = new Date() + " : bookMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
						serverLogs.createServerLogs(serverName);
					}
					catch(IOException e)
					{
						////e.printStackTrace();
					}
					return serverResponse;
					
				}
			}
			else
			{
				
				serverResponse = "Insufficient tickets available for your booking";
				try
				{
					serverLogs.input = new Date() + " : bookMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
					serverLogs.createServerLogs(serverName);
				}
				catch(IOException e)
				{
					////e.printStackTrace();
				}
				return serverResponse;
			}
		}
		else
		{
			
			serverResponse = "Specified movie is not available";
			
			try
			{
				serverLogs.input = new Date() + " : bookMovieTickets ; ServerName : "+serverName+";CustomerID : "+customerID+";MovieID : "+movieID+";MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+serverResponse+")";
				serverLogs.createServerLogs(serverName);
			}
			catch(IOException e)
			{
				////e.printStackTrace();
			}
			
			////return "Specified movie is not available";
			return serverResponse;
		}
	}
	
	public String getSingleServerBookingSchedule(String customerID)
	{
		String bookingReturnString = serverName + ":( ";
		String avtarBooking = "AVATAR:(";
		String avengersBooking = "AVENGERS:(";
		String titanicBooking = "TITANIC:(";
		int flag =0;
		Map<String, MovieShow> avtarShowList = database.get("AVATAR");
		
		if(avtarShowList.size() != 0)
		{
			for(MovieShow movieShow :avtarShowList.values())
			{
				ArrayList <CustomerBooking> clientList = movieShow.customerBookings;
				System.out.println("SIZE : "+clientList.size());
				for(CustomerBooking client : clientList)
				{
//					if(client == null)
//					{
//						System.out.println("Client : null");
//					}
//					else
					{
					System.out.println("Client1 : "+ client.customerID);
					}
					System.out.println("customerID : "+ customerID);

					if(client.customerID.equals(customerID))
					{
						flag =1 ;
						avtarBooking = avtarBooking + movieShow.movieID + "->" + client.ticketCount + ",";
					}
				}
			}
			
			if(flag ==1)
			{
			avtarBooking = avtarBooking.substring(0,avtarBooking.length()-1) + ");";
			}
			else
			{
				avtarBooking = avtarBooking + ");";
			}
		}
		else
		{
			avtarBooking = avtarBooking + ");";
		}
		
		flag = 0;
		Map<String, MovieShow> avengersShowList = database.get("AVENGERS");
		if(avengersShowList.size() != 0)
		{
			for(MovieShow movieShow :avengersShowList.values())
			{
				ArrayList <CustomerBooking> clientList = movieShow.customerBookings;
				
				for(CustomerBooking client : clientList)
				{
					if(client.customerID.equals(customerID))
					{
						flag = 1;
						avengersBooking = avengersBooking + movieShow.movieID + "->" + client.ticketCount + ",";
					}
				}
			}
			if(flag ==1)
			{
			avengersBooking = avengersBooking.substring(0,avengersBooking.length()-1) + ");";
			}
			else
			{
				avengersBooking = avengersBooking + ");";
			}
		}
		else
		{
			avengersBooking = avengersBooking + ");";
		}
		
		flag =0;
		Map<String, MovieShow> titanicShowList = database.get("TITANIC");
		if(titanicShowList.size() != 0)
		{
			for(MovieShow movieShow :titanicShowList.values())
			{
				ArrayList <CustomerBooking> clientList = movieShow.customerBookings;
				
				for(CustomerBooking client : clientList)
				{
					if(client.customerID.equals(customerID))
					{
						flag =1;
						titanicBooking = titanicBooking + movieShow.movieID + "->" + client.ticketCount + ",";
					}
				}
			}
			
			if(flag ==1)
			{
			titanicBooking = titanicBooking.substring(0,titanicBooking.length()-1) + "))";
			}
			else
			{
				titanicBooking = titanicBooking + "))";
			}
		
		}
		else
		{
			titanicBooking = titanicBooking + "))";
		}
		bookingReturnString = bookingReturnString + avtarBooking+avengersBooking+titanicBooking+"\n";
		
		return bookingReturnString;
	}
	
	
	public ArrayList getClientBookingList(String movieName,String movieID)
	{
		ArrayList<CustomerBooking> customerBooking = new ArrayList<>();
		customerBooking = database.get(movieName).get(movieID).customerBookings;

		return customerBooking;
		
	}
	
	
	public void sampleData() throws RemoteException
	{
		if(serverName.equals(Client.CLIENTSERVERNAME_ATWATER))
		{
			addMovieSlot("ATWM120223", "AVATAR", 100);
			addMovieSlot("ATWA120223", "AVENGERS", 50);
			addMovieSlot("ATWA120223", "TITANIC", 50);
		}
		if(serverName.equals(Client.CLIENTSERVERNAME_VERDUN))
		{
			addMovieSlot("VERA120223", "TITANIC", 50);
			addMovieSlot("VERE120223", "AVENGERS", 75);
			addMovieSlot("VERE120223", "AVATAR", 75);
		}
		if(serverName.equals(Client.CLIENTSERVERNAME_OUTREMONT))
		{
			addMovieSlot("OUTM120223", "TITANIC", 200);
			addMovieSlot("OUTE120223", "AVENGERS", 200);
			addMovieSlot("OUTE120223", "AVATAR", 200);
		}
		
	}
	
    public void getcurrentDB()
    {
    ////faltu
			for(MovieShow show : database.get("AVATAR").values())
			{
				ArrayList <CustomerBooking> book = show.customerBookings;
				for(CustomerBooking booking : book)
				{
					System.out.println(show.movieName +" : "+show.movieID+" : "+booking.customerID+ " : "+booking.ticketCount);
				}
			}
			System.out.println("-------------------------");
			for(MovieShow show : database.get("AVENGERS").values())
			{
				ArrayList <CustomerBooking> book = show.customerBookings;
				for(CustomerBooking booking : book)
				{
					System.out.println(show.movieName +" : "+show.movieID+" : "+booking.customerID+ " : "+booking.ticketCount);
				}
			}
			System.out.println("-------------------------");
			System.out.println("-------------------------");
			for(MovieShow show : database.get("TITANIC").values())
			{
				ArrayList <CustomerBooking> book = show.customerBookings;
				for(CustomerBooking booking : book)
				{
					System.out.println(show.movieName +" : "+show.movieID+" : "+booking.customerID+ " : "+booking.ticketCount);
				}
			}
			System.out.println("-------------------------");
			////faltu
    }
    
    public String getMovieTimeSlot(String movieID)
    {
    	return movieID.charAt(3)+"";
    }
    
    public int getBookedMovieCount(String customerID,String server)
    {
    	try
    	{
    		String allCustomerBookings = getBookingSchedule(customerID);
    		String eachServerBooking[] = allCustomerBookings.split("\n");
    		String eachMovie [] = new String [3];
    		int atwaterTicketCount = 0;
    		int verdunTicketCount = 0;
    		int outremontTicketCount = 0;
    		
    		eachServerBooking[0] = eachServerBooking[0].substring(eachServerBooking[0].indexOf('(')+1,eachServerBooking[0].length()-1);
    		eachServerBooking[1] = eachServerBooking[1].substring(eachServerBooking[1].indexOf('(')+1,eachServerBooking[1].length()-1);
    		eachServerBooking[2] = eachServerBooking[2].substring(eachServerBooking[2].indexOf('(')+1,eachServerBooking[2].length()-1);

//    		System.out.println("eachServerBooking[0] : "+eachServerBooking[0]);
//    		System.out.println("eachServerBooking[1] : "+eachServerBooking[1]);
//    		System.out.println("eachServerBooking[2] : "+eachServerBooking[2]);
    		
    		if(serverName.equals(Client.CLIENTSERVERNAME_ATWATER))
    		{
    			//System.out.println("-------IN : "+1);
    			verdunTicketCount = getSingleServerBookedMovieCount(eachServerBooking[1]);
    			outremontTicketCount = getSingleServerBookedMovieCount(eachServerBooking[2]);
    		}
    		else if(serverName.equals(Client.CLIENTSERVERNAME_VERDUN))
    		{
    			//System.out.println("-------IN : "+2);
    			atwaterTicketCount = getSingleServerBookedMovieCount(eachServerBooking[0]);
    			outremontTicketCount = getSingleServerBookedMovieCount(eachServerBooking[2]);
    		}
    		else if(serverName.equals(Client.CLIENTSERVERNAME_OUTREMONT))
    		{
    			//System.out.println("-------IN : "+3);
    			atwaterTicketCount = getSingleServerBookedMovieCount(eachServerBooking[0]);
    			verdunTicketCount = getSingleServerBookedMovieCount(eachServerBooking[1]);
    		}
        	else
        	{
        		//System.out.println("-------IN : "+4);
        		atwaterTicketCount = getSingleServerBookedMovieCount(eachServerBooking[0]);
    			verdunTicketCount = getSingleServerBookedMovieCount(eachServerBooking[1]);
    			outremontTicketCount = getSingleServerBookedMovieCount(eachServerBooking[2]);
        	}
    		
//    		System.out.println("atwaterTicketCount : "+ atwaterTicketCount);
//    		System.out.println("verdunTicketCount : "+ verdunTicketCount);
//    		System.out.println("outremontTicketCount : "+ outremontTicketCount);
    		
    		return atwaterTicketCount + verdunTicketCount + outremontTicketCount;
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	    return 0;
    	}
    }
    
    public boolean checkSameMovieSlots(String movieID, String movieName, String customerID)
    {
    	try
    	{
    		String allCustomerBookings = getBookingSchedule(customerID);
    		String eachServerBooking[] = allCustomerBookings.split("\n");
    		String eachMovie [] = new String [3];
    		int atwaterTicketCount = 0;
    		int verdunTicketCount = 0;
    		int outremontTicketCount = 0;

    		eachServerBooking[0] = eachServerBooking[0].substring(eachServerBooking[0].indexOf('(')+1,eachServerBooking[0].length()-1);
    		eachServerBooking[1] = eachServerBooking[1].substring(eachServerBooking[1].indexOf('(')+1,eachServerBooking[1].length()-1);
    		eachServerBooking[2] = eachServerBooking[2].substring(eachServerBooking[2].indexOf('(')+1,eachServerBooking[2].length()-1);

    		if(getMovieServer(movieID).equals(Client.CLIENTSERVERNAME_ATWATER))
    		{
    			verdunTicketCount = checkSingleServerSameMovieSlots(eachServerBooking[1],movieName, movieID);    			
    			System.out.println("--------verdunTicketCount--------:"+ verdunTicketCount);
    			if(verdunTicketCount > 0)
    			{
    				return true;
    			}
    			outremontTicketCount = checkSingleServerSameMovieSlots(eachServerBooking[2],movieName, movieID);
    			System.out.println("--------outremontTicketCount--------:"+ outremontTicketCount);
    			if(outremontTicketCount > 0)
    			{
    				return true;
    			}
    		}
    		else if(getMovieServer(movieID).equals(Client.CLIENTSERVERNAME_VERDUN))
    		{
    			atwaterTicketCount = checkSingleServerSameMovieSlots(eachServerBooking[0],movieName, movieID);
    			System.out.println("--------eachServerBooking[0]--------:"+ eachServerBooking[0]);
    			System.out.println("--------atwaterTicketCount--------:"+ atwaterTicketCount);
    			if(atwaterTicketCount > 0)
    			{
    				return true;
    			}
    			outremontTicketCount = checkSingleServerSameMovieSlots(eachServerBooking[2],movieName, movieID);
    			System.out.println("--------outremontTicketCount--------:"+ outremontTicketCount);
    			if(outremontTicketCount > 0)
    			{
    				return true;
    			}
    		}
    		else if(getMovieServer(movieID).equals(Client.CLIENTSERVERNAME_OUTREMONT))
    		{
    			atwaterTicketCount = checkSingleServerSameMovieSlots(eachServerBooking[0],movieName, movieID);
    			System.out.println("--------atwaterTicketCount--------:"+ atwaterTicketCount);
    			if(atwaterTicketCount > 0)
    			{
    				return true;
    			}
    			verdunTicketCount = checkSingleServerSameMovieSlots(eachServerBooking[1],movieName, movieID);
    			System.out.println("--------verdunTicketCount--------:"+ verdunTicketCount);
    			if(verdunTicketCount > 0)
    			{
    				return true;
    			}
    		}
    		
    		return false;

    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		return false;
    	}
    }
    
    public int checkSingleServerSameMovieSlots(String input, String movieName , String movieID)
    {
       	String eachMovie [] = new String [3];
    	eachMovie = input.split(";");
    	int ticketCount = 0;
    	int i;
    	
    	if(movieName.equals("AVATAR"))
		{
			i = 0;
		}
		else if(movieName.equals("AVENGERS"))
		{
			i =1;
		}
		else
		{
			i = 2;
		}
    	
    	
		if(eachMovie[i].trim().indexOf("()") == -1)
		{
			System.out.println("Inside () : "+ eachMovie[i]);
			eachMovie[i] = eachMovie[i].substring(eachMovie[i].indexOf("(")+1,eachMovie[i].length()-1);
			System.out.println("Inside () : "+ eachMovie[i]);
			String eachSlotAvtar [] = eachMovie[i].split(",");
			for(String slot : eachSlotAvtar)
			{System.out.println("slot  : "+ slot);
				if(getMovieTimeSlot(movieID).equals(getMovieTimeSlot(slot.substring(0,slot.indexOf('>')-1))))
				{
					ticketCount = ticketCount + Integer.parseInt(slot.substring(slot.indexOf('>')+1));
					if(ticketCount >0)
					{
						return 1;
					}
				}
			}
		}
		return 0;

    }



}
