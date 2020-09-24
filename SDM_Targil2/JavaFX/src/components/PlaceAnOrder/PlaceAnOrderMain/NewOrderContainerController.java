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
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class NewOrderContainerController implements Initializable {

    @FXML
    private Label titleLabel;

    @FXML
    private AnchorPane newOrderCurrentStep;

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
    private eOrderType orderType;
    private Set<Store> setOfStores;
    private Store selectedStore;
    private Cart currentCart;
    private HashMap<Store,Cart> mapStoresToCarts;
    private float deliveryFee;

    public NewOrderContainerController(){
        currentCart = new Cart();
        setOfStores = new HashSet<>();
        customerObjectProperty = new SimpleObjectProperty<>();
        dateObjectProperty = new SimpleObjectProperty<>();
        mapStoresToCarts = new HashMap<>();

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

        setNodeForPane(basicInfoRef);

    }

    private void setNodeForPane(Node node) {
        newOrderCurrentStep.getChildren().clear();
        newOrderCurrentStep.getChildren().add(node);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
    }

    @FXML
    void onBackButtonAction(ActionEvent event) {
        if (currentNode.equals(confirmOrderRef)){
            confirmButton.setDisable(true);
            nextButton.setDisable(false);
            currentNode = basicInfoRef;
            titleLabel.setText("Choose Discounts");
            setNodeForPane(chooseDiscountsRef);
            return;
        }

        if (currentNode.equals(chooseDiscountsRef)){
            titleLabel.setText("Choose Items");
            if (orderType == eOrderType.DYNAMIC_ORDER){
                currentNode =chooseItemsDynamicRef;
                setNodeForPane(chooseItemsDynamicRef);
                return;
            } else if (orderType == eOrderType.STATIC_ORDER){
                currentNode = chooseItemsStaticOrderRef;
                setNodeForPane(chooseItemsStaticOrderRef);
                return;
            }
        }

        if (currentNode.equals(chooseItemsStaticOrderRef)){
            titleLabel.setText("Choose Store");
            currentNode = chooseStoresRef;
            setNodeForPane(chooseStoresRef);
            return;
        }
        if (currentNode.equals(chooseItemsDynamicRef)){
            backButton.setDisable(true);
            currentNode = basicInfoRef;
            titleLabel.setText("Basic Info");
            setNodeForPane(basicInfoRef);
            return;
        }

        if (currentNode.equals(chooseStoresRef)){
            backButton.setDisable(true);
            currentNode = basicInfoRef;
            titleLabel.setText("Basic Info");
            setNodeForPane(basicInfoRef);
            return;
        }
    }

    @FXML
    void onConfirmButtonAction(ActionEvent event) {

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

//
//        System.out.printf("Order details: " +
//                "\n\tCustomer: %s" +
//                "\n\tDate: %s" +
//                "\n\torderType: $%s" +
//                "\n\tStore: " +
//                "\n\tCart: %s", customer.getCustomerName(),dateStr, orderType, sb.toString(), currentCart);

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


        resetInfo();

        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(getClass().getResource("/components/PlaceAnOrder/SuccessOrError/SuccessPopUp.fxml"));
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();


            nextButton.setDisable(false);
            confirmButton.setDisable(true);
            confirmButton.setVisible(false);
            currentNode = basicInfoRef;


            titleLabel.setText("Basic Info");
            setNodeForPane(basicInfoRef);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void resetInfo() {
        if (orderType == eOrderType.STATIC_ORDER){
            chooseStoreController.resetAllFields();
            chooseItemsStaticOrderController.resetFields();
        } else{
            chooseItemsDynamicController.resetFields();
        }
        chooseDiscountsController.resetFields();

        customer = null;
        orderDate = null;
        mapStoresToCarts.clear();
        deliveryFee = 0;
        selectedStore = null;

    }

    @FXML
    void onNextButtonAction(ActionEvent event) {

        if (checkIfCanContinue()){
            getInformationFromCurrentPage();
            openNextTitledPane();
        }
    }



    private void getInformationFromCurrentPage() {

        if (currentNode.equals(chooseStoresRef)){
            this.selectedStore = chooseStoreController.getSelectedStore();
            deliveryFee = selectedStore.getDeliveryCost(customer.getLocation());
            setOfStores.clear();
            setOfStores.add(selectedStore);

            System.out.println("chooseStoresRef returned: {selectedStore=" + selectedStore + ", deliveryCost=" +
                    deliveryFee +"}");
        }

        if (currentNode.equals(chooseItemsStaticOrderRef)){
            //this.currentCart = chooseItemsStaticOrderController.getDummyCart();
            mapStoresToCarts.put(selectedStore,chooseItemsStaticOrderController.getDummyCart());
//            System.out.println("chooseItemsRef returned: {currentCart=" + currentCart +"}");
        }

        if (currentNode.equals(chooseItemsDynamicRef)){
            this.mapStoresToCarts = chooseItemsDynamicController.getMapStoresToCarts();
            deliveryFee = 0;
            for (Store store: mapStoresToCarts.keySet()){
                deliveryFee += store.getDeliveryCost(customer.getLocation());
            }
            System.out.println("chooseItemsDynamicRef returned: " + mapStoresToCarts);

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
//                    currentCart.add(v);
                    mapStoresToCarts.get(selectedStore).add(v);
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
            if (basicInfoController.getDateObjectProperty()==null){
                System.out.println("You must supply an Order date!");
                return false;
            }
        }
        if (currentNode.equals(chooseStoresRef) && orderType == eOrderType.STATIC_ORDER){
            if (chooseStoreController.getSelectedStore() == null){
                System.out.println("You must select a store!");
                return false;
            }
        }
        if (currentNode.equals(chooseItemsDynamicRef) && orderType == eOrderType.DYNAMIC_ORDER){
            if (chooseItemsDynamicController.getMapStoresToCarts().size() == 0){
                System.out.println("Did you order anything?");
                return false;
            }
        }
        if (currentNode.equals(chooseItemsStaticOrderRef)){
            if (chooseItemsStaticOrderController.getDummyCart().getCart().size()==0){
                System.out.println("Cart can't be empty!");
                return false;
            }
        }
        return true;
    }

    private void openNextTitledPane() {

        if (currentNode.equals(basicInfoRef)){
            backButton.setDisable(false);
            if (orderType == eOrderType.STATIC_ORDER) {
                chooseStoreController.setCustomer(customer);
                currentNode = chooseStoresRef;
                titleLabel.setText("Choose Store");
                setNodeForPane(chooseStoresRef);
                return;
            }

            if (orderType == eOrderType.DYNAMIC_ORDER) {
                chooseItemsDynamicController.setCustomer(customer);


                currentNode = chooseItemsDynamicRef;
                titleLabel.setText("Choose Items - Dynamic Order");
                setNodeForPane(chooseItemsDynamicRef);
                return;
            }
        }

        if (currentNode.equals(chooseStoresRef)){
            chooseItemsStaticOrderController.setDataForStaticOrder(selectedStore);
            chooseItemsStaticOrderController.fillCustomerData(customer);
            chooseItemsStaticOrderController.setDeliveryFeeProperty(deliveryFee);
            currentNode = chooseItemsStaticOrderRef;
            setNodeForPane(chooseItemsStaticOrderRef);
            return;
        }

        if (currentNode.equals(chooseItemsStaticOrderRef)){
            if (orderType == eOrderType.STATIC_ORDER){
//                chooseDiscountsController.fillViewsBasedOnStoreAndCart(selectedStore, currentCart);
                chooseDiscountsController.fillViewsBasedOnStoreAndCart(selectedStore, mapStoresToCarts.get(selectedStore));
                chooseDiscountsController.fillCustomerLabels(customer);
//                chooseDiscountsController.fillOrderLabels(currentCart.getCartTotalPrice(), deliveryFee);
                chooseDiscountsController.fillOrderLabels(mapStoresToCarts.get(selectedStore).getCartTotalPrice(), deliveryFee);

                titleLabel.setText("Choose Discounts");
                setNodeForPane(chooseDiscountsRef);
                currentNode = chooseDiscountsRef;
                return;
            }
        }
        if (currentNode.equals(chooseItemsDynamicRef)){
            chooseDiscountsController.fillViewsBasedOnDynamicOrder(mapStoresToCarts);
            currentNode = chooseDiscountsRef;
            titleLabel.setText("Choose Discounts");
            setNodeForPane(chooseDiscountsRef);
            return;
        }

        if (currentNode.equals(chooseDiscountsRef)){

            confirmOrderController.fillViews(customer, mapStoresToCarts);
            currentNode = confirmOrderRef;
            nextButton.setDisable(true);
            confirmButton.setVisible(true);
            confirmButton.setDisable(false);
            setNodeForPane(confirmOrderRef);
            titleLabel.setText("Confirm");
            return;
        }
    }

}
