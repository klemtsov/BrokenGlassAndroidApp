package ru.sbrf.zsb.android.rorb;

import android.app.backup.SharedPreferencesBackupHelper;
import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

import ru.sbrf.zsb.android.helper.ClaimeConstant;
import ru.sbrf.zsb.android.helper.Utils;


/**
 * Created by Oleg on 08.05.2016.
 */
public class Claime {
    private String mId;
    private int mServiceId;
    private int mAddressId;
    private int mClaimeStateId;
    private String mDescription;
    private String mSolution;
    private Date mCreateAt;
    private Date mUpdateAt;
    private String mUpdateBy;
    private boolean mHasPhotos;
    private Service mService;
    private Context mContext;

    private ClaimeStatus mStatus;

    public boolean isHasPhotos() {
        return mHasPhotos || mPhotoList.size() > 0;
    }

    public void setHasPhotos(boolean hasPhotos) {
        mHasPhotos = hasPhotos;
    }

    public String getUpdateBy() {
        return mUpdateBy;
    }

    public void setCreateAt(Date createAt) {
        mCreateAt = createAt;
    }

    public Date getLocalCreateAt()
    {
        return Utils.getLocalDate(mCreateAt);
    }

    public Date getLocalUpdateAt()
    {
        return Utils.getLocalDate(mUpdateAt);
    }


    public void setServiceId(int serviceId) {
        mServiceId = serviceId;
        mService = new Service(ServiceList.get(mContext).getServiceById(mServiceId));
    }

    public void setAddressId(int addressId) {
        mAddressId = addressId;
        mAddress = new Address(AddressList.get(mContext).getAddressByID(mAddressId));
    }

    public void setClaimeStateId(int claimeStateId) {
        mClaimeStateId = claimeStateId;
        mStatus = new ClaimeStatus(ClaimeStatusList.get(mContext).getStateByID(mClaimeStateId));
    }

    public void setUpdateBy(String updateBy) {
        mUpdateBy = updateBy;
    }

    public ClaimeStatus getStatus() {
        return mStatus;
    }

    public void setStatus(ClaimeStatus status) {
        mStatus = status;
    }

    public Date getUpdateAt() {
        return mUpdateAt;
    }

    public void setUpdateAt(Date updateAt) {
        mUpdateAt = updateAt;
    }

    public String getSolution() {
        return mSolution;
    }

    public void setSolution(String solution) {
        mSolution = solution;
    }

    public Address getAddress() {
        return mAddress;
    }

    private Address mAddress;

    public PhotoList getPhotoList() {
        return mPhotoList;
    }

    private PhotoList mPhotoList = new PhotoList();

    public Claime(Context c) {
        mContext = c.getApplicationContext();
        mId = ClaimeConstant.NEW_CLAIME_ID;
        mPhotoList = new PhotoList();
    }

    public Claime(JSONObject json, Context c) throws JSONException {
        this(c);
        updateFromJson(json);
    }

