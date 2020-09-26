package Logic.Customers;

import Logic.Order.Order;
import Logic.Interfaces.hasLocationInterface;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Customer implements hasLocationInterface {
    private int customerId;
    private String customerName;
    private List<Integer> customerLocation;
    private List<Order> orders;
    private float totalPriceOfAllOrders;
    private float aveOrdersPrice;
    private float totalPriceofAllDeliveries;
    private float aveDeliveryPrice;


    public Customer(int id, String name, int xPosition, int yPosition){
        this.customerId = id;
        this.customerName = name;
        customerLocation = new ArrayList<>();
        customerLocation.add(xPosition);
        customerLocation.add(yPosition);
        orders = new ArrayList<>();
        totalPriceOfAllOrders = 0f;
        aveOrdersPrice = 0f;
        totalPriceofAllDeliveries = 0f;
        aveDeliveryPrice = 0f;

    }

    public float getTotalPriceOfAllOrders() {
        return totalPriceOfAllOrders;
    }

    public float getAveOrdersPrice() {
        return aveOrdersPrice;
    }

    public float getTotalPriceofAllDeliveries() {
        return totalPriceofAllDeliveries;
    }

    public float getAveDeliveryPrice() {
        return aveDeliveryPrice;
    }

    public void updateAveOrdersPrice() {
        this.aveOrdersPrice = this.totalPriceOfAllOrders / this.getOrders().size();
        System.out.println("total price of all orders: " + totalPriceOfAllOrders +
                "ave orders price: " + aveOrdersPrice);
    }

    public void updateAveDeliveryPrice() {
        this.aveDeliveryPrice = this.totalPriceofAllDeliveries / this.getOrders().size();
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public List<Order> getOrders() {
        return orders;
    }

    public void setOrders(List<Order> orders) {
        this.orders = orders;
    }

    @Override
    public List<Integer> getLocation() {
        return customerLocation;
    }

    @Override
    public void setLocation(List<Integer> customerLocation) {
        this.customerLocation = customerLocation;
    }

    @Override
    public int getX() {
        return customerLocation.get(0);
    }

    @Override
    public int getY() {
        return customerLocation.get(1);
    }

    @Override
    public void setX(int x) {
        customerLocation.set(0,x);
    }

    @Override
    public void setY(int y) {
        customerLocation.set(1,y);
    }

    public void addOrder(Order order) {
        this.orders.add(order);
        totalPriceOfAllOrders += order.getCartTotal();
        totalPriceofAllDeliveries += order.getTotalDeliveryCost();
        updateAveOrdersPrice();
        updateAveDeliveryPrice();
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
