package Logic.Store;

import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.Cart;
import Logic.Order.Order;
import Logic.Order.StoreItem;
import Resources.Schema.JAXBGenerated.SDMSell;
import Resources.Schema.JAXBGenerated.SDMStore;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import javafx.util.Callback;

import java.util.*;

public class Store {

    private IntegerProperty storeId = new SimpleIntegerProperty(this, "storeId",0);
    private StringProperty storeName = new SimpleStringProperty(this, "storeName","");
    private IntegerProperty deliveryPpk = new SimpleIntegerProperty(this, "deliveryPpk",0);
    private FloatProperty totalDeliveryIncome = new SimpleFloatProperty(this, "totalDeliveryIncome", 0f);
    private List<Integer> storeLocation = FXCollections.observableArrayList();

//    private final ObservableList<InventoryItem> inventoryItems = FXCollections.observableArrayList(InventoryItem.extractor);
    private List<InventoryItem> inventoryItems = FXCollections.observableArrayList();
//    private ObservableList<StoreItem> storeItems = FXCollections.observableArrayList();
    private List<StoreItem> storeItems = FXCollections.observableArrayList();

    public List<StoreItem> getStoreItems() {
        return storeItems;
    }

    public void setStoreItems(List<StoreItem> storeItems) {
        this.storeItems = storeItems;
    }

    //    private int deliveryPpk;
    private HashMap<Integer, Float> mapItemsToAmountSold;

    private HashMap<Integer, Integer> mapItemToPrices;
    private List<Order> orders;


    public Store(){}

    public Store(SDMStore store) {
        setStoreId(store.getId());
        setStoreName(store.getName());
        setDeliveryPpk(store.getDeliveryPpk());

        storeLocation.add(store.getLocation().getX());
        storeLocation.add(store.getLocation().getY());

        this.mapItemsToAmountSold = new HashMap<>();
        this.mapItemToPrices = new HashMap<>();

        for (SDMSell sell : store.getSDMPrices().getSDMSell()) {
            mapItemToPrices.put(sell.getItemId(), sell.getPrice());
            mapItemsToAmountSold.put(sell.getItemId(), (float) 0);
        }

        this.orders = new ArrayList<>();
    }

    public Store(SDMStore store, Inventory inventory) {
        setStoreId(store.getId());
        setStoreName(store.getName());
        setDeliveryPpk(store.getDeliveryPpk());

        storeLocation.add(store.getLocation().getX());
        storeLocation.add(store.getLocation().getY());

        for (SDMSell sell: store.getSDMPrices().getSDMSell()){
            InventoryItem itemToAdd = inventory.getListInventoryItems().stream().filter(i->i.getInventoryItemId() == sell.getItemId()).findFirst().get();
            StoreItem storeItem = new StoreItem(itemToAdd, sell.getPrice());
            storeItems.add(storeItem);
        }


    }

    //Getter and Setter
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //storeID
    public int getStoreId() {
        return storeId.get();
    }
    public IntegerProperty storeIdProperty() {
        return storeId;
    }
    public void setStoreId(int storeId) {
        this.storeId.set(storeId);
    }

    //storeName
    public StringProperty storeNameProperty() {
        return storeName;
    }
    public void setStoreName(String storeName) {
        this.storeName.set(storeName);
    }

    public String getStoreName() {
        return storeName.get();
    }

    //storePPK
    public int getDeliveryPpk() {
        return deliveryPpk.get();
    }

    public IntegerProperty deliveryPpkProperty() {
        return deliveryPpk;
    }

    public void setDeliveryPpk(int deliveryPpk) {
        this.deliveryPpk.set(deliveryPpk);
    }

    //totalDeliveryIncome
    public float getTotalDeliveryIncome() {
        return totalDeliveryIncome.get();
    }
    public FloatProperty totalDeliveryIncomeProperty() {
        return totalDeliveryIncome;
    }
    public void setTotalDeliveryIncome(float totalDeliveryIncome) {
        this.totalDeliveryIncome.set(totalDeliveryIncome);
    }

    //storeLocation
    public List<Integer> getStoreLocation() {
        return storeLocation;
    }

    public void setStoreLocation(ObservableList<Integer> storeLocation) {
        this.storeLocation = storeLocation;
    }

    //inventoryItems
    public List<InventoryItem> getInventoryItems() {
        return inventoryItems;
    }

    public void setInventoryItems(List<InventoryItem> inventoryItems) {
        this.inventoryItems = inventoryItems;
    }

    public void setStoreInventory(ObservableList<InventoryItem> i_inventoryItems) {
        if (this.inventoryItems != null)
            inventoryItems = FXCollections.observableArrayList();
        this.inventoryItems = i_inventoryItems;
    }



    public HashMap<Integer, Float> getMapItemsToAmountSold() {
        if (mapItemsToAmountSold == null)
            mapItemsToAmountSold = new HashMap<>();
        return mapItemsToAmountSold;
    }

    public HashMap<Integer, Integer> getMapItemToPrices() {
        if (mapItemToPrices == null)
            mapItemToPrices = new HashMap<>();
        return mapItemToPrices;
    }







    public List<Order> getOrders() {
        if (orders == null)
            orders = new ArrayList<Order>();

        return orders;
    }



    public void addOrder(Order order) {
        orders.add(order);
        setTotalDeliveryIncome(order.getDeliveryCost()+this.getTotalDeliveryIncome());
        updateStoreInventory(order.getCartForThisOrder());
    }

    private void updateStoreInventory(Cart cart) {
        cart.getCart().forEach((k, v) -> {
            float amountInCart = v.getItemAmount();
            float oldAmountSold = mapItemsToAmountSold.get(k);
            mapItemsToAmountSold.put(k, amountInCart + oldAmountSold);
        });
    }

    public InventoryItem getInventoryItemById(int priceID) {
        for (InventoryItem item : inventoryItems) {
            if (item.getInventoryItemId() == priceID)
                return item;
        }
        return null;
    }

    public void addItemToStoreInventory(InventoryItem item, int price){
        if (inventoryItems.contains(item)){
            return;
        }
        inventoryItems.add(item);
        Collections.sort(inventoryItems);
        mapItemsToAmountSold.put(item.getInventoryItemId(), 0f);
        mapItemToPrices.put(item.getInventoryItemId(), price);
    }

    public float getDeliveryCost(List<Integer> userLocation) {
        return getDeliveryPpk()* getDistance(userLocation);
    }

    public float getDistance(List<Integer> userLocation) {
        if (userLocation.size() != 2 || storeLocation.size() != 2){
            System.out.println("Error: Input lists must  contain 2 points!");
            return -1;
        }
        int xDelta = userLocation.get(0) -storeLocation.get(0);
        int yDelta = userLocation.get(1) -storeLocation.get(1);
        return (float) Math.sqrt((xDelta*xDelta)+(yDelta*yDelta));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Store store = (Store) o;

        return storeId == store.storeId &&
                deliveryPpk == store.deliveryPpk &&
                storeName.equals(store.storeName) &&
                Objects.equals(storeLocation, store.storeLocation);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storeId, storeName, storeLocation, deliveryPpk);
    }

    @Override
    public String toString() {
        return storeId.get() + " " + storeName.get();
    }

    public static Callback<Store, Observable[]> extractor = p -> new Observable[]
            {p.storeIdProperty(), p.storeNameProperty()};




}
