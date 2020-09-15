package Logic.SDM;

import Logic.Customers.Customer;
import Logic.Customers.Customers;
import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.Order;
import Logic.Order.Orders;
import Logic.Store.Store;
import Resources.Schema.JAXBGenerated.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


//https://dev.to/devtony101/javafx-3-ways-of-passing-information-between-scenes-1bm8


public class SDMManager extends SDMFileVerifier{

    //Members
    ///////////////////////////////////////////////////////////////////////////////////////////////////
    private List<Store> stores;
    private ObservableList<Store> stores2;
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

    public ObservableList<Store> getStores2() {
        return stores2;
    }

    public void setStores2(ObservableList<Store> stores2) {
        this.stores2 = stores2;
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
        stores2 = FXCollections.observableArrayList();

        for (SDMStore store: sdm.getSDMStores().getSDMStore()){
            List<Integer> storeLoc = new ArrayList<>();
            storeLoc.add(store.getLocation().getX());
            storeLoc.add(store.getLocation().getY());

            Store newStore = new Store(store);
            Store newStore2 = new Store(store,inventory);

            for (SDMSell sell: store.getSDMPrices().getSDMSell()){
                InventoryItem itemToAdd = inventory.getListInventoryItems().stream().filter(i->i.getInventoryItemId()==sell.getItemId()).findFirst().get();
                if (itemToAdd != null){
                    newStore.getInventoryItems().add(itemToAdd);
                    newStore2.getInventoryItems().add(itemToAdd);
                }
            }

            stores.add(newStore);
            stores2.add(newStore2);
        }
    }


    public void fillSampleData(ObservableList<Store> backingList) {
        for (Store store: stores){
            backingList.add(store);
        }
    }



}
