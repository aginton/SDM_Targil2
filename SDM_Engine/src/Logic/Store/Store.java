package Logic.Store;

import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.Cart;
import Logic.Order.Order;
import Logic.Order.StoreItem;
import Logic.Interfaces.hasLocationInterface;
import Logic.SDM.SDMFileVerifier;
import Logic.SDM.SDMManager;
import Resources.Schema.JAXBGenerated.SDMDiscount;
import Resources.Schema.JAXBGenerated.SDMSell;
import Resources.Schema.JAXBGenerated.SDMStore;
import javafx.beans.Observable;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.util.Callback;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;

public class Store implements hasLocationInterface {

    //Store fields/Properties
    private IntegerProperty storeId = new SimpleIntegerProperty(this, "storeId",0);
    private StringProperty storeName = new SimpleStringProperty(this, "storeName","");
    private IntegerProperty deliveryPpk = new SimpleIntegerProperty(this, "deliveryPpk",0);
    private FloatProperty totalDeliveryIncome = new SimpleFloatProperty(this, "totalDeliveryIncome", 0f);
    private List<Integer> storeLocation = FXCollections.observableArrayList();

    private List<StoreChangeListener> listeners;

    private List<InventoryItem> inventoryItems = FXCollections.observableArrayList();
    private List<StoreItem> storeItems = FXCollections.observableArrayList();
    private HashMap<Integer, Double> mapItemsToAmountSold;

    private HashMap<Integer, Integer> mapItemToPrices;
    private List<Order> storeOrders;
    private List<StoreDiscount> storeDiscounts;

    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //Constructor
    public Store(){}

//    public Store(SDMStore store) {
//        setStoreId(store.getId());
//        setStoreName(store.getName());
//        setDeliveryPpk(store.getDeliveryPpk());
//        listeners = new ArrayList<>();
//        storeDiscounts = new ArrayList<>();
//        this.storeOrders = new ArrayList<>();
//
//
//        storeLocation.add(store.getLocation().getX());
//        storeLocation.add(store.getLocation().getY());
//
//        this.mapItemsToAmountSold = new HashMap<>();
//        this.mapItemToPrices = new HashMap<>();
//
//        for (SDMSell sell : store.getSDMPrices().getSDMSell()) {
//            mapItemToPrices.put(sell.getItemId(), sell.getPrice());
//            mapItemsToAmountSold.put(sell.getItemId(), (float) 0);
//
//        }
//    }

    public Store(SDMStore store, SDMManager sdmManager){
        setStoreId(store.getId());
        setStoreName(store.getName());
        setDeliveryPpk(store.getDeliveryPpk());
        listeners = new ArrayList<>();
        storeDiscounts = new ArrayList<>();
        this.storeOrders = new ArrayList<>();
        this.mapItemsToAmountSold = new HashMap<Integer, Double>();
        this.mapItemToPrices = new HashMap<>();
        storeLocation.add(store.getLocation().getX());
        storeLocation.add(store.getLocation().getY());

        for (SDMSell sell : store.getSDMPrices().getSDMSell()) {
            mapItemToPrices.put(sell.getItemId(), sell.getPrice());
            mapItemsToAmountSold.put(sell.getItemId(), (double) 0);

        }

        for (SDMSell sell: store.getSDMPrices().getSDMSell()){
            InventoryItem itemToAdd = sdmManager.getInventory().getListInventoryItems().stream().filter(i->i.getItemId()==sell.getItemId()).findFirst().get();
            if (itemToAdd != null){
                inventoryItems.add(itemToAdd);

                StoreItem storeItem = new StoreItem(itemToAdd, sell.getPrice());
                storeItems.add(storeItem);
            }
        }
        if (SDMFileVerifier.checkIfStoreHasDiscounts(store)){
            for (SDMDiscount sdmDiscount: store.getSDMDiscounts().getSDMDiscount()){
                storeDiscounts.add(new StoreDiscount(sdmDiscount, this));
            }
        }
    }

//    public Store(SDMStore store, Inventory inventory) {
//        this.mapItemsToAmountSold = new HashMap<Integer, Double>();
//        this.mapItemToPrices = new HashMap<>();
//        this.storeOrders = new ArrayList<>();
//
//        setStoreId(store.getId());
//        setStoreName(store.getName());
//        setDeliveryPpk(store.getDeliveryPpk());
//
//        storeLocation.add(store.getLocation().getX());
//        storeLocation.add(store.getLocation().getY());
//
//        for (SDMSell sell: store.getSDMPrices().getSDMSell()){
//            mapItemToPrices.put(sell.getItemId(), sell.getPrice());
//            mapItemsToAmountSold.put(sell.getItemId(), (double) 0);
//
//            InventoryItem itemToAdd = inventory.getListInventoryItems().stream().filter(i->i.getItemId() == sell.getItemId()).findFirst().get();
//            StoreItem storeItem = new StoreItem(itemToAdd, sell.getPrice());
//            storeItems.add(storeItem);
//        }
//    }




