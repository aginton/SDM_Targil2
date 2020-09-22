package Logic.Order;

import Logic.Inventory.InventoryItem;
import Logic.Store.Store;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;


import java.text.DecimalFormat;
import java.util.Objects;

public class CartItem extends InventoryItem {

    //Using a property for the itemAmount lets the tableViews update automatically
    private DoubleProperty itemAmount = new SimpleDoubleProperty(this, "itemAmount", 0);
    private int price;
    Store storeBoughtFrom;
    private BooleanProperty isOnSale = new SimpleBooleanProperty(this, "isOnSale",false);
    private String discountName;

    public CartItem(InventoryItem item, double amount, int price, Store storeBoughtFrom){
        super(item);
        setItemAmount(amount);
        this.price = price;
        this.storeBoughtFrom = storeBoughtFrom;
        this.discountName = "";
    }

    public CartItem(InventoryItem offerItem, double quantity, int forAdditional, boolean isOnSale, String discountName) {
        super(offerItem);
        setItemAmount(quantity);
        this.price = forAdditional;
        this.discountName = discountName;
        setIsOnSale(isOnSale);
    }

    public double getItemAmount() {
        return itemAmount.get();
    }

    public DoubleProperty itemAmountProperty() {
        return itemAmount;
    }

    public void setItemAmount(double itemAmount) {
        this.itemAmount.set(itemAmount);
    }

    public void addToItemAmount(double amountToAdd){
        double oldAmount = getItemAmount();
        setItemAmount(oldAmount+amountToAdd);
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
        return Double.compare(cartItem.getItemAmount(), getItemAmount()) == 0 &&
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
        DecimalFormat df = new DecimalFormat("#.##");
        String amountString = df.format(getItemAmount());
        return "CartItem{" +
                "itemAmount=" + amountString +
                '}';
    }

    public boolean isIsOnSale() {
        return isOnSale.get();
    }

    public BooleanProperty isOnSaleProperty() {
        return isOnSale;
    }

    public void setIsOnSale(boolean isOnSale) {
        this.isOnSale.set(isOnSale);
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }
}
