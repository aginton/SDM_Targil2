package components.PlaceAnOrder.ChooseDiscounts;

import Logic.Inventory.InventoryItem;
import Logic.Inventory.ePurchaseCategory;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Store.*;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

public class ChooseDiscountsController implements Initializable {

    @FXML
    private ListView<DiscountWrapper> discountsListView;

    @FXML
    private Label ifyoubuyLabel;

    @FXML
    private Label ifyoubuyValueLabel;

    @FXML
    private Label thenyougetLabel;

    @FXML
    private TableView<DiscountOffer> offersTableView;

    @FXML
    private TableColumn<DiscountOffer, Integer> itemIdColumn;

    @FXML
    private TableColumn<DiscountOffer, String> itemNameColumn;

    @FXML
    private TableColumn<DiscountOffer, Double> itemQuantityColumn;

    @FXML
    private TableColumn<DiscountOffer, Integer> forAdditionalCoumn;

    @FXML
    private Label remainingTimesLabel;

    @FXML
    private Button addButton;

    @FXML
    void addButtonAction(ActionEvent event) {

    }

    private final ObservableList<DiscountWrapper> discountWrappers = FXCollections.observableArrayList();
    private final ObservableList<DiscountOffer> discountOffersObservableList = FXCollections.observableArrayList();
    private ChangeListener<DiscountWrapper> discountWrapperChangeListener;
    private DiscountWrapper selectedDiscountWrapper;
    private BooleanProperty isDiscountApplicable = new SimpleBooleanProperty(false);
    private Cart userCart;
    private HashMap<Integer,Float> cartCopy;

    public ChooseDiscountsController(){
        cartCopy = new HashMap<>();
    }

    public void fillViewsForStoreDiscounts(Store store){
        store.getStoreDiscounts().forEach(discount-> discountWrappers.add(new DiscountWrapper(discount)));
        discountsListView.setItems(discountWrappers);

        discountsListView.getSelectionModel().selectedItemProperty().addListener(
                discountWrapperChangeListener = (((observable, oldValue, newValue) -> {
                    selectedDiscountWrapper = newValue;
                    if (newValue != null){
                        setIsDiscountApplicable(newValue.numberTimesDiscountIsApplicable>0);
                        System.out.println("isDiscountApplicable will be set to: " + (newValue.numberTimesDiscountIsApplicable>0));
                        updateOfferDetails();
                    }
                }))
        );
        discountsListView.getSelectionModel().selectFirst();
    }

    private void updateOfferDetails() {
        ifyoubuyValueLabel.setText(getIfYouBuyString(selectedDiscountWrapper.getStoreDiscount().getDiscountCondition()));
        updateOffersTableView(selectedDiscountWrapper.getStoreDiscount().getDiscountOffers());
    }

    private String getIfYouBuyString(DiscountCondition discountCondition) {
        InventoryItem ifYouBuyItem = discountCondition.getIfYouBuyItem();

        StringBuilder res = new StringBuilder(String.valueOf(discountCondition.getQuantity()));
        if (ifYouBuyItem.getPurchaseCategory() == ePurchaseCategory.QUANTITY)
            res.append(" pcks");
        if (ifYouBuyItem.getPurchaseCategory() == ePurchaseCategory.WEIGHT)
            res.append("kgs");

        res.append(" of item ").append(ifYouBuyItem.getItemId()).append( "(").append(ifYouBuyItem.getItemName()).append(")");
        return res.toString();

    }

    private void updateOffersTableView(DiscountOffers discountOffers) {
        discountOffersObservableList.clear();
        discountOffersObservableList.addAll(discountOffers.getDiscountOffers());
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,Integer>("ItemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,String>("ItemName"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,Double>("quantity"));
        forAdditionalCoumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,Integer>("forAdditional"));
        offersTableView.setItems(discountOffersObservableList);
        if (selectedDiscountWrapper.getStoreDiscount().getDiscountOffers().getOperator().equals("ONE-OF")){
            System.out.println("Do something for ONE-OF type");
            offersTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        }
        if (selectedDiscountWrapper.getStoreDiscount().getDiscountOffers().getOperator().equals("ALL-OR-NOTHING")){
            System.out.println("Do something for ONE-OF type");
            offersTableView.setSelectionModel(null);
        }
    }

    public void setIsDiscountApplicable(boolean isDiscountApplicable) {
        this.isDiscountApplicable.set(isDiscountApplicable);
        remainingTimesLabel.setText("This offer can be applied " + selectedDiscountWrapper.numberTimesDiscountIsApplicable + " times");
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        addButton.disableProperty().bind(isDiscountApplicable.not());
    }

    public void updateBasedOnCart(Cart cart){
        for (DiscountWrapper discountWrapper : discountWrappers){
            StoreDiscount discount = discountWrapper.getStoreDiscount();
            discountWrapper.setNumberTimesDiscountIsApplicable(
                   discount.countTimesConditionIsMet(cart)
            );
        }
    }


    private class DiscountWrapper {
        private StoreDiscount storeDiscount;
        private int numberTimesDiscountIsApplicable;

        public DiscountWrapper(StoreDiscount discount){
            this.storeDiscount = discount;
            numberTimesDiscountIsApplicable = 0;
        }

        public StoreDiscount getStoreDiscount() {
            return storeDiscount;
        }

        public int getNumberTimesDiscountIsApplicable() {
            return numberTimesDiscountIsApplicable;
        }

        public void setNumberTimesDiscountIsApplicable(int numberTimesDiscountIsApplicable) {
            this.numberTimesDiscountIsApplicable = numberTimesDiscountIsApplicable;
        }

        @Override
        public String toString() {
            return storeDiscount.getName();
        }


    }

}
