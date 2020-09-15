package Logic.Order;

import Logic.Inventory.InventoryItem;
import Logic.Store.Store;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Observer;

public class CartItem extends InventoryItem {

    private FloatProperty pitemAmount = new SimpleFloatProperty(this, "pitemAmount", 0);
    private float itemAmount;
    private int price;
    Store storeBoughtFrom;

    public CartItem(InventoryItem item, float amount, int price, Store storeBoughtFrom){
        super(item);
        setItemAmount(amount);
//        this.itemAmount = amount;
        this.price = price;
        this.storeBoughtFrom = storeBoughtFrom;
    }



    public float getItemAmount() {
        return itemAmount;
    }

    public void setItemAmount(float itemAmount) {
        this.itemAmount = itemAmount;
    }


    public float getPitemAmount() {
        return pitemAmount.get();
    }

    public FloatProperty pitemAmountProperty() {
        return pitemAmount;
    }

    public void setPitemAmount(float pitemAmount) {
        this.pitemAmount.set(pitemAmount);
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

    @Override
    public String toString() {
        return "CartItem{" +
                "itemAmount=" + itemAmount +
                '}';
    }
}
