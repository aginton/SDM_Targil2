package components.PlaceAnOrder.PlaceAnOrderMain;

import Logic.Customers.Customer;
import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Order.Order;
import Logic.Order.eOrderType;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import components.PlaceAnOrder.BasicInfo.OrderBasicInfoController;
import components.PlaceAnOrder.ChooseDiscounts.ChooseDiscountsStaticOrderController;
import components.PlaceAnOrder.ChooseDiscounts.DynamicOrderDiscounts.DynamicDiscountsController;
import components.PlaceAnOrder.ChooseItems.ChooseItemsStaticOrderController;
import components.PlaceAnOrder.ChooseItems.DynamicOrder.ChooseItemsDynamicOrderController;
import components.PlaceAnOrder.ChooseStores.ChooseStoreController;
import components.PlaceAnOrder.ConfirmOrder.ConfirmOrderController;
import components.PlaceAnOrder.SuccessOrError.AlertInfoBox;
import components.PlaceAnOrder.SuccessOrError.ConfirmBox;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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


    private String TAG = "NewOrderContainerController";
    private Node basicInfoRef, chooseStoresRef, chooseItemsStaticOrderRef, chooseItemsDynamicRef, chooseDiscountsRef, chooseDiscountsDynamicRef, confirmOrderRef, currentNode;
    private OrderBasicInfoController basicInfoController;
    private ChooseStoreController chooseStoreController;
    private ChooseItemsStaticOrderController chooseItemsStaticOrderController;
    private ChooseItemsDynamicOrderController chooseItemsDynamicController;
    private ChooseDiscountsStaticOrderController chooseDiscountsStaticOrderController;
    private DynamicDiscountsController chooseDiscountsDynamicController;
    private ConfirmOrderController confirmOrderController;

    private LocalDate orderDate;
    private Customer customer;
    private eOrderType orderType;
    private Store selectedStore;
    private Cart currentCart;
    private HashMap<Store,Cart> mapStoresToCarts;
    private float deliveryFee;
    private double regularItemsSubtotal;
    private double discountItemsSubtotal;
    private BooleanProperty orderInProgress;
    private BooleanProperty orderInProgressDynamic;



    public NewOrderContainerController(){
//        currentCart = new Cart();
        mapStoresToCarts = new HashMap<>();
        orderInProgress = new SimpleBooleanProperty(false);
        orderInProgressDynamic = new SimpleBooleanProperty(false);

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
            chooseItemsStaticOrderController = chooseItemsLoader.getController();
            orderInProgress.bind(chooseItemsStaticOrderController.orderInProgressProperty());


            //currentCart = chooseItemsController.getDummyCart();


            FXMLLoader chooseItemsDynamicLoader = new FXMLLoader();
            chooseItemsDynamicLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseItems/DynamicOrder/ChooseItemsDynamicOrder.fxml"));
            chooseItemsDynamicRef = chooseItemsDynamicLoader.load();
            chooseItemsDynamicController = chooseItemsDynamicLoader.getController();
            orderInProgressDynamic.bind(chooseItemsDynamicController.orderInProgressProperty());



            //4. choose discounts
            FXMLLoader chooseDiscountsLoader = new FXMLLoader();
            chooseDiscountsLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseDiscounts/ChooseDiscountsStaticOrder.fxml"));
            chooseDiscountsRef = chooseDiscountsLoader.load();
            chooseDiscountsStaticOrderController = chooseDiscountsLoader.getController();

            FXMLLoader chooseDiscountsDynamicLoader = new FXMLLoader();
            chooseDiscountsDynamicLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseDiscounts/DynamicOrderDiscounts/DynamicDiscounts.fxml"));
            chooseDiscountsDynamicRef = chooseDiscountsDynamicLoader.load();
            chooseDiscountsDynamicController = chooseDiscountsDynamicLoader.getController();

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
        currentNode = node;
        setTitleLabel();
    }

    private void setTitleLabel() {
        if (currentNode == basicInfoRef){
            //System.out.println("Current node is basicInfo!");
            titleLabel.setText("Basic Order Info");
        }
        if (currentNode == chooseStoresRef){
            //System.out.println("Current node is chooseStores!");
            titleLabel.setText("Choose Stores");
        }
        if (currentNode.equals(chooseItemsStaticOrderRef)){
            //System.out.println("Current node is chooseItemsStatic - and used .equals() this time!");
            titleLabel.setText("Choose Items");
        }
        if (currentNode.equals(chooseItemsDynamicRef)){
            //System.out.println("Current node is chooseItemsStatic - and used .equals() this time!");
            titleLabel.setText("Choose Items DYNAMICALLY");
        }
        if (currentNode.equals(chooseDiscountsRef)){
            titleLabel.setText("Choose Discounts");
        }
        if (currentNode.equals(chooseDiscountsDynamicRef)){
            titleLabel.setText("Choose Discounts DYNAMICALLY");
        }
        if (currentNode.equals(confirmOrderRef)){
            titleLabel.setText("Confirm Order");
        }
    }

    @FXML
    void onNextButtonAction(ActionEvent event) {
        if (checkIfCanContinue()){
            getInformationFromCurrentPage();
            openNextTitledPane();
        }
    }



    private void getInformationFromCurrentPage() {
        if (currentNode.equals(basicInfoRef)){
            this.orderDate = basicInfoController.getOrderDate();
            this.orderType = basicInfoController.getOrderType();
            this.customer = basicInfoController.getSelectedCustomer();
            System.out.println("basicInfoRef returned: {orderDate=" + orderDate + ", orderType= " + orderType +", customer= " + customer +"}");
        }

        if (currentNode.equals(chooseStoresRef)){
            this.selectedStore = chooseStoreController.getSelectedStore();
            mapStoresToCarts.put(selectedStore, new Cart());
            deliveryFee = selectedStore.getDeliveryCost(customer.getLocation());
            System.out.println("chooseStoresRef returned: {selectedStore=" + selectedStore + ", deliveryCost=" + deliveryFee +"}");
        }

        if (currentNode.equals(chooseItemsStaticOrderRef)){
            mapStoresToCarts = chooseItemsStaticOrderController.getMapStoreToCart();
            regularItemsSubtotal = chooseItemsStaticOrderController.getCartSubtotal();
        }

        if (currentNode.equals(chooseItemsDynamicRef)){
            mapStoresToCarts = chooseItemsDynamicController.getMapStoresToCarts();
            regularItemsSubtotal = chooseItemsDynamicController.getCartsSubtotal();
            deliveryFee = chooseItemsDynamicController.getDeliveryFeeTotal();
            System.out.println("chooseItemsDynamicRef returned: " + mapStoresToCarts);
        }

        if (currentNode.equals(chooseDiscountsRef)){
            discountItemsSubtotal = chooseDiscountsStaticOrderController.getDiscountsSubtotal();
            HashMap<Integer, CartItem> discountItemsToAddToCart = chooseDiscountsStaticOrderController.getMapIdsToDiscountCartItems();
            discountItemsToAddToCart.forEach((k,v)->{
                mapStoresToCarts.get(selectedStore).add(v);
            });
        }

        if (currentNode.equals(chooseDiscountsDynamicRef)){
            discountItemsSubtotal = chooseDiscountsDynamicController.getDiscountsSubtotal();
            HashMap<Store,HashMap<Integer,CartItem>> discountItemsToAdd = chooseDiscountsDynamicController.getMapIdsToDiscountCartItemsDynamic();
            discountItemsToAdd.forEach((store,map)->{
                map.forEach((itemId,cartItem)->{
                    mapStoresToCarts.get(store).add(cartItem);
                });
            });
        }
    }

    private boolean checkIfCanContinue() {
        if (currentNode.equals(basicInfoRef)){
            boolean ans = basicInfoController.hasNecessaryInformation();
            if (!ans)
                new AlertInfoBox().display("Missing Information", "Missing Basic Information","Please enter a valid date and choose existing customer in order to continue.");
            return ans;
        }
        if (currentNode.equals(chooseStoresRef)){
            boolean ans = chooseStoreController.hasNecessaryInformation();
            if (!ans)
                new AlertInfoBox().display("Missing Information", "Missing Store Information","Please choose existing store in order to continue.");
            return ans;
        }
        if (currentNode.equals(chooseItemsStaticOrderRef)){
            boolean ans = chooseItemsStaticOrderController.hasNecessaryInformation();
            if (!ans)
                new AlertInfoBox().display("Empty Cart Error", "Empty Cart","Your cart is currently empty. Please enter a some items in order to continue");
            return ans;
        }
        if (currentNode.equals(chooseItemsDynamicRef)){
            boolean ans = chooseItemsDynamicController.hasNecessaryInformation();
            if (!ans)
                new AlertInfoBox().display("Empty Cart Error", "Empty Cart","Your cart is currently empty. Please enter a some items in order to continue");
            return ans;
        }
        return true;
    }

    private void openNextTitledPane() {

        if (currentNode.equals(basicInfoRef)){
            backButton.setDisable(false);
            if (orderType == eOrderType.STATIC_ORDER) {
                chooseStoreController.setCustomer(customer);
                setNodeForPane(chooseStoresRef);
                return;
            }

            if (orderType == eOrderType.DYNAMIC_ORDER) {
                chooseItemsDynamicController.setCustomer(customer);
                setNodeForPane(chooseItemsDynamicRef);
                return;
            }
        }

        if (currentNode.equals(chooseStoresRef)){
            chooseItemsStaticOrderController.setDataForStaticOrder(selectedStore);
            chooseItemsStaticOrderController.fillCustomerData(customer);
            chooseItemsStaticOrderController.setDeliveryFeeProperty(deliveryFee);
            setNodeForPane(chooseItemsStaticOrderRef);
            return;
        }

        if (currentNode.equals(chooseItemsStaticOrderRef)){
            chooseDiscountsStaticOrderController.fillViewsForStaticOrder(customer, selectedStore, mapStoresToCarts.get(selectedStore),deliveryFee,regularItemsSubtotal);
            setNodeForPane(chooseDiscountsRef);
            return;
        }
        if (currentNode.equals(chooseItemsDynamicRef)){
            chooseDiscountsDynamicController.fillViewsBasedOnDynamicOrder(mapStoresToCarts, deliveryFee, regularItemsSubtotal);
            setNodeForPane(chooseDiscountsDynamicRef);
            return;
        }

        if (currentNode.equals(chooseDiscountsRef) || currentNode.equals(chooseDiscountsDynamicRef)){
            confirmOrderController.fillViews(customer, mapStoresToCarts, orderDate);
            nextButton.setDisable(true);
            confirmButton.setVisible(true);
            confirmButton.setDisable(false);
            setNodeForPane(confirmOrderRef);
            return;
        }
    }

    @FXML
    void onBackButtonAction(ActionEvent event) {
        if (currentNode.equals(confirmOrderRef)){
            confirmButton.setDisable(true);
            nextButton.setDisable(false);
            //titleLabel.setText("Choose Discounts");
            setNodeForPane(chooseDiscountsRef);
            return;
        }

        if (currentNode.equals(chooseDiscountsRef)){
            boolean alreadyAddedSomeDiscounts = chooseDiscountsStaticOrderController.isAlreadyAddedSomeDiscounts();
            if (alreadyAddedSomeDiscounts){
                boolean response = new ConfirmBox().display("Warning", "Discounts already added", "If you go back now, your current discounts will be erased. Do you wish to continue?");
                if (!response)
                    return;
                chooseDiscountsStaticOrderController.resetFields();
                confirmOrderController.resetFields();
            }
            setNodeForPane(chooseItemsStaticOrderRef);
            return;
        }
        if (currentNode.equals(chooseDiscountsDynamicRef)){
            boolean alreadyAddedSomeDiscounts = chooseDiscountsDynamicController.isAlreadyAddedDiscounts();
            if (alreadyAddedSomeDiscounts){
                boolean response = new ConfirmBox().display("Warning", "Discounts already added", "If you go back now, your current discounts will be erased. Do you wish to continue?");
                if (!response)
                    return;

                chooseDiscountsDynamicController.resetFields();
                confirmOrderController.resetFields();
            }
            setNodeForPane(chooseItemsDynamicRef);
            return;
        }
        if (currentNode.equals(chooseItemsStaticOrderRef)){
            if (isOrderInProgress()){
                boolean ans = new ConfirmBox().display("Warning: Order in progress","If you go back now, your cart will be emptied", "Do you wish to continue?");

                if (!ans){
                    return;
                } else{
                    chooseItemsStaticOrderController.resetFields();
                    chooseDiscountsStaticOrderController.resetFields();
                    confirmOrderController.resetFields();
                    setNodeForPane(chooseStoresRef);
                    return;
                }
            } else{
                setNodeForPane(chooseStoresRef);
                return;
            }
        }
        if (currentNode.equals(chooseItemsDynamicRef)){
            if (isOrderInProgressDynamic()){
                boolean response = new ConfirmBox().display("Warning: Order in progress","If you go back now, your cart will be emptied", "Do you wish to continue?");
                if (!response)
                    return;
                chooseItemsDynamicController.resetFields();
                chooseDiscountsDynamicController.resetFields();
                confirmOrderController.resetFields();
                backButton.setDisable(true);
                setNodeForPane(basicInfoRef);
                return;
            }
            backButton.setDisable(true);
            setNodeForPane(basicInfoRef);
            return;
        }

        if (currentNode.equals(chooseStoresRef)){
            backButton.setDisable(true);
            setNodeForPane(basicInfoRef);
            return;
        }
    }

    @FXML
    void onConfirmButtonAction(ActionEvent event) {

        ZoneId defaultZoneId = ZoneId.systemDefault();
        Date date = Date.from(orderDate.atStartOfDay(defaultZoneId).toInstant());

        String dateStr = new SimpleDateFormat("dd/MM\tHH:mm").format(date);


        Order order = new Order(customer,
                date,
                orderType,
                deliveryFee,mapStoresToCarts);

        if (orderType==eOrderType.STATIC_ORDER)
            SDMManager.getInstance().addNewStaticOrder(selectedStore, order, customer);

        if (orderType==eOrderType.DYNAMIC_ORDER)
            SDMManager.getInstance().addNewDynamicOrder(order, customer);


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
        basicInfoController.resetAllFields();

        if (orderType == eOrderType.STATIC_ORDER){
            chooseStoreController.resetAllFields();
            chooseItemsStaticOrderController.resetFields();
        } else{
            chooseItemsDynamicController.resetFields();
        }
        chooseDiscountsStaticOrderController.resetFields();

        customer = null;
        orderDate = null;
        mapStoresToCarts.clear();
        deliveryFee = 0;
        selectedStore = null;
    }

    public boolean isOrderInProgress() {
        return orderInProgress.get();
    }

    public BooleanProperty orderInProgressProperty() {
        return orderInProgress;
    }

    public void setOrderInProgress(boolean orderInProgress) {
        this.orderInProgress.set(orderInProgress);
    }

    public boolean isOrderInProgressDynamic() {
        return orderInProgressDynamic.get();
    }

    public BooleanProperty orderInProgressDynamicProperty() {
        return orderInProgressDynamic;
    }

    public void setOrderInProgressDynamic(boolean orderInProgressDynamic) {
        this.orderInProgressDynamic.set(orderInProgressDynamic);
    }

    public void refreshOthers(){
        System.out.println("\n" + TAG + " - refreshOthers() called");
        //currentCart = new Cart();
        mapStoresToCarts = new HashMap<>();
        currentNode = basicInfoRef;
        backButton.setDisable(true);
        confirmButton.setVisible(false);
        confirmButton.setDisable(false);
        setNodeForPane(basicInfoRef);

        basicInfoController.refresh();
        chooseStoreController.refresh();
    }


}
