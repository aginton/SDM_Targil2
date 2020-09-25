package Logic.Order;

import Logic.Customers.Customer;
import Logic.Inventory.ePurchaseCategory;
import Logic.Store.Store;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Order {
    private static int numOfOrders = 0;

    public static void setNumOfOrders(int numOfOrders) {
        Order.numOfOrders = numOfOrders;
    }

    private OrderId orderId;
    private Customer customer;
    private Date orderDate;
    eOrderType orderType;
    private float totalDeliveryCost;

    private Cart bigCart;
    Set<Store> storesBoughtFrom;
    private double numberOfItemsInOrder;
    private double totalOrderCost;
    private int numberItemsByType;

    public Order(Customer customer,
                 Date orderDate, eOrderType orderType,
                 float totalDeliveryCost,
                 HashMap<Store, Cart> mapStoresToCarts
    ) {
        this.customer = customer;
        this.orderDate = orderDate;
        this.orderType = orderType;
        this.totalDeliveryCost = totalDeliveryCost;
        bigCart = new Cart();
        storesBoughtFrom = new HashSet<>();
        mapStoresToCarts.forEach((store, cart) -> {
            storesBoughtFrom.add(store);
            bigCart.addCartToCart(cart);
        });

        totalOrderCost = bigCart.getCartTotalPrice() + totalDeliveryCost;
        numberOfItemsInOrder = calculateNumberOfItemsInOrder(bigCart);
        numberItemsByType = bigCart.getNumberOfTypesOfItemsInCart();

        if (orderType == eOrderType.STATIC_ORDER || orderType == eOrderType.DYNAMIC_ORDER) {
            numOfOrders++;
            this.orderId = new OrderId(numOfOrders, -1);
        }

        if (orderType == eOrderType.SPLITTED_DYNAMIC_ORDER) {
            //id in format: <dynamic-order-id.store-id>
            Iterator<Store> iterator = storesBoughtFrom.iterator();
            int storeId = iterator.next().getStoreId();
            this.orderId = new OrderId(numOfOrders, storeId);
        }
    }

    public double getNumberOfItemsInOrder() {
        return numberOfItemsInOrder;
    }

    private double calculateNumberOfItemsInOrder(Cart cart) {
        double res = 0;
        for (CartItem item : cart.getCart().values()) {
            if (item.getPurchaseCategory() == ePurchaseCategory.QUANTITY)
                res += item.getItemAmount();
            else if (item.getPurchaseCategory() == ePurchaseCategory.WEIGHT)
                res++;
        }
        return res;
    }


    public static int getNumOfOrders() {
        return numOfOrders;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Cart getBigCart() {
        return bigCart;
    }

    public int getNumberItemsByType() {
        return numberItemsByType;
    }

    public Set<Store> getStoresBoughtFrom() {
        return storesBoughtFrom;
    }

    public OrderId getOrderId() {
        return orderId;
    }

    public int getNumberOfStoresInvolved() {
        return storesBoughtFrom.size();
    }

    public eOrderType getOrderType() {
        return orderType;
    }

    public double getCartTotal() {
        double preciseVal = bigCart.getCartTotalPrice();
        BigDecimal bd = new BigDecimal(preciseVal).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    public float getTotalDeliveryCost() {
        return totalDeliveryCost;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Cart getCartForThisOrder() {
        return bigCart;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return orderId == order.orderId &&
                Float.compare(order.totalDeliveryCost, totalDeliveryCost) == 0 &&
                Objects.equals(customer, order.customer) &&
                Objects.equals(orderDate, order.orderDate) &&
                Objects.equals(bigCart, order.bigCart);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderId, customer, orderDate, totalDeliveryCost, bigCart);
    }

    public double getTotalOrderCost() {
        return totalOrderCost;
    }

    public void setTotalOrderCost(double totalOrderCost) {
        this.totalOrderCost = totalOrderCost;
    }

    public String getNamesOfStoresInvolved(){
        StringBuilder sb = new StringBuilder("");
        for (Store store: storesBoughtFrom){
            sb.append(store.getStoreName() + ", ");
        }
        return sb.toString();
    }
}
