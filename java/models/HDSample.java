package models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.parceler.Parcel;

import java.util.List;

@Parcel
@JsonIgnoreProperties(ignoreUnknown = true)
public class HDSample {
    @JsonProperty
    public String barcode;
    @JsonProperty
    public String clzRes;
    @JsonProperty
    public String imageData;
    @JsonProperty
    public String s3Url;
    @JsonProperty
    public String date;
    @JsonProperty
    public String hddClzLabel;
    @JsonProperty
    public String userHddLabel;
    @JsonProperty
    public String testerId;
    @JsonProperty
    public List<Classification> topFive;

    public HDSample() {
    }

    public void setBarcode (String serial_code) {
        this.barcode = serial_code;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setClzRes (String clzRes_) { this.clzRes = clzRes_; }

    public String getClzRes () { return clzRes; }

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

    public List<Classification> getTopFive() {
        return topFive;
    }

    public void setTopFive(List<Classification> topFive) {
        this.topFive = topFive;
    }

}
