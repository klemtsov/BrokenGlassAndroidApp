package ru.sbrf.zsb.android.rorb;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Date;
import java.util.StringTokenizer;

import ru.sbrf.zsb.android.helper.AddressConstants;
import ru.sbrf.zsb.android.helper.Utils;

/**
 * Created by Администратор on 27.05.2016.
 */
public class Address extends RefObject {
    private int mOsbCode;
    private String mCity;
    private String mAddressName;
    private String mLocation;
    private Float mLatitude;
    private Float mLongitude;
    private Date mUpdateAt;
    private String mUpdateBy;

    @Override
    public String toString() {
        return String.format("%s, %s, %s", mCity, mName, mAddressName );
    }

    public Address()
    {

    }

    public Address(Address address)
    {
        this.mId = address.getId();
        this.mOsbCode = address.getOsbCode();
        this.mName = address.getName();
        this.mCity = address.getCity();
        this.mAddressName = address.getAddressName();
        this.mLocation = address.getLocation();
        this.mLatitude = address.getLatitude();
        this.mLongitude = address.getLongitude();
        this.mUpdateBy = address.getUpdateBy();
        this.mUpdateAt = (Date)address.getUpdateAt();
    }

    public Address(JSONObject json) throws JSONException {
        mId = json.getInt(AddressConstants.ID_TAG);
        mOsbCode = json.getInt(AddressConstants.OSBCODE_TAG);
        mName = Utils.fromJsonString(json, AddressConstants.OSBNAME_TAG);
        mCity = Utils.fromJsonString(json, AddressConstants.CITY_TAG);
        mAddressName = Utils.fromJsonString(json, AddressConstants.ADDRESS_NAME_TAG);
        mLocation = Utils.fromJsonString(json, AddressConstants.LOCATION_TAG);
        mLatitude = BigDecimal.valueOf(json.getDouble(AddressConstants.LATITUDE_TAG)).floatValue();
        mLongitude = BigDecimal.valueOf(json.getDouble(AddressConstants.LONGITUDE_TAG)).floatValue();
        mUpdateAt = Utils.ConvertToDate(json.getString(AddressConstants.UPDATEAT_TAG));
        mUpdateBy = Utils.fromJsonString(json, AddressConstants.UPDATEBY_TAG);
    }

    public String  getUpdateBy() {
        return mUpdateBy;
    }

    public void setUpdateBy(String updateBy) {
        mUpdateBy = updateBy;
    }

    public Date getUpdateAt() {
        return mUpdateAt;
    }

    public void setUpdateAt(Date updateAt) {
        mUpdateAt = updateAt;
    }

    public Float getLongitude() {
        return mLongitude;
    }

    public void setLongitude(Float longitude) {
        mLongitude = longitude;
    }

    public Float getLatitude() {
        return mLatitude;
    }

    public void setLatitude(Float latitude) {
        mLatitude = latitude;
    }

    public String getLocation() {
        return mLocation;
    }

    public void setLocation(String location) {
        mLocation = location;
    }

    public String getAddressName() {
        return mAddressName;
    }

    public void setAddressName(String addressName) {
        mAddressName = addressName;
    }

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public int getOsbCode() {
        return mOsbCode;
    }

    public void setOsbCode(int osbCode) {
        mOsbCode = osbCode;
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }
}
