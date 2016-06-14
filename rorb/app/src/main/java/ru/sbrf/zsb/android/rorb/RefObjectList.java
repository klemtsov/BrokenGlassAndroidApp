package ru.sbrf.zsb.android.rorb;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

/**
 * Created by Администратор on 28.05.2016.
 */
public abstract class RefObjectList<T> extends ArrayList<T> {
    protected Context mContext;
    private Class<AsyncTask> mTask;

    public RefObjectList(Context c)
    {
        mContext = c.getApplicationContext();;
    }


    public Context getContext() {
        return mContext;
    }

    public abstract void saveToDb();

    public abstract void loadFromDb();
}
