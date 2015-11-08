/**
 * Created by John on 11/1/2015.
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server{
    DatagramSocket socket = null;           //initialize
    DatagramPacket replyPacket = null;
    public int maxSeq;
    private int seqNumber = 0;
    boolean on = true;
    //public int test = 0;    //fail every other ack

    public Server() {

    }

    public void createAndListenSocket() {
        try {
            socket = new DatagramSocket(7777);      //listen on port 7777
            byte[] incomingData = new byte[1024];   //initialize incoming data

            while (on) {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);  //get incoming packet
                socket.receive(incomingPacket);
                String message = new String(incomingPacket.getData());  //get string from packet
                String delims = "[ ]+";     //set delims for parsing string
                String[] check = message.split(delims);     //split string into DATA, sequence number, and message

                if(check[0].equals("DATA")) //check to make sure its DATA tag
                    if(Integer.parseInt(check[1]) == seqNumber) //check for correct sequence number
                    {
                        System.out.println(check[2]);   //print msg
                        InetAddress IPAddress = incomingPacket.getAddress();    //get address of sender
                        int port = incomingPacket.getPort();    //get port of sender
                        String reply = ("ACK " + seqNumber + " \n");    //create ACK string
                        byte[] data = reply.getBytes(); //convert to byte data
                        replyPacket = new DatagramPacket(data, data.length, IPAddress, port);   //create reply packet
                        //if(test%2 == 1)   //test, make it fail every other time
                        socket.send(replyPacket);
                        seqNumber++;
                        //test++;   //testing
                    }
                    else    //if sequence number is wrong, resend the previous ACK
                        socket.send(replyPacket);
                if(seqNumber == maxSeq) {   //if max seq. number reached, end
                    on = false;
                    System.out.println("max seq number reached");
                }
                if(!on)
                    socket.close();
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: java Server <max sequence #>");
            return;
        }
        Server server = new Server();
        server.maxSeq = Integer.parseInt(args[0]);  //set max sequence number
        server.createAndListenSocket();
    }
}

