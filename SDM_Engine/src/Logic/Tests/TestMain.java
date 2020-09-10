package Logic.Tests;

import Logic.SDM.SDMFileVerifier;
import Logic.SDM.SDMManager;

import java.io.File;

public class TestMain {


    public static void main(String[] args) {
        File file = new File("C:\\Users\\Adam\\Desktop\\Java Course\\SDM_Targil2\\SDM_Engine\\src\\Resources\\XML\\ex2-small.xml");

        SDMFileVerifier sdmFileVerifier = new SDMFileVerifier(file);
        SDMManager sdmManager = SDMManager.getInstance();
        sdmManager.loadNewSDMFile(sdmFileVerifier);

        if (sdmManager.getIsValidFile()){
            System.out.println("YES");
        }else{
            System.out.println("NO");
        }


    }

}
