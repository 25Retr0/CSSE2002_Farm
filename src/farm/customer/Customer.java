package farm.customer;

import farm.sales.Cart;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;


/**
 * A customer who interacts with the farmer's business.
 * Keeps a record of the customer's information.
 */
public class Customer {

    private String name;
    private int phoneNumber;
    private String address;
    private Cart cart;

    private int hash;

    /**
     * Create a new customer instance with their details.
     *
     * @param name - The name of the customer
     * @param phoneNumber - The customer's phone number
     * @param address - The address of the customer
     *
     * @ensures - That the name and address is non-empty.
     * @ensures - That the phone number is a positive number.
     * @ensures - That the name and address are stripped of trailing whitespaces.
     */
    public Customer(String name, int phoneNumber, String address) {

        this.setName(name);
        this.setPhoneNumber(phoneNumber);
        this.address = address;

        this.cart = new Cart();

        this.createHashCode();
    }

    /**
     * Checks if the given name is non-empty.
     *
     * @param name - The name to be checked
     * @return true if the name is not empty, false if empty
     */
    private boolean checkNonEmptyName(String name) {
        return !name.isEmpty();
    }

    /**
     * Strips the given string of preceding and trailing whitespace
     *
     * @param str - the string to be striped.
     * @return the string with trailing whitespace removed
     */
    private String stripTrailingSpace(String str) {
        return str.stripTrailing();
    }

    /**
     * Check if the given phonenumber of positive
     *
     * @param phoneNumber - the phone number to be checked
     * @return true if positive number, false if not.
     */
    private boolean checkPositivePhone(int phoneNumber) {
        return (phoneNumber > 0);
    }

    /**
     * Retrieve the name of the customer.
     *
     * @return The customer's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Update the current name of the customer with a new one.
     *
     * @param newName - The new name to override the current name.
     */
    public void setName(String newName) {
        if (checkNonEmptyName(newName)) {
            this.name = stripTrailingSpace(newName);
        }
    }

    /**
     * Retrieve the phone number of the customer.
     *
     * @return the customer's phone number.
     */
    public int getPhoneNumber() {
        return this.phoneNumber;
    }

    /**
     * Set the current phone number of the customer to be newPhone
     *
     * @param newPhone - The phone number to override the current phone number.
     */
    public void setPhoneNumber(int newPhone) {

        if (checkPositivePhone(newPhone)) {
            this.phoneNumber = newPhone;
        }
    }

    /**
     * Retrieve the address of the customer.
     *
     * @return The customer address.
     */
    public String getAddress() {
        return this.address;
    }

    /**
     * Set the current address of the customer to be newAddress.
     *
     * @param newAddress - The address to override the current address.
     */
    public void setAddress(String newAddress) {
        if (checkNonEmptyName(newAddress)) {
            this.address = stripTrailingSpace(newAddress);
        }
    }

    /**
     * Retrieves the customers cart.
     *
     * @return Their shopping cart.
     */
    public Cart getCart() {
        return this.cart;
    }

    /**
     * Returns a string representation of this customer class. The representation contains the name
     * of the customer, followed by their phone number and address separated by ' | '.
     *
     * @return The formatted string representation of the customer.
     */
    @Override
    public String toString() {
        return ("Name: " + this.getName() + " | " + "Phone Number: "
                + this.getPhoneNumber() + " | " + "Address: " + this.getAddress());
    }

    /**
     * Creates a unique hashcode for the customer that respects the equals(Object) method.
     */
    private void createHashCode() {
        String code = "";
        String name = this.getName();
        String phone = String.valueOf(this.getPhoneNumber());

        for (int i = 0; i < name.length(); i++) {
            if (Character.isDigit(name.charAt(i)) || Character.isLetter(name.charAt(i))) {
                code += Character.getNumericValue(name.charAt(i));
            }
        }

        for (int i = 0; i < phone.length(); i++) {
            code += Character.getNumericValue(phone.charAt(i));
        }

        String finalCode = "";

        if (code.length() >= 10) {
            for (int i = 0; i < 9; i++) {
                char numAtIndex = code.charAt(i);
                finalCode += numAtIndex;
            }
        } else {
            for (int i = 0; i < 9 - code.length(); i++) {
                char numAtIndex = code.charAt(i);
                finalCode += numAtIndex;
            }
        }
        this.hash = Integer.valueOf(finalCode);
    }

    /**
     * A hashcode method that respects the equals(Object) method.
     *
     * @return An appropriate hashcode value for this instance.
     */
    @Override
    public int hashCode() {
        return this.hash;
    }

    /**
     * Determines whether the provided object is equal to this customer instance.
     *
     * For customers, equality is defined by having the same phone number and name; addresses are not considered.
     * Note: that customer names are case sensitive.
     *
     * @param obj - The object with which to compare
     * @return true if the other object is a customer with the same phone number and name as the current customer.
     */
    public boolean equals(Object obj) {
        if (obj instanceof Customer) {
            String name = ((Customer) obj).getName();
            int phoneNum = ((Customer) obj).getPhoneNumber();

            if (Objects.equals(name, this.getName()) && phoneNum == this.getPhoneNumber()) {
                return true;
            }
        }
        return false;
    }
}
