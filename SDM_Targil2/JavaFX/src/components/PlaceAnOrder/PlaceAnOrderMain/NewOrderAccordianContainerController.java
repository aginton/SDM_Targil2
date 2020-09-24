package components.PlaceAnOrder.PlaceAnOrderMain;

import Logic.Customers.Customer;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Order.Order;
import Logic.Order.eOrderType;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import components.PlaceAnOrder.BasicInfo.OrderBasicInfoController;
import components.PlaceAnOrder.ChooseDiscounts.ChooseDiscountsController;
import components.PlaceAnOrder.ChooseItems.ChooseItemsStaticOrderController;
import components.PlaceAnOrder.ChooseItems.DynamicOrder.ChooseItemsDynamicOrderController;
import components.PlaceAnOrder.ChooseStores.ChooseStoreController;
import components.PlaceAnOrder.ConfirmOrder.ConfirmOrderController;
import javafx.beans.property.FloatProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class NewOrderAccordianContainerController implements Initializable {
    @FXML
    private Accordion accordian;

    @FXML
    private TitledPane basicInfoTitledPane;

    @FXML
    private AnchorPane basicInfoAnchorPane;

    @FXML
    private TitledPane chooseStoresTitledPane;

    @FXML
    private AnchorPane chooseStoresAnchorPane;

    @FXML
    private TitledPane chooseItemsTitledPane;

    @FXML
    private AnchorPane chooseItemsAnchorPane;

    @FXML
    private TitledPane chooseDiscountsTitledPane;

    @FXML
    private AnchorPane chooseDiscountsAnchorPane;

    @FXML
    private TitledPane confirmTitledPane;

    @FXML
    private AnchorPane confirmAnchorPane;

    @FXML
    private Button backButton;

    @FXML
    private Button nextButton;

    @FXML
    private Button confirmButton;



    private Node basicInfoRef, chooseStoresRef, chooseItemsStaticOrderRef, chooseItemsDynamicRef, chooseDiscountsRef, confirmOrderRef, currentNode;
    private OrderBasicInfoController basicInfoController;
    private ChooseStoreController chooseStoreController;
    private ChooseItemsStaticOrderController chooseItemsStaticOrderController;
    private ChooseItemsDynamicOrderController chooseItemsDynamicController;
    private ChooseDiscountsController chooseDiscountsController;
    private ConfirmOrderController confirmOrderController;

    private LocalDate orderDate;
    private Customer customer;
    private ObjectProperty<Customer> customerObjectProperty;
    private ObjectProperty<LocalDate> dateObjectProperty;
    private FloatProperty deliveryFeeTotal;
    private eOrderType orderType;
    private Set<Store> setOfStores;
    private Store selectedStore;
    private Cart currentCart;
    private HashMap<Store,Cart> mapStoresToCarts;
    private float deliveryFee;


    public NewOrderAccordianContainerController(){
        currentCart = new Cart();
        setOfStores = new HashSet<>();
        customerObjectProperty = new SimpleObjectProperty<>();
        dateObjectProperty = new SimpleObjectProperty<>();
        deliveryFeeTotal = new SimpleFloatProperty(0);

        try {
            //1. choose basic info (customer, order type, date)
            FXMLLoader basicInfoLoader = new FXMLLoader();
            basicInfoLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/BasicInfo/OrderBasicInfo.fxml"));
            basicInfoRef = basicInfoLoader.load();
            basicInfoController = basicInfoLoader.getController();
            this.customerObjectProperty.bind(basicInfoController.customerObjectPropertyProperty());
            this.dateObjectProperty.bind(basicInfoController.dateObjectPropertyProperty());


            //2. if static order, choose store
            FXMLLoader staticOrderLoader = new FXMLLoader();
            staticOrderLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseStores/ChooseStore.fxml"));
            chooseStoresRef = staticOrderLoader.load();
            chooseStoreController = staticOrderLoader.getController();

            //3. choose items for cart
            FXMLLoader chooseItemsLoader = new FXMLLoader();
            chooseItemsLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseItems/ChooseItems.fxml"));
            chooseItemsStaticOrderRef = chooseItemsLoader.load();
            chooseItemsStaticOrderController = chooseItemsLoader.getController();

            //currentCart = chooseItemsController.getDummyCart();


            FXMLLoader chooseItemsDynamicLoader = new FXMLLoader();
            chooseItemsDynamicLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseItems/DynamicOrder/ChooseItemsDynamicOrder.fxml"));
            chooseItemsDynamicRef = chooseItemsDynamicLoader.load();
            chooseItemsDynamicController = chooseItemsDynamicLoader.getController();



            //4. choose discounts
            FXMLLoader chooseDiscountsLoader = new FXMLLoader();
            chooseDiscountsLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseDiscounts/ChooseDiscounts.fxml"));
            chooseDiscountsRef = chooseDiscountsLoader.load();
            chooseDiscountsController = chooseDiscountsLoader.getController();

            FXMLLoader confirmOrderLoader = new FXMLLoader();
            confirmOrderLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ConfirmOrder/ConfirmOrder.fxml"));
            confirmOrderRef = confirmOrderLoader.load();
            confirmOrderController = confirmOrderLoader.getController();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentNode = basicInfoRef;
        backButton.setDisable(true);
        confirmButton.setVisible(false);
        confirmButton.setDisable(false);

        accordian.setExpandedPane(basicInfoTitledPane);

        basicInfoAnchorPane.getChildren().clear();
        basicInfoAnchorPane.getChildren().add(basicInfoRef);
        AnchorPane.setBottomAnchor(basicInfoRef, 0.0);
        AnchorPane.setLeftAnchor(basicInfoRef, 0.0);
        AnchorPane.setRightAnchor(basicInfoRef, 0.0);
        AnchorPane.setTopAnchor(basicInfoRef, 0.0);

        basicInfoTitledPane.setExpanded(true);
        setNodeForPane(chooseStoresAnchorPane,chooseStoresRef);

        setNodeForPane(chooseDiscountsAnchorPane,chooseDiscountsRef);

        setNodeForPane(confirmAnchorPane,confirmOrderRef);

    }

    @FXML
    void nextButtonAction(ActionEvent event) {
        getInformationFromCurrentPage();
        if (checkIfCanContinue()){
            openNextTitledPane();
        }
    }



    private void getInformationFromCurrentPage() {

        if (currentNode.equals(chooseStoresRef)){
            this.selectedStore = chooseStoreController.getSelectedStore();
            setOfStores.clear();
            setOfStores.add(selectedStore);
//            this.deliveryFee = selectedStore.getDeliveryCost(customer.getLocation());
            setDeliveryFeeTotal(selectedStore.getDeliveryCost(customer.getLocation()));

            System.out.println("chooseStoresRef returned: {selectedStore=" + selectedStore + ", deliveryCost=" +
                    deliveryFeeTotal.get() +"}");
        }

        if (currentNode.equals(chooseItemsStaticOrderRef)){
            this.currentCart = chooseItemsStaticOrderController.getDummyCart();
            System.out.println("chooseItemsRef returned: {currentCart=" + currentCart +"}");
        }

        if (currentNode.equals(chooseItemsDynamicRef)){
            this.mapStoresToCarts = chooseItemsDynamicController.getMapStoresToCarts();
//            this.deliveryFee = chooseItemsDynamicController.getDeliveryFeeTotal();
            setDeliveryFeeTotal(selectedStore.getDeliveryCost(customerObjectProperty.getValue().getLocation()));

            System.out.println("chooseItemsDynamicRef returned: " + mapStoresToCarts);
            //this.setOfStores = chooseItemsDynamicController.getStoresBoughtFrom();
        }

        if (currentNode.equals(basicInfoRef)){
            this.orderDate = basicInfoController.getOrderDate();
            this.orderType = basicInfoController.getOrderType();
            this.customer = basicInfoController.getSelectedCustomer();
            System.out.println("basicInfoRef returned: {orderDate=" +
                    orderDate + ", orderType= " + orderType +", customer= " + customer +"}");
        }

        if (currentNode.equals(chooseDiscountsRef)){
            if (orderType == eOrderType.STATIC_ORDER){
                HashMap<Integer, CartItem> discountItemsToAddToCart = chooseDiscountsController.getMapIdsToDiscountCartItems();
                discountItemsToAddToCart.forEach((k,v)->{
                    currentCart.add(v);
                });
            }
            if (orderType == eOrderType.DYNAMIC_ORDER){
                HashMap<Store,HashMap<Integer,CartItem>> discountItemsToAdd = chooseDiscountsController.getMapIdsToDiscountCartItemsDynamic();

                discountItemsToAdd.forEach((store,map)->{
                    map.forEach((itemId,cartItem)->{
                        mapStoresToCarts.get(store).add(cartItem);
                    });
                });
            }
        }
    }

    private boolean checkIfCanContinue() {
        if (currentNode.equals(basicInfoRef)){
            if (orderDate==null){
                System.out.println("You must supply an Order date!");
                return false;
            }
        }
        if (currentNode.equals(chooseStoresRef) && orderType == eOrderType.STATIC_ORDER){
            if (selectedStore==null){
                System.out.println("You must select a store!");
                return false;
            }
        }
        if (currentNode.equals(chooseItemsDynamicRef) && orderType == eOrderType.DYNAMIC_ORDER){
            if (mapStoresToCarts.values().size() == 0){
                System.out.println("Did you order anything?");
                return false;
            }
        }
        if (currentNode.equals(chooseItemsStaticOrderRef)){
            if (currentCart.getCart().size()==0){
                System.out.println("Cart can't be empty!");
                return false;
            }
        }
        return true;
    }

    private void openNextTitledPane() {

        if (currentNode.equals(basicInfoRef)){
            basicInfoTitledPane.setCollapsible(true);
            backButton.setDisable(false);
            if (orderType == eOrderType.STATIC_ORDER) {

                chooseStoreController.setCustomer(customer);

                accordian.setExpandedPane(chooseStoresTitledPane);
                currentNode = chooseStoresRef;
                basicInfoTitledPane.setCollapsible(false);
                return;
            }

            if (orderType == eOrderType.DYNAMIC_ORDER) {
                chooseItemsStaticOrderController.setDataForDynamicOrder();

                setNodeForPane(chooseItemsAnchorPane, chooseItemsDynamicRef);
                chooseItemsDynamicController.bindCustomer(this.customerObjectProperty);


                accordian.setExpandedPane(chooseItemsTitledPane);
                currentNode = chooseItemsDynamicRef;
                return;
            }
        }

        if (currentNode.equals(chooseStoresRef)){
            chooseItemsStaticOrderController.setDataForStaticOrder(selectedStore);
            chooseItemsStaticOrderController.fillCustomerData(customer);
            chooseItemsStaticOrderController.setDeliveryFeeProperty(getDeliveryFeeTotal());

            chooseItemsAnchorPane.getChildren().clear();
            chooseItemsAnchorPane.getChildren().add(chooseItemsStaticOrderRef);
            AnchorPane.setBottomAnchor(chooseItemsStaticOrderRef, 0.0);
            AnchorPane.setLeftAnchor(chooseItemsStaticOrderRef, 0.0);
            AnchorPane.setRightAnchor(chooseItemsStaticOrderRef, 0.0);
            AnchorPane.setTopAnchor(chooseItemsStaticOrderRef, 0.0);

            currentNode = chooseItemsStaticOrderRef;
            accordian.setExpandedPane(chooseItemsTitledPane);
            return;
        }

        if (currentNode.equals(chooseItemsStaticOrderRef)){
            if (orderType == eOrderType.STATIC_ORDER){
                chooseDiscountsController.fillViewsBasedOnStoreAndCart(selectedStore, currentCart);
                chooseDiscountsController.fillCustomerLabels(customer);
                chooseDiscountsController.fillOrderLabels(currentCart.getCartTotalPrice(), getDeliveryFeeTotal());
            }
            accordian.setExpandedPane(chooseDiscountsTitledPane);
            currentNode = chooseDiscountsRef;
            return;
        }
        if (currentNode.equals(chooseItemsDynamicRef)){
            chooseDiscountsController.fillViewsBasedOnDynamicOrder(mapStoresToCarts);
            accordian.setExpandedPane(chooseDiscountsTitledPane);
            currentNode = chooseDiscountsRef;
            return;
        }

        if (currentNode.equals(chooseDiscountsRef)){
            if (orderType == eOrderType.STATIC_ORDER){
//                confirmOrderController.fillViewsForData(customer,orderDate, orderType,setOfStores,currentCart,deliveryFee);
                confirmOrderController.fillViewsForData(customer,orderDate, orderType,selectedStore,currentCart,getDeliveryFeeTotal());

            }
            if (orderType == eOrderType.DYNAMIC_ORDER){
                confirmOrderController.fillViewsForDynamicData(customer, orderDate, mapStoresToCarts, getDeliveryFeeTotal());
            }
            currentNode = confirmOrderRef;
            accordian.setExpandedPane(confirmTitledPane);
            nextButton.setDisable(true);
            confirmButton.setVisible(true);
            confirmButton.setDisable(false);
            return;
        }

        if (currentNode.equals(confirmOrderRef)){
            confirmOrderController.bindCustomer(customerObjectProperty);
            confirmOrderController.bindDate(dateObjectProperty);
            System.out.println("Do something on confirm order...");
            return;
        }
    }

    private void setNodeForPane(AnchorPane anchorPane, Node node) {
        anchorPane.getChildren().clear();
        anchorPane.getChildren().add(node);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(chooseItemsDynamicRef, 0.0);
        AnchorPane.setRightAnchor(chooseItemsDynamicRef, 0.0);
        AnchorPane.setTopAnchor(chooseItemsDynamicRef, 0.0);
    }

    @FXML
    void backButtonAction(ActionEvent event) {
        if (currentNode.equals(confirmOrderRef)){
            confirmButton.setDisable(true);
            nextButton.setDisable(false);
            currentNode = basicInfoRef;
            accordian.setExpandedPane(chooseDiscountsTitledPane);
            return;
        }

        if (currentNode.equals(chooseDiscountsRef)){
            if (orderType == eOrderType.DYNAMIC_ORDER){
                currentNode =chooseItemsDynamicRef ;
            } else{
                currentNode = chooseItemsStaticOrderRef;
            }
            accordian.setExpandedPane(chooseItemsTitledPane);
            return;
        }

        if (currentNode.equals(chooseItemsStaticOrderRef)){
            currentNode = chooseStoresRef;
            accordian.setExpandedPane(chooseStoresTitledPane);
            return;
        }
        if (currentNode.equals(chooseItemsDynamicRef)){
            backButton.setDisable(true);
            currentNode = basicInfoRef;
            accordian.setExpandedPane(basicInfoTitledPane);
            return;
        }

        if (currentNode.equals(chooseStoresRef)){
            backButton.setDisable(true);
            currentNode = basicInfoRef;
            accordian.setExpandedPane(basicInfoTitledPane);
            return;
        }
    }

    @FXML
    void confirmButtonAction(ActionEvent event) {

        if (orderType == eOrderType.STATIC_ORDER){
            setOfStores.add(selectedStore);
        }

        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date date = Date.from(orderDate.atStartOfDay(defaultZoneId).toInstant());

        String dateStr = new SimpleDateFormat("dd/MM\tHH:mm").format(date);
        StringBuilder sb = new StringBuilder();
        for (Store store: setOfStores){
            sb.append(store.getStoreName());
            sb.append(", ");
        }


        System.out.printf("Order details: " +
                "\n\tCustomer: %s" +
                "\n\tDate: %s" +
                "\n\torderType: $%s" +
                "\n\tStore: " +
                "\n\tCart: %s", customer.getCustomerName(),dateStr, orderType, sb.toString(), currentCart);

        Order order = new Order(customer.getLocation(),
                date,
                6,
                currentCart,
                setOfStores,
                orderType);

        if (orderType==eOrderType.STATIC_ORDER)
            SDMManager.getInstance().addNewStaticOrder(selectedStore, order);

        if (orderType==eOrderType.DYNAMIC_ORDER)
            SDMManager.getInstance().addNewDynamicOrder(mapStoresToCarts.keySet(), order);
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/components/PlaceAnOrder/SuccessOrError/SuccessPopUp.fxml"));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
            resetFields();


            nextButton.setDisable(false);
            confirmButton.setDisable(true);
            confirmButton.setVisible(false);
            currentNode = basicInfoRef;
            chooseItemsStaticOrderController.resetFields();
            accordian.setExpandedPane(basicInfoTitledPane);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetFields() {
        setOfStores.clear();
        selectedStore = null;
        orderDate = null;
        customer = null;
        currentCart = null;
        if (orderType == eOrderType.DYNAMIC_ORDER)
            chooseItemsDynamicController.resetFields();
        else
            chooseItemsStaticOrderController.resetFields();
        confirmOrderController.resetFields();
    }


    public LocalDate getDateObjectProperty() {
        return dateObjectProperty.get();
    }

    public ObjectProperty<LocalDate> dateObjectPropertyProperty() {
        return dateObjectProperty;
    }

    public void setDateObjectProperty(LocalDate dateObjectProperty) {
        this.dateObjectProperty.set(dateObjectProperty);
    }

    public float getDeliveryFeeTotal() {
        return deliveryFeeTotal.get();
    }

    public FloatProperty deliveryFeeTotalProperty() {
        return deliveryFeeTotal;
    }

    public void setDeliveryFeeTotal(float deliveryFeeTotal) {
        this.deliveryFeeTotal.set(deliveryFeeTotal);
    }
}