    public JSONObject toJson() throws JSONException {
        JSONObject result = new JSONObject();
        result.put(ClaimeConstant.ID_TAG, mId);
        result.put(ClaimeConstant.SERVICE_ID_TAG, mService.getId());
        result.put(ClaimeConstant.ADDRESS_ID_TAG, mAddress.getId());
        result.put(ClaimeConstant.CLAIME_STATE_ID_TAG, mStatus.getId());
        result.put(ClaimeConstant.DESCRIPTION_TAG, mDescription);
        result.put(ClaimeConstant.SOLUTION_TAG, mSolution);
        result.put(ClaimeConstant.CREATE_AT_TAG, Utils.getStringFromDateTime(getCreateAt()));
        result.put(ClaimeConstant.UPDATE_AT_TAG, Utils.getStringFromDateTime(getUpdateAt()));
        result.put(ClaimeConstant.HAS_PHOTOS_TAG, mPhotoList.size() > 0);
        if (mPhotoList.size() > 0) {
            result.put(ClaimeConstant.PHOTO_ARR_TAG, mPhotoList.getJSONArray());
        }
        return result;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getId() {
        return mId;
    }

    public void setId(String id) {
        mId = id;
    }

    public void setDescription(String info) {
        mDescription = info;
    }

    public int getStateID() {
        return mClaimeStateId;
    }

    public void setStateID(int stateID) {
        mClaimeStateId = stateID;
    }

    public Date getCreateAt() {
        return mCreateAt;
    }

    public String getDatePart() {
        String format = "dd.MM.yyyy";
        if (getUpdateAt() == null) {
            if (getCreateAt() != null) {
                return Utils.getStringFromDateTime(getLocalCreateAt(), format);
            }
            else
                return null;
        }
        return Utils.getStringFromDateTime(getLocalUpdateAt(), format);
    }

    public String getTimePart() {
        String format = "HH:mm";
        if (getUpdateAt() == null) {
            if (getCreateAt() != null) {
                return Utils.getStringFromDateTime(getCreateAt(), format);
            }
            else
            {
                return null;
            }

        }
        return Utils.getStringFromDateTime(getUpdateAt(), format);
    }

    public Service getService() {
        return mService;
    }

    public void setService(Service mService) {
        this.mService = mService;
    }

    public void setAddress(Address address) {
        mAddress = address;
    }


    public String SaveNewClame() {
        DBHelper db = new DBHelper(mContext);
        if (getId() == ClaimeConstant.NEW_CLAIME_ID) {
            String id = db.getVirtualIdForClaime();
            this.setId(id);
            ClaimeList.get(mContext).getItems().add(0, this);
        }
        saveToLocalDb();
        //db.saveClaimeToDb(this);
        return this.getId();
    }

    public void updateFromJson(JSONObject json) throws JSONException {
        setId(json.getString(ClaimeConstant.ID_TAG));
        setServiceId(json.getInt(ClaimeConstant.SERVICE_ID_TAG));
        setAddressId(json.getInt(ClaimeConstant.ADDRESS_ID_TAG));
        setClaimeStateId(json.getInt(ClaimeConstant.CLAIME_STATE_ID_TAG));
        setDescription(Utils.fromJsonString(json, ClaimeConstant.DESCRIPTION_TAG));
        setSolution(Utils.fromJsonString(json,ClaimeConstant.SOLUTION_TAG));
        setCreateAt(Utils.ConvertToDate(json.optString(ClaimeConstant.CREATE_AT_TAG)));
        setUpdateBy(json.optString(ClaimeConstant.UPDATE_AT_TAG));
        setHasPhotos(json.getBoolean(ClaimeConstant.HAS_PHOTOS_TAG));

        JSONArray photos = json.getJSONArray(ClaimeConstant.PHOTO_ARR_TAG);
        if (photos != null)
        {
            for (int i = 0; i < photos.length(); i++) {
                mPhotoList.add(new Photo(getId(), photos.getJSONObject(i)));
            }
        }

    }

    public void updatePhotosInLocal() {
        DBHelper db = new DBHelper(mContext);
        db.updatePhotos(this);
    }

    //Загрузка фоток в заявку из локальной базы
    public void loadPhotosFromLocal() {
        mPhotoList.clear();
        DBHelper db = new DBHelper(mContext);
        for (Photo p :
                db.getPhotoListFromDb(getId())) {
            mPhotoList.add(p);
        }

    }

    public void saveToLocalDb() {
        if (ClaimeStatusList.isNew(mStatus) )
        {
            mUpdateAt = new Date();
        }
        DBHelper db = new DBHelper(mContext);
        db.saveClaimeToDb(this);
    }

    //Удаление заявки из локальной базы но не удаляет из списка
    public void deleteFromDb() {
        DBHelper db = new DBHelper(mContext);
        db.openDB();
        db.beginTransaction();
        try {
            db.deleteClaime(this.getId());
            db.setTransactionSuccessful();
        }
        finally {
          db.endTransaction();
          db.close();
        }

    }
}
