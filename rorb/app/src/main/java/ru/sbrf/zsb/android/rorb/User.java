package ru.sbrf.zsb.android.rorb;

import java.util.Date;

/**
 * Created by munk1 on 16.06.2016.
 */
public class User {
    private int mId;
    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mToken;
    private Date mExpireToken;
    private Date mLastLogin;
    private byte[] mAvatarImg;

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getFirstName() {
        return mFirstName;
    }

    public void setFirstName(String mFirstName) {
        this.mFirstName = mFirstName;
    }

    public String getLastName() {
        return mLastName;
    }

    public void setLastName(String mLastName) {
        this.mLastName = mLastName;
    }

    public String getToken() {
        return mToken;
    }

    public void setToken(String mToken) {
        this.mToken = mToken;
    }

    public byte[] getAvatarImg() {
        return mAvatarImg;
    }

    public void setAvatarImg(byte[] mAvatarImg) {
        this.mAvatarImg = mAvatarImg;
    }

    public Date getExpireToken() {
        return mExpireToken;
    }

    public void setExpireToken(Date mExpireToken) {
        this.mExpireToken = mExpireToken;
    }

    public Date getLastLogin() {
        return mLastLogin;
    }

    public void setLastLogin(Date mLastLogin) {
        this.mLastLogin = mLastLogin;
    }

    public boolean isEmpty(){
        return this.mId == 0;
    }
}
