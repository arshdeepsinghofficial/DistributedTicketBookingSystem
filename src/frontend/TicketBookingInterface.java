package frontend;

import java.rmi.RemoteException;

import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.jws.soap.SOAPBinding.Style;

@WebService
@SOAPBinding(style=Style.RPC)
public interface TicketBookingInterface 
{
	public String addMovieSlot(String customerID,String movieID, String movieName, int movieCapacity);
	
	public String removeMovieSlots (String customerID,String movieID, String movieName);
	
	public String listMovieShowsAvailability (String customerID,String movieName);
	
	public String bookMovieTickets (String customerID,String movieID,String movieName,int numberOfTickets);
	
	public String getBookingSchedule (String customerID);
	
	public String cancelMovieTickets (String customerID, String movieID, String movieName, int numberOfTickets);

	public String exchangeTickets (String customerID, String old_movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets);

}
