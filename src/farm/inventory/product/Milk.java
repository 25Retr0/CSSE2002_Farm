package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

/**
 * A class representing an instance of milk.
 */
public class Milk extends Product {

    private Barcode barcode;
    private Quality quality;

    private int hash;

    /**
     * Create a milk instance with no additional details.
     * Item quality is not specified, so will default to be REGULAR.
     */
    public Milk() {
        setBarcode(Barcode.MILK);
        setQuality(Quality.REGULAR);
        setHash();
    }

    /**
     * Create a milk instance with a quality value.
     *
     * @param quality - the quality level to assign to this milk.
     */
    public Milk(Quality quality) {
        setBarcode(Barcode.MILK);
        setQuality(quality);
        setHash();
    }
}
