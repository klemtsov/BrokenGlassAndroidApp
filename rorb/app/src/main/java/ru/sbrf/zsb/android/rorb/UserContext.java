package ru.sbrf.zsb.android.rorb;

import android.content.Context;
import android.util.Log;

import java.util.Currency;
import java.util.Date;

import ru.sbrf.zsb.android.exceptions.UserInsertDbException;
import ru.sbrf.zsb.android.exceptions.UserRegistrationException;
import ru.sbrf.zsb.android.netload.NetFetcher;

/**
 * Created by munk1 on 16.06.2016.
 */
public class UserContext {

    private static UserContext mInstance;
    private UserList mAllUsers;
    private User mCurrentUser;
    private Context mContextApplication;

    public static UserContext getCurrentUserContext(Context ctx){
        if(mInstance == null){
            mInstance = new UserContext(ctx);
        }
        return mInstance;
    }

    public UserContext(Context ctx) {
        mContextApplication = ctx;
        mAllUsers = UserList.get(ctx);
        mCurrentUser = getLastSignInUser();
    }

    public User getCurrentUser() {
        return mCurrentUser;
    }

    private User getLastSignInUser(){
         return mAllUsers.getLastSignInUser();
    }

    public User RegistrationUser(UserRegistrationModel user) throws UserRegistrationException, UserInsertDbException {
        User result = new User();
        NetFetcher httpClient = new NetFetcher(mContextApplication);
        try{
            httpClient.userRegistration(user);
            result = mAllUsers.insertUserDb(new User(user.getEmail(),user.getFirstName(),user.getLastName()));
        }
        catch (UserRegistrationException ex){
            Log.e("ERROR",ex.getMessage());
            throw ex;
        }
        catch(UserInsertDbException ex){
            Log.e("ERROR",ex.getMessage());
        }

        return result;
    }


}
