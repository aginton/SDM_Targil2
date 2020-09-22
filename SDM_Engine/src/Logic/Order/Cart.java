package Logic.Order;

import Logic.Inventory.ePurchaseCategory;
import Logic.Store.Store;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class Cart {

    private HashMap<Integer,CartItem> cart;
    private HashMap<Integer,CartItem> discountCart;
    private DoubleProperty cartTotalPrice;
//    private float cartTotalPrice = 0;

    public Cart() {
        this.cart = new HashMap<Integer,CartItem>();
        this.discountCart = new HashMap<Integer,CartItem>();
        cartTotalPrice = new SimpleDoubleProperty(this, "cartTotalPrice", 0);
    }

    public int getNumberOfTypesOfItemsInCart()
    {
        return cart.size();
    }

    public int getNumItemsInCart() {
        int numberOfItems = 0;
        for(CartItem item : cart.values()) {
            if (item.getPurchaseCategory() == ePurchaseCategory.QUANTITY) {
                numberOfItems += Math.round(item.getItemAmount());
            }
            if (item.getPurchaseCategory() == ePurchaseCategory.WEIGHT) {
                numberOfItems++;
            }
        }

        return numberOfItems;
    }

    public HashMap<Integer,CartItem> getCart() {
        return cart;
    }

    public boolean isEmpty()
    {
        return cart.isEmpty();
    }


    //TODO: Should change data structure from map of items to set of items
    public void add(CartItem cartItem) {
        int id = cartItem.getItemId();
        double cartOldTotal = getCartTotalPrice();
        double amountToAdd = cartItem.getPrice()*cartItem.getItemAmount();
        setCartTotalPrice(cartOldTotal+amountToAdd);

        if (cartItem.isIsOnSale()){
            if (discountCart.containsKey(id)) {
                CartItem existingItem = discountCart.get(id);
                double amountInCart = existingItem.getItemAmount();
                existingItem.setItemAmount(amountInCart + cartItem.getItemAmount());
                return;
            }
            discountCart.put(id, cartItem);
        } else{
            if (cart.containsKey(id)) {
                CartItem existingItem = cart.get(id);
                double amountInCart = existingItem.getItemAmount();
                existingItem.setItemAmount(amountInCart + cartItem.getItemAmount());
                return;
            }
            cart.put(id, cartItem);
        }
    }

    public HashMap<Integer, CartItem> getDiscountCart() {
        return discountCart;
    }

    public double getCartTotalPrice() {
        return cartTotalPrice.get();
    }

    public DoubleProperty cartTotalPriceProperty() {
        return cartTotalPrice;
    }

    public void setCartTotalPrice(double cartTotalPrice) {
        this.cartTotalPrice.set(cartTotalPrice);
    }

    public void setCart(HashMap<Integer, CartItem> cart) {
        this.cart = cart;
    }

    public void setDiscountCart(HashMap<Integer, CartItem> discountCart) {
        this.discountCart = discountCart;
    }

    public Set<Store> getStoresBoughtFrom() {
        Set<Store> storesBoughtFrom = new HashSet<>();
        cart.forEach((k,v) -> storesBoughtFrom.add(v.getStoreBoughtFrom()));
        return storesBoughtFrom;
    }

    public void updateItemAmount(CartItem selectedItem, Double value) {
        if (!cart.containsKey(selectedItem.getItemId())){
            return;
        }
        CartItem existingItem = cart.get(selectedItem.getItemId());
        existingItem.setItemAmount(value);
    }

    public void removeItemFromCart(CartItem selectedItem) {
        if (!cart.containsKey(selectedItem.getItemId()))
            return;
        else
            cart.remove(selectedItem.getItemId());
    }

    @Override
    public String toString() {
        if (cart.isEmpty())
            return "empty cart";

        StringBuilder sb = new StringBuilder( "Cart {");
        cart.forEach((k,v)->{
            sb.append("\n\titem " + v.getItemId() + ", amount= ");
            sb.append(String.valueOf(v.getItemAmount()));
        });
        sb.append("}");
        return sb.toString();
    }

}
