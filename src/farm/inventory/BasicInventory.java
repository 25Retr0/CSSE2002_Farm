package farm.inventory;

import farm.core.FailedTransactionException;
import farm.core.InvalidStockRequestException;
import farm.inventory.product.*;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

import java.util.ArrayList;
import java.util.List;

/**
 * A very basic inventory that both stores and handles products individually.
 * Only supports operation on single Products at a time.
 */
public class BasicInventory implements Inventory {

    private List<Product> stock;

    /**
     * A very basic inventory that both stores and handles products individually.
     * Only supports operation on single Products at a time.
     */
    public BasicInventory() {
        stock = new ArrayList<>();
    }

    /**
     * Adds a new product with corresponding barcode to the inventory.
     *
     * @param barcode - the barcode of the product to add
     * @param quality - the quality of added product
     */
    @Override
    public void addProduct(Barcode barcode, Quality quality) {
        if (barcode == Barcode.EGG) {
            stock.add(new Egg(quality));
        } else if (barcode == Barcode.JAM) {
            stock.add(new Jam(quality));
        } else if (barcode == Barcode.MILK) {
            stock.add(new Milk(quality));
        } else if (barcode == Barcode.WOOL) {
            stock.add(new Wool(quality));
        }
    }

    /**
     * Throws an InvalidStockRequestException with the message:
     *  "Current inventory is not fancy enough. Please supply products one at a time."
     * @param barcode - the barcode of the product to add
     * @param quality - the quality of the added product
     * @param quantity - the amount of product to add
     * @throws InvalidStockRequestException - always, since Basic inventories
     * never support quantities > 1.
     */
    @Override
    public void addProduct(Barcode barcode, Quality quality, int quantity)
            throws InvalidStockRequestException {
        throw new InvalidStockRequestException(
                "Current inventory is not fancy enough. Please supply products one at a time.");
    }

    /**
     * Determines if a product exists in the inventory with the given barcode.
     *
     * @param barcode - the barcode of the product to check
     * @return true iff a product exists, else false.
     */
    @Override
    public boolean existsProduct(Barcode barcode) {
        if (!stock.isEmpty()) {
            for (Product product : stock) {
                if (product.getBarcode() == barcode) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Removes the first product with corresponding barcode from the inventory.
     *
     * @param barcode - the barcode of the product to be removed
     * @return A list containing the removed product if it exists, else an empty list.
     */
    @Override
    public List<Product> removeProduct(Barcode barcode) {
        List<Product> removedStock = new ArrayList<>();

        if (!stock.isEmpty() && existsProduct(barcode)) {
            for (Product product : stock) {
                if (product.getBarcode() == barcode) {
                    stock.remove(product);
                    removedStock.add(product);
                    return removedStock;
                }
            }
        }
        return removedStock;
    }

    /**
     * Throws an FailedTransactionException with the message:
     *  "Current inventory is not fancy enough. Please purchase products one at a time."
     *
     * @param barcode - The barcode of the product to be removed.
     * @param quantity - The total amount of the product to remove from the inventory.
     * @return A list containing the removed product if it exists, else an empty list.
     * @throws FailedTransactionException - always, since Basic inventories
     * never support quantities > 1.
     */
    @Override
    public List<Product> removeProduct(Barcode barcode, int quantity)
            throws FailedTransactionException {
        throw new FailedTransactionException(
                "Current inventory is not fancy enough. Please purchase products one at a time.");
    }

    /**
     * Retrieves the full stock currently held in the inventory.
     *
     * @return A list containing all products currently stored in the inventory.
     */
    @Override
    public List<Product> getAllProducts() {
        return stock;
    }
}
