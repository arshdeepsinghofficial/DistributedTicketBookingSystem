package Client;

import java.io.IOException;
import java.net.URL;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Date;
import java.util.Scanner;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

//import Server.*;
//import logs.Logs;
import frontend.TicketBookingInterface;
//import TicketBookingInterfaceApp.TicketBookingInterface;
import Client.Client;
//import logs.Logs;

public class Client 
{
	public static final String CLIENTTYPE_CUSTOMER = "C"; 
	public static final String CLIENTTYPE_ADMIN = "A"; 
	public static final String CLIENTSERVER_ATWATER = "ATW"; 
	public static final String CLIENTSERVER_VERDUN = "VER"; 
	public static final String CLIENTSERVER_OUTREMONT = "OUT"; 
	public static final String CLIENTSERVERNAME_ATWATER = "ATWATER"; 
	public static final String CLIENTSERVERNAME_VERDUN = "VERDUN"; 
	public static final String CLIENTSERVERNAME_OUTREMONT = "OUTREMONT";
	public static final int SERVERPORT_ATWATER = 2345;
	public static final int SERVERPORT_VERDUN = 2346;
	public static final int SERVERPORT_OUTREMONT = 2347;
	public static final String OPERATION_SUCCESS = "1";
	public static final String OPERATION_FAIL = "0";
	public static final String MOVIENAME_AVATAR = "AVATAR";
	public static final String MOVIENAME_AVENGERS = "AVENGERS";
	public static final String MOVIENAME_TITANIC = "TITANIC";
	public static final int UDPSERVERPORT = 2348;
	public static final int UDPPORTID_ATWATER = 4444;
	public static final int UDPPORTID_VERDUN = 5555;
	public static final int UDPPORTID_OUTREMONT = 6666;
	public static final int METHODTYPE_ADDMOVIESLOT = 1;
	public static final int METHODTYPE_REMOVEMOVIESLOT = 2;
	public static final int METHODTYPE_LISTMOVIESHOWSAVAILABILITY = 3;
	public static final int METHODTYPE_BOOKMOVIETICKET = 4;
	public static final int METHODTYPE_GETBOOKINGSCHEDULE = 5;
	public static final int METHODTYPE_CANCELMOVIE = 6;
	public static final int CLIENTBOOKINGLIST = 7;
	public static final String SERVERLOGSPATH = "E:\\Logs\\Server\\";
	public static final String CLIENTLOGSPATH = "E:\\Logs\\Client\\";
	
	private String clientID;
	private String clientType;
	private String clientServer;
	private int clientServerPort;
	
	//static public Logs clientLogs;
	
	public static String args[];
	
	//static public Logs clientLogs;
	
	public Client(String clientID)throws IOException
	{
		this.clientID = clientID.toUpperCase();
		this.clientType = getClientType(clientID);
		this.clientServer = getClientServer(clientID);
		this.clientServerPort = getClientServerPort(clientID);
		
		/////clientLogs = new Logs(CLIENTLOGSPATH+clientID+".txt");
	}
	
	public static void main(String args[])
	{
		Client.args = args;
		login();
	}
	
