package frontend;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

import javax.xml.ws.Endpoint;



public class FrontEnd implements Runnable
{
    public static final int FRONTEND_UDPPORT = 8081;
    public static final int SEQUENCER_UDPPORT = 8082;
    public static final int RM_MULTICASTUDPPORT = 8083;
    public static final int RM1_UDPPORT = 7777;
    public static final int RM2_UDPPORT = 8888;
    public static final int RM3_UDPPORT = 9999;
    public static String RM1Response_A = "";
    public static String RM2Response_I = "";
    public static String RM3Response_S = "";
    public static int RM1_AInvalidResponseCounter = 0;
    public static int RM2_IInvalidResponseCounter = 0;
    public static int RM3_SInvalidResponseCounter = 0;
    public static final String RM1_NAME = "RMArsh";
    public static final String RM2_NAME = "RMIshan";
    public static final String RM3_NAME = "RMSaryu";
    public static final String RM1LISTNER_NAME = "RMListnerArsh";
    public static final String RM2LISTNER_NAME = "RMListnerIshan";
    public static final String RM3LISTNER_NAME = "RMListnerSaryu";

    public static Queue <Request>requestQueue;
    public static String failureType;
    
    public static long startTime;
    public static long endTime;
    public static long timetaken;

    FrontEnd()
    {
        requestQueue = new LinkedList<>();

        UDPRMFEListner RMListner1 = new UDPRMFEListner(RM1LISTNER_NAME,this);
        UDPRMFEListner RMListner2 = new UDPRMFEListner(RM2LISTNER_NAME,this);
        UDPRMFEListner RMListner3 = new UDPRMFEListner(RM3LISTNER_NAME,this);

        Thread thread1 = new Thread(RMListner1);
        Thread thread2 = new Thread(RMListner2);
        Thread thread3 = new Thread(RMListner3);

        thread1.start();
        thread2.start();
        thread3.start();

    }

