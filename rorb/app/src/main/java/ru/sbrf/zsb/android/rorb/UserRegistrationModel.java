package ru.sbrf.zsb.android.rorb;

/**
 * Created by Администратор on 17.06.2016.
 */
public class UserRegistrationModel {
    private String mEmail;
    private String mFirstName;
    private String mLastName;
    private String mPassword;
    private String mConfirmPassword;

    public UserRegistrationModel() {
    }

    public UserRegistrationModel(String mEmail, String mFirstName, String mLastName, String mPassword, String mConfirmPassword) {
        this.mEmail = mEmail;
        this.mFirstName = mFirstName;
        this.mLastName = mLastName;
        this.mPassword = mPassword;
        this.mConfirmPassword = mConfirmPassword;
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

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getConfirmPassword() {
        return mConfirmPassword;
    }

    public void setConfirmPassword(String mConfirmPassword) {
        this.mConfirmPassword = mConfirmPassword;
    }
}
