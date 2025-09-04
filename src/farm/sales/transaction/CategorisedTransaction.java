package farm.sales.transaction;

import farm.customer.Customer;
import farm.inventory.product.*;
import farm.inventory.product.data.*;
import farm.sales.ReceiptPrinter;

import java.util.*;

/**
 * A transaction type that allows products to be categorised by their types,
 * not solely as isolated individual products. The resulting receipt therefore
 * displays purchased types with an associated quantity purchased and subtotal,
 * rather than a single line for each product.
 */
public class CategorisedTransaction extends Transaction {

    /**
     * Construct a new categorised transaction for an associated customer.
     * @param customer - the customer who is starting the transaction (beginning to shop).
     */
    public CategorisedTransaction(Customer customer) {
        super(customer);
    }

    /**
     * Retrieves all unique product types of the purchases associated with the transaction.
     * @return a set of all product types in the transaction.
     */
    public Set<Barcode> getPurchasedTypes() {
        Set<Barcode> purchasedSet = new HashSet<>();

        for (Product purchases : this.getPurchases()) {
            purchasedSet.add(purchases.getBarcode());
        }

        return purchasedSet;
    }

    /**
     * Retrieves all products associated with the transaction, grouped by their type.
     *
     * @return the products in the transaction, grouped by their type.
     */
    public Map<Barcode, List<Product>> getPurchasesByType() {

        Map<Barcode, List<Product>> purchaseMap = new HashMap<>();

        for (Barcode barcode : this.getPurchasedTypes()) {

            List<Product> purchaseList = new ArrayList<>();

            for (Product purchase : this.getPurchases()) {
                if (purchase.getBarcode() == barcode) {
                    purchaseList.add(purchase);
                }
            }
            purchaseMap.put(barcode, purchaseList);
        }
        return purchaseMap;
    }

    /**
     * Retrieves the number of products of a particular type associated with the transaction
     * @param type - the product type i.e. to find egg quantity would be the egg barcode
     * @return the number of products of the specified type associated with the transaction
     */
    public int getPurchaseQuantity(Barcode type) {
        try {
            return this.getPurchasesByType().get(type).size();
        } catch (NullPointerException e) {
            return 0;
        }
    }

    /**
     * Determines the total price for the provided product type within this transaction.
     * @param type - the product type, i.e. the barcode
     * @return the total price for all instances of that product type within the transaction,
     * or 0 if no items are that type are associated with the transaction
     */
    public int getPurchaseSubtotal(Barcode type) {
        if (getPurchaseQuantity(type) == 0) {
            return 0;
        }
        return getPurchaseQuantity(type) * type.getBasePrice();
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

            // I need to order the items to match Barcode.values()
            List<Barcode> itemByKey = new ArrayList<>(this.getPurchasesByType().keySet());
            Collections.sort(itemByKey);

            for (Barcode barcode : itemByKey) {
                List<String> entry = Arrays.asList(
                        barcode.getDisplayName(),
                        String.valueOf(getPurchaseQuantity(barcode)),
                        "$" + String.format("%.2f", (double) barcode.getBasePrice() / 100),
                        "$" + String.format("%.2f", (double) getPurchaseSubtotal(barcode) / 100)
                );
                entries.add(entry);
            }

            return ReceiptPrinter.createReceipt(headings, entries, total, customerName);
        }

        return ReceiptPrinter.createActiveReceipt();
    }
}

