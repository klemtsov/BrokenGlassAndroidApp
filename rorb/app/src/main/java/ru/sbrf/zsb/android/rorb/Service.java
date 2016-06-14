package ru.sbrf.zsb.android.rorb;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Ref;
import java.util.Date;

import ru.sbrf.zsb.android.helper.SeviceConstants;
import ru.sbrf.zsb.android.helper.Utils;


public class Service extends RefObject {
    private String mCode;
    private int mOrder;
    private Date mUpdateAt;
    private String mUpdateBy;

    public Service()
    {

    }

    public Service(Service serv)
    {
        this.setId(serv.getId());
        this.setName(serv.getName());
        this.setOrder(serv.getOrder());
        this.setUpdateAt((Date)serv.getUpdateAt().clone());
        this.setUpdateBy(serv.getUpdateBy());
    }

    public Service(JSONObject json) throws JSONException {
        mId = json.getInt(SeviceConstants.ID_TAG);
        mCode = Utils.fromJsonString(json, SeviceConstants.CODE_TAG);
        mName = Utils.fromJsonString(json, SeviceConstants.NAME_TAG);
        mOrder = json.optInt(SeviceConstants.ORDER_TAG);
        mUpdateAt = Utils.ConvertToDate(json.getString(SeviceConstants.UPDATEAT_TAG));
        mUpdateBy = Utils.fromJsonString(json, SeviceConstants.UPDATEABY_TAG);
    }


    public String getCode() {
        return mCode;
    }

    public void setCode(String code) {
        mCode = code;
    }

    public int getOrder() {
        return mOrder;
    }

    public void setOrder(int order) {
        mOrder = order;
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

    @Override
    public String toString() {
        return mName;
    }
}
