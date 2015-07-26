package models;

import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.List;

@Parcel
public class HDSample {
    public String parse_id;
    public String date;
    public String customer_input_label;
    public List<Classification> classifications = new ArrayList<>();

    public String getParse_id() {
        return parse_id;
    }

    public void setParse_id(String parse_id) {
        this.parse_id = parse_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getCustomer_input_label() {
        return customer_input_label;
    }

    public void setCustomer_input_label(String customer_input_label) {
        this.customer_input_label = customer_input_label;
    }

    public List<Classification> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<Classification> classifications) {
        this.classifications = classifications;
    }
}
