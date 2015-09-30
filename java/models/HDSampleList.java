package models;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

/**
 * Created by bski on 9/29/15.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class HDSampleList {
    @JsonProperty
    public List<HDSample> samples;

    public List<HDSample> getSamples() {
        return samples;
    }

    public void setSamples(List<HDSample> samples) {
        this.samples = samples;
    }
}
