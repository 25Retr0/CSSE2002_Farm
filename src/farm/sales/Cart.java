package farm.sales;

import java.util.ArrayList;
import java.util.List;
import farm.inventory.product.*;

/**
 * A shopping cart that stores the customer products until they check out.
 */
public class Cart {

    private List<Product> shoppingCart;

    /**
     * A shopping cart that stores the customer products until they check out.
     */
    public Cart() {
        this.shoppingCart = new ArrayList<>();
    }

    /**
     * Adds a given product to the shopping cart.
     *
     * @param product - the product to add.
     */
    public void addProduct(Product product) {
        this.shoppingCart.add(product);
    }

    /**
     * Empty out the shopping cart.
     */
    public void setEmpty() {
        this.shoppingCart.clear();
    }

    /**
     * Returns if the cart is empty.
     *
     * @return true iff there is nothing in the art, else false.
     */
    public boolean isEmpty() {
        return this.shoppingCart.isEmpty();
    }

    /**
     * Retrieves all the products in the Cart in the order they were added.
     *
     * @return a list of all products in the cart
     * @ensures the returned list is a shallow copy and cannot modify the original cart
     */
    public List<Product> getContents() {
        return List.copyOf(this.shoppingCart);
    }
}
