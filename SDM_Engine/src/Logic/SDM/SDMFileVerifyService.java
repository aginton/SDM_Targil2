//package Logic.SDM;
//
//import Resources.Schema.JAXBGenerated.SuperDuperMarketDescriptor;
//import javafx.beans.property.BooleanProperty;
//import javafx.beans.property.SimpleBooleanProperty;
//import javafx.beans.property.SimpleStringProperty;
//import javafx.beans.property.StringProperty;
//import javafx.concurrent.Service;
//import javafx.concurrent.Task;
//
//import java.io.File;
//
//public class SDMFileVerifyService extends Service<SuperDuperMarketDescriptor> {
//
//    private SuperDuperMarketDescriptor sdmDescriptor;
//    private File fileRef;
//    private String loadingErrorMessage;
//    private StringProperty verifyStatus;
//    private BooleanProperty isValidFile;
//
//    public SDMFileVerifyService(File file){
//        sdmDescriptor = null;
//        fileRef = file;
//        isValidFile = new SimpleBooleanProperty(false);
//        verifyStatus = new SimpleStringProperty("");
//    }
//
//    @Override
//    protected Task<SuperDuperMarketDescriptor> createTask() {
//        return new SDMFileVerifierTask(fileRef);
////        this.verifyStatus.bind(task.messageProperty());
////        this.isValidFile.bind(task.isValidFileProperty());
////        return task;
//    }
//
//    public SuperDuperMarketDescriptor getSdmDescriptor() {
//        return sdmDescriptor;
//    }
//
//    public String getVerifyStatus() {
//        return verifyStatus.get();
//    }
//
//    public StringProperty verifyStatusProperty() {
//        return verifyStatus;
//    }
//
//    public void setVerifyStatus(String verifyStatus) {
//        this.verifyStatus.set(verifyStatus);
//    }
//
//    public boolean isIsValidFile() {
//        return isValidFile.get();
//    }
//
//    public BooleanProperty isValidFileProperty() {
//        return isValidFile;
//    }
//
//    public void setIsValidFile(boolean isValidFile) {
//        this.isValidFile.set(isValidFile);
//    }
//}
