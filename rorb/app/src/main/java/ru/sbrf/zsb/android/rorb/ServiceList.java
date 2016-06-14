package ru.sbrf.zsb.android.rorb;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;

import ru.sbrf.zsb.android.netload.NetFetcher;

/**
 * Created by Администратор on 13.05.2016.
 */
public class ServiceList extends  RefObjectList<Service> {
    private static ServiceList sServiceList;
    public ServiceList(Context c)
    {
        super(c);
    }

    public static ServiceList get(Context c)
    {
        if (sServiceList == null)
        {
            DBHelper db = new DBHelper(c);
            Log.d(MainActivity.TAG, "Запуск загрузки списка сервисов из бд");
            sServiceList = db.getServiceListFromDb();
            //sServiceList.reload();
        }
        return sServiceList;
    }

    /*public static void set(ServiceList list, Context c)
    {
        if (list == null)
        {
            sServiceList = new ServiceList(c);
        }
        else
        {
            sServiceList = list;
        }
        sServiceList.saveToDb();
    }
    */

    public void saveToDb()
    {
        new DBHelper(mContext).UpdateAllServices(this);
    }

    @Override
    public void loadFromDb() {
        DBHelper db = new DBHelper(mContext);
        clear();
        addAll(db.getServiceListFromDb());
    }


    public Service getServiceById(int id)
    {
        for (Service item: this) {
            if (item.getId() == id)
                return item;
        }
        return null;
    }

    public int getServiceIndexById(int id)
    {
        int i = -1;
        for (Service item: this) {
            i++;
            if (item.getId() == id) {
                return i;
            }
        }
        return -1;
    }

    private void reload() {
        new FetchStatuses().execute();
    }

    private class  FetchStatuses extends AsyncTask<Void, Void, Void> {

        private String error = null;

        @Override
        protected Void doInBackground(Void... params) {
            NetFetcher nf = new NetFetcher(ServiceList.this.mContext);
            try {
                sServiceList = nf.fetchServices();
            } catch (JSONException e) {
                error = e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (error != null) {
                Toast.makeText(mContext, error, Toast.LENGTH_LONG);
            }
            else {
                Toast.makeText(mContext, "Загрузка статусов завершена!", Toast.LENGTH_LONG);
                Log.d(MainActivity.TAG, "Загрузка сервисов завершена!");
            }
        }
    }

}
