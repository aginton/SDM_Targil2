package components.PlaceAnOrder.ChooseDiscounts.DynamicOrderDiscounts;

import Logic.Customers.Customer;
import Logic.Inventory.InventoryItem;
import Logic.Inventory.ePurchaseCategory;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Order.eOrderType;
import Logic.Store.*;
import javafx.beans.property.*;
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

public class DynamicDiscountsController implements Initializable {


    @FXML
    private Label cartSubtotalLabel;

    @FXML
    private Label deliveryFeeLabel;

    @FXML
    private Label totalCostLabel;
    @FXML
    private Label customerLabel;

    @FXML
    private Label customerLocationLabel;

    @FXML
    private ListView<DiscountWrapper> discountsListView;

    @FXML
    private Label ifyoubuyLabel;

    @FXML
    private Label discountTypeLabel;

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
    private TableView<CartItem> cartTable;

    @FXML
    private TableColumn<CartItem, Integer> cartItemIdColumn;

    @FXML
    private TableColumn<CartItem, String> cartItemNameColumn;

    @FXML
    private TableColumn<CartItem, Double> cartQuantityColumn;

    @FXML
    private TableColumn<CartItem, Integer> cartForAdditionalColumn;

    @FXML
    private TableColumn<CartItem, Double> costColumn;

    @FXML
    private TableColumn<CartItem, String> cartDiscountNameColumn;

    @FXML
    private Button addButton;

    private final ObservableList<DiscountWrapper> discountWrappers = FXCollections.observableArrayList();
    private final ObservableList<DiscountOffer> discountOffersObservableList = FXCollections.observableArrayList();
    private ChangeListener<DiscountWrapper> discountWrapperChangeListener;
    private DiscountWrapper selectedDiscountWrapper;
    private BooleanProperty isDiscountApplicable = new SimpleBooleanProperty(false);
    private Store selectedStore;
    private boolean alreadyAddedDiscounts;


    private HashMap<Store, HashMap<Integer,Double>> dummyCartsRepresentation;
    private HashMap<Store,HashMap<Integer,CartItem>> mapOfMapIdsToDiscountItem;


    TableView.TableViewSelectionModel<DiscountOffer> defaultSelectionModel;
    private DoubleProperty subtotal;
    private DoubleProperty regularItemsSubtotal;
    private FloatProperty deliveryFee;
    private DoubleProperty total;
    private ObservableList<CartItem> cartItems;
    private eOrderType orderType;

    public DynamicDiscountsController(){
        cartItems = FXCollections.observableArrayList();
        dummyCartsRepresentation = new HashMap<>();
        regularItemsSubtotal = new SimpleDoubleProperty();
        mapOfMapIdsToDiscountItem = new HashMap<>();
        subtotal = new SimpleDoubleProperty(this, "subtotal",0);
        total = new SimpleDoubleProperty(this, "total",0);
        deliveryFee = new SimpleFloatProperty(this, "deliveryFee",0);
    }

    public boolean isAlreadyAddedDiscounts() {
        return alreadyAddedDiscounts;
    }

    @FXML
    void addButtonAction(ActionEvent event) {
        if (!alreadyAddedDiscounts)
            alreadyAddedDiscounts = true;
        String operator = selectedDiscountWrapper.getStoreDiscount().getDiscountOffers().getOperator();
        System.out.println("addButton called for " + operator + " discount");

        if (operator.equals("ALL-OR-NOTHING")){
            offersTableView.getItems().forEach(item->{
                System.out.println("Adding " + item.getItemName() + "to chosenDiscountOffers");
                addSelectedOfferToMapDynamic(item, selectedDiscountWrapper.store);
            });
            updateDummyCartsAfterUsingDiscount(selectedDiscountWrapper.getStoreDiscount().getDiscountCondition(),selectedDiscountWrapper.store);
            updateDiscountWrappers();
        } else{
            DiscountOffer selectedOffer = offersTableView.getSelectionModel().getSelectedItem();
            System.out.println("Adding " + selectedOffer.getItemName() + "to chosenDiscountOffers");
            addSelectedOfferToMapDynamic(selectedOffer,selectedDiscountWrapper.store);
            updateDummyCartsAfterUsingDiscount(selectedDiscountWrapper.getStoreDiscount().getDiscountCondition(),selectedDiscountWrapper.store);
            updateDiscountWrappers();
        }
    }