    //Getter and Setter
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////

    public List<StoreItem> getStoreItems() { return storeItems; }
    public void setStoreItems(List<StoreItem> storeItems) {
        this.storeItems = storeItems;
    }

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
    @Override
    public List<Integer> getLocation() {
        return storeLocation;
    }

    @Override
    public void setLocation(List<Integer> storeLocation) {
        this.storeLocation = storeLocation;
    }

    @Override
    public int getX() {
        return storeLocation.get(0);
    }

    @Override
    public int getY() {
        return storeLocation.get(1);
    }

    @Override
    public void setX(int x) {
        storeLocation.set(0,x);
    }

    @Override
    public void setY(int y) {
        storeLocation.set(1,y);
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

    public List<StoreDiscount> getStoreDiscounts() {
        return storeDiscounts;
    }

    public void setStoreDiscounts(List<StoreDiscount> storeDiscounts) {
        this.storeDiscounts = storeDiscounts;
    }

    public HashMap<Integer, Double> getMapItemsToAmountSold() {
        if (mapItemsToAmountSold == null)
            mapItemsToAmountSold = new HashMap<Integer, Double>();
        return mapItemsToAmountSold;
    }

    public HashMap<Integer, Integer> getMapItemToPrices() {
        if (mapItemToPrices == null)
            mapItemToPrices = new HashMap<>();
        return mapItemToPrices;
    }



    public List<Order> getStoreOrders() {
        if (storeOrders == null)
            storeOrders = new ArrayList<Order>();

        return storeOrders;
    }

    public void addStoreChangeListener(StoreChangeListener listener){
        listeners.add(listener);
    }
    public void notifyStoreWasChanged(){
        for (StoreChangeListener storeChangeListener: listeners){
            storeChangeListener.storeWasChanged(this);
        }
    }


    public String getNameById(int id){
        for (StoreItem item: storeItems){
            if (item.getItemId() ==id)
                return item.getItemName();
        }
        return "";
    }

    public float getDeliveryCost(List<Integer> userLocation) {
        float val = getDeliveryPpk()* getDistance(userLocation);
        BigDecimal bd = new BigDecimal(val).setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
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


    public void addOrder(Order order) {
        storeOrders.add(order);
        setTotalDeliveryIncome(order.getDeliveryCost()+this.getTotalDeliveryIncome());
        updateStoreInventory(order.getCartForThisOrder());
        notifyStoreWasChanged();
    }

    private void updateStoreInventory(Cart cart) {
        cart.getCart().forEach((k, v) -> {
            double amountInCart = v.getItemAmount();
            Double oldAmountSold = mapItemsToAmountSold.get(k);
            mapItemsToAmountSold.put(k, amountInCart + oldAmountSold);
            StoreItem storeItem = getStoreItemById(k);
            storeItem.increaseTotalAmountSold(v.getItemAmount());
        });
        //notifyStoreWasChanged();
    }

    private StoreItem getStoreItemById(int id){
        for (StoreItem storeItem: storeItems){
            if (storeItem.getItemId() == id)
                return storeItem;
        }
        return null;
    }

    public InventoryItem getInventoryItemById(int priceID) {
        for (InventoryItem item : inventoryItems) {
            if (item.getItemId() == priceID)
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
        mapItemsToAmountSold.put(item.getItemId(), 0.0);
        mapItemToPrices.put(item.getItemId(), price);
        notifyStoreWasChanged();

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




    public Map<StoreDiscount, Integer> getListOfDiscountsForCart(Cart cart){
        Map<StoreDiscount,Integer> res = new HashMap<>();
        for (StoreDiscount discount: this.storeDiscounts){
            int timesConditionIsMet = discount.countTimesConditionIsMet(cart);
            if (timesConditionIsMet>0){
                res.put(discount, timesConditionIsMet);
            }
        }
        return res;
    }

    public int getNormalPriceForItem(InventoryItem k) {
        for (StoreItem item: storeItems){
            if (item.getItemId()==k.getItemId())
                return item.getNormalPrice();
        }
        return Integer.MAX_VALUE;
    }
}
