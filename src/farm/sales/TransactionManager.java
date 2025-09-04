package farm.sales;

import farm.inventory.product.Product;
import farm.sales.transaction.*;
import farm.core.FailedTransactionException;

/**
 * The controlling class for all transactions.
 *
 * Opens and closes transactions, as well as ensuring only one transaction is active at
 * any given time.
 *
 * Does not create transactions but rather keeps track of the currently ongoing transaction and
 * thus the subsequent customer cart associated with it.
 */
public class TransactionManager {


    private Transaction ongoingTransaction;

    /**
     * Constructor for TransactionManager that is the controlling class for all transactions.
     *
     * Opens and closes transactions, as well as ensuring only one
     * transaction is active at any given time
     */
    public TransactionManager() {
    }

    /**
     * Determine whether a transaction is currently in progress.
     *
     * @return true iff a transaction is in progress, else false.
     */
    public boolean hasOngoingTransaction() {
        return this.ongoingTransaction != null;
    }

    /**
     * Begins managing the specified transaction, provided one is not already ongoing
     *
     * @param transaction - the transaction to set as the manager's ongoing transaction
     * @throws FailedTransactionException - iff a transaction is already in progress
     */
    public void setOngoingTransaction(Transaction transaction) throws FailedTransactionException {
        if (this.hasOngoingTransaction()) {
            throw new FailedTransactionException();
        }

        this.ongoingTransaction = transaction;
    }

    /**
     * Adds the given product to the cart of the customer associated with the current transaction.
     *
     * The product can only be added if there is currently an ongoing transaction and that
     * transaction has not already been finalised.
     *
     * @param product - the product to add to customer's cart
     * @throws FailedTransactionException - iff there is no ongoing transaction or the transaction
     * has already been finalised.
     * @requires the provided product is known to be valid for purchase, i.e. has been successfully
     * retrieved from the farm's inventory
     */
    public void registerPendingPurchase(Product product) throws FailedTransactionException {
        if (!hasOngoingTransaction()) {
            throw new FailedTransactionException();
        }
        this.ongoingTransaction.getAssociatedCustomer().getCart().addProduct(product);
    }

    /**
     * Finalises the currently ongoing transaction and makes ready the TransactionManager to
     * accept a new ongoing transaction.
     *
     * @return - the finalised transaction
     * @throws FailedTransactionException - iff there is no currently ongoing transaction to close.
     */
    public Transaction closeCurrentTransaction() throws FailedTransactionException {
        if (!hasOngoingTransaction()) {
            throw new FailedTransactionException();
        }

        this.ongoingTransaction.finalise();
        Transaction closedTransaction = this.ongoingTransaction;

        this.ongoingTransaction = null;

        return closedTransaction;

    }
}
