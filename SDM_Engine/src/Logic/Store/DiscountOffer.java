package Logic.Store;

import Logic.Inventory.InventoryItem;

public class DiscountOffer {
    protected double quantity;
    protected InventoryItem offerItem;
    protected int forAdditional;


    public DiscountOffer(double quantity, InventoryItem offerItem, int forAdditional) {
        this.quantity = quantity;
        this.offerItem = offerItem;
        this.forAdditional = forAdditional;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public InventoryItem getOfferItem() {
        return offerItem;
    }

    public void setOfferItem(InventoryItem offerItem) {
        this.offerItem = offerItem;
    }

    public int getForAdditional() {
        return forAdditional;
    }

    public void setForAdditional(int forAdditional) {
        this.forAdditional = forAdditional;
    }

    public String getItemName(){
        return offerItem.getItemName();
    }

    public Integer getItemId(){
        return offerItem.getItemId();
    }


    @Override
    public String toString() {
        return "DiscountOffer{" +
                "quantity=" + quantity +
                ",of itemId=" + offerItem.getItemId() +
                ", forAdditional=" + forAdditional +
                '}';
    }
}
