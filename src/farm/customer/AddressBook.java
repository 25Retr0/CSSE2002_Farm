package farm.customer;

import farm.core.CustomerNotFoundException;
import farm.core.DuplicateCustomerException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * The address book where the farmer stores their customers' details.
 * Keeps track of all the customers that come and visit the Farm.
 */
public class AddressBook {

    private List<Customer> customers;

    /**
     * The address book where the farmer stores their customers' details.
     * Keeps track of all the customers that come and visit the Farm.
     */
    public AddressBook() {
        this.customers = new ArrayList<>();
    }

    /**
     * Add a new customer to the address book.  If the address book already contains the customer to be added,
     * throws a duplicate customer exception with a message containing the string representation of the customer
     * identified as a duplicate.
     *
     * @param customer - The customer to be added.
     * @throws DuplicateCustomerException if the customer already exists in the address book. Contains a message
     * of the Customer's representation
     * @ensures - The address book contains no duplicate customers.
     */
    public void addCustomer(Customer customer) throws DuplicateCustomerException {

        if (this.containsCustomer(customer)) {
            throw new DuplicateCustomerException(customer.toString());
        } else {
            this.customers.add(customer);
        }

    }

    /**
     * Retrieve all customer records stored in the address book.
     *
     * @return a list of all customers in the address book
     * @ensures - the returned list is a shallow copy and cannot modify the original address book
     */
    public List<Customer> getAllRecords() {
        return new ArrayList<>(this.customers);
    }

    /**
     * Check to see if a customer is already in the address book.
     *
     * @param customer - The customer to check
     * @return true iff the customer already exists, else false
     */
    public boolean containsCustomer(Customer customer) {
        return customers.contains(customer);
    }

    /**
     * Lookup a customer in address book, if they exist using their details.
     *
     * @param name - The name of the customer to lookup.
     * @param phoneNumber - The phone number of the customer.
     * @return The customer iff they exist in the address book
     * @throws CustomerNotFoundException if there is no customer matching the information in the address book
     */
    public Customer getCustomer(String name, int phoneNumber) throws CustomerNotFoundException {

        for (Customer customer : this.customers) {
            String custName = customer.getName();
            int custPhone = customer.getPhoneNumber();

            if (Objects.equals(custName, name) && custPhone == phoneNumber) {
                return customer;
            }
        }
        throw new CustomerNotFoundException();
    }
}
