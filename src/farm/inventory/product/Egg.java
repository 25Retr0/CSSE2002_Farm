package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

/**
 * A class representing an instance of an egg.
 */
public class Egg extends Product {

    private Barcode barcode;
    private Quality quality;

    private int hash;

    /**
     * Create an egg instance with no additional details.
     * Item quality is not specified, so will default to be REGULAR.
     */
    public Egg() {
        setBarcode(Barcode.EGG);
        setQuality(Quality.REGULAR);
        setHash();
    }

    /**
     * Create an egg instance with a quality value.
     *
     * @param quality - the quality level to assign to this egg.
     */
    public Egg(Quality quality) {
        setBarcode(Barcode.EGG);
        setQuality(quality);
        setHash();
    }
}