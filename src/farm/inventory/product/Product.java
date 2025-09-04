package farm.inventory.product;

import farm.inventory.product.data.*;

/**
 * An abstract class representing an instance of a product.
 * Each product is a single instance of a specific item.
 */
public abstract class Product {

    private Barcode barcode;
    private Quality quality;

    private int hash;

    /**
     * Make gradescope happy. Basic method for "creating" an instance of a product without a quality given.
     */
    protected Product() {

    }

    /**
     * Basic method for "creating" an instance of a product with a quality given.
     * @param quality - the quality level to assign to this product.
     */
    protected Product(Quality quality) {
        setQuality(quality);
    }

    /**
     * Accessor method for the product's identifier.
     *
     * @return the identifying Barcode for this product.
     */
    public Barcode getBarcode() {
        return this.barcode;
    }

    /**
     * Retrieve the products base sale price.
     *
     * @return the price of the product. In cents.
     */
    public int getBasePrice() {
        return this.barcode.getBasePrice();
    }

    /**
     * Retrieve the product's display name, for visual/textual representation.
     *
     * @return the product's display name.
     */
    public String getDisplayName() {
        return this.barcode.getDisplayName();
    }

    /**
     * Retrieve the product's quality.
     *
     * @return the quality level for this product.
     */
    public Quality getQuality() {
        return this.quality;
    }

    /**
     * Returns a string representation of this product class. The representation contains the
     * display name of the product, followed by its base price and quality.
     * It is in the form 'name': 'price'c 'quality'
     *
     * @return The formatted string representation of the product.
     */
    public String toString() {
        return (this.getDisplayName() + ": " + this.getBasePrice()
                + "c" + " *" + this.quality + "*");
    }

    /**
     * If two instances of product are equal to each other.
     * Equality is defined by having the same barcode, and quality.
     *
     * @param obj - The object with which to compare
     * @return true iff the other object is a product with the same barcode, and quality as the current product.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Product) {

            Barcode barcode1 = ((Product) obj).getBarcode();
            Quality quality1 = ((Product) obj).getQuality();

            if (barcode1 == this.getBarcode() && quality1 == this.getQuality()) {
                return true;
            }
        }
        return false;
    }

    /**
     * A hashcode method that respects the equals(Object) method.
     *
     * @return An appropriate hashcode value for this instance.
     */
    public int hashCode() {
        return this.hash;
    }

    /**
     * Creates a unique hashcode for a product that respects the equals(Object) method.
     */
    protected void createHashCode() {

        String code = "";
        String barcodeString = String.valueOf(this.getBarcode());

        for (int i = 0; i < barcodeString.length(); i++) {
            char ch = barcodeString.charAt(i);
            if (Character.getNumericValue(ch) > 20) {
                code += Character.getNumericValue(ch) - 20;
            } else if (Character.getNumericValue(ch) > 10) {
                code += Character.getNumericValue(ch) - 10;
            } else {
                code += Character.getNumericValue(ch);
            }
        }
        String qualityString = String.valueOf(this.getQuality());

        for (int i = 0; i < qualityString.length(); i++) {
            char ch = qualityString.charAt(i);
            if (Character.getNumericValue(ch) > 10) {
                code += Character.getNumericValue(ch) - 10;
            } else {
                code += Character.getNumericValue(ch);
            }
        }

        String finalCode = "";

        for (int i = 0; i < 9; i++) {
            char numAtIndex = code.charAt(i);
            finalCode += numAtIndex;
        }

        this.hash = Integer.valueOf(finalCode);
    }

    /**
     * Set the barcode of the product.
     *
     * @param barcode - the barcode to update to
     */
    protected void setBarcode(Barcode barcode) {
        this.barcode = barcode;
    }

    /**
     * Set the quality of the product
     *
     * @param quality - the quality to update to
     */
    protected void setQuality(Quality quality) {
        this.quality = quality;
    }

    /**
     * Sets the hashcode of the product
     */
    protected void setHash() {
        this.createHashCode();
    }

}

