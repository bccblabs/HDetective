package models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class HDSample {
    public String parse_id;
    public String date;
    public String serial_code;
    public byte[] image_data;
    public String label;
    public String product_name;

    public String getParse_id() {
        return parse_id;
    }

    public void setParse_id(String parse_id) {
        this.parse_id = parse_id;
    }

    public String getDate() {
        return date;
    }

    public void setSerial_code(String serial_code) {
        this.serial_code = serial_code;
    }

    public byte[] getImage_data() {
        return image_data;
    }

    public void setImage_data(byte[] image_data) {
        this.image_data = image_data;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSerial_code() {
        return serial_code;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }
}
