package farm.core;

import farm.customer.AddressBook;
import farm.customer.Customer;
import farm.inventory.Inventory;
import farm.inventory.FancyInventory;
import farm.inventory.product.*;
import farm.inventory.product.data.*;
import farm.sales.TransactionHistory;
import farm.sales.TransactionManager;
import farm.sales.transaction.Transaction;

import java.util.ArrayList;
import java.util.List;

/**
 * Top-level model class responsible for storing and making updates to the data and smaller
 * model entities that make up the internal state of a farm.
 */
public class Farm {

    private Inventory inventory;
    private AddressBook addressBook;
    private TransactionManager transactionManager;
    private TransactionHistory transactionHistory;

    /**
     * Creates a new farm instance wih an inventory and address book supplied
     *
     * @param inventory - the inventory through which access to the farm's stock is provisioned
     * @param addressBook - the address book storing the farm's customer records
     */
    public Farm(Inventory inventory, AddressBook addressBook) {
        this.inventory = inventory;
        this.addressBook = addressBook;
        this.transactionManager = new TransactionManager();
        this.transactionHistory = new TransactionHistory();
    }

    /**
     * Retrieves all customer records currently stored in the farm's address book.
     *
     * @return a list of all customers in the address book
     * @ensures the returned list is a shallow copy and cannot modify the original address book
     */
    public List<Customer> getAllCustomers() {
        return new ArrayList<>(this.addressBook.getAllRecords());
        //return List.copyOf(this.addressBook.getAllRecords());
    }

    /**
     * Retrieves all products currently stored in the farm's inventory.
     *
     * @return a list of all products in the inventory
     * @ensures the returned list is a shallow copy and cannot modify the original inventory
     */
    public List<Product> getAllStock() {
        return new ArrayList<>(this.inventory.getAllProducts());
    }

    /**
     * Retrieves the farm's transaction manager.
     *
     * @return the farm's transaction manager
     */
    public TransactionManager getTransactionManager() {
        return this.transactionManager;
    }

    /**
     * Retrieves the farm's transaction history.
     *
     * @return the farm's transaction history
     */
    public TransactionHistory getTransactionHistory() {
        return this.transactionHistory;
    }

    /**
     * Saves the supplied customer in the farm's address book
     *
     * @param customer - the customer to add to the address book
     * @throws DuplicateCustomerException - if the address book already contains this customer
     */
    public void saveCustomer(Customer customer) throws DuplicateCustomerException {
        this.addressBook.addCustomer(customer);
    }

    /**
     * Adds a single product of the specified type and quality to the farm's inventory.
     *
     * @param barcode - the product type to add to the inventory
     * @param quality - the quality of the product to add to the inventory
     */
    public void stockProduct(Barcode barcode, Quality quality) {
        this.inventory.addProduct(barcode, quality);
    }

    /**
     * Adds some quantity of products of the specific type and quality to the farm's inventory.
     *
     * @param barcode - the product type to add to the inventory
     * @param quality - the quality of the product to add to the inventory
     * @param quantity - the number of products to add to the inventory
     * @throws IllegalArgumentException - if a negative quantity is provided
     * @throws InvalidStockRequestException - if the quantity is greater than 1 when a
     * FancyInventory is not in use.
     */
    public void stockProduct(Barcode barcode, Quality quality, int quantity)
            throws InvalidStockRequestException {

        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be at least 1.");
        } else if (quantity > 1 && !(inventory instanceof FancyInventory)) {
            throw new InvalidStockRequestException();
        }

