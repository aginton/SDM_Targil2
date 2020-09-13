package Logic.Customers;

import java.util.ArrayList;
import java.util.List;

public class Customers {
    private List<Customer> customers;

    public Customers(){
        customers = new ArrayList<>();
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public void add(Customer newCustomer) {
        customers.add(newCustomer);
    }
}
