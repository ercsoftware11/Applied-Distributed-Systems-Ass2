import java.io.Serializable;

/*****************************
 *
 * File: MovieOrder.java
 * Author: Ethan Cannon
 * Date: 08/05/19
 * Purpose: To handle object creation and handling of movie order calculations
 *
 *****************************/

public class MovieOrder implements Task, Serializable {

    // required fields for computation
    private int qty;
    private double price;

    // tax is declared to be 30 percent for movies
    private final double tax = .30;

    // variable for result
    String total;

    MovieOrder(int qty, double price) {
        this.qty = qty;
        this.price = price;
    }

    MovieOrder() {
        this(0, 0.0);
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    @Override
    public void executeTask() {

        // calculate amount before tax
        double totalBeforeTax = this.getQty() * this.getPrice();

        // calculate how much tax will be
        double taxAmount = totalBeforeTax * tax;

        // add this tax amount to the total
        double totalAfterTax = totalBeforeTax + taxAmount;

        // set the result to the total variable
        this.setTotal(String.format("%1$-20s %2$-20s %3$-20s %4$-20s\n",
                "Number of Movies: " + this.getQty(),
                "Price: " + String.format("%.2f", this.getPrice()),
                "Tax: " + String.format("%.2f", taxAmount),
                "Bill Total for Books: " + String.format("%.2f", totalAfterTax))
        );
    }

    @Override
    public Object getResult() {

        // access the total variable
        return this.getTotal();
    }
}