    private void updateDiscountWrappers() {
        for (DiscountWrapper discountWrapper: discountsListView.getItems()){
            int val =0;
            if (orderType == eOrderType.DYNAMIC_ORDER)
                val = discountWrapper.getStoreDiscount().countTimesConditionIsMet(dummyCartsRepresentation.get(discountWrapper.store));


            if (selectedDiscountWrapper == discountWrapper){
                discountWrapper.setTimesDiscountCanBeApplied(val);
                setIsDiscountApplicable(selectedDiscountWrapper.getTimesDiscountCanBeApplied()>0);

            }
        }
    }

    private void updateDummyCartsAfterUsingDiscount(DiscountCondition discountCondition, Store store) {
        Double oldAmount = dummyCartsRepresentation.get(store).get(discountCondition.getIfYouBuyItem().getItemId());
        double amountToSubtract = discountCondition.getQuantity();
        Double newAmount = (oldAmount - amountToSubtract);
        dummyCartsRepresentation.get(store).put(discountCondition.getIfYouBuyItem().getItemId(), newAmount);
        System.out.println("New dummy cart for store:" + store.getStoreName() +":" + dummyCartsRepresentation.get(store));
    }

    private void addSelectedOfferToMapDynamic(DiscountOffer selectedItem, Store store) {
        if (mapOfMapIdsToDiscountItem.get(store) == null){
            HashMap<Integer,CartItem> map = new HashMap<>();
            mapOfMapIdsToDiscountItem.put(store,map);
        }
        if (mapOfMapIdsToDiscountItem.get(store).get(selectedItem.getItemId()) == null){
            System.out.println(selectedItem + " was not yet in chosenDiscountOffers");
            CartItem item = new CartItem(selectedItem.getOfferItem()
                    ,selectedItem.getQuantity(),selectedItem.getForAdditional()
                    ,true, selectedDiscountWrapper.getStoreDiscount().getName(), store);

            mapOfMapIdsToDiscountItem.get(store).put(item.getItemId(), item);
            cartItems.add(item);
        } else{
            System.out.println("This item was already in dummyCart");
            CartItem item = mapOfMapIdsToDiscountItem.get(store).get(selectedItem.getItemId());
            item.addToItemAmount(selectedItem.getQuantity());
        }
        double subtotal = getSubtotal();
        subtotal += (selectedItem.getQuantity()*selectedItem.getForAdditional());
        setSubtotal(subtotal);

        discountsListView.refresh();
        cartTable.refresh();
        System.out.println("discountItemsToAddToCart is now: " + mapOfMapIdsToDiscountItem);
    }


    private void updateOfferDetails() {
        ifyoubuyValueLabel.setText(getIfYouBuyString(selectedDiscountWrapper.getStoreDiscount().getDiscountCondition()));
        discountTypeLabel.setText(selectedDiscountWrapper.storeDiscount.getDiscountOffers().getOperator());
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

        offersTableView.setItems(discountOffersObservableList);
//        if (selectedDiscountWrapper.getStoreDiscount().getDiscountOffers().getOperator().equals("ONE-OF")){
//            offersTableView.setSelectionModel(defaultSelectionModel);
//            System.out.println("Do something for ONE-OF type");
//            offersTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//            offersTableView.getSelectionModel().selectFirst();
//        }
        if (selectedDiscountWrapper.getStoreDiscount().getDiscountOffers().getOperator().equals("ALL-OR-NOTHING")){
            System.out.println("Do something for ALL-OR-NOTHING type");
            offersTableView.setSelectionModel(null);
        } else {
            offersTableView.setSelectionModel(defaultSelectionModel);
            System.out.println("Do something for ONE-OF type");
            offersTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            offersTableView.getSelectionModel().selectFirst();
        }
    }

