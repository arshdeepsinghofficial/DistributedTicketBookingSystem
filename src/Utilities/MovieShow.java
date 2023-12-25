package Utilities;
import java.util.ArrayList;

public class MovieShow 
{
	public static final String CLIENTSERVER_ATWATER = "ATW"; 
	public static final String CLIENTSERVER_VERDUN = "VER"; 
	public static final String CLIENTSERVER_OUTREMONT = "OUT";
	
	public String movieID;
	public String movieName;
	public String movieShowServer;
	public int movieShowCapacity;
	public int movieShowBookingCount;
	public ArrayList <CustomerBooking> customerBookings;
	
	MovieShow(String movieName)
	{
		this.movieName = movieName;
	}
	
	public MovieShow(String movieID, String movieName,int movieShowCapacity)
	{
		this.movieID = movieID;
		this.movieName = movieName;
		this.movieShowCapacity = movieShowCapacity;
		this.movieShowServer = getServerName(movieID);
		this.movieShowBookingCount = 0;
		customerBookings = new ArrayList<>();
		
	}
	
	public String getServerName(String movieShowID)
	{
		if(movieShowID.substring(0, 3).equals(CLIENTSERVER_ATWATER))
		{
			return CLIENTSERVER_ATWATER;
		}
		else if(movieShowID.substring(0, 3).equals(CLIENTSERVER_ATWATER))
		{
			return CLIENTSERVER_VERDUN;
		}
		else
		{
			return CLIENTSERVER_OUTREMONT;
		}
	}
	
	
}
