package Logic.SDM;

import javafx.concurrent.Service;
import javafx.concurrent.Task;

import java.io.File;

public class SDMVerifierService extends Service<Boolean> {

    private File file;
    private Boolean res;
    public SDMVerifierService(File file){
        this.file = file;


    }
    @Override
    protected Task<Boolean> createTask() {
        if (file != null){
            System.out.println("SDMVerifierService received non-null file!");
        }
        else{
            System.out.println("SDMVerifierService received null for file!");
        }
        return new SDMFileVerifierTask(file);
    }
}
