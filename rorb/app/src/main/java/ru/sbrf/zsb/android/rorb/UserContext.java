package ru.sbrf.zsb.android.rorb;

import android.content.Context;

import java.util.Currency;
import java.util.Date;

/**
 * Created by munk1 on 16.06.2016.
 */
public class UserContext {

    private static UserContext mInstance;
    private UserList mAllUsers;
    private User mCurrentUser;

    public static UserContext getCurrentUserContext(Context ctx){
        if(mInstance == null){
            mInstance = new UserContext(ctx);
        }
        return mInstance;
    }

    public UserContext(Context ctx) {
        mAllUsers = UserList.get(ctx);
        mCurrentUser = getLastSignInUser();
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    private User getLastSignInUser(){
         return mAllUsers.getLastSignInUser();
    }


}
