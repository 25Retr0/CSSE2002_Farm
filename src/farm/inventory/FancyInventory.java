package farm.inventory;

import farm.core.FailedTransactionException;
import farm.core.InvalidStockRequestException;
import farm.inventory.product.*;
import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

import java.util.ArrayList;
import java.util.List;

/**
 * A fancy inventory which stores products in stacks, enabling quantity information.
 */
public class FancyInventory implements Inventory {

    private List<Product> stock;

    /**
     * A fancy inventory which stores products in stacks, enabling quantity information.
     */
    public FancyInventory() {
        stock = new ArrayList<>();
    }

    /**
     * Adds a new product with corresponding barcode to the inventory
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
     * Adds the specified quantity of the product with corresponding barcode to the inventory,
     * provided that the implementing inventory supports adding multiple products at once
     *
     * @param barcode  - the barcode of the product to add
     * @param quality  - the quality of the added product
     * @param quantity - the amount of product to add
     * @throws InvalidStockRequestException if implementing inventory does not support adding
    *          multiple products at once - reporting that the inventory is not fancy enough
     */
    @Override
    public void addProduct(Barcode barcode, Quality quality, int quantity)
            throws InvalidStockRequestException {
        for (int i = 0; i < quantity; i++) {
            addProduct(barcode, quality);
        }
    }

    /**
     * Determines if a product exists in the inventory with the given barcode.
     *
     * @param barcode - the barcode of the product to check
     * @return true iff a product exists, else false
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
     * Removes the first product with corresponding barcode from the inventory
     *
     * @param barcode - the barcode of the product to be removed
     * @return A list containing the removed product if it exists, else and empty list.
     */
    @Override
    public List<Product> removeProduct(Barcode barcode) {
        List<Product> removedStock = new ArrayList<>();

        if (!stock.isEmpty() && existsProduct(barcode)) {
            List<Product> products = getProductsByType(barcode);
            Product highestQualProd = products.getFirst();
            stock.remove(highestQualProd);
            removedStock.add(highestQualProd);
        }
        return removedStock;
    }


    /**
     * Get all the products of a chosen type, sorted by highest quality to lowest.
     *
     * @param barcode - the barcode of the product
     * @return the products in a list ordered by highest quality
     */
    private List<Product> getProductsByType(Barcode barcode) {

        List<Product> allProducts = getAllProducts();
        List<Product> productOfType = new ArrayList<>();
        List<Product> sortedProducts = new ArrayList<>();

        for (Product product : allProducts) {
            if (product.getBarcode() == barcode) {
                productOfType.add(product);
            }
        }

        // loop 4 times. once for each quality possible. and add if found.
        for (int i = 1; i <= 4; i++) {
            for (Product product : productOfType) {
                if (i == 1) {
                    if (product.getQuality() == Quality.IRIDIUM) {
                        sortedProducts.add(product);
                    }
                } else if (i == 2) {
                    if (product.getQuality() == Quality.GOLD) {
                        sortedProducts.add(product);
                    }
                } else if (i == 3) {
                    if (product.getQuality() == Quality.SILVER) {
                        sortedProducts.add(product);
                    }
                } else {
                    if (product.getQuality() == Quality.REGULAR) {
                        sortedProducts.add(product);
                    }
                }
            }
        }
        return sortedProducts;
    }

    /**
     * Removes the given number of products with corresponding barcode from the inventory,
     * provided that the implementing inventory supports removing multiple products at once.
     *
     * @param barcode  - The barcode of the product to be removed.
     * @param quantity - The total amount of the product to remove from the inventory.
     * @return A list containing the removed product if it exists, else an empty list.
     * @throws FailedTransactionException if implementing inventory does not support removing
     *         multiple products at once - reporting that the inventory is not fancy enough
     */
    @Override
    public List<Product> removeProduct(Barcode barcode, int quantity)
            throws FailedTransactionException {

        List<Product> removedStock = new ArrayList<>();

        /* get all the products of the barcode into a list.
        Then loop through and sort the list. Get first everytime then
`       */

        if (this instanceof FancyInventory) {
            for (int i = 0; i < quantity; i++) {
                List<Product> products = getProductsByType(barcode);
                if (!products.isEmpty()) {
                    List<Product> removedProduct = removeProduct(barcode);
                    removedStock.add(removedProduct.getFirst());
                }
            }

            return removedStock;
        }
        throw new FailedTransactionException("Inventory is not fancy enough.");
    }

    /**
     * Retrieves the full stock currently held in the inventory. The returned list must be
     * grouped by product type as per the order defined in Barcode.
     *
     * @return A list containing all products currently stored in the inventory
     */
    @Override
    public List<Product> getAllProducts() {
        List<Product> productStock = new ArrayList<>();

        for (Barcode barcode : Barcode.values()) {
            for (Product product : this.stock) {
                if (barcode == product.getBarcode()) {
                    productStock.add(product);
                }
            }
        }
        return productStock;
    }

    /**
     * Get the quantity of a specific product in the inventory.
     *
     * @param barcode - the barcode of the product
     * @return the amount of the corresponding product currently in the inventory
     */
    public int getStockedQuantity(Barcode barcode) {
        int count = 0;

        for (Product product : getAllProducts()) {
            if (product.getBarcode() == barcode) {
                count++;
            }
        }
        return count;
    }
}
