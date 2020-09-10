package Logic.Inventory;

import Resources.Schema.JAXBGenerated.SDMItem;
import javafx.beans.property.*;
import javafx.scene.paint.Color;

import java.util.Objects;

public class InventoryItem implements Comparable<InventoryItem>, InventoryItemInterface {

    private IntegerProperty inventoryItemId = new SimpleIntegerProperty(this, "inventoryItemId", 0);
    private StringProperty itemName = new SimpleStringProperty(this, "itemName", "");
    private ObjectProperty<ePurchaseCategory> purchaseCategory = new SimpleObjectProperty<ePurchaseCategory>(this, "purchaseCategory", ePurchaseCategory.QUANTITY);


    public InventoryItem(SDMItem item) {
        setInventoryItemId(item.getId());
        setItemName(item.getName());
        setPurchaseCategory(ePurchaseCategory.valueOf(item.getPurchaseCategory().toUpperCase()));
    }

    public InventoryItem(InventoryItem item){
        this.inventoryItemId = item.inventoryItemIdProperty();
        setInventoryItemId(item.getInventoryItemId());

        setItemName(item.getItemName());
        this.itemName = item.itemNameProperty();

        setPurchaseCategory(item.getPurchaseCategory());
        this.purchaseCategory = item.purchaseCategoryProperty();
    }

    @Override
    public int getInventoryItemId() {
        return inventoryItemId.get();
    }

    public IntegerProperty inventoryItemIdProperty() {
        return inventoryItemId;
    }

    public void setInventoryItemId(int inventoryItemId) {
        this.inventoryItemId.set(inventoryItemId);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        if (o instanceof InventoryItem){
            InventoryItem oitem = (InventoryItem) o;
            return getInventoryItemId() == oitem.getInventoryItemId() && itemName.equals(oitem.itemName) && purchaseCategory.equals(oitem.purchaseCategory);
        } else{
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(inventoryItemId, itemName, purchaseCategory);
    }

    @Override
    public int compareTo(InventoryItem o) {
        return this.getInventoryItemId() - o.getInventoryItemId();
    }

}
