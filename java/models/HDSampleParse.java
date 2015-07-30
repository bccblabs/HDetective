package models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import java.util.Date;
/**
 * Created by bski on 7/25/15.
 */
@ParseClassName("HDSample")
public class HDSampleParse extends ParseObject {

    public void setHDPhoto (ParseFile photo) { put ("hd_photo", photo); }

    public ParseFile getHDPhoto () { return getParseFile("hd_photo"); }

    public void setSerialCode (String serialCode) { put ("serial_code", serialCode); }

    public String getSerialCode () { return getString("serial_code"); }

    public void setClassifiedLabel(String label) { put ("label", label); }

    public String getClassifiedLabel() { return getString("label"); }

    public void setProductName (String product_name) { put ("product_name", product_name); }

    public String getProductName () { return getString ("product_name"); }
}
