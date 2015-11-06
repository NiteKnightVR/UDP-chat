/**
 * Created by John on 11/1/2015.
 */
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class Server{
    DatagramSocket socket = null;
    public int maxSeq;
    private int seqNumber = 0;
    boolean on = true;

    public Server() {

    }

    public void createAndListenSocket() {
        try {
            socket = new DatagramSocket(7777);
            byte[] incomingData = new byte[1024];

            while (on) {
                DatagramPacket incomingPacket = new DatagramPacket(incomingData, incomingData.length);
                socket.receive(incomingPacket);
                String message = new String(incomingPacket.getData());
                String delims = "[ ]+";
                String[] check = message.split(delims);

                DatagramPacket replyPacket = null;

                if(check[0].equals("DATA"))
                    if(Integer.parseInt(check[1]) == seqNumber)
                    {
                        System.out.println(check[2]);
                        InetAddress IPAddress = incomingPacket.getAddress();
                        int port = incomingPacket.getPort();
                        String reply = ("ACK " + seqNumber + " \n");
                        byte[] data = reply.getBytes();
                        replyPacket = new DatagramPacket(data, data.length, IPAddress, port);
                        socket.send(replyPacket);
                        seqNumber++;
                    }
                    else
                        socket.send(replyPacket);
                if(seqNumber == maxSeq) {
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
        server.maxSeq = Integer.parseInt(args[0]);
        server.createAndListenSocket();
    }
}

