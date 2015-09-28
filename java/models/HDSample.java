package models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class HDSample {
    public String date;
    public String testerId;
    public String imageData;
    public String userHddLabel;
    public String hddClzLabel;
    public String s3Url;

//    public String barcode;
//
//    public void setBarcode (String serial_code) {
//        this.barcode = serial_code;
//    }
//
//    public String getBarcode() {
//        return barcode;
//    }

    public void setImageData(String image_data) {
        this.imageData = image_data;
    }

    public String getImageData () {
        return imageData;
    }

    public void setS3Url (String s3_url) {
        this.s3Url = s3_url;
    }

    public String getS3Url () {
        return s3Url;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return date;
    }

    public void setHddClzLabel(String label) {
        this.hddClzLabel = label;
    }

    public String getHddClzLabel() {
        return hddClzLabel;
    }

    public void setUserHddLabel(String product_name) {
        this.userHddLabel = product_name;
    }

    public String getUserHddLabel() {
        return userHddLabel;
    }

    public void setTesterId (String tester_id) {
        this.testerId = tester_id;
    }

    public String getTesterId () {
        return testerId;
    }

}
