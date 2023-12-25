package frontend;

import javax.jws.WebService;

@WebService(endpointInterface = "frontend.TicketBookingInterface")
public class TicketBooking implements TicketBookingInterface
{
	public String serverResponse;
	
	//
	public String addMovieSlot(String customerID,String movieID, String movieName, int movieCapacity)
	{
		serverResponse = "Message received AT TOP in add Movie slot at server";
		System.out.println(serverResponse);
		Request request = new Request();
		request.clientID = customerID;
		request.requestType = "0";
		request.operationType = "addMovieSlot";
		request.movieID = movieID;
		request.movieName = movieName;
		request.movieCapacity = movieCapacity+"";
		request.requestSequence = FrontEnd.SendToSequencer(request.toString());
		System.out.println("dONE ...!!!!");
		System.out.println("request.toString()\t" + request.toString());

		FrontEnd.addRequestToQueue(request);
		
		serverResponse = "Message received AT END in add Movie slot at server";
		System.out.println(serverResponse);
		
		FrontEnd.compareRMResponses();
		return serverResponse;
	}
	
	public String removeMovieSlots (String customerID,String movieID, String movieName)
	{
		Request request = new Request();
		request.clientID = customerID;
		request.requestType = "0";
		request.operationType = "removeMovieSlots";
		request.movieID = movieID;
		request.movieName = movieName;
		
		request.requestSequence = FrontEnd.SendToSequencer(request.toString());
		FrontEnd.addRequestToQueue(request);
		FrontEnd.compareRMResponses();


		return serverResponse;
	}
	
	public String listMovieShowsAvailability (String customerID,String movieName)
	{
		Request request = new Request();
		request.clientID = customerID;
		request.requestType = "0";
		request.operationType = "listMovieShowsAvailability";
		request.movieName = movieName;
		
		request.requestSequence = FrontEnd.SendToSequencer(request.toString());
		FrontEnd.addRequestToQueue(request);
		FrontEnd.compareRMResponses();
		
		return serverResponse;
		
	}
	
	public String bookMovieTickets (String customerID,String movieID,String movieName,int numberOfTickets)
	{
		System.out.println("customerID"+customerID);
		Request request = new Request();
		request.clientID = customerID;
		request.requestType = "0";
		request.operationType = "bookMovieTickets";
		request.movieID = movieID;
		request.movieName = movieName;
		request.customerID = customerID;
		request.numberOfTickets = numberOfTickets+"";
		
		request.requestSequence = FrontEnd.SendToSequencer(request.toString());
		FrontEnd.addRequestToQueue(request);
		FrontEnd.compareRMResponses();
		
		return serverResponse;
	}
	
	public String getBookingSchedule (String customerID)
	{
		Request request = new Request();
		request.clientID = customerID;
		request.requestType = "0";
		request.operationType = "getBookingSchedule";
		request.customerID = customerID;
		
		request.requestSequence = FrontEnd.SendToSequencer(request.toString());
		FrontEnd.addRequestToQueue(request);
		FrontEnd.compareRMResponses();
		
		return serverResponse;
	}
	
	public String cancelMovieTickets (String customerID, String movieID, String movieName, int numberOfTickets)
	{
		Request request = new Request();
		request.clientID = customerID;
		request.requestType = "0";
		request.operationType = "cancelMovieTickets";
		request.customerID = customerID;
		request.movieID = movieID;
		request.movieName = movieName;
		request.numberOfTickets = numberOfTickets+"";
		
		request.requestSequence = FrontEnd.SendToSequencer(request.toString());
		FrontEnd.addRequestToQueue(request);
		FrontEnd.compareRMResponses();
		
		return serverResponse;
	}

	public String exchangeTickets (String customerID, String old_movieName, String movieID, String new_movieID, String new_movieName, int numberOfTickets)
	{
		Request request = new Request();
		request.clientID = customerID;
		request.requestType = "0";
		request.operationType = "exchangeTickets";
		request.customerID = customerID;
		request.oldMovieName = old_movieName;
		request.oldMovieID = movieID;
		request.movieID = new_movieID;
		request.movieName = new_movieName;
		request.numberOfTickets = numberOfTickets+"";
		
		request.requestSequence = FrontEnd.SendToSequencer(request.toString());
		FrontEnd.addRequestToQueue(request);
		FrontEnd.compareRMResponses();
		
		return serverResponse;
	}

}
