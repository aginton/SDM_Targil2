package components.PlaceAnOrder.ChooseDiscounts;

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

public class ChooseDiscountsController implements Initializable {


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

    private HashMap<Integer, Double> dummyCartRepresentation;
    private HashMap<Integer, CartItem> mapIdsToDiscountCartItems;

    private HashMap<Store, HashMap<Integer,Double>> dummyCartsRepresentation;
    private HashMap<Store,HashMap<Integer,CartItem>> mapOfMapIdsToDiscountItem;



    TableView.TableViewSelectionModel<DiscountOffer> defaultSelectionModel;
    private DoubleProperty subtotal;
    private FloatProperty deliveryFee;
    private DoubleProperty total;
    private ObservableList<CartItem> cartItems;
    private eOrderType orderType;

    public ChooseDiscountsController(){
        cartItems = FXCollections.observableArrayList();
        dummyCartsRepresentation = new HashMap<>();
        mapIdsToDiscountCartItems = new HashMap<>();
        mapOfMapIdsToDiscountItem = new HashMap<>();
    }


    @FXML
    void addButtonAction(ActionEvent event) {
        String operator = selectedDiscountWrapper.getStoreDiscount().getDiscountOffers().getOperator();
        System.out.println("addButton called for " + operator + " discount");
        if (operator.equals("ONE-OF")){
            DiscountOffer selectedOffer = offersTableView.getSelectionModel().getSelectedItem();
            System.out.println("Adding " + selectedOffer.getItemName() + "to chosenDiscountOffers");
            if (orderType==eOrderType.STATIC_ORDER){
                addSelectedOfferToMapStatic(selectedOffer);
                updateDummyCartAfterUsingDiscount(selectedDiscountWrapper.getStoreDiscount().getDiscountCondition());
            } else if (orderType == eOrderType.DYNAMIC_ORDER){
                addSelectedOfferToMapDynamic(selectedOffer,selectedDiscountWrapper.store);
                updateDummyCartsAfterUsingDiscount(selectedDiscountWrapper.getStoreDiscount().getDiscountCondition(),selectedDiscountWrapper.store);
            }

            updateDiscountWrappers();
        }

        if (operator.equals("ALL-OR-NOTHING")){
            offersTableView.getItems().forEach(item->{
                System.out.println("Adding " + item.getItemName() + "to chosenDiscountOffers");
                if (orderType == eOrderType.STATIC_ORDER)
                    addSelectedOfferToMapStatic(item);
                if (orderType == eOrderType.DYNAMIC_ORDER)
                    addSelectedOfferToMapDynamic(item, selectedDiscountWrapper.store);
            });

            updateDummyCartAfterUsingDiscount(selectedDiscountWrapper.getStoreDiscount().getDiscountCondition());
            updateDiscountWrappers();
        }
    }



    private void updateDiscountWrappers() {
        for (DiscountWrapper discountWrapper: discountsListView.getItems()){
            int val =0;
            if (orderType == eOrderType.DYNAMIC_ORDER)
                val = discountWrapper.getStoreDiscount().countTimesConditionIsMet(dummyCartsRepresentation.get(discountWrapper.store));
            else if (orderType==eOrderType.STATIC_ORDER)
                val = discountWrapper.getStoreDiscount().countTimesConditionIsMet(dummyCartRepresentation);

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

    private void updateDummyCartAfterUsingDiscount(DiscountCondition discountCondition) {
        Double oldAmount = dummyCartRepresentation.get(discountCondition.getIfYouBuyItem().getItemId());
        double amountToSubtract = discountCondition.getQuantity();
        Double newAmount = (oldAmount - amountToSubtract);
        dummyCartRepresentation.put(discountCondition.getIfYouBuyItem().getItemId(), newAmount);
        System.out.println("New dummy cart:" + dummyCartRepresentation);
    }


    private void addSelectedOfferToMapStatic(DiscountOffer selectedItem) {
        //if the item has not yet been added to map...
        if (mapIdsToDiscountCartItems.get(selectedItem.getItemId()) == null){
            System.out.println(selectedItem + " was not yet in chosenDiscountOffers");
            CartItem item = new CartItem(selectedItem.getOfferItem()
                    ,selectedItem.getQuantity(),selectedItem.getForAdditional()
                    ,true, selectedDiscountWrapper.getStoreDiscount().getName(), selectedStore);
            mapIdsToDiscountCartItems.put(item.getItemId(), item);
            cartItems.add(item);
        } else{
            System.out.println("This item was already in dummyCart");
            CartItem item = mapIdsToDiscountCartItems.get(selectedItem.getItemId());
            item.addToItemAmount(selectedItem.getQuantity());
        }
        double subtotal = getSubtotal();
        subtotal += (selectedItem.getQuantity()*selectedItem.getForAdditional());
        setSubtotal(subtotal);

        discountsListView.refresh();
        cartTable.refresh();
        System.out.println("discountItemsToAddToCart is now: " + mapIdsToDiscountCartItems);
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
        if (selectedDiscountWrapper.getStoreDiscount().getDiscountOffers().getOperator().equals("ONE-OF")){
            offersTableView.setSelectionModel(defaultSelectionModel);
            System.out.println("Do something for ONE-OF type");
            offersTableView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
            offersTableView.getSelectionModel().selectFirst();
        }
        if (selectedDiscountWrapper.getStoreDiscount().getDiscountOffers().getOperator().equals("ALL-OR-NOTHING")){
            System.out.println("Do something for ALL-OR-NOTHING type");
            offersTableView.setSelectionModel(null);
        }
    }

    public void setIsDiscountApplicable(boolean isDiscountApplicable) {
        this.isDiscountApplicable.set(isDiscountApplicable);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        subtotal = new SimpleDoubleProperty(this, "subtotal",0);
        cartSubtotalLabel.textProperty().bind(subtotal.asString("%.2f"));
        total = new SimpleDoubleProperty(this, "total",0);
        deliveryFee = new SimpleFloatProperty(this, "deliveryFee",0);
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

    public void fillViewsBasedOnDynamicOrder(HashMap<Store, Cart> mapStoresToCarts) {
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

    public void fillViewsBasedOnStoreAndCart(Store selectedStore, Cart inputCart) {
        orderType = eOrderType.STATIC_ORDER;
        System.out.println("Filling info based on static order");
        dummyCartRepresentation = createDummyCartRepresentation(inputCart);
        mapIdsToDiscountCartItems = new HashMap<>();
        this.selectedStore = selectedStore;
        clearItemsFromScreen();


        if (selectedStore.getStoreDiscounts().size()==0){
            System.out.println("This store has no discounts!");
            setIsDiscountApplicable(false);
            return;
        }

        selectedStore.getStoreDiscounts().forEach(discount-> discountWrappers.add(new DiscountWrapper(discount)));
        for (DiscountWrapper discountWrapper : discountWrappers){
            StoreDiscount discount = discountWrapper.getStoreDiscount();
            int timesDiscountCanBeApplied = discount.countTimesConditionIsMet(dummyCartRepresentation);
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



    public HashMap<Integer, CartItem> getMapIdsToDiscountCartItems() {
        return mapIdsToDiscountCartItems;
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
        cartItems.clear();
        cartTable.getItems().clear();
        setDeliveryFee(0);
        setSubtotal(0);
        mapOfMapIdsToDiscountItem.clear();
        mapIdsToDiscountCartItems.clear();
        discountsListView.getItems().clear();
        discountOffersObservableList.clear();
        discountWrappers.clear();
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
