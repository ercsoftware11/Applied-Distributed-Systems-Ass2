import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/*****************************
 *
 * File: ServerCoordinator.java
 * Author: Ethan Cannon
 * Date: 15/05/19
 * Purpose: To act as 'middle man' for client. This class will accept connections from clients
 * where they can send an object pending calculation. ServerCoordinator will determine the object
 * and forward it onto the respective server for calculations. ServerCoordinator will then wait for
 * a result, before forwarding it back to the client
 *
 *****************************/

public class ServerCoordinator {

    // keep track number of client connections
    public static int serverCoordinatorCount = 0;

    public static void main(String args[]) {
        try {

            // setup server
            int serverPort = 6811;

            // socket for server to listen for clients
            ServerSocket listenSocket = new ServerSocket(serverPort);

            // begin listening
            while (true) {

                // listen for connection
                Socket clientSocket = listenSocket.accept();

                // execute connection for servercoordinator
                Connection c = new Connection(clientSocket);
            }
        } catch (IOException e) {
            System.out.println("Listen socket:" + e.getMessage());
        }
    }
}

class Connection extends Thread {

    // declare variables

    // sockets
    private Socket clientSocket;
    private Socket serverSocket;

    // client streams
    private ObjectOutputStream oosClient;
    private ObjectInputStream oisClient;

    // server streams
    private ObjectOutputStream oosServer;
    private ObjectInputStream oisServer;

    Connection(Socket aClientSocket) {
        try {

            // assign socket
            clientSocket = aClientSocket;

            // assign client streams
            oosClient = new ObjectOutputStream(clientSocket.getOutputStream());
            oisClient = new ObjectInputStream(clientSocket.getInputStream());

            // run thread
            this.start();

        } catch (IOException e) {
            System.out.println("Connection:" + e.getMessage());
        }
    }

    public void run() {
        try {

            // declare scope limited variables
            // constants
            final int BOOK_SERVER_PORT = 6812; // movie server port
            final int MOVIE_SERVER_PORT = 6813; // book server port
            final String LOCALHOST = "localhost"; // server name

            // other variables
            Object serverResult; // object (string) to store result from server

            // read data sent from client
            Object object = oisClient.readObject();

            // increment counter
            ServerCoordinator.serverCoordinatorCount++;

            // display output to command screen
            System.out.println("ServerCoordinator received client object number: " +
                    ServerCoordinator.serverCoordinatorCount);

            if (object instanceof BookOrder) {

                // assign socket
                serverSocket = new Socket(LOCALHOST, BOOK_SERVER_PORT);

                // setup input output streams
                oosServer = new ObjectOutputStream(serverSocket.getOutputStream());
                oisServer = new ObjectInputStream(serverSocket.getInputStream());

                // print status
                System.out.println("Sending to Server for Book ....");

                // send to server
                oosServer.writeObject(object);

                // assign ServerBook result to local variable pending return
                serverResult = oisServer.readObject();

            } else if (object instanceof MovieOrder) {

                // assign socket
                serverSocket = new Socket(LOCALHOST, MOVIE_SERVER_PORT);

                // setup input and output streams
                oosServer = new ObjectOutputStream(serverSocket.getOutputStream());
                oisServer = new ObjectInputStream(serverSocket.getInputStream());

                // print status
                System.out.println("Sending to Server for Movie ....");

                // send to server
                oosServer.writeObject(object);

                // assign ServerMovie result to local variable pending return
                serverResult = oisServer.readObject();

            } else {

                // if neither BookOrder or MovieOrder (should not occur), assign string
                serverResult = "Not assigned";
            }

            // print status
            System.out.println("Returning Order Back to Original Client ....");

            // conditional checks complete, return object to client
            oosClient.writeObject(serverResult);

        } catch (EOFException e) {
            System.out.println("EOF: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IO Error: " + e.getMessage());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                // close all sockets
                if (clientSocket != null) {
                    clientSocket.close();
                }

                if (serverSocket != null) {
                    serverSocket.close();
                }

                // close streams
                if (oosServer != null) {
                    oosServer.close();
                }

                if (oisServer != null) {
                    oisServer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(-1);
            }
        }
    }
}
