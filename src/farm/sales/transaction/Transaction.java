package farm.sales.transaction;

import farm.customer.Customer;
import farm.inventory.product.Product;
import farm.sales.ReceiptPrinter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Keeps track of what items are to be (or have been) purchased and by whom.
 */
public class Transaction {

    private boolean active;
    private Customer associatedCustomer;
    private List<Product> finalisedTransaction;


    /**
     * Construct a new transaction for an associated customer.
     * Always active at time of creation.
     */
    public Transaction(Customer customer) {
        this.associatedCustomer = customer;
        this.active = true;
    }

    /***
     * Retrieves the customer associated with this transaction
     * @return the customer of the transaction
     */
    public Customer getAssociatedCustomer() {
        return this.associatedCustomer;
    }


    /***
     * Retrieves all products associated with the transaction.
     * If the transaction has been finalised, this is all the products that were
     * 'locked in' as final purchases at that time. If the transaction is active,
     * all products currently in associated customer's cart.
     *
     * @return the list of purchases comprising the transaction
     */
    public List<Product> getPurchases() {
        if (this.active) {
            return List.copyOf(this.associatedCustomer.getCart().getContents());
        } else {
            return List.copyOf(finalisedTransaction);
        }
    }

    /***
     * Calculates the total price of all the current products in the transaction.
     *
     * @return the total price calculated
     */
    public int getTotal() {
        int total = 0;

        List<Product> currentProducts = getPurchases();

        for (Product currentProduct : currentProducts) {
            total += currentProduct.getBasePrice();
        }
        return total;
    }

    /***
     * Determines if the transaction is finalised (i.e. sale completed) or not.
     *
     * @return true iff the transaction is over, else false.
     */
    public boolean isFinalised() {
        return !this.active;
    }

    /***
     * Mark a transaction as finalised and update the transaction's internal state accordingly.
     * Locks all pending purchases previously added, such that they are now treated as final
     * purchases and no additional modification can be made, and empties the customer's cart.
     */
    public void finalise() {

        this.finalisedTransaction = new ArrayList<>();

        for (Product product : this.getPurchases()) {
            this.finalisedTransaction.add(product);
        }

        this.associatedCustomer.getCart().setEmpty();

        this.active = false;
    }

    /***
     * Returns a string representation of this transaction and its current state.
     * The representation contains information about the customer, the transaction's status,
     * and the associated products.
     *
     * @return a string representation in the form
     *  Transaction {Customer: 'customer', Status: 'status', Associated Products: 'products'}.
     */
    public String toString() {

        String status;
        if (isFinalised()) {
            status = "Finalised";
        } else {
            status = "Active";
        }

        List<String> products = new ArrayList<>();

        for (Product purchase : this.getPurchases()) {
            products.add(purchase.toString());
        }

        String strOfProducts = String.join(", ", products);

        String customerInfo = this.associatedCustomer.toString();

        return "Transaction {" + "Customer: " + customerInfo.substring(6) + ", Status: "
                + status + ", Associated Products: [" + strOfProducts + "]}";
    }

    /***
     * Converts the transaction into a formatted receipt for display, using the ReceiptPrinter.
     **
     * @return a String representation of the transaction receipt
     */
    public String getReceipt() {
        if (this.isFinalised()) {

            String customerName = this.associatedCustomer.getName();
            String total = "$" + String.format("%.2f", (double) this.getTotal() / 100);
            List<String> headings = Arrays.asList("Item", "Price");
            List<List<String>> entries = new ArrayList<>();

            for (Product product : this.getPurchases()) {
                List<String> entry = Arrays.asList(product.getDisplayName(),
                        "$" + String.format("%.2f", (double) product.getBasePrice() / 100));
                entries.add(entry);
            }

            return ReceiptPrinter.createReceipt(headings, entries, total, customerName);
        }

        return ReceiptPrinter.createActiveReceipt();
    }
}
