package Util;

import android.text.TextUtils;
import java.util.List;

public class Util {

    public static double parseDouble(String val, double defValue){
        if(TextUtils.isEmpty(val)) return defValue;
        try{
            return Double.parseDouble(val);
        }catch(NumberFormatException e){
            return defValue;
        }
    }

    public static String getSampleProductName (String serial_code) {
        switch (serial_code) {
            case "WD4000FYYZ-01UL1B1":
                return "Enterprise 4TB";
            case "WD60EZRX-00MVLB1":
                return "Green 6TB";
            case "WD30EURS-63SPKY0":
                return "GreenPower 3TB";
        }
        return "Not Found";
    }

    public static String joinStrings (List<String> strings, String delimiter) {
        String result = "";
        for (String x:strings) {
            String hd_string = null;
            switch (x) {
                case "Dell_2TB": {
                    hd_string = "Dell (Unlabeled Hardrive) 2 TB";
                    break;
                }
                case "WD_Black_Enterprise_500Gb": {
                    hd_string = "Western Digital Enterprise Black 500 GB";
                    break;
                }
                case "WD_Red_4.0TB": {
                    hd_string = "Western Digital Red 4 TB";
                    break;
                }
                case "WD_Green_6.0TB": {
                    hd_string = "Western Digital Green 6 TB";
                    break;
                }
                case "WD_Green_power": {
                    hd_string = "Western Digital Green Power";
                    break;
                }
                case "WD_Red_6.0TB": {
                    hd_string = "Western Digital Red 6 TB";
                    break;
                }
                case "not_WD": {
                    hd_string = "Not Western Digital Manufactured";
                    break;
                }
                case "WD": {
                    hd_string = "Western Digital Manufactured";
//                    hd_string = "WD";
                    break;
                }
                case "small_board" : {
                    hd_string = "Small Board";
                    break;
                }
                case "large_board": {
                    hd_string = "Large Board";
                    break;
                }
                case "non_WD": {
                    hd_string = "Not Western Digital Manufactured";
                    break;
                }
            }
            if (hd_string != null)
                result += hd_string + delimiter;
        }
        return result.substring(0, result.length()-1);
    }

}
