package Logic.Store;


import Logic.Order.Cart;
import Logic.Order.CartItem;
import Resources.Schema.JAXBGenerated.SDMDiscount;

public class StoreDiscount {
    protected String name;
    protected DiscountCondition discountCondition;
    protected DiscountOffers discountOffers;
    protected Store store;

    public StoreDiscount(SDMDiscount sdmDiscount, Store store){
        this.name = sdmDiscount.getName();
        this.store = store;
        this.discountCondition = new DiscountCondition(sdmDiscount.getIfYouBuy(),store);
        this.discountOffers = new DiscountOffers(sdmDiscount.getThenYouGet(), store);

    }

    public Store getStore() {
        return store;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public DiscountCondition getDiscountCondition() {
        return discountCondition;
    }

    public void setDiscountCondition(DiscountCondition discountCondition) {
        this.discountCondition = discountCondition;
    }

    public DiscountOffers getDiscountOffers() {
        return discountOffers;
    }

    public void setDiscountOffers(DiscountOffers discountOffers) {
        this.discountOffers = discountOffers;
    }

    @Override
    public String toString() {
        return name;
//        StringBuilder res = new StringBuilder("StoreDiscount{{name=")
//                .append(name).append("}, if-you-buy={")
//                .append(discountCondition.toString()).append("}, then-you-get ")
//                .append(discountOffers.toString());
//
//
//        return res.toString();
    }

    public int countTimesConditionIsMet(Cart cart){
        CartItem item = cart.getCart().get(discountCondition.getIfYouBuyItem().getItemId());
        if (item != null){
            double quotient = (discountCondition.getQuantity()) / (item.getItemAmount());
            return (int) Math.floor(quotient);
        } else{
            return 0;
        }
    }
}
