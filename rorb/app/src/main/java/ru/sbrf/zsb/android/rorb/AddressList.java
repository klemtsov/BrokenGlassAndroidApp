package ru.sbrf.zsb.android.rorb;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import ru.sbrf.zsb.android.netload.NetFetcher;

public class AddressList extends RefObjectList<Address> {
    private static AddressList sAddressList;

    public AddressList(Context c) {
        super(c);
    }


    public static AddressList get(Context c){
        if (sAddressList == null){
            DBHelper db = new DBHelper(c);
            Log.d(MainActivity3.TAG, "Запуск загрузки списка адресов из локальной бд");
            sAddressList = db.getAddressListFromDb();
        }
        return sAddressList;
    }

    /*public static void set(AddressList list, Context c)
    {
        if (list == null)
        {
            sAddressList = new AddressList(c);
        }
        else
        {
            sAddressList = list;
        }
        sAddressList.saveToDb();
    }
    */



    @Override
    public void saveToDb()
    {
        DBHelper db = new DBHelper(mContext);
        db.UpdateAllAddresses(this);
    }

    @Override
    public void loadFromDb() {
        DBHelper db = new DBHelper(mContext);
        clear();
        addAll(db.getAddressListFromDb());
    }


    public Address getAddressByID(int id) {
        for (Address s : this) {
            if (s.getId() == id) {
                return s;
            }
        }
        return null;
    }

    private class  FetchAddresses extends AsyncTask<Void, Void, Void> {

        private String error;

        @Override
        protected Void doInBackground(Void... params) {
            NetFetcher nf = new NetFetcher(AddressList.this.mContext);
            try {
                sAddressList = nf.fetchAddresses();
            } catch (Exception e) {
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
                Toast.makeText(mContext, "Загрузка адресов завершена", Toast.LENGTH_LONG);
                Log.d(MainActivity3.TAG, "Загрузка адресов завершена");
            }


        }
    }

}
