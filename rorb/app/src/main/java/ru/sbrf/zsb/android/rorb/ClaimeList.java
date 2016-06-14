package ru.sbrf.zsb.android.rorb;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Oleg on 08.05.2016.
 */
public class ClaimeList {
    private static ClaimeList sClaimeList;
    private static String TAG = "ClaimList";
    private final ClaimeStatusList mStates;
    private ArrayList<Claime> mItems;
    private Context mContext;
    // initially offset will be 0, later will be updated while parsing the json
    private int offSet = 0;

    public static ClaimeList get(Context c) {
        if (sClaimeList == null) {
            sClaimeList = new ClaimeList(c);
        }
        return sClaimeList;
    }

    //Чтение заявок из локальной базы
    private void readFromDb() {
        DBHelper db = new DBHelper(mContext);
        sClaimeList.setItems(db.getClameListFromDb());
    }

    //Сохранение заявок в локальную базу
    public void saveToDb()
    {
        DBHelper db = new DBHelper(mContext);
        Log.d(MainActivity.TAG, "Загрузка заявок в БД");
        db.saveClaimeListToDb(getItems());
        Log.d(MainActivity.TAG, "Загрузка заявок в БД завершена");
    }

    public void deleteClaime(Claime claime)
    {
        DBHelper db = new DBHelper(mContext);
        db.openDB();
        db.beginTransaction();
        try{
            db.deleteClaime(claime.getId());
            mItems.remove(claime);
            db.setTransactionSuccessful();
        }
        finally {
            db.endTransaction();
        }

    }


    public ArrayList<Claime> getItems() {
        return mItems;
    }

    public Claime getClaimeById(String id) {
        for (Claime t : mItems) {
            if (t.getId().equals(id)) {
                return t;
            }
        }
        return null;
    }

    private ClaimeList(Context c) {
        mContext = c.getApplicationContext();
        mItems = new ArrayList<>();
        mStates = ClaimeStatusList.get(c);

    }

    public void setItems(ArrayList<Claime> items) {
        this.mItems = items;
    }

}

