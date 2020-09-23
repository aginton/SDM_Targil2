package Logic.Order;

import Logic.Inventory.ePurchaseCategory;
import Logic.Store.Store;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

public class Cart {

    private HashMap<Integer,CartItem> cart;
    private HashMap<Integer,CartItem> discountCart;
    private DoubleProperty cartTotalPrice;


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


    public void add(CartItem cartItem) {
        int id = cartItem.getItemId();
        double cartOldTotal = getCartTotalPrice();
        double addToCartTotal = cartItem.getPrice()*cartItem.getItemAmount();
        setCartTotalPrice(cartOldTotal+addToCartTotal);

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

        System.out.println("At end of addOperation:");
        System.out.println("\tHashMap<Integer,CartItem> cart=" + cart);
        System.out.println("\tHashMap<Integer,CartItem> discountCart=" + discountCart);
    }

    private CartItem getCartItemFromCart(CartItem cartItem) {
        CartItem res = null;

        if (!cartItem.isIsOnSale()) {
            for (CartItem item : cart.values()) {
                if (item.getItemId() == cartItem.getItemId() && item.getItemName().equals(cartItem.getItemName())) {
                    res = item;
                    break;
                }
            }
        }
        else if (cartItem.isIsOnSale()) {
            for (CartItem item : cart.values()) {
                if (item.getItemId() == cartItem.getItemId() && item.getItemName().equals(cartItem.getItemName())
                        && item.getDiscountName().equals(cartItem.getDiscountName())) {
                    res = item;
                    break;
                }
            }
        }
        return res;
    }



    public HashMap<Integer, CartItem> getDiscountCart() {
        return discountCart;
    }

    public double getCartTotalPrice() {
        double val = cartTotalPrice.get();
        BigDecimal bd = new BigDecimal(val).setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
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

        StringBuilder sb = new StringBuilder( "Cart:\n\t cartTotalPrice=" + getCartTotalPrice());
        sb.append("\n\titems={");
        cart.forEach((k,v)->{
            sb.append("{item " + v.getItemId() + ", amount= ");
            sb.append(String.valueOf(v.getItemAmount())).append("},");
        });
        sb.append("}");
        sb.append("\n\tdiscount-items={");
        discountCart.forEach((k,v)->{
            sb.append("{item " + v.getItemId() + ", amount= ");
            sb.append(String.valueOf(v.getItemAmount())).append("},");
        });
        sb.append("}");
        return sb.toString();
    }

    public void addCartToCart(Cart v) {
        for (CartItem item: v.getCart().values()){
            add(item);
        }
        for (CartItem item: v.getDiscountCart().values()){
            add(item);
        }
    }
}
