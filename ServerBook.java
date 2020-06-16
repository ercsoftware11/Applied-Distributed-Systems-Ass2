import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*****************************
 *
 * File: ServerBook.java
 * Author: Ethan Cannon
 * Date: 15/05/19
 * Purpose: To accept objects sent from ServerCoordinator, compute the result of the required
 * order, and send it back to the ServerCoordinator. This ensure all calculations are done on the
 * server side, to ensure maximum performance for client.
 *
 *****************************/

public class ServerBook {

    // keep track of number of calculations completed
    public static int serverBookCount = 0;

    public static void main(String args[]) {
        try {

            // setup server
            int serverPort = 6812;
            ServerSocket listenSocket = new ServerSocket(serverPort);

            // begin listening
            while (true) {
                Socket clientSocket = listenSocket.accept();
                BookConnection c = new BookConnection(clientSocket);
            }

        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }
}

class BookConnection extends Thread {

    // declare socket
    private Socket clientSocket;

    // declare input output streams
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    BookConnection(Socket aClientSocket) {
        try {

            // assign socket
            clientSocket = aClientSocket;

            // assign input and output streams
            // output must be before input
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            ois = new ObjectInputStream(clientSocket.getInputStream());

            // run thread
            this.start();

        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {

        try {

            // read object sent from servercoordinator
            Object object = ois.readObject();

            // increment counter
            ServerBook.serverBookCount++;

            // print output
            System.out.println("Server Received Book Object Number: " + ServerBook.serverBookCount);

            // cast object to book order
            // should only receive bookorder objects as per server coordinator
            BookOrder bo = (BookOrder) object;

            // compute result based on object
            Task computeItem = new BookOrder(bo.getQty(), bo.getPrice());
            computeItem.executeTask();

            // print output
            System.out.println("Computed Total Bill for Book Order. Sending back to client....");

            // send acknowledgement back to servercoordinator
            oos.writeObject(computeItem.getResult());

        } catch (EOFException e) {
            System.out.println("EOF: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {

                // close socket
                if (clientSocket != null) {
                    clientSocket.close();
                }

                // close streams
                if (oos != null) {
                    oos.close();
                }

                if (ois != null) {
                    ois.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
}

