package Logic.Customers;

import Logic.Interfaces.customersChangeInterface;
import Logic.Interfaces.inventoryChangeInterface;
import Logic.Order.Order;

import java.util.ArrayList;
import java.util.List;

public class Customers {

    private List<Customer> customers;
    private List<customersChangeInterface> listeners;

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

    public void addListener(customersChangeInterface listener){
        listeners.add(listener);
    }

    public void addOrderToCustomer(Order order, Customer customer) {

        customer.addOrder(order);
        notifyListeners();
    }

    public void notifyListeners(){
        for (customersChangeInterface listener: listeners)
            listener.onCustomersChange();
    }
}
