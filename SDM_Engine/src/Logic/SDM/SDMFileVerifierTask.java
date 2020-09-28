package Logic.SDM;

import Resources.Schema.JAXBGenerated.*;
import javafx.beans.property.*;
import javafx.concurrent.Task;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SDMFileVerifierTask extends Task<Boolean> {

    private SuperDuperMarketDescriptor sdmDescriptor;
    private File fileRef;
    private BooleanProperty isValidFile;
    private StringProperty loadingErrorMessage;
    private String errorString;
    private String msg;
    private StringProperty loadingMessage;
    private DoubleProperty p;


    public SDMFileVerifierTask(File file){
        this.fileRef = file;
        loadingMessage = new SimpleStringProperty("");
        isValidFile = new SimpleBooleanProperty(false);
        loadingErrorMessage = new SimpleStringProperty("");
        msg = new String("");
        p = new SimpleDoubleProperty(0);
    }

    public double getP() {
        return p.get();
    }

    public DoubleProperty pProperty() {
        return p;
    }

    public void setP(double p) {
        this.p.set(p);
    }

    @Override
    protected Boolean call() throws Exception {
        return tryToVerifyFile(fileRef);
    }

    private Boolean tryToVerifyFile(File fileRef) {

        JAXBContext jaxbContext = null;

        try {
            jaxbContext = JAXBContext.newInstance(SuperDuperMarketDescriptor.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SuperDuperMarketDescriptor sdm = (SuperDuperMarketDescriptor) jaxbUnmarshaller.unmarshal(fileRef);

            if (isSDMValidAppWise(sdm)) {
                setIsValidFile(true);
                sdmDescriptor = sdm;
                SDMManager.getInstance().loadNewSDMFile(sdmDescriptor);
                setIsValidFile(true);
                return true;
            } else{
                setIsValidFile(false);
                return false;
            }

        } catch (JAXBException e) {
            e.printStackTrace();
            setLoadingErrorMessage(loadingErrorMessage.get().concat("Encountered JAXException: File is not valid schema"));
            setIsValidFile(false);
            return false;
        }
    }

    public boolean isSDMValidAppWise(SuperDuperMarketDescriptor sdm) {
        boolean areItemIdsUnique,areStoreIdsUnique, areCustomerIdsUnique, isStoreUsingUniqueItemIds,
                isStoreUsingExistingItemIds, isEachExistingItemSoldSomewhere,
                areLocationsLegal, areItemsOnSaleSoldAtStore;

        List<SDMStore> sdmStores = sdm.getSDMStores().getSDMStore();

        //Since we expect no duplicates, we can store Item and Store ids in lists
        List<Integer> listOfStoreIds = getListOfStoreIds(sdm.getSDMStores().getSDMStore());
        List<List<Integer>> listOfStoreLocations = getListOfStoreLocations(sdm.getSDMStores().getSDMStore());

        List<Integer> listOfCustomerIds = getListOfCustomerIds(sdm.getSDMCustomers());
        List<List<Integer>> listOfCustomerLocations = getListOfCustomerLocations(sdm.getSDMCustomers().getSDMCustomer());
        List<Integer> listOfItemIds = getListOfItemIds(sdm.getSDMItems().getSDMItem());

        //error 3.2
        areItemIdsUnique = checkListOfIntsUnique(listOfItemIds, "course.java.sdm.engine.SDM-Items");
        updateMessageAndProgress(1,"Check 1: Are all Inventory Item IDs unique?: ", areItemIdsUnique);


        //error 3.3
        areStoreIdsUnique = checkListOfIntsUnique(listOfStoreIds, "course.java.sdm.engine.SDM-Stores");
        updateMessageAndProgress(2,"Check 2: Are all store IDs unique? : ", areStoreIdsUnique);

        areCustomerIdsUnique = checkListOfIntsUnique(listOfCustomerIds, "SDMCustomer");
        updateMessageAndProgress(3,"Check 3: Are customer IDs unique? : ", areCustomerIdsUnique);

        isStoreUsingExistingItemIds = checkItemsSoldExist(sdmStores, listOfItemIds);
        updateMessageAndProgress(4,"Check 4: Do stores only sell items with existing IDs? :", isStoreUsingExistingItemIds);

        isEachExistingItemSoldSomewhere = checkEachExistingItemSoldSomewhere(sdmStores, listOfItemIds);
        updateMessageAndProgress(5,"Check 5: Is every existing item sold somewhere? : ", isEachExistingItemSoldSomewhere);

        isStoreUsingUniqueItemIds = checkStoreUsesUniqueItemIds(sdmStores);
        updateMessageAndProgress(6,"Check 6: Do stores sell items with unique IDs? : ",isStoreUsingUniqueItemIds);

        areLocationsLegal = checkLocationsAreAllowed(listOfStoreLocations, listOfCustomerLocations);
        updateMessageAndProgress(7,"Check 7: Are all customer and store locations legal? : ", areLocationsLegal);


        areItemsOnSaleSoldAtStore = checkIfItemsOnSaleAreSoldAtStores(sdmStores);
        updateMessageAndProgress(8,"Check 8: Do stores only have discounts for items they currently sell? : ", areItemsOnSaleSoldAtStore);

        return (areItemIdsUnique && areStoreIdsUnique && areCustomerIdsUnique &&
                isStoreUsingExistingItemIds && isStoreUsingUniqueItemIds &&
                isEachExistingItemSoldSomewhere && areLocationsLegal && areItemsOnSaleSoldAtStore);

    }

    private void updateMessageAndProgress(int i, String question, boolean answer) {
        updateProgress(i,8);
        msg = msg.concat("\n"+question+answer);
        if (answer == false){
            msg = msg.concat("\n"+errorString + "\n");
        }
        updateMessage(msg);


        try {
            Thread.sleep(250);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean checkIfItemsOnSaleAreSoldAtStores(List<SDMStore> sdmStores) {
        boolean res = true;
        for (SDMStore store: sdmStores){

            if (checkIfStoreHasDiscounts(store)){

                if (!checkIfDiscountItemsAtStoreAreLegal(store)){
                    res = false;
                }

            }
        }
        return res;
    }

    public static boolean checkIfStoreHasDiscounts(SDMStore store) {
        try{
            return store.getSDMDiscounts().getSDMDiscount().size()>0;
        } catch (NullPointerException npe){
            return false;
        }
    }

    private boolean checkIfDiscountItemsAtStoreAreLegal(SDMStore store) {
        errorString = new String("");
        boolean res = true;
        Set<Integer> setOfItemIds = getSetOfItemIdsAtStore(store);

        List<Integer> listOfIfYouBuyIds = getListOfIfYouBuyIdsAtStore(store);

        List<Integer> listThenYouGetIDs = getListOfThenYouGetIdsAtStore(store);
//        StringBuilder sb3 = new StringBuilder("Store " + store.getName() + " has following then-you-get-ids: {");
//        for (Integer i: listThenYouGetIDs){
//            sb3.append(String.valueOf(i)).append(",");
//        }
//        sb3.append("}\n");
//        System.out.println(sb3.toString());

        for (Integer i: listOfIfYouBuyIds){
            if (setOfItemIds.add(i)){
                errorString = errorString.concat(String.format("\n\tStore %d has if-you-buy for item id=%d, but no such item id found in store inventory!",store.getId(),i));

                res =false;
            }
        }

        for (Integer i: listThenYouGetIDs){
            if (setOfItemIds.add(i)){
                errorString = errorString.concat(String.format("\n\tStore %d has then-you-get for item id=%d, but no such item id found in store inventory!",store.getId(),i));
//                setLoadingErrorMessage(loadingErrorMessage.get().concat(
//                        String.format("Store %d has then-you-get for item id=%d, but no such item id found in store inventory!\n",store.getId(),i)
//                ));
                res =false;
            }
        }

        return res;
    }

    public String getLoadingMessage() {
        return loadingMessage.get();
    }

    public StringProperty loadingMessageProperty() {
        return loadingMessage;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage.set(loadingMessage);
    }

    private List<Integer> getListOfCustomerIds(SDMCustomers sdmCustomers) {
        return sdmCustomers.getSDMCustomer().stream().map(customer->customer.getId()).collect(Collectors.toList());
    }

    public List<List<Integer>> getListOfCustomerLocations(List<SDMCustomer> sdmCustomers) {
        List<List<Integer>> res = new ArrayList<>();
        for (SDMCustomer customer: sdmCustomers){
            res.add(getCustomerLocation(customer));
        }
        return res;
    }

    public List<Integer> getListOfItemIdsAtStore(SDMStore store){
        List<Integer> res = new ArrayList<>();
        for (SDMSell sdmSell: store.getSDMPrices().getSDMSell()){
            res.add(sdmSell.getItemId());
        }
        return res;
    }
    public Set<Integer> getSetOfItemIdsAtStore(SDMStore store){
        Set<Integer> res = new HashSet<>();
        for (SDMSell sdmSell: store.getSDMPrices().getSDMSell()){
            res.add(sdmSell.getItemId());
        }
        return res;
    }
    public List<Integer> getListOfIfYouBuyIdsAtStore(SDMStore store){
        List<Integer> res = new ArrayList<>();
        for (SDMDiscount discount: store.getSDMDiscounts().getSDMDiscount()){
            res.add(discount.getIfYouBuy().getItemId());
        }
        return res;
    }

    public List<Integer> getListOfThenYouGetIdsAtStore(SDMStore store){
        List<Integer> res = new ArrayList<>();
        for (SDMDiscount discount: store.getSDMDiscounts().getSDMDiscount()){
            for (SDMOffer offer: discount.getThenYouGet().getSDMOffer()){
                res.add(offer.getItemId());
            }
        }
        return res;
    }

    public List<List<Integer>> getListOfStoreLocations(List<SDMStore> sdmStores) {
        List<List<Integer>> res = new ArrayList<>();
        for (SDMStore store: sdmStores){
            res.add(getStoreLocation(store));
        }
        return res;
    }

    public List<Integer> getStoreLocation(SDMStore store){
        List<Integer> res = new ArrayList<>();
        res.add(store.getLocation().getX());
        res.add(store.getLocation().getY());
        return res;
    }

    public List<Integer> getCustomerLocation(SDMCustomer customer){
        List<Integer> res = new ArrayList<>();
        res.add(customer.getLocation().getX());
        res.add(customer.getLocation().getY());
        return res;
    }

    public List<Integer> getListOfStoreIds(List<SDMStore> sdmStores) {
        List<Integer> res = new ArrayList<>();

        for (SDMStore store: sdmStores){
            res.add(store.getId());
        }
        return  res;
    }

    public List<Integer> getListOfItemIds(List<SDMItem> sdmItems) {
        List<Integer> res = new ArrayList<>();

        for (SDMItem item: sdmItems){
            res.add(item.getId());
        }
        return  res;
    }


    private boolean checkLocationsAreAllowed(List<List<Integer>> listOfStoreLocations, List<List<Integer>> listOfCustomerLocations) {
        Boolean res = true;
        Set<List<Integer>> set = new HashSet<>();
        errorString = new String("");

        for (List<Integer> loc: listOfStoreLocations){
            if (set.add(loc) == false){
                //setLoadingErrorMessage(loadingErrorMessage.get().concat(String.format("Error: Store location (%d,%d) appears more than once\n",loc.get(0), loc.get(1))));
                errorString = errorString.concat(String.format("\n\tError: Store location (%d,%d) appears more than once",loc.get(0), loc.get(1)));
                res = false;
            }
        }
        for (List<Integer> loc: listOfCustomerLocations){
            if (set.add(loc) == false){
                errorString = errorString.concat(String.format("\n\tError: Customer location (%d,%d) appears more than once",loc.get(0), loc.get(1)));
                res = false;
            }
        }
        return res;
    }


    public boolean checkEachExistingItemSoldSomewhere(List<SDMStore> sdmStores, List<Integer> listOfExistingItemIds) {
        boolean res = true;
        Set<Integer> itemsSold = new HashSet<>();
        errorString = new String("");

        for (SDMStore store: sdmStores){
            for (SDMSell sold: store.getSDMPrices().getSDMSell()){
                itemsSold.add(sold.getItemId());
            }
        }

        for (Integer existingItem: listOfExistingItemIds){
            if (!itemsSold.contains(existingItem)){
                //setLoadingErrorMessage(loadingErrorMessage.get().concat("Error: Item with id= " + existingItem + " is not sold in any store."));
                errorString=errorString.concat("\n\tError: Item with id= " + existingItem + " is not sold in any store.");
                res = false;
            }
        }
        return res;
    }

    public boolean checkItemsSoldExist(List<SDMStore> sdmStores, List<Integer> listOfAllowedIds) {
        errorString = new String("");
        boolean res = true;

        for (SDMStore store: sdmStores){
            List<SDMSell> itemsSold = store.getSDMPrices().getSDMSell();

            for (SDMSell sold: itemsSold){
                if (!listOfAllowedIds.contains(sold.getItemId())){

                    errorString = errorString.concat("\n\tError: Store-Id = "+ store.getId() + " has item with item-Id= " + sold.getItemId() + ", but no such id exists in SDMItems!");
                    res = false;
                }
            }
        }
        return res;
    }

    public SuperDuperMarketDescriptor getSdmDescriptor() {
        return sdmDescriptor;
    }

    public boolean checkStoreUsesUniqueItemIds(List<SDMStore> sdmStores) {
        boolean res = true;
        errorString = new String("");
        for (SDMStore store: sdmStores){
            List<SDMSell> itemsSold = store.getSDMPrices().getSDMSell();
            Set<Integer> tmpSet = new HashSet<>();

            for (SDMSell sold: itemsSold){
                if (!tmpSet.add(sold.getItemId())){

                    errorString = errorString.concat("\n\tError: Store-Id = " + store.getId() + " is selling multiple items with id =" + sold.getItemId());
                    res = false;
                }
            }
        }

        return res;
    }

    public boolean checkListOfIntsUnique(List<Integer> inputList, String problematicType){
        boolean res = true;
        Set<Integer> tmpSet = new HashSet<>();
        errorString = new String("");
        for (Integer num: inputList){
            if (!tmpSet.add(num)){
                errorString = errorString.concat("\n\tError: id=" + num + " is not unique for type " + problematicType);
                res = false;
            }
        }
        return res;
    }


    public boolean isIsValidFile() {
        return isValidFile.get();
    }

    public BooleanProperty isValidFileProperty() {
        return isValidFile;
    }

    public void setIsValidFile(boolean isValidFile) {
        this.isValidFile.set(isValidFile);
    }

    public void setLoadingErrorMessage(String loadingErrorMessage) {
        this.loadingErrorMessage.set(loadingErrorMessage);
    }
}
