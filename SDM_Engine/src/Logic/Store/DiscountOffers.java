package Logic.Store;

import Logic.Inventory.InventoryItem;
import Resources.Schema.JAXBGenerated.SDMOffer;
import Resources.Schema.JAXBGenerated.ThenYouGet;

import java.util.ArrayList;
import java.util.List;

public class DiscountOffers {
    protected List<DiscountOffer> discountOffers;
    protected String operator;


    public DiscountOffers(ThenYouGet thenYouGet, Store store) {
        this.operator = thenYouGet.getOperator();
        discountOffers = new ArrayList<>();
        for (SDMOffer sdmOffer: thenYouGet.getSDMOffer()){
            InventoryItem offerItem = store.getInventoryItemById(sdmOffer.getItemId());
            discountOffers.add(new DiscountOffer(sdmOffer.getQuantity(),offerItem,sdmOffer.getForAdditional()));
        }
    }

    public List<DiscountOffer> getDiscountOffers() {
        return discountOffers;
    }

    public void setDiscountOffers(List<DiscountOffer> discountOffers) {
        this.discountOffers = discountOffers;
    }

    public String getOperator() {
        return operator;
    }

    public void setOperator(String operator) {
        this.operator = operator;
    }

    @Override
    public String toString() {
        StringBuilder res = new StringBuilder(operator+" {");
        for (DiscountOffer offer: discountOffers){
            res.append("{quantity=").append(offer.getQuantity())
                    .append(", item-Id=");
            res.append(offer.getOfferItem().getItemId());
            res.append(", for additional" + offer.getForAdditional());
            res.append("}, ");
        }
        res.append("}");
        return res.toString();

//        return "DiscountOffers{" +
//                "discountOffers=" + discountOffers +
//                ", operator='" + operator + '\'' +
//                '}';
    }
}
