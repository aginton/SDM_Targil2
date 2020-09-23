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
import components.PlaceAnOrder.ChooseItems.ChooseItemsController;
import components.PlaceAnOrder.ChooseItems.DynamicOrder.ChooseItemsDynamicOrderController;
import components.PlaceAnOrder.ChooseStores.ChooseStoreController;
import components.PlaceAnOrder.ConfirmOrder.ConfirmOrderController;
import javafx.beans.property.ObjectProperty;
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
    private ChooseItemsController chooseItemsController;
    private ChooseItemsDynamicOrderController chooseItemsDynamicController;
    private ChooseDiscountsController chooseDiscountsController;
    private ConfirmOrderController confirmOrderController;

    private LocalDate orderDate;
    private Customer customer;
    private ObjectProperty<Customer> customerObjectProperty;
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

        try {
            //1. choose basic info (customer, order type, date)
            FXMLLoader basicInfoLoader = new FXMLLoader();
            basicInfoLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/BasicInfo/OrderBasicInfo.fxml"));
            basicInfoRef = basicInfoLoader.load();
            basicInfoController = basicInfoLoader.getController();


            //2. if static order, choose store
            FXMLLoader staticOrderLoader = new FXMLLoader();
            staticOrderLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseStores/ChooseStore.fxml"));
            chooseStoresRef = staticOrderLoader.load();
            chooseStoreController = staticOrderLoader.getController();

            //3. choose items for cart
            FXMLLoader chooseItemsLoader = new FXMLLoader();
            chooseItemsLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseItems/ChooseItems.fxml"));
            chooseItemsStaticOrderRef = chooseItemsLoader.load();
            chooseItemsController = chooseItemsLoader.getController();
            chooseItemsController.setUpCustomerBinding(this.customerObjectProperty);
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

        chooseStoresAnchorPane.getChildren().clear();
        chooseStoresAnchorPane.getChildren().add(chooseStoresRef);

        AnchorPane.setBottomAnchor(chooseStoresRef, 0.0);
        AnchorPane.setLeftAnchor(chooseStoresRef, 0.0);
        AnchorPane.setRightAnchor(chooseStoresRef, 0.0);
        AnchorPane.setTopAnchor(chooseStoresRef, 0.0);

        chooseDiscountsAnchorPane.getChildren().clear();
        chooseDiscountsAnchorPane.getChildren().add(chooseDiscountsRef);

        AnchorPane.setBottomAnchor(chooseDiscountsRef, 0.0);
        AnchorPane.setLeftAnchor(chooseDiscountsRef, 0.0);
        AnchorPane.setRightAnchor(chooseDiscountsRef, 0.0);
        AnchorPane.setTopAnchor(chooseDiscountsRef, 0.0);

        confirmAnchorPane.getChildren().clear();
        confirmAnchorPane.getChildren().add(confirmOrderRef);

        AnchorPane.setBottomAnchor(confirmOrderRef, 0.0);
        AnchorPane.setLeftAnchor(confirmOrderRef, 0.0);
        AnchorPane.setRightAnchor(confirmOrderRef, 0.0);
        AnchorPane.setTopAnchor(confirmOrderRef, 0.0);

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
            this.deliveryFee = selectedStore.getDeliveryCost(customer.getLocation());
            System.out.println("chooseStoresRef returned: {selectedStore=" + selectedStore + ", deliveryCost=" +
                    deliveryFee +"}");
        }

        if (currentNode.equals(chooseItemsStaticOrderRef)){
            this.currentCart = chooseItemsController.getDummyCart();
            System.out.println("chooseItemsRef returned: {currentCart=" + currentCart +"}");
        }

        if (currentNode.equals(chooseItemsDynamicRef)){
            this.mapStoresToCarts = chooseItemsDynamicController.getMapStoresToCarts();
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
            HashMap<Integer, CartItem> discountItemsToAddToCart = chooseDiscountsController.getMapIdsToDiscountCartItems();
            discountItemsToAddToCart.forEach((k,v)->{
                currentCart.add(v);
            });
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
        if (currentNode.equals(confirmOrderRef)){
            System.out.println("Do something on confirm order...");

            return;
        }

        if (currentNode.equals(chooseDiscountsRef)){
            confirmOrderController.fillViewsForData(customer,orderDate, orderType,setOfStores,currentCart,deliveryFee);
            currentNode = confirmOrderRef;
            accordian.setExpandedPane(confirmTitledPane);
            nextButton.setDisable(true);
            confirmButton.setVisible(true);
            confirmButton.setDisable(false);
            return;
        }

        if (currentNode.equals(chooseItemsStaticOrderRef)){
            if (orderType == eOrderType.STATIC_ORDER){
                chooseDiscountsController.fillViewsBasedOnStoreAndCart(selectedStore, currentCart);
                chooseDiscountsController.fillCustomerLabels(customer);
                chooseDiscountsController.fillOrderLabels(currentCart.getCartTotalPrice(), deliveryFee);
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

        if (currentNode.equals(chooseStoresRef)){
            chooseItemsController.setDataForStaticOrder(selectedStore);
            chooseItemsController.fillCustomerData(customer);
            chooseItemsController.setDeliveryFeeProperty(deliveryFee);

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


        if (currentNode.equals(basicInfoRef)){
            backButton.setDisable(false);
            if (orderType == eOrderType.STATIC_ORDER) {
                chooseStoreController.setCustomer(customer);
                chooseStoreController.bindCustomer(customerObjectProperty);
                accordian.setExpandedPane(chooseStoresTitledPane);
                currentNode = chooseStoresRef;
                return;
            }

            if (orderType == eOrderType.DYNAMIC_ORDER) {
                chooseItemsController.setDataForDynamicOrder();

                chooseItemsAnchorPane.getChildren().clear();
                chooseItemsAnchorPane.getChildren().add(chooseItemsDynamicRef);
                AnchorPane.setBottomAnchor(chooseItemsDynamicRef, 0.0);
                AnchorPane.setLeftAnchor(chooseItemsDynamicRef, 0.0);
                AnchorPane.setRightAnchor(chooseItemsDynamicRef, 0.0);
                AnchorPane.setTopAnchor(chooseItemsDynamicRef, 0.0);

                accordian.setExpandedPane(chooseItemsTitledPane);
                currentNode = chooseItemsDynamicRef;
                return;
            }
        }

    }

    @FXML
    void backButtonAction(ActionEvent event) {
        if (currentNode.equals(confirmOrderRef)){
            confirmButton.setDisable(true);
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

        SDMManager.getInstance().addNewStaticOrder(selectedStore, order);
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
            chooseItemsController.resetFields();
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
    }

}
