import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*****************************
 *
 * File: ServerMovie.java
 * Author: Ethan Cannon
 * Date: 15/05/19
 * Purpose: To accept objects sent from ServerCoordinator, compute the result of the required
 * order, and send it back to the ServerCoordinator. This ensure all calculations are done on the
 * server side, to ensure maximum performance for client.
 *
 *****************************/

public class ServerMovie {

    // keep track of number of calculations completed
    public static int serverMovieCount = 0;

    public static void main(String args[]) {
        try {

            // keep track of number of calcuations

            // setup server
            int serverPort = 6813;
            ServerSocket listenSocket = new ServerSocket(serverPort);

            // begin listening
            while (true) {
                Socket clientSocket = listenSocket.accept();
                MovieConnection c = new MovieConnection(clientSocket);
            }

        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }
}

class MovieConnection extends Thread {

    // declare socket
    private Socket clientSocket;

    // declare input out streams
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

    MovieConnection(Socket aClientSocket) {

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
            ServerMovie.serverMovieCount++;

            // print output
            System.out.println("Server Received Movie Object Number: " + ServerMovie.serverMovieCount);

            // cast object to movie order
            // should only receive movieorder objects as per server coordinator
            MovieOrder mo = (MovieOrder) object;

            // compute result based on object properties
            Task computeItem = new MovieOrder(mo.getQty(), mo.getPrice());
            computeItem.executeTask();

            // print output
            System.out.println("Computed Total Bill for Movie Order. Sending back to client....");

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


