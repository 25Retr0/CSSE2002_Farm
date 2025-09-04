package farm.inventory.product;

import farm.inventory.product.data.Barcode;
import farm.inventory.product.data.Quality;

/**
 * A class representing an instance of wool.
 */
public class Wool extends Product {

    private Barcode barcode;
    private Quality quality;

    private int hash;

    /**
     * Create a wool instance with no additional details.
     * Item quality is not specified, so will default to be REGULAR.
     */
    public Wool() {
        setBarcode(Barcode.WOOL);
        setQuality(Quality.REGULAR);
        setHash();
    }

    /**
     * Create a wool instance with a specific quality value.
     *
     * @param quality - the quality level to assign to this wool.
     */
    public Wool(Quality quality) {
        setBarcode(Barcode.WOOL);
        setQuality(quality);
        setHash();
    }
}
