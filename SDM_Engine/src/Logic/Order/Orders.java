package Logic.Order;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.List;

public class Orders {

    private List<Order> orders;
    //private ObservableList<Order> ordersObservableList;
    public List<OrderChangeListener> listeners;

    public Orders() {
        orders = new ArrayList<>();
        //ordersObservableList = FXCollections.observableArrayList();
        listeners = new ArrayList<>();
    }

    public void addOrdersChangeListener(OrderChangeListener listener){
        listeners.add(listener);
    }

    public List<Order> getOrders(){
        if (orders == null){
            orders = new ArrayList<Order>();
        }
        return this.orders;
    }

    public void addOrder(Order order){
        orders.add(order);
        //ordersObservableList.add(order);
        notifyListenersOrderWasAdded(order);
    }

    private void notifyListenersOrderWasAdded(Order order) {
        for (OrderChangeListener listener: listeners){
            listener.orderWasAdded(order);
        }
    }

//    public ObservableList<Order> getOrdersObservableList() {
//
//        return ordersObservableList;
//    }
//
//    public void setOrdersObservableList(ObservableList<Order> ordersObservableList) {
//        this.ordersObservableList = ordersObservableList;
//    }
}
