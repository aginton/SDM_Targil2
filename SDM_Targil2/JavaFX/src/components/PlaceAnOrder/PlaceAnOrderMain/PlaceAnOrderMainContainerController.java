package components.PlaceAnOrder.PlaceAnOrderMain;

import Logic.Customers.Customer;
import Logic.Order.Cart;
import Logic.Order.Order;
import Logic.Order.eOrderType;
import Logic.SDM.SDMManager;
import Logic.Store.Store;
import components.PlaceAnOrder.BasicInfo.OrderBasicInfoController;
import components.PlaceAnOrder.ChooseItems.ChooseItemsController;
import components.PlaceAnOrder.ChooseStores.ChooseStoreController;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;

public class PlaceAnOrderMainContainerController implements Initializable {

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Button nextButton;

    @FXML
    private Button backButton;


    private Node chooseBasicInfoRef, chooseStoresRef, chooseItemsRef;
    private SimpleObjectProperty<Node> currentNode;
    private OrderBasicInfoController basicInfoController;
    private ChooseStoreController chooseStoreController;
    private ChooseItemsController chooseItemsController;

    private Customer customer;
    private Set<Store> setOfStores;
    private Store selectedStore;
    private LocalDate orderDate;
    private eOrderType orderType;
    private Cart currentCart;


    private BooleanProperty hasBasicInfo = new SimpleBooleanProperty(this,"hasBasicInfo",false);
    private boolean canContinue;


    public PlaceAnOrderMainContainerController(){
        orderDate = LocalDate.now();
        currentCart = new Cart();
        currentNode = new SimpleObjectProperty<>();
        setOfStores = new HashSet<>();

        try {
            FXMLLoader basicInfoLoader = new FXMLLoader();
            basicInfoLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/BasicInfo/OrderBasicInfo.fxml"));
            chooseBasicInfoRef = basicInfoLoader.load();
            basicInfoController = basicInfoLoader.getController();

            FXMLLoader staticOrderLoader = new FXMLLoader();
            staticOrderLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseStores/ChooseStore.fxml"));
            chooseStoresRef = staticOrderLoader.load();
            chooseStoreController = staticOrderLoader.getController();

            FXMLLoader chooseItemsLoader = new FXMLLoader();
            chooseItemsLoader.setLocation(getClass().getResource("/components/PlaceAnOrder/ChooseItems/ChooseItems.fxml"));
            chooseItemsRef = chooseItemsLoader.load();
            chooseItemsController = chooseItemsLoader.getController();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainAnchorPane.getChildren().clear();
        setCurrentNode(chooseBasicInfoRef);
        mainAnchorPane.getChildren().add(getCurrentNode());

        backButton.disableProperty().bind(currentNode.isEqualTo(chooseBasicInfoRef));

    }

    @FXML
    void nextButtonAction(ActionEvent event) {
        System.out.println("Current node: " + currentNode.getValue().getId());

        mainAnchorPane.getChildren().clear();

        if (getCurrentNode() == chooseBasicInfoRef){
            orderType= basicInfoController.getOrderType();
            orderDate = basicInfoController.getOrderDate();
            customer = basicInfoController.getSelectedCustomer();

            //need to pass in data to display accurate delivery cost based on user
            if (orderType == eOrderType.STATIC_ORDER){

                if (canContinueFromBasicInfo()){
                    System.out.println("Customer " + customer.getCustomerName() + " needs to choose store for " + orderType + " on date " + orderDate);
                    //chooseStoreController.setDeliveryCost();
                    mainAnchorPane.getChildren().add(chooseStoresRef);
                }
                else{
                    System.out.println("Don't have basic info");
                }
            }

            if (orderType == eOrderType.DYNAMIC_ORDER){
                System.out.println("Customer " + customer.getCustomerName() + " wants a dynamic order" + orderType + " on date " + orderDate);
                mainAnchorPane.getChildren().add(chooseItemsRef);
            }
        }

        if (getCurrentNode() == chooseStoresRef){
            selectedStore = chooseStoreController.getSelectedStore();
            System.out.println("Customer " + customer.getCustomerName() + " chose store "+selectedStore.getStoreName() + " for " + orderType + " on date " + orderDate);
            setOfStores.add(selectedStore);
            //chooseItemsController.setDataForStaticOrder(selectedStore,customer,orderType,orderDate);
            mainAnchorPane.getChildren().add(chooseItemsRef);
        }
        if (getCurrentNode() == chooseItemsRef){
            System.out.println("Confirm was pressed!");
            mainAnchorPane.getChildren().add(chooseBasicInfoRef);
            confirmAction();
        }

        setCurrentNode(mainAnchorPane.getChildren().get(0));
    }



    private boolean canContinueFromBasicInfo() {
        if (orderDate == null){
            Alert a1 = new Alert(Alert.AlertType.ERROR);
            a1.setContentText("Please enter an Order date!");
            a1.setHeaderText(null);
            a1.showAndWait();
        }
        return (orderDate != null) && (orderType != null) && (customer != null);
    }

    private boolean canContinueFromChooseItems(){
        if (currentCart.getCart().size() == 0){
            Alert a1 = new Alert(Alert.AlertType.ERROR);
            a1.setContentText("Cart cannot be empty!");
            a1.setHeaderText(null);
            a1.showAndWait();
            return false;
        }
        return true;
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

    @FXML
    void onBackButtonAction(ActionEvent event) {
        mainAnchorPane.getChildren().clear();


        setCurrentNode(mainAnchorPane.getChildren().get(0));
    }

    public Node getCurrentNode() {
        return currentNode.get();
    }

    public SimpleObjectProperty<Node> currentNodeProperty() {
        return currentNode;
    }

    public void setCurrentNode(Node currentNode) {
        this.currentNode.set(currentNode);
    }

}
