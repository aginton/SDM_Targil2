package Logic.Order;

import Logic.Inventory.InventoryItem;
import Logic.Store.Store;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;


import java.util.Objects;

public class CartItem extends InventoryItem {

    private FloatProperty itemAmount = new SimpleFloatProperty(this, "itemAmount", 0);
    private int price;
    Store storeBoughtFrom;

    public CartItem(InventoryItem item, float amount, int price, Store storeBoughtFrom){
        super(item);
        setItemAmount(amount);
        this.price = price;
        this.storeBoughtFrom = storeBoughtFrom;
    }

    public float getItemAmount() {
        return itemAmount.get();
    }

    public FloatProperty itemAmountProperty() {
        return itemAmount;
    }

    public void setItemAmount(float itemAmount) {
        this.itemAmount.set(itemAmount);
    }

    public int getPrice() {
        return price;
    }
    public void setPrice(int price) {
        this.price = price;
    }

    public Store getStoreBoughtFrom() {
        return storeBoughtFrom;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        CartItem cartItem = (CartItem) o;
        return Float.compare(cartItem.getItemAmount(), getItemAmount()) == 0 &&
                price == cartItem.price;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), itemAmount, price);
    }

    public void increaseAmount(float i) {
        setItemAmount(getItemAmount()+i);
    }
    public void decreaseAmount(float i) {
        setItemAmount(getItemAmount()-i);
        if (getItemAmount()<0)
            setItemAmount(0);
    }

//    @Override
//    public String toString() {
//        return "CartItem{" +
//                "itemAmount=" + itemAmount +
//                ", price=" + price +
////                ", storeBoughtFrom=" + (storeBoughtFrom.equals(null)? " not yet known" : storeBoughtFrom)   +
//                '}';
//    }
}
