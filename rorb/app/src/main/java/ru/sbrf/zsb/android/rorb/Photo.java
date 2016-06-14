package ru.sbrf.zsb.android.rorb;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import ru.sbrf.zsb.android.helper.ClaimeConstant;
import ru.sbrf.zsb.android.helper.Utils;

/**
 * Created by Администратор on 30.05.2016.
 */
public class Photo {
    public static final int MAX_IMAGE_SIZE = 1024;
    public static final int MAX_THUMBNAIL_SIZE = 200;

    private int mId;
    private String mClaimeId;
    private byte[] mFile;
    private byte[] mThumbnail;
    private boolean mDelFile;


    public Photo(String claimeId, boolean delFile)
    {
        mClaimeId = claimeId;
        mDelFile = delFile;
    }

    public Photo(String claimeId, JSONObject json) throws JSONException {
        this(claimeId, false);
        setId(json.getInt(ClaimeConstant.PHOTO_PIC_ID_TAG));
        String arr = json.getString(ClaimeConstant.PHOTO_THUMB_TAG);
        setFile(Utils.fromBase64ToArray(arr));
        arr = json.getString(ClaimeConstant.PHOTO_THUMB_TAG);
        setThumbnail(Utils.fromBase64ToArray(arr));
    }

    public int getId() {
        return mId;
    }

    public void setId(int id) {
        mId = id;
    }

    public String getClaimeId() {
        return mClaimeId;
    }

    public void setClaimeId(String claimeId) {
        mClaimeId = claimeId;
    }



    public void setFilename(String filename) {
        if (Utils.isNullOrWhitespace(filename)) return;;

        try {

            mFile = Utils.resizeImage(filename, Photo.MAX_IMAGE_SIZE);
            //FileInputStream f = new FileInputStream(mFilename);
            //mFile = new byte[f.available()];
            //f.read(mFile, 0, mFile.length);
            mThumbnail = Utils.resizeImage(filename, Photo.MAX_THUMBNAIL_SIZE);
            //удаляем фотки, если они сделаны камерой
            if (mDelFile) {
                File file = new File(filename);
                file.delete();
            }

        } catch (Exception e) {
            e.printStackTrace();

        }
       // mFilename = null;
    }

    public byte[] getFile() {
        return mFile;
    }

    public void setFile(byte[] file) {
        mFile = file;
    }

    public byte[] getThumbnail() {
        return mThumbnail;
    }

    public void setThumbnail(byte[] thumbnail) {
        mThumbnail = thumbnail;
    }

    public JSONObject toJSON() throws JSONException {
        JSONObject result = new JSONObject();
        String data = Utils.fromArrayToBase64(mFile);
        result.put(ClaimeConstant.PHOTO_PIC_TAG, data);
        data = Utils.fromArrayToBase64(mThumbnail);
        result.put(ClaimeConstant.PHOTO_THUMB_TAG, data);
        return result;
    }
}
