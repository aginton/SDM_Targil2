package Logic.SDM;

import Resources.Schema.JAXBGenerated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SDMFileVerifier {
    private SuperDuperMarketDescriptor sdmDescriptor;
    private File fileRef;
    private String loadingErrorMessage;
    private Boolean isValidFile;


    ///////////////////////////////////////////////////////////////////////////////////
    public SDMFileVerifier(){
        sdmDescriptor = null;
        fileRef = null;
        isValidFile = false;
        loadingErrorMessage = "No File provided";
    }


    public SDMFileVerifier(File file){
        this.fileRef = file;
        this.loadingErrorMessage = "";
        JAXBContext jaxbContext = null;

        try {
            jaxbContext = JAXBContext.newInstance(SuperDuperMarketDescriptor.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            SuperDuperMarketDescriptor sdm = (SuperDuperMarketDescriptor) jaxbUnmarshaller.unmarshal(file);

            if (isSDMValidAppWise(sdm)) {
                isValidFile = true;
                sdmDescriptor = sdm;
            } else{
                isValidFile = false;
                System.out.println(loadingErrorMessage);
            }

        } catch (JAXBException e) {
            e.printStackTrace();
            loadingErrorMessage = loadingErrorMessage.concat("Encountered JAXException: File is not valid schema");
        }
    }

    public SDMFileVerifier(SDMFileVerifier sdmFileVerifier){
        this.fileRef=sdmFileVerifier.getFileRef();
        this.sdmDescriptor=sdmFileVerifier.getSDMDescriptor();
        this.loadingErrorMessage="";
        this.isValidFile=true;
    }
    ///////////////////////////////////////////////////////////////////////////////////

    ///////////////////////////////////////////////////////////////////////////////////
    public String getLoadingErrorMessage() {
        return loadingErrorMessage;
    }

    public void setLoadingErrorMessage(String loadingErrorMessage) {
        this.loadingErrorMessage = loadingErrorMessage;
    }

    public SuperDuperMarketDescriptor getSDMDescriptor() {
        return sdmDescriptor;
    }

    public void setSDMDescriptor(SuperDuperMarketDescriptor sdmDescriptor) {
        this.sdmDescriptor = sdmDescriptor;
    }

    public Boolean getIsValidFile() {
        return isValidFile;
    }

    public void setIsValidFile(Boolean validFile) {
        isValidFile = validFile;
    }

    public File getFileRef() {
        return fileRef;
    }

    public void setFileRef(File fileRef) {
        this.fileRef = fileRef;
    }

    ///////////////////////////////////////////////////////////////////////////////////

    public boolean isSDMValidAppWise(SuperDuperMarketDescriptor sdm) {
        boolean areItemIdsUnique,areStoreIdsUnique,isStoreUsingUniqueItemIds,
                isStoreUsingExistingItemIds, isEachExistingItemSoldSomewhere,
                areLocationsLegal, areCustomerIdsUnique;

        List<SDMStore> sdmStores = sdm.getSDMStores().getSDMStore();

        //Since we expect no duplicates, we can store Item and Store ids in lists
        List<Integer> listOfStoreIds = getListOfStoreIds(sdm.getSDMStores().getSDMStore());
        List<List<Integer>> listOfStoreLocations = getListOfStoreLocations(sdm.getSDMStores().getSDMStore());

        List<Integer> listOfCustomerIds = getListOfCustomerIds(sdm.getSDMCustomers());
        List<List<Integer>> listOfCustomerLocations = getListOfCustomerLocations(sdm.getSDMCustomers().getSDMCustomer());

        List<Integer> listOfItemIds = getListOfItemIds(sdm.getSDMItems().getSDMItem());


        //error 3.2
        areItemIdsUnique = checkListOfIntsUnique(listOfItemIds, "course.java.sdm.engine.SDM-Items");
        //error 3.3
        areStoreIdsUnique = checkListOfIntsUnique(listOfStoreIds, "course.java.sdm.engine.SDM-Stores");
        //error 3.4
        isStoreUsingExistingItemIds = checkItemsSoldExist(sdmStores, listOfItemIds);
        //error 3.5
        isEachExistingItemSoldSomewhere = checkEachExistingItemSoldSomewhere(sdmStores, listOfItemIds);
        //error 3.6
        isStoreUsingUniqueItemIds = checkStoreUsesUniqueItemIds(sdmStores);
        //error 3.7
        //areLocationsLegal = checkLocationsAreAllowed(sdmStores);
        areLocationsLegal = checkLocationsAreAllowed(listOfStoreLocations, listOfCustomerLocations);


        areCustomerIdsUnique = checkListOfIntsUnique(listOfCustomerIds, "SDMCustomer");

        return (areItemIdsUnique && areStoreIdsUnique && areCustomerIdsUnique && isStoreUsingExistingItemIds && isStoreUsingUniqueItemIds && isEachExistingItemSoldSomewhere && areLocationsLegal);
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

        for (List<Integer> loc: listOfStoreLocations){
            if (set.add(loc) == false){
                loadingErrorMessage = loadingErrorMessage.concat(String.format("Error: Store location (%d,%d) appears more than once\n",loc.get(0), loc.get(1)));
                res = false;
            }
        }
        for (List<Integer> loc: listOfCustomerLocations){
            if (set.add(loc) == false){
                loadingErrorMessage = loadingErrorMessage.concat(String.format("Error: Customer location (%d,%d) appears more than once\n",loc.get(0), loc.get(1)));
                res = false;
            }
        }
        return res;
    }


    public boolean checkEachExistingItemSoldSomewhere(List<SDMStore> sdmStores, List<Integer> listOfExistingItemIds) {
        boolean res = true;
        Set<Integer> itemsSold = new HashSet<>();

        for (SDMStore store: sdmStores){
            for (SDMSell sold: store.getSDMPrices().getSDMSell()){
                itemsSold.add(sold.getItemId());
            }
        }

        for (Integer existingItem: listOfExistingItemIds){
            if (!itemsSold.contains(existingItem)){
                loadingErrorMessage = loadingErrorMessage.concat("Error: Item with id= " + existingItem + " is not sold in any store.");
                res = false;
            }
        }
        return res;
    }

    public boolean checkItemsSoldExist(List<SDMStore> sdmStores, List<Integer> listOfAllowedIds) {
        boolean res = true;

        for (SDMStore store: sdmStores){
            List<SDMSell> itemsSold = store.getSDMPrices().getSDMSell();

            for (SDMSell sold: itemsSold){
                if (!listOfAllowedIds.contains(sold.getItemId())){
                    loadingErrorMessage = loadingErrorMessage.concat("Error: Store-Id = "+ store.getId() + " has item with item-Id= " + sold.getItemId() + ", but no such id exists in SDMItems!");
                    res = false;
                }
            }
        }
        return res;
    }

    public boolean checkStoreUsesUniqueItemIds(List<SDMStore> sdmStores) {
        boolean res = true;

        for (SDMStore store: sdmStores){
            List<SDMSell> itemsSold = store.getSDMPrices().getSDMSell();
            Set<Integer> tmpSet = new HashSet<>();

            for (SDMSell sold: itemsSold){
                if (!tmpSet.add(sold.getItemId())){
                    loadingErrorMessage = loadingErrorMessage.concat("Error: Store-Id = " + store.getId() + " is selling multiple items with id =" + sold.getItemId());
                    res = false;
                }
            }
        }

        return res;
    }

    public boolean checkListOfIntsUnique(List<Integer> inputList, String problematicType){
        boolean res = true;
        Set<Integer> tmpSet = new HashSet<>();

        for (Integer num: inputList){
            if (!tmpSet.add(num)){
                loadingErrorMessage = loadingErrorMessage.concat("Error: id=" + num + " is not unique for type " + problematicType + "\n");
                res = false;
            }
        }
        return res;
    }
}
