# Applied Distributed Systems Assignment 2 Specification

An online store selling two productions (Book and Movie) would like to create an application that
allows customer to purchase their productions. The application should be implemented using TCP
client/server model, where client can enter basic order information and the system will calculate total
bill and return to client. The store needs to server a large number of customers; they need two servers to
handle the operations of each product type separately (one Server for Book and another Server for
Movie). The communications between client and the system are handled by another server acting as a
coordinator. The coordinator server receives order information from client send to the server of the
corresponding product type for calculating total bill. The computed information is then sent back to the
coordinator to pass onto client.
