package Logic.Store;

import Logic.Order.Order;
import Logic.Order.StoreItem;
import Logic.Store.Store;

public interface StoreChangeListener {

    void orderWasAdded(Store store, Order order);

    void itemPriceChanged(Store store, StoreItem item);

    void newStoreItemAdded(Store store, StoreItem item);

    void storeItemWasDeleted(Store store,StoreItem item);
}

