/**
 * Created by John on 11/1/2015.
 */
import java.io.IOException;
import java.net.*;
import java.util.Scanner;

public class Client {
    DatagramSocket socket;
    InetAddress IPAddress;
    private static boolean on = true;
    private int seqNumber = 0;
    public Client() {

    }

    public void createAndListenSocket(String address) {
        try {
            socket = new DatagramSocket();
            IPAddress = InetAddress.getByName(address);

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String message) {
        try {
            socket.setSoTimeout(5000);
        } catch (SocketException e) {
            System.out.println("Error setting timeout" + e);
        }

        try {

            byte[] incomingData = new byte[1024];
            message = "DATA " + seqNumber + " " + message + " \n";
            byte[] data = message.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(data, data.length, IPAddress, 7777);
            socket.send(sendPacket);

            DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
            socket.receive(incomingPacket);
            String response = new String(incomingPacket.getData());

            String delims = "[ ]+";
            String[] check = response.split(delims);

            if (check[0].equals("ACK")) {
                if (Integer.parseInt(check[1]) == seqNumber) {
                    seqNumber++;
                }
            }

        }catch (SocketTimeoutException e) {
            System.out.println("Timed out, resending");
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

        while(on)
        {
            Scanner s= new Scanner(System.in);
            String x = s.next();
            client.sendMessage(x);
        }
    }
}

