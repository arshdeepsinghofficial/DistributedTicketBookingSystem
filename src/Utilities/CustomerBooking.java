package Utilities;

public class CustomerBooking 
{
	public String customerID;
	public String bookedMovieName;
	public String bookedMovieID;
	public int ticketCount;
	
	public CustomerBooking(String customerID, String bookedMovieName, String bookedMovieID, int ticketCount)
	{
		this.customerID = customerID;
		this.bookedMovieName = bookedMovieName;
		this.bookedMovieID = bookedMovieID;
		this.ticketCount = ticketCount;
	}
	public CustomerBooking()
	{
		
	}
}