    @Override
    public void run() {
        try
        {

            TicketBooking ticketBooking = new TicketBooking();
            Endpoint endpoint = Endpoint.publish("http://localhost:8080/TicketBooking", ticketBooking);
            if(endpoint.isPublished())
            {
                System.out.println(" Server Started ");
            }
            else
            {
                System.out.println(" Server is down!Please try later");
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    public static void main(String areg[])throws Exception
    {
        FrontEnd frontEnd = new FrontEnd();
        Thread frontEndThread = new Thread(frontEnd);
        frontEndThread.start();
    }

    public static String SendToSequencer(String requestFromFE)
    {
        String message = requestFromFE;
        DatagramSocket socket = null;
        System.out.println("SEND TO THE SEQUENCER .. !!!");

        try
        {
            socket = new DatagramSocket();
            byte [] input = message.getBytes();
            InetAddress hostname = InetAddress.getByName("192.168.189.119");
            DatagramPacket request = new DatagramPacket(input, message.length(), hostname, SEQUENCER_UDPPORT);
            startTime = System.nanoTime();
            socket.send(request);

            DatagramSocket receiverSocket = new DatagramSocket(FRONTEND_UDPPORT);
            byte [] output = new byte[1024];
            DatagramPacket reply = new DatagramPacket(output, output.length);
            if (message.split("-_-")[0].equals("2")){
                receiverSocket.close();
                return "1";
            }
            try {
                receiverSocket.setSoTimeout(5000);
                receiverSocket.receive(reply);
                System.out.println("In front End" + new String(reply.getData()));
                receiverSocket.close();
                return new String(reply.getData());
            }
            catch(Exception r)
            {
                receiverSocket.close();
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
            //return Client.OPERATION_FAIL;
        }
        finally
        {
            if(socket != null)
            {
                socket.close();
            }
        }
        return "";
    }
    public static void addRequestToQueue(Request request)
    {
        requestQueue.add(request);
    }
    public static Request getRequestFromQueue(Request request)
    {
        return requestQueue.peek();
    }
    public static String compareRMResponses()
    {
        try
        {
        	endTime = System.nanoTime();
        	timetaken = endTime - startTime;
            Thread.currentThread().sleep(2000);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
//        RM3Response_S = RM2Response_I;
        System.out.println("RM1Response_A : "+ FrontEnd.RM1Response_A);
        System.out.println("RM2Response_I : "+ FrontEnd.RM2Response_I);
        System.out.println("RM3Response_S : "+ FrontEnd.RM3Response_S);
        if(RM1Response_A.equals(RM2Response_I) && RM1Response_A.equals(RM3Response_S))
        {
            return RM1Response_A.trim();
        }
        else if (RM1Response_A.equals("") && RM2Response_I.equals(RM3Response_S)){
            failureType = "2";
            sendBackupInitiationMessage(RM1_NAME);
            return RM2Response_I.trim();
        }
//        else if (RM2Response_I.equals("")){
//                failureType = "2";
//                sendBackupInitiationMessage(RM2_NAME);
//        }else if (RM3Response_S.equals("")){
//                failureType = "2";
//                sendBackupInitiationMessage(RM3_NAME);
//        }
        else if((RM1Response_A.equals(RM2Response_I) && (!RM1Response_A.equals(RM3Response_S))))
        {
            RM3_SInvalidResponseCounter++;
            if(RM3_SInvalidResponseCounter >= 3)
            {
                failureType = "2";
                sendBackupInitiationMessage(RM3_NAME);
                RM3_SInvalidResponseCounter = 0;
            }
            return RM1Response_A.trim();
        }
        else if ((!RM1Response_A.equals(RM2Response_I) && (RM1Response_A.equals(RM3Response_S))))
        {
            RM2_IInvalidResponseCounter++;
            if(RM2_IInvalidResponseCounter >= 3)
            {
                failureType = "2";
                sendBackupInitiationMessage(RM2_NAME);
                RM2_IInvalidResponseCounter = 0;
            }
            return RM1Response_A.trim();
        }
        else if((RM2Response_I.equals(RM1Response_A) && (!RM2Response_I.equals(RM3Response_S))))
        {
            RM3_SInvalidResponseCounter++;
            if(RM3_SInvalidResponseCounter >= 3)
            {
                failureType = "2";
                sendBackupInitiationMessage(RM3_NAME);
                RM3_SInvalidResponseCounter = 0;
            }
            return RM2Response_I.trim();

        }
        else if ((!RM2Response_I.equals(RM1Response_A) && (RM2Response_I.equals(RM3Response_S))))
        {
            RM1_AInvalidResponseCounter++;
            if(RM1_AInvalidResponseCounter >= 3)
            {
                failureType = "2";

                sendBackupInitiationMessage(RM1_NAME);
                RM1_AInvalidResponseCounter = 0;
            }
            return RM2Response_I.trim();

        }
        else if((RM3Response_S.equals(RM1Response_A) && (!RM3Response_S.equals(RM2Response_I))))
        {
            RM2_IInvalidResponseCounter++;
            if(RM2_IInvalidResponseCounter >= 3)
            {
                failureType = "2";

                sendBackupInitiationMessage(RM2_NAME);
                RM2_IInvalidResponseCounter = 0;
            }
            return RM3Response_S.trim();

        }
        else if ((!RM3Response_S.equals(RM1Response_A) && (RM3Response_S.equals(RM2Response_I))))
        {
            RM1_AInvalidResponseCounter++;
            if(RM1_AInvalidResponseCounter >= 3)
            {
                failureType = "2";

                sendBackupInitiationMessage(RM1_NAME);
                RM1_AInvalidResponseCounter = 0;
            }
            return RM3Response_S.trim();
        }
//        RM1Response_A = null;
//        RM2Response_I = null;
//        RM3Response_S = null;
        return RM2Response_I.trim();
    }
    public static void sendBackupInitiationMessage(String RMName)
    {
        Thread thread = new Thread(new Runnable()
        {
            public void run()
            {
                String recoveryRequest = new String();
                recoveryRequest = "2-_-"+ RMName+"-_-";
                System.out.println("Backup Message is : " + recoveryRequest);
                SendToSequencer(recoveryRequest.toString());
            }
        });
        thread.start();

    }


}