    public void setIsDiscountApplicable(boolean isDiscountApplicable) {
        this.isDiscountApplicable.set(isDiscountApplicable);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        cartSubtotalLabel.textProperty().bind(regularItemsSubtotal.add(subtotal).asString("%.2f"));
        deliveryFeeLabel.textProperty().bind(deliveryFee.asString("%.2f"));
        totalCostLabel.textProperty().bind((subtotal.add(deliveryFee)).asString("%.2f"));
        addButton.disableProperty().bind(isDiscountApplicable.not());
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,Integer>("ItemId"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,String>("ItemName"));
        itemQuantityColumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,Double>("quantity"));
        forAdditionalCoumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,Integer>("forAdditional"));
        defaultSelectionModel = offersTableView.getSelectionModel();


        cartItemIdColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("itemId"));
        cartItemNameColumn.setCellValueFactory(new PropertyValueFactory<CartItem,String>("itemName"));
        cartForAdditionalColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Integer>("price"));
        cartQuantityColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Double>("itemAmount"));
        costColumn.setCellValueFactory(new PropertyValueFactory<CartItem,Double>("cost"));
        cartDiscountNameColumn.setCellValueFactory(new PropertyValueFactory<CartItem,String>("discountName"));

        cartTable.setItems(cartItems);
    }

    public void setRegularItemsSubtotal(double regularItemsSubtotal) {
        this.regularItemsSubtotal.set(regularItemsSubtotal);
    }

    public void fillViewsBasedOnDynamicOrder(HashMap<Store, Cart> mapStoresToCarts, float deliveryFee, double regularItemsSubtotal) {
        this.deliveryFee.set(deliveryFee);
        setRegularItemsSubtotal(regularItemsSubtotal);
        System.out.println("Filling info based on dynamic order");
        orderType = eOrderType.DYNAMIC_ORDER;
        mapStoresToCarts.forEach((k,v)->{
            dummyCartsRepresentation.put(k, createDummyCartRepresentation(v));
        });

        boolean atLeastOneSale = false;
        for (Store store: dummyCartsRepresentation.keySet()){
            if (store.getStoreDiscounts().size()==0){
                System.out.println("Store " + store.getStoreName() + " has no discounts!");
            } else{
                System.out.println("Store " + store.getStoreName() + " has following discounts: " + store.getStoreDiscounts());
                atLeastOneSale=true;
            }
        }
        if (!atLeastOneSale)
            setIsDiscountApplicable(false);

        dummyCartsRepresentation.keySet().forEach(store->{
            System.out.println("");
            store.getStoreDiscounts().forEach(discount-> discountWrappers.add(new DiscountWrapper(discount)));
        });

        for (DiscountWrapper discountWrapper : discountWrappers){
            StoreDiscount discount = discountWrapper.getStoreDiscount();
            Store store = discountWrapper.store;
            int timesDiscountCanBeApplied = discount.countTimesConditionIsMet(dummyCartsRepresentation.get(store));
            System.out.println("Based on current cart, discount: " + discount.getName() + " can be applied " + timesDiscountCanBeApplied + " times");
            discountWrapper.setTimesDiscountCanBeApplied(timesDiscountCanBeApplied);
        }
        setUpDiscountsListView();
    }

    private void setUpDiscountsListView() {
        discountsListView.setItems(discountWrappers);
        discountsListView.getSelectionModel().selectedItemProperty().addListener(
                discountWrapperChangeListener = (((observable, oldValue, newValue) -> {
                    System.out.println("Change Listener called!");
                    selectedDiscountWrapper = newValue;
                    if (newValue != null){
                        System.out.println("isDiscountApplicable will be set to: " + (newValue.getTimesDiscountCanBeApplied() >0));
                        setIsDiscountApplicable(selectedDiscountWrapper.getTimesDiscountCanBeApplied()>0);
                        updateOfferDetails();
                        //remainingTimesLabel.setText("This offer can be applied " + selectedDiscountWrapper.getTimesDiscountCanBeApplied() + " times");
                    }
                }))
        );
        discountsListView.getSelectionModel().selectFirst();
    }

    private void clearItemsFromScreen() {
        discountsListView.getItems().clear();
        discountWrappers.clear();
        offersTableView.getItems().clear();
        //remainingTimesLabel.setText("");
    }

    private HashMap<Integer, Double> createDummyCartRepresentation(Cart inputCart) {
        HashMap res = new HashMap<Integer, Double>();
        for (CartItem cartItem: inputCart.getCart().values()){
            res.put(cartItem.getItemId(), cartItem.getItemAmount());
        }
        return res;
    }


    public HashMap<Store, HashMap<Integer,CartItem>> getMapIdsToDiscountCartItemsDynamic() {
        return mapOfMapIdsToDiscountItem;
    }

    public void fillCustomerLabels(Customer customer) {
        customerLabel.setText("Customer: "+customer.getCustomerName());
        customerLocationLabel.setText("Customer Location: " + customer.getLocation());
    }

    public void fillOrderLabels(double cartTotalPrice, float deliveryFee) {
        setDeliveryFee(deliveryFee);
        setSubtotal(cartTotalPrice);
    }

    public double getSubtotal() {
        return subtotal.get();
    }

    public DoubleProperty subtotalProperty() {
        return subtotal;
    }

    public void setSubtotal(double subtotal) {
        this.subtotal.set(subtotal);
    }

    public float getDeliveryFee() {
        return deliveryFee.get();
    }

    public FloatProperty deliveryFeeProperty() {
        return deliveryFee;
    }

    public void setDeliveryFee(float deliveryFee) {
        this.deliveryFee.set(deliveryFee);
    }

    public void resetFields() {
        alreadyAddedDiscounts = false;
        cartItems.clear();
        cartTable.getItems().clear();
        setDeliveryFee(0);
        setSubtotal(0);
        mapOfMapIdsToDiscountItem.clear();
        discountsListView.getItems().clear();
        discountOffersObservableList.clear();
        discountWrappers.clear();
        ifyoubuyValueLabel.setText("");
        thenyougetLabel.setText("");
    }

    public double getDiscountsSubtotal() {
        double total = 0;
        for (HashMap<Integer, CartItem> map: mapOfMapIdsToDiscountItem.values()){
            for (CartItem item: map.values()){
                total += item.getPrice();
            }
        }
        return total;
    }


    private class DiscountWrapper {
        private Store store;
        private StoreDiscount storeDiscount;
        private IntegerProperty timesDiscountCanBeApplied;

        public DiscountWrapper(StoreDiscount discount){
            this.storeDiscount = discount;
            timesDiscountCanBeApplied = new SimpleIntegerProperty(0);
            this.store = discount.getStore();
        }

        public IntegerProperty timesDiscountCanBeAppliedProperty() {
            return timesDiscountCanBeApplied;
        }

        public StoreDiscount getStoreDiscount() {
            return storeDiscount;
        }

        public int getTimesDiscountCanBeApplied() {
            return timesDiscountCanBeApplied.get();
        }

        public void setTimesDiscountCanBeApplied(int timesDiscountCanBeApplied) {
            this.timesDiscountCanBeApplied.set(timesDiscountCanBeApplied);
        }

        @Override
        public String toString() {
            return storeDiscount.getName() + "x (" + getTimesDiscountCanBeApplied() + ")";
        }

    }

}
