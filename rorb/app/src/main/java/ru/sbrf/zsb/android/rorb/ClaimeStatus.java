package ru.sbrf.zsb.android.rorb;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import ru.sbrf.zsb.android.helper.AddressConstants;
import ru.sbrf.zsb.android.helper.StatusConstants;
import ru.sbrf.zsb.android.helper.Utils;

public class ClaimeStatus {
    private int mId;
    private String mCode;
    private String mName;
    private Date mUpdateAt;
    private String mUpdateBy;

    public ClaimeStatus(JSONObject json) throws JSONException {
        mId = json.getInt(StatusConstants.ID_TAG);
        mCode = Utils.fromJsonString(json, StatusConstants.CODE_TAG);
        mName = Utils.fromJsonString(json, StatusConstants.NAME_TAG);
        mUpdateAt = Utils.ConvertToDate(json.getString(StatusConstants.UPDATEAT_TAG));
        mUpdateBy = Utils.fromJsonString(json, StatusConstants.UPDATEAT_TAG);
    }

    public ClaimeStatus()
    {

    }

    public ClaimeStatus(ClaimeStatus status)
    {
        if (status == null)
        {
            throw new IllegalArgumentException("Параметр не может быть null!");
        }
        this.setId(status.getId());
        this.setCode(status.getCode());
        this.setName(status.getName());
        this.setUpdateAt((Date)status.getUpdateAt().clone());
    }


    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public Date getUpdateAt() {
        return mUpdateAt;
    }

    public void setUpdateAt(Date updateAt) {
        mUpdateAt = updateAt;
    }

    public String getUpdateBy() {
        return mUpdateBy;
    }

    public void setUpdateBy(String updateBy) {
        mUpdateBy = updateBy;
    }
}
