package Logic.Inventory;



import Logic.Interfaces.inventoryChangeListener;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Order.Order;
import Logic.Store.Store;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

public class Inventory  {

    private List<InventoryItem> listInventoryItems;
    private HashMap<InventoryItem, Double> mapItemsToTotalSold;
    private HashMap<InventoryItem, Float> mapItemsToAvePrice;
    private HashMap<InventoryItem, Set<Store>> mapItemsToStoresWithItem;
    private List<inventoryChangeListener> listeners;

    public Inventory() {
        this.listInventoryItems = new ArrayList<InventoryItem>();
        this.mapItemsToTotalSold = new HashMap<InventoryItem, Double>();
        this.mapItemsToAvePrice = new HashMap<InventoryItem, Float>();
        this.mapItemsToStoresWithItem = new HashMap<InventoryItem, Set<Store>>();
        listeners = new ArrayList<>();
    }


    public void addNewItemToInventory(InventoryItem item) {
        listInventoryItems.add(item);
        mapItemsToTotalSold.put(item, 0.0);
        mapItemsToAvePrice.put(item, 0f);
        mapItemsToStoresWithItem.put(item, new HashSet<Store>());
    }


    public InventoryItem getInventoryItemById(int id) {
        for (InventoryItem item : listInventoryItems) {
            if (item.getItemId() == id)
                return item;
        }
        return null;
    }


    public HashMap<InventoryItem, Double> getMapItemsToTotalSold() {
        return mapItemsToTotalSold;
    }

    public HashMap<InventoryItem, Float> getMapItemsToAvePrice() {
        return mapItemsToAvePrice;
    }

    public HashMap<InventoryItem, Set<Store>> getMapItemsToStoresWithItem() {
        return mapItemsToStoresWithItem;
    }

    public void updateSalesMap(Order order) {
        Cart cart = order.getCartForThisOrder();
        cart.getCart().forEach((k, v) -> updateSalesMap(v));
        notifyListeners();
        notifyListenersSalesMapChanged();
    }



    private void updateSalesMap(CartItem cartItem) {
        InventoryItem item = getInventoryItemById(cartItem.getItemId());
        Double oldAmount = mapItemsToTotalSold.get(item);
        mapItemsToTotalSold.put(item, oldAmount + cartItem.getItemAmount());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(mapItemsToTotalSold, inventory.mapItemsToTotalSold) &&
                Objects.equals(listInventoryItems, inventory.listInventoryItems);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mapItemsToTotalSold, listInventoryItems);
    }

    public List<InventoryItem> getListInventoryItems() {
        if (listInventoryItems == null)
            listInventoryItems = new ArrayList<InventoryItem>();
        return listInventoryItems;
    }


    public void updateAvePrice() {

        for(InventoryItem item : listInventoryItems) {
            Set<Store> setOStores = mapItemsToStoresWithItem.get(item);
            float sum = 0;
            for (Store store : setOStores) {
                //sum += store.getMapItemToPrices().get(item.getItemId());
                sum += store.getNormalPriceForItem(item.getInventoryItem());
            }
            int size = setOStores.size();
            mapItemsToAvePrice.put(item, sum / size);
        }
        notifyListeners();
    }

    public void updateStoresCarryingItems(List<Store> stores) {

        for(InventoryItem item : listInventoryItems) {
            Set<Store> setOfStores = mapItemsToStoresWithItem.get(item);
            for(Store store : stores) {
                if (!setOfStores.contains(store) && store.doesStoryCarryItem(item))
                    setOfStores.add(store);
//                if (!setOfStores.contains(item) && store.getInventoryItems().contains(item))
//                    setOfStores.add(store);
            }
            mapItemsToStoresWithItem.put(item, setOfStores);
        }
    }

    public Set<Store> getStoresCarryingItem(InventoryItem item){
        return mapItemsToStoresWithItem.get(item);
    }

    public List<Integer> getListOfInventoryItemIds(){
        return listInventoryItems.stream().map(item-> item.getItemId()).collect(Collectors.toList());
    }

    public List<InventoryItem> getListOfItemsNotSoldByStore(Store store){
        return listInventoryItems.stream().filter( item-> !mapItemsToStoresWithItem.get(item).contains(store)).collect(Collectors.toList());
    }

    public void addListener(inventoryChangeListener listener){
        listeners.add(listener);
    }

    private void notifyListeners(){
        for (inventoryChangeListener listener: listeners)
            listener.onInventoryChanged();
    }

    private void notifyListenersSalesMapChanged() {
        for (inventoryChangeListener listener: listeners)
            listener.onInventoryChanged();
    }

    public float getAveragePriceForItem(InventoryItem item) {
        int sum = 0;
        float res = 0;
        for (Store store: mapItemsToStoresWithItem.get(item)){
            sum += store.getNormalPriceForItem(item);
        }
        res = sum / mapItemsToStoresWithItem.get(item).size();
        BigDecimal bd = new BigDecimal(res).setScale(2, RoundingMode.HALF_UP);
        return bd.floatValue();
    }

    public double getTotalSoldForItem(InventoryItem item) {
        double total = mapItemsToTotalSold.get(item);
        BigDecimal bd = new BigDecimal(total).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
