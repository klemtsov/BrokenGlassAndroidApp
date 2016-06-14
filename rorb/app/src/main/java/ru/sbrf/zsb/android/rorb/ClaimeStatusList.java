package ru.sbrf.zsb.android.rorb;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;

import ru.sbrf.zsb.android.netload.NetFetcher;

/**
 * Created by Oleg on 10.05.2016.
 */
public class ClaimeStatusList extends RefObjectList<ClaimeStatus> {
    public static final int STATUS_ALL_ID = 0;
    private static ClaimeStatusList mList;

    private final static int new_img = R.drawable.new_status;
    private final static int sended_img = R.drawable.sended;
    private final static int inwork_img = R.drawable.in_work;
    private final static int sucess_img = R.drawable.success;
    private final static int rejected_img = R.drawable.rejected;

    public static int getStateImage(int stateId) {
        switch (stateId) {
            case 1:
                return new_img;
            case 2:
                return sended_img;
            case 3:
                return inwork_img;
            case 4:
                return sucess_img;
            case 5:
                return rejected_img;
            default:
                return new_img;
        }
    }

    public ClaimeStatusList(Context c) {
        super(c);
        addAllStatus();
    }

    private void addAllStatus() {
        ClaimeStatus claimeStatus = new ClaimeStatus();
        claimeStatus.setId(STATUS_ALL_ID);
        claimeStatus.setName(mContext.getString(R.string.claime_status_all_name));
        claimeStatus.setCode(mContext.getString(R.string.claime_status_all_code));
        add(claimeStatus);
    }

    public static ClaimeStatusList get(Context c) {
        if (mList == null) {
            DBHelper db = new DBHelper(c);
            Log.d(MainActivity.TAG, "Запуск загрузки списка статусов из БД");
            mList = db.getStatusListFromDb();
        }
        return mList;
    }

    public static boolean isNew(ClaimeStatus status) {
        return (status.getId() == 1);
    }

   /* public static void set(ClaimeStatusList list, Context c) {
        if (list == null) {
            mList = new ClaimeStatusList(c);
        } else {
            mList = list;
        }
        mList.saveToDb();
    }
    */

    public void saveToDb() {
        DBHelper db = new DBHelper(mContext);
        db.UpdateAllStatuses(this);
    }

    @Override
    public void loadFromDb() {
        DBHelper db = new DBHelper(mContext);
        clear();
        addAllStatus();
        this.addAll(db.getStatusListFromDb());
    }


    public ClaimeStatus getStateByID(int id) {
        for (ClaimeStatus s : this) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    private class FetchStatuses extends AsyncTask<Void, Void, Void> {
        private String error = null;

        @Override
        protected Void doInBackground(Void... params) {
            NetFetcher nf = new NetFetcher(ClaimeStatusList.this.mContext);
            try {
                mList = nf.fetchStatuses();
            } catch (JSONException e) {
                error = e.getMessage();
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (error != null) {
                Toast.makeText(mContext, error, Toast.LENGTH_LONG);
            } else {
                Toast.makeText(mContext, "Загрузка статусов завершена!", Toast.LENGTH_LONG);
                Log.d(MainActivity.TAG, "Загрузка статусов завершена!");
            }
        }
    }
}
