package Logic.UtilityMethods;

import java.io.File;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class Util {

    public static String readFilePath() {
        File f;
        Scanner in = new Scanner(System.in);

        while (true) {
            try {
                System.out.println("Enter file path. To go back to main menu, enter 'cancel': ");
                String fileName = in.nextLine().trim();

                if (fileName.equalsIgnoreCase("cancel"))
                    return "q";

                else if (fileName.length() <= 3 || !fileName.substring(fileName.length() - 3).toLowerCase().equals("xml")) {
                    System.out.println("Error: Invalid Path. Please only enter path to existing XML file\n");
                } else {
                    f = new File(fileName);
                    if (f.exists())
                        return fileName;
                    System.out.println("Could not load file at location: " + fileName);
                }

            } catch (NoSuchElementException | IllegalStateException e) {
                System.out.println("Cannot open: " + e.getMessage());
            } catch (SecurityException se) {
                System.out.println("Error: Security exception - " + se.getMessage());
            } catch (NullPointerException npe) {
                System.out.println("Null pointer exception: " + npe.getMessage());
            }
        }
    }


}
