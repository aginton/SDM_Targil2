package Logic.Interfaces;

import Logic.Inventory.InventoryItem;

public interface inventoryChangeInterface {
    void onInventoryChanged();
    void onSalesMapChanged();
    void onNumberCarryingStoresChanged(InventoryItem item);
}
