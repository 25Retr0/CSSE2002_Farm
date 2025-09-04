package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

/**
 * A class representing an instance of jam.
 */
public class Jam extends Product {

    private Barcode barcode;
    private Quality quality;

    private int hash;

    /**
     * Create a jam instance with no additional details.
     * Item quality is not specified, so will default to be REGULAR.
     */
    public Jam() {
        setBarcode(Barcode.JAM);
        setQuality(Quality.REGULAR);
        setHash();
    }

    /**
     * Create a jam instance with a quality value.
     *
     * @param quality - the quality level to assign to this jam.
     */
    public Jam(Quality quality) {
        setBarcode(Barcode.JAM);
        setQuality(quality);
        setHash();
    }
}