        this.inventory.addProduct(barcode, quality, quantity);
    }

    /**
     * Set the provided transaction as the current ongoing transaction.
     *
     * @param transaction - the transaction to set as ongoing.
     * @throws FailedTransactionException - if the farm's transaction manager rejects the request
     * to being managing this transaction.
     * @requires - the customer associated with transaction exists in the farm's address book.
     */
    public void startTransaction(Transaction transaction) throws FailedTransactionException {
        this.transactionManager.setOngoingTransaction(transaction);
    }

    /**
     * Attempts to add a single product of the given type to the customer's shopping cart.
     * Method attempts to retrieve a product form the farm's inventory and register it as a
     * pending purchase via the farm's transaction manager, then reports the number of products
     * successfully added (for this method, this will always be either 0 or 1).
     *
     * If no transaction is ongoing, the following message is shown:
     *      "Cannot add to cart when no customer has started shopping."
     *
     * @param barcode - the product type to add.
     * @return the number of products successfully added to the cart i.e. if no products of this
     * type exists in the inventory, this method will return 0
     * @throws FailedTransactionException - if no transaction is ongoing
     */
    public int addToCart(Barcode barcode) throws FailedTransactionException {
        int numberOfProductsAdded = 0;

        if (this.transactionManager != null) {
            if (!this.transactionManager.hasOngoingTransaction()) {
                throw new FailedTransactionException("Cannot add to cart when no customer "
                        + "has started shopping.");
            }

            if (this.inventory.existsProduct(barcode)) {
                List<Product> purchase = this.inventory.removeProduct(barcode);
                this.transactionManager.registerPendingPurchase(purchase.getFirst());
                numberOfProductsAdded++;
            }
        }
        return numberOfProductsAdded;
    }

    /**
     * Attempts to add the specified number of products of the given type to the customer's
     * shopping cart. Method attempts to retrieve the request products from the farm's inventory
     * and registers them as pending purchases via the farm's transaction manager, then reports
     * the number of products successfully added. If the inventory does not contain enough stock,
     * adds as many products as possible to the cart.
     *
     * @param barcode - the product type to add
     * @param quantity - the number of products to add
     * @return the number of products successfully added to the cart
     * @throws FailedTransactionException if no transaction is ongoing, or if the quantity is
     * greater than 1, when a FancyInventory is not in use.
     * @throws IllegalArgumentException if a quantity less than 1 is entered.
     */
    public int addToCart(Barcode barcode, int quantity)
            throws FailedTransactionException {

        int numberOfProductsAdded = 0;

        if (this.transactionManager != null) {
            if (!this.transactionManager.hasOngoingTransaction()) {
                throw new FailedTransactionException("Cannot add to cart when no customer "
                        + "has started shopping.");
            } else if (quantity < 1) {
                throw new IllegalArgumentException("Quantity must be atleast 1.");
            } else if (!(this.inventory instanceof FancyInventory)) {
                throw new FailedTransactionException("Current inventory is not fancy enough. "
                        + "Please purchase products one at a time.");
            }

            int sufficientStock = 0;
            for (Product stock : getAllStock()) {
                if (stock.getBarcode() == barcode) {
                    sufficientStock++;
                }
            }

            if (quantity <= sufficientStock) {
                List<Product> purchases = inventory.removeProduct(barcode, quantity);
                for (Product product : purchases) {
                    this.transactionManager.registerPendingPurchase(product);
                    numberOfProductsAdded++;
                }
            }
        }
        return numberOfProductsAdded;
    }

    /**
     * Closes the ongoing transaction. If items have been purchased, this also records the
     * transaction in the farm's history. If no items are purchased then no history is recorded.
     *
     * @return true iff finalised transaction contained products.
     * @throws FailedTransactionException - if transaction cannot be closed.
     */
    public boolean checkout() throws FailedTransactionException {
        try {
            Transaction transaction = this.transactionManager.closeCurrentTransaction();
            if (!transaction.getPurchases().isEmpty()) {
                this.transactionHistory.recordTransaction(transaction);
                return true;
            }
        } catch (FailedTransactionException e) {
            throw new FailedTransactionException();
        }
        return false;
    }

    /**
     * Retrieves the receipt associated with the most recent transaction.
     *
     * @return the receipt associated with the most recent transaction.
     */
    public String getLastReceipt() {
        return this.transactionHistory.getLastTransaction().getReceipt();
    }

    /**
     * Retrieves a customer from the address book.
     *
     * @param name - the name of the customer
     * @param phoneNumber - the phone number of the customer
     * @return the customer instance matching the name and phone number
     * @throws CustomerNotFoundException - if the customer does not exists in the address book
     */
    public Customer getCustomer(String name, int phoneNumber) throws CustomerNotFoundException {
        return this.addressBook.getCustomer(name, phoneNumber);
    }

}

























