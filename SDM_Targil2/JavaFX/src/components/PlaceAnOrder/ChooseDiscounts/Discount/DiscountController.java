package components.PlaceAnOrder.ChooseDiscounts.Discount;

import Logic.Order.Cart;
import Logic.Order.CartItem;
import Logic.Order.StoreItem;
import Logic.Store.DiscountOffer;
import Logic.Store.StoreDiscount;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;

import java.net.URL;
import java.util.ResourceBundle;

public class DiscountController implements Initializable {

    @FXML
    private Text numberText;

    @FXML
    private Label discountNameText;

    @FXML
    private Button useButton;

    @FXML
    private TableView<DiscountOffer> itemsTableview;

    @FXML
    private TableColumn<DiscountOffer, Void> radioButtonColumn;

    @FXML
    private TableColumn<DiscountOffer, Integer> itemIdColumn;

    @FXML
    private TableColumn<DiscountOffer, String> itemNameColumn;

    @FXML
    private TableColumn<DiscountOffer, Integer> specialPriceColumn;

    @FXML
    private TableColumn<DiscountOffer, Double> amoutColumn;

    @FXML
    private Text textDescription;


    private StoreDiscount discount;
    private String operator;
    private BooleanProperty isActive;
    private IntegerProperty remainingTimes;
    private ObservableList<DiscountOffer> discountOffers;
    private ChangeListener<DiscountOffer> discountOfferChangeListener;
    private DiscountOffer selectedDiscountOffer;

    public DiscountController(){
        setIsActive(false);
        setRemainingTimes(0);
        discountOffers = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        isActive.bind(remainingTimesProperty().greaterThan(0));
    }

    public void setDiscountInformation(StoreDiscount discount){
        this.discount = discount;
        discountNameText.setText(discount.getName());
        operator = discount.getDiscountOffers().getOperator();
        discount.getDiscountOffers().getDiscountOffers().forEach(offer->this.discountOffers.add(offer));
        updateTableForDiscount();
    }

    private void updateTableForDiscount() {
        itemIdColumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,Integer>("itemId"));
        amoutColumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,Double>("quantity"));
        specialPriceColumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,Integer>("forAdditional"));
        itemNameColumn.setCellValueFactory(new PropertyValueFactory<DiscountOffer,String>("itemName"));
//        itemNameColumn.setCellValueFactory(cellData->{
//            DiscountOffer offer = cellData.getValue();
//            String itemName = discount.getStore().getNameById(offer.getItemId());
//            return new ReadOnlyStringWrapper(itemName);
//        });




    }

    public void updateBasedOnCart(Cart cart){
        setRemainingTimes(discount.countTimesConditionIsMet(cart));

    }

    public boolean isIsActive() {
        return isActive.get();
    }

    public BooleanProperty isActiveProperty() {
        return isActive;
    }

    public void setIsActive(boolean isActive) {
        this.isActive.set(isActive);
    }

    public int getRemainingTimes() {
        return remainingTimes.get();
    }

    public IntegerProperty remainingTimesProperty() {
        return remainingTimes;
    }

    public void setRemainingTimes(int remainingTimes) {
        this.remainingTimes.set(remainingTimes);
    }
}
