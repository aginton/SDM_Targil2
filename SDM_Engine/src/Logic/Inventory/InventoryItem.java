package Logic.Inventory;

import Resources.Schema.JAXBGenerated.SDMItem;
import javafx.beans.property.*;

import java.util.Objects;

public class InventoryItem implements Comparable<InventoryItem>, InventoryItemInterface {

    private IntegerProperty itemId = new SimpleIntegerProperty(this, "itemId", 0);
    private StringProperty itemName = new SimpleStringProperty(this, "itemName", "");
    private ObjectProperty<ePurchaseCategory> purchaseCategory = new SimpleObjectProperty<ePurchaseCategory>(this, "purchaseCategory", ePurchaseCategory.QUANTITY);


    public InventoryItem(SDMItem item) {
        setItemId(item.getId());
        setItemName(item.getName());
        setPurchaseCategory(ePurchaseCategory.valueOf(item.getPurchaseCategory().toUpperCase()));
    }

    public InventoryItem(InventoryItem item){
        this.itemId = item.itemIdProperty();
        setItemId(item.getItemId());

        setItemName(item.getItemName());
        this.itemName = item.itemNameProperty();

        setPurchaseCategory(item.getPurchaseCategory());
        this.purchaseCategory = item.purchaseCategoryProperty();
    }

    @Override
    public int getItemId() {
        return itemId.get();
    }

    public IntegerProperty itemIdProperty() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId.set(itemId);
    }

    public StringProperty itemNameProperty() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName.set(itemName);
    }

    public String getItemName() {
        return itemName.get();
    }

    @Override
    public ePurchaseCategory getPurchaseCategory() {
        return purchaseCategory.get();
    }

    public ObjectProperty<ePurchaseCategory> purchaseCategoryProperty() {
        return purchaseCategory;
    }

    public void setPurchaseCategory(ePurchaseCategory purchaseCategory) {
        this.purchaseCategory.set(purchaseCategory);
    }

    public InventoryItem getInventoryItem(){
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        if (o instanceof InventoryItem){
            InventoryItem oitem = (InventoryItem) o;
            return getItemId() == oitem.getItemId() && itemName.equals(oitem.itemName) && purchaseCategory.equals(oitem.purchaseCategory);
        } else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(itemId, itemName, purchaseCategory);
    }

    @Override
    public int compareTo(InventoryItem o) {
        return this.getItemId() - o.getItemId();
    }

    @Override
    public String toString() {
        return "InventoryItem{" +
                "itemId=" + getItemId() +
                ", itemName=" + getItemName() +
                '}';
    }
}
