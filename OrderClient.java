import java.io.*;
import java.net.Socket;
import java.util.InputMismatchException;
import java.util.Scanner;

/*****************************
 *
 * File: OrderClient.java
 * Author: Ethan Cannon
 * Date: 15/05/19
 * Purpose: To accept user input, ensure no input issues, and send to ServerCoordinator
 *
 *****************************/

public class OrderClient {

    // constant string variables
    private static final String SENDING_TO_SERVER = "SENDING OBJECT TO SERVER.....";
    private static final String RECEIVING_FROM_SERVER = "RECEIVING COMPUTED OBJECT FROM SERVER.....";
    private static final String ASTERICK_LB= "**************************";
    private static final String EQUAL_LB = "==============================";

    // socket for connection
    private static Socket s = null;

    public static void main(String [] args) {

        while(true) {
            try {

                // declare server port
                int serverPort = 6811;

                // assign socket
                s = new Socket("localhost", serverPort);

                // decalre input and output streams for sending and receiving
                ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(s.getInputStream());

                // prepare scanner to read user input
                Scanner userInput = new Scanner(System.in);

                // print heading of file
                System.out.println("\nPLEASE PLACE YOUR ORDER BY SELECTING AN OPTION");
                System.out.println(ASTERICK_LB);
                System.out.println("1. Purchase a Book");
                System.out.println("2. Purchase a Movie");
                System.out.println("3. Exit");
                System.out.println(ASTERICK_LB);

                // user input variables
                int userSelectedNumber = 0;
                int qty;
                double price;

                // ask for option
                System.out.print("Enter your option: ");
                userSelectedNumber = userInput.nextInt();

                // check option
                if(userSelectedNumber == 1) {

                    // option is a book - prompt for number of books
                    System.out.print("Enter the of books: ");
                    qty = userInput.nextInt();

                    // prompt for price per book
                    System.out.print("Enter the price per book: ");
                    price = userInput.nextDouble();

                    // print status update
                    System.out.println(SENDING_TO_SERVER);

                    // prepare object and send
                    BookOrder bo = new BookOrder(qty, price);
                    oos.writeObject(bo);

                } else if (userSelectedNumber ==2) {

                    // option is movie - prompt for number of movies
                    System.out.print("Enter the of movies: ");
                    qty = userInput.nextInt();

                    // prompt for price per movie
                    System.out.print("Enter the price per movie: ");
                    price = userInput.nextDouble();

                    // print status update
                    System.out.println(SENDING_TO_SERVER);

                    // prepare and send
                    MovieOrder mo = new MovieOrder(qty, price);
                    oos.writeObject(mo);

                } else if(userSelectedNumber == 3) {
                    // exit program
                    System.exit(0);
                } else {
                    // throw exception with invalid format message
                    throw new InputMismatchException();
                }

                // print line break
                System.out.println(RECEIVING_FROM_SERVER);

                // server should return object which can be casted to string
                // additional functionality may be added at a later stage however
                // this is not within the scope of this project
                System.out.println((String) ois.readObject());

                // print line break
                System.out.println(EQUAL_LB);

            } catch (IOException | ClassNotFoundException  e) {
                e.printStackTrace();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input - please try again");
            }
            finally {
                // if socket is not null (socket has been assigned)
                if (s != null) try {
                    // close the socket
                    s.close();
                } catch (IOException e) {
                    System.out.println("close:" + e.getMessage());
                }
            }
        }
    }
}
