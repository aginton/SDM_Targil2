package Logic.SDM;

import Logic.Customers.Customer;
import Logic.Customers.Customers;
import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.*;
import Logic.Store.Store;
import Resources.Schema.JAXBGenerated.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

            Store newStore = new Store(store);
            //Store newStore2 = new Store(store,inventory);

            for (SDMSell sell: store.getSDMPrices().getSDMSell()){
                InventoryItem itemToAdd = inventory.getListInventoryItems().stream().filter(i->i.getInventoryItemId()==sell.getItemId()).findFirst().get();
                if (itemToAdd != null){
                    newStore.getInventoryItems().add(itemToAdd);
                    //newStore2.getInventoryItems().add(itemToAdd);

                    StoreItem storeItem = new StoreItem(itemToAdd, sell.getPrice());
                    newStore.getStoreItems().add(storeItem);
                }
            }

            stores.add(newStore);
            //storesObservableList.add(newStore);
            //stores2.add(newStore2);
        }
    }

    public void addNewStaticOrder(Store storeChoice, Order order) {
        storeChoice.addOrder(order);
        orderHistory.addOrder(order);
        inventory.updateSalesMap(order);
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



}
