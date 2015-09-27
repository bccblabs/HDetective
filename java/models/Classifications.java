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
    public List<Classification> top_3;

    public Classifications() {
    }

    public List<Classification> getClassifications() {
        return top_3;
    }

    public void setClassifications(List<Classification> Classifications) {
        this.top_3 = Classifications;
    }

}
