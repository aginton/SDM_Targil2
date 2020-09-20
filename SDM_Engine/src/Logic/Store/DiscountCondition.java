package Logic.Store;

import Logic.Inventory.InventoryItem;
import Resources.Schema.JAXBGenerated.IfYouBuy;

public class DiscountCondition {
    protected double quantity;
    protected InventoryItem ifYouBuyItem;


    public DiscountCondition(IfYouBuy ifYouBuy, Store store) {
        this.ifYouBuyItem = store.getInventoryItemById(ifYouBuy.getItemId());
        this.quantity = ifYouBuy.getQuantity();
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public InventoryItem getIfYouBuyItem() {
        return ifYouBuyItem;
    }

    public void setIfYouBuyItem(InventoryItem ifYouBuyItem) {
        this.ifYouBuyItem = ifYouBuyItem;
    }

    @Override
    public String toString() {
        return  "quantity=" + quantity +
                ", itemId=" + String.valueOf(ifYouBuyItem.getItemId()) +
                "(" + ifYouBuyItem.getItemName() + ")";
    }
}
