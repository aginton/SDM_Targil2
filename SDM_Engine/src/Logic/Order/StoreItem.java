package Logic.Order;

import Logic.Inventory.InventoryItem;

public class StoreItem extends InventoryItem {

    private int normalPrice;
    private double totalAmountSoldAtStore;

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

    public double getTotalAmountSoldAtStore() {
        return totalAmountSoldAtStore;
    }

    public void setTotalAmountSoldAtStore(double totalAmountSoldAtStore) {
        this.totalAmountSoldAtStore = totalAmountSoldAtStore;
    }

    public void increaseTotalAmountSold(double amountToIncreaseBy){
        double oldAmount = getTotalAmountSoldAtStore();
        setTotalAmountSoldAtStore(oldAmount+amountToIncreaseBy);
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
            boolean sameName = this.getItemId()==((InventoryItem) obj).getItemId();
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
