package farm.sales;

import farm.inventory.product.Product;
import farm.inventory.product.data.Barcode;
import farm.sales.transaction.*;
import farm.core.FailedTransactionException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static java.lang.Integer.MIN_VALUE;

/**
 * A record of all past transactions.
 *
 * Handles retrieval of statistics about past transactions, such as earnings and popular products.
 */
public class TransactionHistory {

    private List<Transaction> record;

    /**
     * Creates a new TransactionHistory that holds a record of all past transactions
     * and handles retrieval of statistics about past transactions.
     */
    public TransactionHistory() {
        this.record = new ArrayList<>();
    }

    /**
     * Adds the given transaction to the record of all past transactions.
     *
     * @param transaction - the transaction to add to the record
     * @requires - the transaction to be recorded has been finalised
     */
    public void recordTransaction(Transaction transaction) {
        this.record.add(transaction);
    }

    /**
     * Retrieves the most recent transaction.
     *
     * @return the most recent transaction added to the record.
     */
    public Transaction getLastTransaction() {
        return this.record.getLast();
    }


    /**
     * Calculate the gross earnings, i.e. total income, from all transactions.
     *
     * Total income refers to the sum of all totals reported by each individual completed
     * transaction stored in the history, as calculated by that particular transaction's Transaction.getTotal();
     *
     * @return - the gross earnings from all transactions in history, in cents
     */
    public int getGrossEarnings() {

        int grossEarnings = 0;

        if (!this.record.isEmpty()) {
            for (Transaction transaction : this.record) {
                grossEarnings += transaction.getTotal();
            }
        }
        return grossEarnings;
    }

    /**
     * Calculate the gross earnings, i.e. total income, from all sales of
     * a particular product type.
     *
     * @param type - the Barcode of the item of interest
     * @return the gross earnings from all sales of the product type, in cents.
     */
    public int getGrossEarnings(Barcode type) {
        int grossEarningsByType = 0;

        if (!this.record.isEmpty()) {
            for (Transaction transaction : this.record) {

                if (transaction instanceof SpecialSaleTransaction) {
                    grossEarningsByType +=
                            ((SpecialSaleTransaction) transaction).getPurchaseSubtotal(type);
                } else if (transaction instanceof CategorisedTransaction) {
                    for (Map.Entry<Barcode, List<Product>> entry :
                         ((CategorisedTransaction) transaction).getPurchasesByType().entrySet()) {
                        grossEarningsByType +=
                                ((CategorisedTransaction) transaction).getPurchaseSubtotal(type);
                    }
                } else {
                    for (Product purchase : transaction.getPurchases()) {
                        if (purchase.getBarcode() == type) {
                            grossEarningsByType += purchase.getBasePrice();
                        }
                    }
                }
            }
        }
        return grossEarningsByType;
    }

    /**
     * Calculate the number of transactions made.
     *
     * @return the number of transactions in total
     */
    public int getTotalTransactionsMade() {
        return this.record.size();
    }

    /**
     * Calculate the number of products sold over all transactions.
     *
     * @return the total number of products sold
     */
    public int getTotalProductsSold() {
        int productsSold = 0;

        if (!this.record.isEmpty()) {
            for (Transaction transaction : this.record) {
                productsSold += transaction.getPurchases().size();
            }
        }
        return productsSold;
    }

    /**
     * Calculates the number of products sold of a particular type, over all transactions.
     *
     * @param type - the Barcode for the product of interest
     * @return the total number of products sold, for that particular product
     */
    public int getTotalProductsSold(Barcode type) {
        int totalProductsSold = 0;

        if (!this.record.isEmpty()) {
            for (Transaction transaction : this.record) {

                if (transaction instanceof SpecialSaleTransaction) {
                    totalProductsSold +=
                            ((SpecialSaleTransaction) transaction).getPurchaseQuantity(type);
                } else if (transaction instanceof CategorisedTransaction) {
                    totalProductsSold +=
                            ((CategorisedTransaction) transaction).getPurchaseQuantity(type);
                } else {
                    for (Product purchase : transaction.getPurchases()) {
                        if (purchase.getBarcode() == type) {
                            totalProductsSold++;
                        }
                    }
                }
            }
        }
        return totalProductsSold;
    }

