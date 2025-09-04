package farm.sales.transaction;

import farm.customer.Customer;
import farm.inventory.product.Product;
import farm.inventory.product.data.Barcode;
import farm.sales.ReceiptPrinter;

import java.util.*;

/**
 * A transaction type that builds on the functionality of a categorised transaction,
 * allowing store-wide discounts to be applied to all products of a nominated type.
 *
 * This can be thought of as akin to putting on a special sale such as a seasonal
 * special for lower price jam, or an end-of-year sale discounting all products.
 */
public class SpecialSaleTransaction extends CategorisedTransaction {

    private Map<Barcode, Integer> discounts = new HashMap<>();

    /**
     * Construct a new special sale transaction for an associated customer, with an empty set of discounts.
     * @param customer who started the transaction
     */
    public SpecialSaleTransaction(Customer customer) {
        super(customer);
    }

    /**
     *Construct a new special sale transaction for an associated customer,
     * with a set of discounts to be applied to nominated product types on purchasing.
     *
     * @param customer - the customer who is starting the transaction
     * @param discounts - a mapping from product barcodes to the associated discount applied on purchasing, amounts
     *                  signified as an integer percentage
     *
     * @require 0 <= discount amount <=100
     */
    public SpecialSaleTransaction(Customer customer, Map<Barcode, Integer> discounts) {
        super(customer);
        this.discounts = new HashMap<>(discounts);
    }

    /**
     * Retrieves the discount percentage that will be applied for a particular product type, as an integer percentage.
     * If there is no discount percentage for that Product, returns 0
     *
     * @param type - the product type, i.e. the barcode
     * @return the amount the product is discounted by, as an integer percentage
     */
    public int getDiscountAmount(Barcode type) {

        if (this.discounts != null) {
            for (Map.Entry<Barcode, Integer> entry : this.discounts.entrySet()) {
                if (entry.getKey() == type) {
                    return entry.getValue();
                }
            }
        }
        return 0;
    }

    /**
     * Determines the total price for the provided product type within this transaction, with any specific discount
     * applied.
     *
     * @param type - the product type, i.e. the barcode, whose subtotal should be calculated
     * @return the total (discounted price for all instances of that product type within this transaction
     */
    public int getPurchaseSubtotal(Barcode type) {
        int subtotal = super.getPurchaseSubtotal(type);
        return subtotal - (subtotal * getDiscountAmount(type) / 100);
    }

    /**
     * Calculate the total price (with discounts) of all the current products in the transaction.
     *
     * @return the total (discounted) price calculated
     */
    public int getTotal() {
        int total = 0;

        for (Map.Entry<Barcode, List<Product>> entry : super.getPurchasesByType().entrySet()) {
            Barcode barcode = entry.getKey();
            total += getPurchaseSubtotal(barcode);
        }
        return total;
    }

    /**
     * Calculate how much the customer has saved from discounts.
     *
     * @return the numerical savings from discounts
     */
    public int getTotalSaved() {
        return super.getTotal() - this.getTotal();
    }

    /**
     * Returns a string representation of this transaction and its current state.
     * The representation contains information about the customer, the transaction's status,
     * the associated products, and the discounts to be applied.
     *
     * @return The formatted string representation of the product.
     */
    public String toString() {
        String transactionStr = super.toString();
        int length = transactionStr.length();

        return transactionStr.substring(0, length - 1) + ", Discounts: " + this.discounts + "}";
    }

    /**
     * Converts the transaction into a formatted receipt for display.
     *
     * @return the styled receipt representation of this transaction
     */
    public String getReceipt() {
        if (this.isFinalised()) {
            String customerName = this.getAssociatedCustomer().getName();
            String total = "$" + String.format("%.2f", (double) this.getTotal() / 100);
            List<String> headings = Arrays.asList("Item", "Qty", "Price (ea.)", "Subtotal");
            List<List<String>> entries = new ArrayList<>();
            String totalSavings = "$" + String.format("%.2f", (double) this.getTotalSaved() / 100);

            // I need to order the items to match Barcode.values()
            List<Barcode> itemByKey = new ArrayList<>(this.getPurchasesByType().keySet());
            Collections.sort(itemByKey);

            for (Barcode barcode : itemByKey) {

                List<String> entry;

                if (getDiscountAmount(barcode) > 0) {
                    entry = Arrays.asList(
                            barcode.getDisplayName(),
                            String.valueOf(getPurchaseQuantity(barcode)),
                            "$" + String.format("%.2f",
                                    (double) barcode.getBasePrice() / 100),
                            "$" + String.format("%.2f",
                                    (double) getPurchaseSubtotal(barcode) / 100),
                            "Discount applied! " + getDiscountAmount(barcode)
                                    + "% off " + barcode.getDisplayName()
                    );
                } else {
                    entry = Arrays.asList(
                            barcode.getDisplayName(),
                            String.valueOf(getPurchaseQuantity(barcode)),
                            "$" + String.format("%.2f",
                                    (double) barcode.getBasePrice() / 100),
                            "$" + String.format("%.2f",
                                    (double) getPurchaseSubtotal(barcode) / 100)
                    );
                }
                entries.add(entry);
            }

            if (getTotalSaved() > 0) {
                return ReceiptPrinter.createReceipt(headings, entries,
                        total, customerName, totalSavings);
            }
            return ReceiptPrinter.createReceipt(headings, entries, total, customerName);
        }
        return ReceiptPrinter.createActiveReceipt();
    }
}
