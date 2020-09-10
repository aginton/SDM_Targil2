package Logic.Order;

import Logic.Store.Store;

public interface SalesItemInterface {

    public int getNormalPrice();
    public int getSalesPrice();
    public Store getStoreBroughtFrom();
    public float getItemAmountNormal();
    public float getItemAmountDiscount();
}
