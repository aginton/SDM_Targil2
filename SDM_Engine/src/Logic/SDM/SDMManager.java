package Logic.SDM;

import Logic.Customers.Customer;
import Logic.Customers.Customers;
import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.*;
import Logic.Store.Store;
import Logic.Store.StoreDiscount;
import Resources.Schema.JAXBGenerated.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;


//https://dev.to/devtony101/javafx-3-ways-of-passing-information-between-scenes-1bm8


public class SDMManager extends SDMFileVerifier{

    //Members
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private List<Store> stores;
    private ObservableList<Store> storesObservableList;

    private Inventory inventory;
    private Orders orderHistory;
    private Customers customers;



    //Constructor
    ///////////////////////////////////////////////////////////////////////////////////////////////////

    //https://www.tutorialspoint.com/design_pattern/singleton_pattern.htm

    //create an object of SingleObject
    private final static SDMManager INSTANCE = new SDMManager();

    //make the constructor private so that this class cannot be instantiated
    private SDMManager(){
        super();
    }

    //Get the only object available
    public static SDMManager getInstance() {
        return INSTANCE;
    }


    //Getter and Setter methods
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public List<Store> getStores() {
        return stores;
    }

    public void setStores(List<Store> stores) {
        this.stores = stores;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Orders getOrderHistory() {
        return orderHistory;
    }

    public void setOrderHistory(Orders orderHistory) {
        this.orderHistory = orderHistory;
    }

    public ObservableList<Store> getStoresObservableList() {
        return storesObservableList;
    }

    public void setStoresObservableList(ObservableList<Store> storesObservableList) {
        this.storesObservableList = storesObservableList;
    }

    public Customers getCustomers() {
        return customers;
    }

    public void setCustomers(Customers customers) {
        this.customers = customers;
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////////

    //methods
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    public void loadNewSDMFile(SDMFileVerifier sdmForChosenFile){
        if (sdmForChosenFile.getIsValidFile()){

            System.out.println("The chosen file IS VALID!!! Hurray!");
            setFileRef(sdmForChosenFile.getFileRef());
            setIsValidFile(true);
            setLoadingErrorMessage("");
            setSDMDescriptor(sdmForChosenFile.getSDMDescriptor());

            createCustomers(getSDMDescriptor());

            createInventory(getSDMDescriptor());

            createStores(getSDMDescriptor());
            Order.setNumOfOrders(0);

            inventory.updateStoresCarryingItems(stores);
            inventory.updateAvePrice();
            orderHistory = new Orders();
            System.out.println("Finished creating SDM!");

        } else{
            System.out.println("The chosen file IS INVALID!!! GASP!");
        }
    }

    private void createCustomers(SuperDuperMarketDescriptor sdmDescriptor) {
        customers = new Customers();
        for (SDMCustomer customer: sdmDescriptor.getSDMCustomers().getSDMCustomer()){
            Customer newCustomer =
                    new Customer(customer.getId(), customer.getName(), customer.getLocation().getX(), customer.getLocation().getY());

            customers.add(newCustomer);
            System.out.println("Created following customer: " + newCustomer);
        }
    }


    private void createInventory(SuperDuperMarketDescriptor sdm) {
        inventory = new Inventory();
        for (SDMItem sdmItem: sdm.getSDMItems().getSDMItem()){
            InventoryItem item = new InventoryItem(sdmItem);
            inventory.addNewItemToInventory(item);
        }
    }

    public void createStores(SuperDuperMarketDescriptor sdm){
        stores = new ArrayList<Store>();
        storesObservableList = FXCollections.observableArrayList();

        for (SDMStore store: sdm.getSDMStores().getSDMStore()){
            List<Integer> storeLoc = new ArrayList<>();
            storeLoc.add(store.getLocation().getX());
            storeLoc.add(store.getLocation().getY());

            Store newStore = new Store(store,this);

            stores.add(newStore);
            System.out.println("Created following store: " + newStore);
            StringBuilder discountsStringBuilder = new StringBuilder("Discounts:");
            for (StoreDiscount storeDiscount: newStore.getStoreDiscounts()){
                discountsStringBuilder.append("\n\t"+storeDiscount);
            }
            String discountString = discountsStringBuilder.append("\n").toString();
            System.out.println(discountString);
        }
    }

    public void addNewStaticOrder(Store storeChoice, Order order) {
        storeChoice.addOrder(order);
        orderHistory.addOrder(order);
        inventory.updateSalesMap(order);
    }

    private String getItemNameById(int id){
        for (InventoryItem item: inventory.getListInventoryItems()){
            if (item.getItemId() == id)
                return item.getItemName();
        }
        return "";
    }

    public void addNewDynamicOrder(Set<Store> storesBoughtFrom, Order order) {
        addSplittedOrdersToStores(storesBoughtFrom, order);
        orderHistory.addOrder(order);
        inventory.updateSalesMap(order);
    }

    private void addSplittedOrdersToStores(Set<Store> storesBoughtFrom, Order order) {
        storesBoughtFrom.forEach(store -> {
            Cart cartForStore = ExtractCartForStore(store, order);
            float deliveryCostForStore = store.getDeliveryCost(order.getUserLocation());

            Set<Store> storeForThisSubOrder = new HashSet<Store>();
            storeForThisSubOrder.add(store);
            Order orderForStore = new Order(order.getUserLocation(),
                    order.getOrderDate(),
                    deliveryCostForStore,
                    cartForStore,
                    storeForThisSubOrder,
                    eOrderType.SPLITTED_DYNAMIC_ORDER);
            store.addOrder(orderForStore);
        });
    }

    private Cart ExtractCartForStore(Store store, Order order) {
        Cart cartForStore = new Cart();
        order.getCartForThisOrder().getCart().forEach((key,cartItem) -> {
            if (cartItem.getStoreBoughtFrom() == store) {
                cartForStore.add(cartItem);
            }
        });

        return cartForStore;
    }


    public void fillSampleData(ObservableList<Store> backingList) {
        for (Store store: stores){
            backingList.add(store);
        }
    }


    public Cart findCheapestCartForUser(HashMap<InventoryItem, Double> mapItemsChosenToAmount) {
        Cart cart = new Cart();

        mapItemsChosenToAmount.forEach((item,amount) -> {
            Store cheapestStore = findCheapestStoreForItem(item);
            int cheapestPrice = cheapestStore.getMapItemToPrices().get(item.getItemId());
            CartItem cartItem = new CartItem(item, amount, cheapestPrice, cheapestStore);
            cart.add(cartItem);
        });

        return cart;
    }

    public Store findCheapestStoreForItem(InventoryItem item) {
        Comparator<Store> comparator = (store1, store2) -> store1.getMapItemToPrices()
                .get(item.getItemId()).compareTo(store2.getMapItemToPrices().get(item.getItemId()));
        Set<Store> storesWithItem = inventory.getMapItemsToStoresWithItem().get(item);
        Store cheapestStore = Collections.min(storesWithItem, comparator);

        return cheapestStore;
    }

}
