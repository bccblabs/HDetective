package models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;
import org.parceler.Parcel;

import java.util.List;
/**
 * Created by bski on 1/15/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@Parcel
public class Classifications {
    @JsonProperty
    public List<Classification> predictions;

    public Classifications() {
    }

    public List<Classification> getClassifications() {
        return predictions;
    }

    public void setClassifications(List<Classification> Classifications) {
        this.predictions = Classifications;
    }

}
