/**
 * Created by John on 11/1/2015.
 */
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {
    DatagramSocket socket;      //initialize
    InetAddress IPAddress;
    private static boolean on = true;
    private int seqNumber = 0;  //start at 0
    public Client() {

    }

    public void createAndListenSocket(String address) {
        try {
            socket = new DatagramSocket();  //create new socket
            IPAddress = InetAddress.getByName(address); //set address

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {   //send the String message
        try {
            socket.setSoTimeout(5000);  //set timeout to 5 seconds
        } catch (SocketException e) {
            System.out.println("Error setting timeout" + e);
        }

        try {

            byte[] incomingData = new byte[1024];   //reserve space for incoming ACK
            message = "DATA " + seqNumber + " " + message + " \n";  //create message to send
            byte[] data = message.getBytes();   //convert to bytes
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 7777); //convert to packet
            socket.send(sendPacket);    //send the packet

            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);  //get ACK back from Server
            socket.receive(incomingPacket);
            String response = new String(incomingPacket.getData()); //convert to string

            String delims = "[ ]+"; //set delims
            String[] check = response.split(delims);    //split ACK into ACK and sequence number

            if (check[0].equals("ACK")) {   //check for ACK tag
                if (Integer.parseInt(check[1]) == seqNumber) {  //check seq. number
                    seqNumber++;
                }
            }

        }catch (SocketTimeoutException e) {
            System.out.println("Timed out, resending"); //if timeout, print, and resend
            sendMessage(message);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("Usage: java Client <address>");
            return;
        }
        Client client = new Client();
        client.createAndListenSocket(args[0]);

        while(on)   //get input and send, can use ctrl + c to end;
        {
            Scanner s= new Scanner(System.in);
            String x = s.next();
            client.sendMessage(x);
        }
    }
}

