package Util;

public class Util {
    public static String getSampleProductName (String serial_code) {
        switch (serial_code) {
            case "WD4000FYYZ-01UL1B1":
                return "Enterprise 4TB";
            case "WD60EZRX-00MVLB1":
                return "Green 6TB";
            case "WD30EURS-63SPKY0":
                return "GreenPower 3TB";
            case "WD5000AAKX-00ERMA0":
                return "WD Blue 500GB";
        }
        return "Not Found";
    }
}
