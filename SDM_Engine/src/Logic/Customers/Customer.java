package Logic.Customers;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer {
    private int customerId;
    private String customerName;
    private List<Integer> customerLocation;

    public Customer(int id, String name, int xPosition, int yPosition){
        this.customerId = id;
        this.customerName = name;
        customerLocation = new ArrayList<>();
        customerLocation.add(xPosition);
        customerLocation.add(yPosition);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Customer customer = (Customer) o;
        return customerId == customer.customerId &&
                customerName.equals(customer.customerName) &&
                customerLocation.equals(customer.customerLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, customerName, customerLocation);
    }

    @Override
    public String toString() {
        return "Customer{" +
                "customerId=" + customerId +
                ", customerName='" + customerName + '\'' +
                ", customerLocation=" + customerLocation +
                '}';
    }
}
