package components.PlaceAnOrder.PlaceAnOrderMain;

import Logic.Customers.Customer;
import Logic.Inventory.ePurchaseCategory;
import Logic.Order.Cart;
import Logic.Order.eOrderType;
import Logic.Store.Store;
import components.PlaceAnOrder.BasicInfo.OrderBasicInfoController;
import components.PlaceAnOrder.ChooseDiscounts.ChooseDiscountsController;
import components.PlaceAnOrder.ChooseItems.ChooseItemsController;
import components.PlaceAnOrder.ChooseStores.ChooseStoreController;
import components.PlaceAnOrder.ConfirmOrder.ConfirmOrderController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class NewOrderMainContainerController implements Initializable {

    @FXML
    private AnchorPane newOrderCurrentPage;

    @FXML
    private Button nextButton;

    private Node basicInfoRef, chooseStoresRef, chooseItemsRef, chooseDiscountsRef, confirmOrderRef;
    private OrderBasicInfoController basicInfoController;
    private ChooseStoreController chooseStoreController;
    private ChooseItemsController chooseItemsController;
    private ChooseDiscountsController chooseDiscountsController;
    private ConfirmOrderController confirmOrderController;

    private LocalDate orderDate;
    private Customer customer;
    private ePurchaseCategory purchaseCategory;
    private eOrderType orderTye;
    private Set<Store> setOfStores;
    private Store selectedStore;
    private Cart currentCart;


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
        newOrderCurrentPage.getChildren().clear();
        newOrderCurrentPage.getChildren().add(basicInfoRef);
    }


    @FXML
    void backButtonAction(ActionEvent event) {

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
            if (orderTye == eOrderType.DYNAMIC_ORDER){
                chooseItemsController.setDataForDynamicOrder();
                newOrderCurrentPage.getChildren().add(chooseItemsRef);
            } else if (orderTye == eOrderType.STATIC_ORDER){
                newOrderCurrentPage.getChildren().add(chooseStoresRef);
            }
            return;
        }

        if (newOrderCurrentPage.getChildren().get(0) == chooseStoresRef){
            newOrderCurrentPage.getChildren().clear();
            chooseItemsController.setDataForStaticOrder(selectedStore);
            newOrderCurrentPage.getChildren().add(chooseItemsRef);
            return;
        }
        if (newOrderCurrentPage.getChildren().get(0) == chooseItemsRef && orderTye == eOrderType.STATIC_ORDER){
            newOrderCurrentPage.getChildren().clear();
            chooseDiscountsController.fillViewsForStoreDiscounts(selectedStore);
            chooseDiscountsController.updateBasedOnCart(currentCart);
            newOrderCurrentPage.getChildren().add(chooseDiscountsRef);
            return;
        }
        if (newOrderCurrentPage.getChildren().get(0) == chooseDiscountsRef){
            newOrderCurrentPage.getChildren().clear();
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
        if (newOrderCurrentPage.getChildren().get(0)==basicInfoRef){
            this.orderDate = basicInfoController.getOrderDate();
            this.orderTye = basicInfoController.getOrderType();
            this.customer = basicInfoController.getSelectedCustomer();
        }

        if (newOrderCurrentPage.getChildren().get(0) == chooseStoresRef){
            this.selectedStore = chooseStoreController.getSelectedStore();
        }
        if (newOrderCurrentPage.getChildren().get(0) == chooseStoresRef){
            this.currentCart = chooseItemsController.getCurrentCart();
        }
    }

    private boolean checkIfCanContinue() {
        if (newOrderCurrentPage.getChildren().get(0)==basicInfoRef){
            if (orderDate==null){
                System.out.println("You must supply an Order date!");
                return false;
            }
        }
        if (newOrderCurrentPage.getChildren().get(0)==chooseStoresRef && orderTye == eOrderType.STATIC_ORDER){
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