	public static void login()
	{
		Scanner input = new Scanner(System.in);
		String clientID = "";
		
		System.out.println("Please Enter your ID : ");
		try
		{
			clientID = input.nextLine().toUpperCase();
			standardiseClientID(clientID);
			validateClient(clientID);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}
	
	public static String standardiseClientID(String clientID)
	{
		return clientID.trim().toUpperCase();
	}
	
	public static void validateClient(String clientID) throws Exception
	{
		String clientServer = "";
		String clientType = "";
		if(clientID.length() == 8)
		{
			clientServer = getClientServer(clientID);
			clientType = getClientType(clientID);
		}
		else
		{
			System.out.println("client ID is not in proper format");
		    login();
		}
		
		if(clientType.equals(CLIENTTYPE_CUSTOMER))
		{
			if(clientServer.equals(CLIENTSERVER_ATWATER)||clientServer.equals(CLIENTSERVER_VERDUN)||clientServer.equals(CLIENTSERVER_OUTREMONT)) 
			{
				System.out.println("*-----------Login Successfull. Welcome "+ clientID +"----------*");
//				clientLogs = new Logs(CLIENTLOGSPATH+clientID+".txt");
//				clientLogs.input = new Date() + " ; Login - Success ; ClientName : "+ clientID +"; ServerName : "+clientServer;
//				clientLogs.createClientLogs(clientID);
				new Client(clientID).customer();
			}
		}
		else if (clientType.equals(CLIENTTYPE_ADMIN))
		{
			if(clientServer.equals(CLIENTSERVER_ATWATER)||clientServer.equals(CLIENTSERVER_VERDUN)||clientServer.equals(CLIENTSERVER_OUTREMONT)) 
			{
				System.out.println("*-----------Login Successfull. Welcome "+ clientID +"----------*");
//				clientLogs = new Logs(CLIENTLOGSPATH+clientID+".txt");
//				clientLogs.input = new Date() + " ; Login - Success ; ClientName : "+ clientID +"; ServerName : "+clientServer;
//				clientLogs.createClientLogs(clientID);
				new Client(clientID).admin();
			}
		}
		else 
			System.out.println("client ID is either wrong or not in proper format");
		    login();
	}
	
	public static String getClientServer(String clientID)
	{
		return clientID.substring(0, 3);
	}
	
	public static String getClientType(String clientID)
	{
		return clientID.substring(3,4);
	}
	
	public static int getClientServerPort(String clientID)
	{
		if(getClientServer(clientID).equals(CLIENTSERVER_ATWATER))
		{
			return SERVERPORT_ATWATER;
		}
		else if(getClientServer(clientID).equals(CLIENTSERVER_VERDUN))
		{
			return SERVERPORT_VERDUN;
		}
		else
		{
			return SERVERPORT_OUTREMONT;
		}
	}
	
	public void admin()
	{
		try
		{
			System.out.println("REACHED ADMIN");
			URL url = new URL("http://localhost:8080/TicketBooking?wsdl");
			QName qName = new QName("http://frontend/","TicketBookingService");
			Service service = Service.create(url,qName);
			TicketBookingInterface remoteAdmin = service.getPort(TicketBookingInterface.class);//addMovieData(remoteAdmin);

			String choice ;
			String movieShowID;
			String movieName;
			int movieCapacity;
			int numberOfTickets;
			String operationStatus = "";
			String showAvailibility = "";
			String operationMessage;
			do
			{
				choice = this.adminMenu();
				System.out.println("Choice : "+ choice);
				
				switch(choice)
				{
					case "1":
					{
						movieShowID = this.getMovieID();
						movieName = this.getMovieName();
						movieCapacity = this.getMovieCapacity();
						
						System.out.println("cHECK THE Msgs...!!!" + movieShowID + " <- movieShowID \n" + "moviename-->>" + movieName + "\n capacity--??" + movieCapacity);
						operationMessage = remoteAdmin.addMovieSlot(clientID,movieShowID, movieName, movieCapacity);
						System.out.println(operationMessage);
						
//						clientLogs.input = new Date() + " : addMovieSlot ; ClientName : "+ clientID +"; ServerName : "+clientServer+";MovieShowID : "+movieShowID+";MovieName : "+movieName+";Capacity : "+movieCapacity+"; Server Response :"+operationMessage;
//						clientLogs.createClientLogs(clientID);
						
						break;
					}
					case "2":
					{
						movieShowID = this.getMovieID();
						movieName = this.getMovieName();
						operationMessage = remoteAdmin.removeMovieSlots(clientID,movieShowID, movieName);

						System.out.println(operationMessage);
//						clientLogs.input = new Date() + " : removeMovieSlot ; ClientName : "+ clientID +"; ServerName : "+clientServer+";MovieShowID : "+movieShowID+";MovieName : "+movieName+"; Server Response :("+operationMessage+")";
//						clientLogs.createClientLogs(clientID);
						break;
					}
					case "3":
					{
						try {
						movieName = this.getMovieName();
						operationMessage = remoteAdmin.listMovieShowsAvailability(clientID,movieName);
						
//						clientLogs.input = new Date() + " : listMovieShowsAvailability ; ClientName : "+ clientID +"; ServerName : "+clientServer+";MovieName : "+movieName+"; Server Response :"+operationMessage;
//						clientLogs.createClientLogs(clientID);
						
						System.out.println(operationMessage);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						break;
					}
					case "4":
					{
						try
						{
							movieShowID = this.getMovieID();
							movieName = this.getMovieName();
							numberOfTickets = this.getNumberOfTickets();
							operationMessage = remoteAdmin.bookMovieTickets(clientID,movieShowID,movieName,numberOfTickets);
							
							System.out.println(operationMessage);
							
//							clientLogs.input = new Date() + " : bookMovieTickets ; ClientName : "+ clientID +"; ServerName : "+clientServer+";MovieShowID : "+movieShowID+";MovieName : "+movieName+";Number Of Tickets : "+numberOfTickets+"; Server Response :"+operationMessage;
//							clientLogs.createClientLogs(clientID);
							
							break;
						}
						catch(Exception e)
						{
							System.out.println("Tickets Booked");
							//clientLogs.input = new Date() + " : bookMovieTickets ; ClientName : "+ clientID +"; ServerName : "+clientServer+";MovieShowID : "+movieShowID+";MovieName : "+movieName+";Number Of Tickets : "+numberOfTickets+"; Server Response :"+operationMessage;
//							clientLogs.input = "Tickets Booked";
//							clientLogs.createClientLogs(clientID);
							e.printStackTrace();
						}
						break;
					}
					case "5":
					{
						try
						{
							operationMessage = remoteAdmin.getBookingSchedule(clientID);
							System.out.println(operationMessage);
							
//							clientLogs.input = new Date() + " : getBookingSchedule ; ClientName : "+ clientID +"; ServerName : "+clientServer+"; Server Response :"+operationMessage;
//							clientLogs.createClientLogs(clientID);
							
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						break;
					}
					case "6":
					{
						try
						{
							movieShowID = this.getMovieID();
							movieName = this.getMovieName();
							numberOfTickets = this.getNumberOfTickets();
							operationMessage = remoteAdmin.cancelMovieTickets(clientID,movieShowID,movieName,numberOfTickets);
							
							System.out.println(operationMessage);
							
//							clientLogs.input = new Date() + " : bookMovieTickets ; ClientName : "+ clientID +"; ServerName : "+clientServer+";MovieShowID : "+movieShowID+";MovieName : "+movieName+";Number Of Tickets : "+numberOfTickets+"; Server Response :"+operationMessage;
//							clientLogs.createClientLogs(clientID);
							
							break;
						}
						catch(Exception e)
						{
							System.out.println("Tickets Cancelled");
//							clientLogs.input = "Tickets Cancelled";
//							clientLogs.createClientLogs(clientID);
							//e.printStackTrace();
						}
						break;
					}
					
					case "7":
					{
						try
						{
							System.out.print("Old Movie : ");
							String oldmovieShowID = this.getMovieID();
							System.out.print("New Movie : ");
							movieShowID = this.getMovieID();
							System.out.print("Old Movie : ");
							String oldmovieName = this.getMovieName();
							System.out.print("New Movie : ");
							movieName = this.getMovieName();
							numberOfTickets = this.getNumberOfTickets();
							operationMessage = remoteAdmin.exchangeTickets(clientID,oldmovieName,oldmovieShowID,movieShowID,movieName,numberOfTickets);
							System.out.println(operationMessage);

//							clientLogs.input = new Date() + " : exchangeTickets ; ServerName : "+clientServer+";CustomerID : "+clientID+";Old MovieID : "+oldmovieShowID+";Old MovieName : "+oldmovieName+";New MovieID : "+movieShowID+";New MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+operationMessage+")";
//							clientLogs.createClientLogs(clientID);

							break;
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					
					case "8":
					{
						System.out.println("Logging Off ! Thanks for Using System");
						
//						clientLogs.input = new Date() + " ; LogOff - Success ; ClientName : "+ clientID +"; ServerName : "+clientServer;
//						clientLogs.createClientLogs(clientID);
						
						break;
					}
					
					default :
					{
						System.out.println("You Entered Wrong Option");
						break;
					}
				}
				
			}while(!choice.equals("8"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public String adminMenu()
	{
		System.out.println("*--------------------------------------------*");
		System.out.println("Please choose an option : ");
		System.out.println("1. Add Movie Slots");
		System.out.println("2. Remove Movie Slot");
		System.out.println("3. List Movie Shows Availibility");
		System.out.println("4. Book Movie Tickets");
		System.out.println("5. Get Booking Schedule");
		System.out.println("6. Cancel Movie Tickets");
		System.out.println("7. Exchange Movie Tickets");
		System.out.println("8. Logout");
		System.out.println("Waiting for your response : ");	
		
		return this.getInput();
	}
	
	public void customer() throws Exception
	{
		URL url = new URL("http://localhost:8080/TicketBooking/"+clientServer+"?wsdl");
		QName qName = new QName("http://webservice.com/","TicketBookingService");
		Service service = Service.create(url,qName);
		TicketBookingInterface remoteCustomer = service.getPort(TicketBookingInterface.class);
		
		String choice ;
		String movieShowID;
		String movieName;
		int movieCapacity;
		int numberOfTickets;
		String operationStatus = "";
		String showAvailibility = "";
		String operationMessage = "";
		do
		{
			choice = this.customerMenu();
			System.out.println("Choice : "+ choice);
			
			switch(choice)
			{
				case "1":
				{
					try
					{
						movieShowID = this.getMovieID();
						movieName = this.getMovieName();
						numberOfTickets = this.getNumberOfTickets();
						operationMessage = remoteCustomer.bookMovieTickets(clientID,movieShowID,movieName,numberOfTickets);
						
						System.out.println(operationMessage);
						
//						clientLogs.input = new Date() + " : bookMovieTickets ; ClientName : "+ clientID +"; ServerName : "+clientServer+";MovieShowID : "+movieShowID+";MovieName : "+movieName+";Number Of Tickets : "+numberOfTickets+"; Server Response :"+operationMessage;
//						clientLogs.createClientLogs(clientID);
						
						break;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				case "2":
				{
					try
					{
						operationMessage = remoteCustomer.getBookingSchedule(clientID);
						System.out.println(operationMessage);
						
//						clientLogs.input = new Date() + " : getBookingSchedule ; ClientName : "+ clientID +"; ServerName : "+clientServer+"; Server Response :"+operationMessage;
//						clientLogs.createClientLogs(clientID);
						
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
					break;
				}
				case "3":
				{
					try
					{
						movieShowID = this.getMovieID();
						movieName = this.getMovieName();
						numberOfTickets = this.getNumberOfTickets();
						operationMessage = remoteCustomer.cancelMovieTickets(clientID,movieShowID,movieName,numberOfTickets);
						
						System.out.println(operationMessage);
						
//						clientLogs.input = new Date() + " : bookMovieTickets ; ClientName : "+ clientID +"; ServerName : "+clientServer+";MovieShowID : "+movieShowID+";MovieName : "+movieName+";Number Of Tickets : "+numberOfTickets+"; Server Response :"+operationMessage;
//						clientLogs.createClientLogs(clientID);
						
						break;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				case "4":
				{
					try
					{

						System.out.print("Old Movie : ");
						String oldmovieShowID = this.getMovieID();
						System.out.print("New Movie : ");
						movieShowID = this.getMovieID();
						System.out.print("Old Movie : ");
						String oldmovieName = this.getMovieName();
						System.out.print("New Movie : ");
						movieName = this.getMovieName();
						numberOfTickets = this.getNumberOfTickets();
						operationMessage = remoteCustomer.exchangeTickets(clientID,oldmovieName,oldmovieShowID,movieShowID,movieName,numberOfTickets);
						System.out.println(operationMessage);

//						clientLogs.input = new Date() + " : exchangeTickets ; ServerName : "+clientServer+";CustomerID : "+clientID+";Old MovieID : "+oldmovieShowID+";Old MovieName : "+oldmovieName+";New MovieID : "+movieShowID+";New MovieName : "+movieName+";Number Of Tickets :"+numberOfTickets+"; Server Response :("+operationMessage+")";
//						clientLogs.createClientLogs(clientID);

						break;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				case "5":
				{
					System.out.println("Logging Off ! Thanks for Using System");
					
//					clientLogs.input = new Date() + " ; LogOff - Success ; ClientName : "+ clientID +"; ServerName : "+clientServer;
//					clientLogs.createClientLogs(clientID);
					
					break;
				}
				default :
				{
					System.out.println("You Entered Wrong Option");
					break;
				}
			}
			
		}while(!choice.equals("5"));
	}
	
	public String customerMenu()
	{
		System.out.println("*--------------------------------------------*");
		System.out.println("Please choose an option : ");
		System.out.println("1. Book Movie Tickets");
		System.out.println("2. Get Booking Schedule");
		System.out.println("3. Cancel Movie Tickets");
		System.out.println("4. Exchange Movie Tickets");
		System.out.println("5. Logout");
		System.out.println("Waiting for your response : ");	
		
		return this.getInput();
	}
	
	public String getInput()
	{
		Scanner input = new Scanner(System.in);
		String choice = input.nextLine();
		return choice;
	}
	
	public String getMovieName()
	{
		String input = "";
		while(true)
		{
			System.out.println("Please choose Movie Name : ");
			System.out.println("1. AVATAR");
			System.out.println("2. AVENGERS");
			System.out.println("3. TITANIC");

			input = this.getInput();	

			if(input.equals("1"))
			{
				return "AVATAR";
			}
			else if (input.equals("2"))
			{
				return "AVENGERS";
			}
			else if(input.equals("3"))
			{
				return "TITANIC";
			}
			else
			{
				System.out.println("Please enter a valid choice");
			}
		}
	}
	
	public String getMovieID()
	{
		String movieName = "";
		System.out.println("Please Enter Movie ID : ");
		movieName = getInput();
		if(movieName.length() != 10)
		{
			System.out.println("Invalid Movie ID");
			return getMovieID();
		}
		else
		{
			return movieName;
		}
	}
	
	public int getMovieCapacity()
	{
		int capacity = 0;
		System.out.println("Please Enter Movie Capacity : ");
		capacity = Integer.parseInt(getInput());
		return capacity;
	}
	
	public int getNumberOfTickets()
	{
		int numberOfTickets = 0;
		try
		{
			System.out.println("Please Enter Number of Tickets : ");
			numberOfTickets = Integer.parseInt(getInput());
			return numberOfTickets;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return numberOfTickets;
		}
		
	}
	
	public void addMovieData(TicketBookingInterface ai)throws RemoteException
	{
		if(clientServer.equals(CLIENTSERVER_ATWATER))
		{
			ai.addMovieSlot(clientID,"ATWM240323","AVATAR",100);
			ai.addMovieSlot(clientID,"ATWA250323","AVENGERS",70);
			ai.addMovieSlot(clientID,"ATWE260323","TITANIC",50);
		}
		else if(clientServer.equals(CLIENTSERVER_VERDUN))
		{
			ai.addMovieSlot(clientID,"VERM240323","AVATAR",200);
			ai.addMovieSlot(clientID,"VERA250323","AVENGERS",200);
			ai.addMovieSlot(clientID,"VERE260323","TITANIC",200);
		}
		else
		{
			ai.addMovieSlot(clientID,"OUTM240323","AVATAR",40);
			ai.addMovieSlot(clientID,"OUTA250323","AVENGERS",150);
			ai.addMovieSlot(clientID,"OUTE260323","TITANIC",500);
		}
	}

}
