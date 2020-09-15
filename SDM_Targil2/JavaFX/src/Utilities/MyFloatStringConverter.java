package Utilities;

import javafx.util.converter.FloatStringConverter;

public class MyFloatStringConverter extends FloatStringConverter {
    @Override
    public Float fromString(final String value) {
        return value.isEmpty() || !isNumber(value) ? null
                : super.fromString(value);
    }

    public boolean isNumber(String value) {

        try{
            Float parsedFloat = Float.parseFloat(value);
            return parsedFloat>0;

        } catch(NumberFormatException nfe){
            System.out.println("Threw Number Format Exception");
            return false;
        }
//
//        int size = value.length();
//        for (int i = 0; i < size; i++) {
//            if (!Character.isDigit(value.charAt(i))) {
//                return false;
//            }
//        }
//        return size > 0;
    }

}
