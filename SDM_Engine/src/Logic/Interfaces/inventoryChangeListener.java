package Logic.Interfaces;

import Logic.Inventory.InventoryItem;

public interface inventoryChangeListener {
    void onInventoryChanged();
    void onSalesMapChanged();
    void onNumberCarryingStoresChanged(InventoryItem item);
}
