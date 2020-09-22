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
import components.PlaceAnOrder.ChooseStores.ChooseStoreController;
import components.PlaceAnOrder.ConfirmOrder.ConfirmOrderController;
import components.PlaceAnOrder.SuccessOrError.ConfirmBox;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class NewOrderMainContainerController implements Initializable {

    @FXML
    private AnchorPane newOrderCurrentPage;

    @FXML
    private Button nextButton;

    @FXML
    private Button confirmButton;

    @FXML
    private AnchorPane orderSummaryAnchorPane;


    private Node basicInfoRef, chooseStoresRef, chooseItemsRef, chooseDiscountsRef, confirmOrderRef;
    private OrderBasicInfoController basicInfoController;
    private ChooseStoreController chooseStoreController;
    private ChooseItemsController chooseItemsController;
    private ChooseDiscountsController chooseDiscountsController;
    private ConfirmOrderController confirmOrderController;

    private LocalDate orderDate;
    private Customer customer;
    private eOrderType orderType;
    private Set<Store> setOfStores;
    private Store selectedStore;
    private Cart currentCart;
    private float deliveryFee;


    public NewOrderMainContainerController(){
        currentCart = new Cart();
        setOfStores = new HashSet<>();

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
            chooseItemsRef = chooseItemsLoader.load();
            chooseItemsController = chooseItemsLoader.getController();
            chooseItemsController.getAddToCartButton().setOnAction(e->{
                currentCart = chooseItemsController.getCurrentCart();
                System.out.println("NewOrderMain received cart: " + currentCart);
            });

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

    void confirmAction() {

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

        currentCart = chooseItemsController.getCurrentCart();

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
        chooseItemsController.emptyCurrentCart();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        newOrderCurrentPage.getChildren().clear();
        newOrderCurrentPage.getChildren().add(basicInfoRef);
    }


    @FXML
    void backButtonAction(ActionEvent event) {

        if (newOrderCurrentPage.getChildren().get(0) == chooseStoresRef){
            swapCurrentPage(basicInfoRef);
            return;
        }

        if (newOrderCurrentPage.getChildren().get(0) == chooseItemsRef && orderType == eOrderType.STATIC_ORDER){
            boolean ans = ConfirmBox.display("Go Back?", "Are you sure you want to go back?");
            if (ans){
                swapCurrentPage(chooseStoresRef);
            }

            return;
        }
        if (newOrderCurrentPage.getChildren().get(0) == chooseItemsRef && orderType == eOrderType.DYNAMIC_ORDER){
            swapCurrentPage(basicInfoRef);
            return;
        }
        if (newOrderCurrentPage.getChildren().get(0) == chooseDiscountsRef){
            swapCurrentPage(chooseItemsRef);
            return;
        }

        if (newOrderCurrentPage.getChildren().get(0) == confirmOrderRef){
            swapCurrentPage(chooseDiscountsRef);
            return;
        }
    }

    private void swapCurrentPage(Node newCurrentNode) {
        newOrderCurrentPage.getChildren().clear();
        newOrderCurrentPage.getChildren().add(newCurrentNode);
    }

    @FXML
    void nextButtonAction(ActionEvent event) {
        getInformationFromCurrentPage();
        if (checkIfCanContinue()){
            goToNextPage();
        }
    }

    private void goToNextPage() {
        if (newOrderCurrentPage.getChildren().get(0)==basicInfoRef){
            newOrderCurrentPage.getChildren().clear();
            if (orderType == eOrderType.DYNAMIC_ORDER){
                chooseItemsController.setDataForDynamicOrder();
                newOrderCurrentPage.getChildren().add(chooseItemsRef);
            } else if (orderType == eOrderType.STATIC_ORDER){
                chooseStoreController.setCustomer(customer);
                newOrderCurrentPage.getChildren().add(chooseStoresRef);
            }
            return;
        }

        if (newOrderCurrentPage.getChildren().get(0) == chooseStoresRef){
            newOrderCurrentPage.getChildren().clear();
            chooseItemsController.setDataForStaticOrder(selectedStore);
            chooseItemsController.setDeliveryFeeProperty(deliveryFee);
            newOrderCurrentPage.getChildren().add(chooseItemsRef);
            return;
        }
        if (newOrderCurrentPage.getChildren().get(0) == chooseItemsRef && orderType == eOrderType.STATIC_ORDER){
            newOrderCurrentPage.getChildren().clear();
            chooseDiscountsController.fillViewsBasedOnStoreAndCart(selectedStore, currentCart);
            newOrderCurrentPage.getChildren().add(chooseDiscountsRef);
            return;
        }
        if (newOrderCurrentPage.getChildren().get(0) == chooseDiscountsRef){
            newOrderCurrentPage.getChildren().clear();
            confirmOrderController.fillViewsForData(customer,orderDate, orderType,setOfStores,currentCart,deliveryFee);
            newOrderCurrentPage.getChildren().add(confirmOrderRef);
            return;
        }

        if (newOrderCurrentPage.getChildren().get(0) == confirmOrderRef){
            System.out.println("Do something on confirm order...");
            newOrderCurrentPage.getChildren().clear();
            newOrderCurrentPage.getChildren().add(basicInfoRef);
            return;
        }

    }

    private void getInformationFromCurrentPage() {
        if (newOrderCurrentPage.getChildren().get(0) == chooseStoresRef){
            //currentCart = new Cart();
            this.selectedStore = chooseStoreController.getSelectedStore();
            setOfStores.clear();
            setOfStores.add(selectedStore);
            this.deliveryFee = selectedStore.getDeliveryCost(customer.getLocation());
            System.out.println("getInformationFromCurrentPage() returned: {selectedStore=" + selectedStore + ", deliveryCost=" +
                    deliveryFee +"}");
        }
        if (newOrderCurrentPage.getChildren().get(0) == chooseItemsRef){
            this.currentCart = chooseItemsController.getCurrentCart();
            System.out.println("getInformationFromCurrentPage() returned: {currentCart=" + currentCart +"}");
        }

        if (newOrderCurrentPage.getChildren().get(0)==basicInfoRef){
            this.orderDate = basicInfoController.getOrderDate();
            this.orderType = basicInfoController.getOrderType();
            this.customer = basicInfoController.getSelectedCustomer();
            System.out.println("getInformationFromCurrentPage() returned: {orderDate=" +
                    orderDate + ", orderType= " + orderType +", customer= " + customer +"}");
        }

        if (newOrderCurrentPage.getChildren().get(0)==chooseDiscountsRef){
            HashMap<Integer, CartItem> discountItemsToAddToCart = chooseDiscountsController.getDiscountItemsToAddToCart();
            discountItemsToAddToCart.forEach((k,v)->{
                currentCart.add(v);
            });
        }
    }

    private boolean checkIfCanContinue() {
        if (newOrderCurrentPage.getChildren().get(0)==basicInfoRef){
            if (orderDate==null){
                System.out.println("You must supply an Order date!");
                return false;
            }
        }
        if (newOrderCurrentPage.getChildren().get(0)==chooseStoresRef && orderType == eOrderType.STATIC_ORDER){
            if (selectedStore==null){
                System.out.println("You must select a store!");
                return false;
            }
        }
        if (newOrderCurrentPage.getChildren().get(0)==chooseItemsRef){
            if (currentCart.getCart().size()==0){
                System.out.println("Cart can't be empty!");
                return false;
            }
        }
        return true;
    }


}
