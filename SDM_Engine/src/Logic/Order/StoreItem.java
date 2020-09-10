package Logic.Order;

import Logic.Inventory.InventoryItem;
import Logic.Store.Store;

public class StoreItem extends InventoryItem {

    private int price;
    private float totalAmountSoldAtStore;

    public StoreItem(InventoryItem inventoryItem, int price){
        super(inventoryItem);
        this.price = price;
        totalAmountSoldAtStore = 0;
    }


    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public float getTotalAmountSoldAtStore() {
        return totalAmountSoldAtStore;
    }

    public void setTotalAmountSoldAtStore(float totalAmountSoldAtStore) {
        this.totalAmountSoldAtStore = totalAmountSoldAtStore;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (getClass() != obj.getClass())
            return false;
        else{
            boolean sameName = this.getInventoryItemId()==((InventoryItem) obj).getInventoryItemId();
            boolean sameId = this.getItemName()==((InventoryItem) obj).getItemName();
            boolean sameCategory = this.getPurchaseCategory()==((InventoryItem) obj).getPurchaseCategory();

            return (sameId && sameName && sameCategory);
        }
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
