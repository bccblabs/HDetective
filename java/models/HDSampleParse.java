package models;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.Date;
import java.util.List;

/**
 * Created by bski on 7/25/15.
 */
@ParseClassName("HDSample")
public class HDSampleParse extends ParseObject {

    public ParseUser getUser() { return getParseUser(getString("user_id"));}

    public void setUser (String user_id) { put ("user_id", user_id);}

    public void setHDPhoto (ParseFile photo) { put ("hd_photo", photo); }

    public ParseFile getHDPhoto () { return getParseFile("hd_photo"); }

    public void setReported (boolean reported) { put("reported", reported);}

    public boolean getReported () { return getBoolean("reported"); }

    public void setReportedDate (Date reportedDate) { put ("reportedDate", reportedDate); }

    public Date getReportedDate () { return getDate("reportedDate"); }

    public void setSerialCode (String serialCode) { put ("serial_code", serialCode); }

    public String getSerialCode () { return getString("serial_code"); }

    public void setClassifiedLabel(String label) { put ("label", label); }

    public String getClassifiedLabel() { return getString("label"); }

    public Double getClassificationProb () { return getDouble("prob"); }

    public void setClassificationProb (Double prob) { put ("prob", prob); }
}
