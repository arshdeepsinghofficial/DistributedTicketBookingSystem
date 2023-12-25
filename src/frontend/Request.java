package frontend;

import java.util.Queue;

public class Request 
{
	public String requestType = "null";
	public String requestSequence = "null";
	public String serverID = "null";
	public String clientID = "null";
	public String operationType = "null";
	public String movieName = "null";
	public String movieID = "null";
	public String movieCapacity = "null";
	public String customerID = "null";
	public String numberOfTickets = "null";
	public String oldMovieName = "null";
	public String oldMovieID = "null";
	
	
	public String requestResendCount = "0";
//	public Queue requestQueue;
	
	 @Override
	 public String toString() {
		 return requestType + "-_-" + 
				 //requestSequence + "-_-" +  
				 serverID+  "-_-" +
				 clientID+  "-_-"+
				 operationType + "-_-"+
				 movieName + "-_-" +
				 movieID + "-_-"+
				 movieCapacity + "-_-"+
				 customerID + "-_-" +
				 numberOfTickets + "-_-" +
				 oldMovieName + "-_-" +
				 oldMovieID;
	    }
	
}