    /**
     * Retrieves the transaction with the highest gross earnings, i.e. reported total.
     * If there are multiple return the one that first was recorded.
     *
     * @return the transaction with the highest gross earnings.
     */
    public Transaction getHighestGrossingTransaction() {
        int highestGrossSales = MIN_VALUE;
        Transaction highestGrossingTransaction = null;
        
        for (Transaction transaction : this.record) {
            if (transaction.getTotal() > highestGrossSales) {
                highestGrossSales = transaction.getTotal();
                highestGrossingTransaction = transaction;
            }
        }

        return highestGrossingTransaction;
    }

    /**
     * Calculates which type of product has had the highest quantity sold overall. If two products
     * have sold the same quantity resulting in a tie, return the one appearing first in the
     * Barcode enum.
     *
     * @return the identifier for the product type of most popular product.
     */
    public Barcode getMostPopularProduct() {

        List<Barcode> barcodes = Arrays.asList(
                Barcode.EGG,
                Barcode.JAM,
                Barcode.MILK,
                Barcode.WOOL);

        Barcode mostPopular = Barcode.EGG;
        int mostPopularCount = MIN_VALUE;

        for (Transaction transaction : this.record) {
            if (transaction instanceof SpecialSaleTransaction) {
                for (Barcode type : barcodes) {
                    int count = ((SpecialSaleTransaction) transaction).getPurchaseQuantity(type);
                    if (count > mostPopularCount) {
                        mostPopular = type;
                        mostPopularCount = count;
                    }
                }
            } else if (transaction instanceof CategorisedTransaction) {
                for (Barcode type : barcodes) {
                    int count = ((CategorisedTransaction) transaction).getPurchaseQuantity(type);
                    if (count > mostPopularCount) {
                        mostPopular = type;
                        mostPopularCount = count;
                    }
                }
            } else {
                for (Barcode type : barcodes) {
                    int typeCount = 0;
                    for (Product product : transaction.getPurchases()) {
                        if (product.getBarcode() == type) {
                            typeCount++;
                        }
                    }
                    if (typeCount > mostPopularCount) {
                        mostPopularCount = typeCount;
                        mostPopular = type;
                    }
                }
            }
        }
        return mostPopular;
    }

    /**
     * Calculate the average amount spent by customers across all transactions.
     * If there have been no products sold, return 0.0d.
     *
     * @return the average amount spent overall, in cents (with decimals, up to 3).
     */
    public double getAverageSpendPerVisit() {
        double totalSpend = 0.0;
        int numberOfTransactions = this.record.size();

        if (!this.record.isEmpty()) {
            for (Transaction transaction : this.record) {
                totalSpend += transaction.getTotal();
            }
        }
        return Math.round((totalSpend / numberOfTransactions) * 100.0) / 100.0;
    }

    /**
     * Calculates the average amount a product has been discounted by, across all sales of that
     * product. If there have been no products sold, return 0.0d.
     *
     * @param type - identifier of the product of interest
     * @return the average discount for the product, int cents (with decimals)
     */
    public double getAverageProductDiscount(Barcode type) {
        double totalDiscount = 0.0;
        int numberOfTransactions = this.record.size();

        if (!this.record.isEmpty()) {
            for (Transaction transaction : this.record) {
                if (transaction instanceof SpecialSaleTransaction) {
                    totalDiscount +=
                            ((SpecialSaleTransaction) transaction).getDiscountAmount(type);
                }
            }
        }
        return Math.round(totalDiscount / numberOfTransactions * 100) / 100.0;
    }
}



























