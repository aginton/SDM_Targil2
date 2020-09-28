package Logic.Customers;

import Logic.Interfaces.customersChangeListener;
import Logic.Order.Order;

import java.util.ArrayList;
import java.util.List;

public class Customers {

    private List<Customer> customers;
    private List<customersChangeListener> listeners;

    public Customers(){

        customers = new ArrayList<>();
        listeners = new ArrayList<>();
    }

    public List<Customer> getListOfCustomers() {
        return customers;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public void add(Customer newCustomer) {
        customers.add(newCustomer);
    }

    public void addListener(customersChangeListener listener){
        listeners.add(listener);
    }

    public void addOrderToCustomer(Order order, Customer customer) {

        System.out.println("about to add order to customer: " + customer.getCustomerName());
        customer.addOrder(order);
        notifyListeners();
    }

    public void notifyListeners(){
        for (customersChangeListener listener: listeners)
            listener.onCustomersChange();
    }
}
