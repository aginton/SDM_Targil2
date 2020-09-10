package Logic.SDM;

import Logic.Inventory.Inventory;
import Logic.Inventory.InventoryItem;
import Logic.Order.Order;
import Logic.Order.Orders;
import Logic.Store.Store;
import Resources.Schema.JAXBGenerated.SDMItem;
import Resources.Schema.JAXBGenerated.SDMSell;
import Resources.Schema.JAXBGenerated.SDMStore;
import Resources.Schema.JAXBGenerated.SuperDuperMarketDescriptor;
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
//        backingList.add(new Person("Waldo", "Soller", "random notes 1"));
//        backingList.add(new Person("Herb", "Dinapoli", "random notes 2"));
//        backingList.add(new Person("Shawanna", "Goehring", "random notes 3"));
//        backingList.add(new Person("Flossie", "Goehring", "random notes 4"));
//        backingList.add(new Person("Magdalen", "Meadors", "random notes 5"));
//        backingList.add(new Person("Marylou", "Berube", "random notes 6"));
//        backingList.add(new Person("Ethan", "Nieto", "random notes 7"));
//        backingList.add(new Person("Elli", "Combes", "random notes 8"));
//        backingList.add(new Person("Andy", "Toupin", "random notes 9"));
//        backingList.add(new Person("Zenia", "Linwood", "random notes 10"));
    }




}
