package Logic.Order;

import Logic.Inventory.InventoryItem;

public class StoreItem extends InventoryItem {

    private int normalPrice;
    private float totalAmountSoldAtStore;

    public StoreItem(InventoryItem inventoryItem, int normalPrice){
        super(inventoryItem);
        this.normalPrice = normalPrice;
        totalAmountSoldAtStore = 0;
    }

    public int getNormalPrice() {
        return normalPrice;
    }

    public void setNormalPrice(int normalPrice) {
        this.normalPrice = normalPrice;
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
