package ru.sbrf.zsb.android.rorb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.Date;

import ru.sbrf.zsb.android.exceptions.UserInsertDbException;
import ru.sbrf.zsb.android.helper.Utils;

public class DBHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 5;
    private static final String DB_NAME = "brglass_db";
    private static final String CLAIME_TBL = "claime";
    public static final String ADDRESS_TBL = "address";
    public static final String STATUS_TBL = "status";
    public static final String SERVICE_TBL = "service";
    private static final String PHOTO_TBL = "photo";
    private static final String SEQUENCE_TBL = "sequence_tbl";
    private static final String LAST_UPDATE_TBL = "last_rf_updates";
    private static final String SEQUENCE_TBL_CODE_COL = "code";
    private static final String SEQUENCE_TBL_CLAIME_ID = "CLAIME_ID";
    private static final String USER_TBL = "user";
    private final Context mContext;
    protected SQLiteDatabase mDb;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        mContext = context;

    }

    public void onCreate(SQLiteDatabase db) {
        Log.d(MainActivity3.TAG, " --- onCreate database --- ");
        //ContentValues cv = new ContentValues();

        db.execSQL("create table " + SEQUENCE_TBL + " (_id integer, code text);");
        ContentValues val = new ContentValues();
        val.put("_id", 1);
        val.put("code", SEQUENCE_TBL_CLAIME_ID);
        long id = db.insert(SEQUENCE_TBL, null, val);

        //Создаем таблицу дат последних обновлений справочников
        db.execSQL("create table " + LAST_UPDATE_TBL
                + " (table_name text,"
                + " last_update datetime);");

        // создаем таблицу статусов
        db.execSQL("create table " + STATUS_TBL
                + " (_id integer primary key,"
                + " name text,"
                + " code text,"
                + " update_at datetime,"
                + " update_by text);");
        //создам таблицу адресов
        db.execSQL("create table " + ADDRESS_TBL
                + " (_id integer primary key,"
                + " osb_code text,"
                + " osb_name text,"
                + " city text,"
                + " location text,"
                + " latitude real,"
                + " longitude real,"
                + " update_at datetime,"
                + " update_by text);");


        //создаем таблицу сервисов
        db.execSQL("create table " + SERVICE_TBL
                + " (_id integer primary key,"
                + "code text,"
                + " name text,"
                + " sort_order int,"
                + " update_at datetime,"
                + " update_by text);");

        //создаем таблицу обращений
        db.execSQL("create table " + CLAIME_TBL
                + " (_id real,"
                + " service_id text,"
                + " address_id int,"
                + " status_id int,"
                + " description text,"
                + " solution text,"
                + " create_at datetime,"
                + " update_at datetime,"
                + " update_by text,"
                + " has_photos integer);");

        //создаем таблицу фотографий
        db.execSQL("create table " + PHOTO_TBL
                + " (_id integer primary key autoincrement,"
                + " claime_id text,"
                + " photo blob,"
                + " thumbnail blob);");

        db.execSQL("create table " + USER_TBL
                + " (_id integer primary key autoincrement,"
                + " email text NOT NULL,"
                + " first_name text,"
                + " last_name text,"
                + " token text,"
                + " expire_token datetime,"
                + " last_login datetime,"
                + " avatar_img blob,"
                + " is_login integer default 0,"
                + " CONSTRAINT cnstr_user_email UNIQUE (email)"
                + ");"
        );

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      /*db.execSQL("drop table status");
        db.execSQL("drop table address");
        db.execSQL("drop table service");
        db.execSQL("drop table claime");
        db.execSQL("drop table photo");
        */
/*      Log.d(MainActivity3.TAG, " --- UPDATE DATABASE --- ");
        db.execSQL("create table " + USER_TBL
                + " (_id integer primary key autoincrement,"
                + " email text NOT NULL,"
                + " first_name text,"
                + " last_name text,"
                + " token text,"
                + " expire_token datetime,"
                + " last_login datetime,"
                + " avatar_img blob,"
                + " is_login integer default 0,"
                + " CONSTRAINT cnstr_user_email UNIQUE (email)"
                + ");"
        );*/
    }

    public void openDB() {
        if (mDb == null || !mDb.isOpen()) {
            mDb = getWritableDatabase();
        }
    }


    public void saveClaimeListToDb(ArrayList<Claime> list) {
        openDB();
        mDb.beginTransaction();
        try {

            String[] args = new String[]{"1"};
            int rows = mDb.delete(CLAIME_TBL, " status_id > 1", null);

            for (Claime claime : list) {
                insertClaimeToDB(claime, false);
            }
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
            mDb.close();
        }
    }

    //Создаем список заявок из локальной базы
    public ArrayList<Claime> getClameListFromDb() {
        String[] columns = null;
        String orderBy = " ifnull(update_at, create_at) desc";
        ArrayList<Claime> result = new ArrayList<Claime>();
        Cursor c = null;
        openDB();
        c = mDb.query(CLAIME_TBL, null, null, null, null, null, orderBy);

        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    Claime claime = new Claime(mContext);
                    claime.setId(c.getString(c.getColumnIndex("_id")));
                    claime.setServiceId(c.getInt(c.getColumnIndex("service_id")));
                    claime.setAddressId(c.getInt(c.getColumnIndex("address_id")));
                    claime.setClaimeStateId(c.getInt(c.getColumnIndex("status_id")));
                    claime.setDescription(c.getString(c.getColumnIndex("description")));
                    claime.setSolution(c.getString(c.getColumnIndex("solution")));
                    claime.setCreateAt(Utils.ConvertToDateSQLITE(c.getString(c.getColumnIndex("create_at"))));
                    claime.setUpdateAt(Utils.ConvertToDateSQLITE(c.getString(c.getColumnIndex("update_at"))));
                    claime.setUpdateBy(c.getString(c.getColumnIndex("update_by")));
                    claime.setHasPhotos(c.getInt(c.getColumnIndex("has_photos")) > 0);
                    result.add(claime);
                } while (c.moveToNext());
            }
            c.close();
        }
        mDb.close();
        return result;
    }


    public int updateClaimeToDb(Claime claime) {
        openDB();
        try {
            ContentValues val = new ContentValues();
            val.put("service_id", claime.getService().getId());
            val.put("address_id", claime.getAddress().getId());
            val.put("status_id", claime.getStatus().getId());
            val.put("description", claime.getDescription());
            val.put("solution", claime.getSolution());
            val.put("create_at", Utils.getStringFromDateSQLITE(claime.getCreateAt()));
            val.put("update_at", Utils.getStringFromDateSQLITE(claime.getUpdateAt()));
            val.put("update_by", claime.getUpdateBy());
            val.put("has_photos", claime.isHasPhotos() ? 1 : 0);
            int res = mDb.update(CLAIME_TBL, val, " _id = ?", new String[]{claime.getId()});
            deletePhotos(claime.getId(), false);
            for (Photo p :
                    claime.getPhotoList()) {
                p.setClaimeId(claime.getId());
                insertPhotoToDb(p, false);
            }
            return res;
        } finally {
            mDb.close();
        }
    }

    public long insertClaimeToDB(Claime claime, boolean closeDb) {
        openDB();
        try {
            ContentValues val = new ContentValues();
            val.put("_id", claime.getId());
            val.put("service_id", claime.getService().getId());
            val.put("address_id", claime.getAddress().getId());
            val.put("status_id", claime.getStatus().getId());
            val.put("description", claime.getDescription());
            val.put("solution", claime.getSolution());
            val.put("create_at", Utils.getStringFromDateSQLITE(claime.getCreateAt()));
            val.put("update_at", Utils.getStringFromDateSQLITE(claime.getUpdateAt()));
            val.put("update_by", claime.getUpdateBy());
            val.put("has_photos", claime.isHasPhotos() ? 1 : 0);
            long rowId = mDb.insert(CLAIME_TBL, null, val);
            for (Photo p :
                    claime.getPhotoList()) {
                p.setClaimeId(Long.toString(rowId));
                insertPhotoToDb(p, false);
            }
            return rowId;
        } finally {
            if (closeDb)
                mDb.close();
        }
    }


    public void saveClaimeToDb(Claime claime) {
        if (updateClaimeToDb(claime) == 0) {
            insertClaimeToDB(claime, true);
        }
    }

    //Список статусов из локальной базы
    public ClaimeStatusList getStatusListFromDb() {
        ClaimeStatusList result = new ClaimeStatusList(mContext);
        Cursor c = null;
        openDB();
        c = mDb.query(STATUS_TBL, null, null, null, null, null, null);

        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    ClaimeStatus status = new ClaimeStatus();
                    status.setId(c.getInt(c.getColumnIndex("_id")));
                    status.setName(c.getString(c.getColumnIndex("name")));
                    status.setCode(c.getString(c.getColumnIndex("code")));
                    status.setUpdateAt(Utils.ConvertToDateSQLITE(c.getString(c.getColumnIndex("update_at"))));
                    status.setUpdateBy(c.getString(c.getColumnIndex("update_by")));
                    result.add(status);
                } while (c.moveToNext());
            }
            c.close();
        }
        mDb.close();
        return result;
    }

    public long insertStatusToDb(ClaimeStatus status, boolean closeDb) {
        openDB();
        try {
            ContentValues val = new ContentValues();
            val.put("_id", status.getId());
            val.put("name", status.getName());
            val.put("code", status.getCode());
            val.put("update_at", Utils.getStringFromDateSQLITE(status.getUpdateAt()));
            val.put("update_by", status.getUpdateBy());
            return mDb.insert(STATUS_TBL, null, val);
        } finally {
            if (closeDb)
                mDb.close();
        }


    }

    public int updateStatusToDb(ClaimeStatus status, boolean closeDb) {
        openDB();
        try {
            ContentValues val = new ContentValues();
            val.put("name", status.getName());
            val.put("code", status.getCode());
            val.put("update_at", Utils.getStringFromDateSQLITE(status.getUpdateAt()));
            val.put("update_by", status.getUpdateBy());
            return mDb.update(STATUS_TBL, val, " _id = ?", new String[]{Integer.toString(status.getId())});
        } finally {
            if (closeDb)
                mDb.close();
        }
    }

    public void UpdateAllStatuses(ClaimeStatusList statuses) {
        Log.d(MainActivity3.TAG, "Сохранение статусов в локальную БД");
        openDB();
        Date maxUpdateAt = null;
        mDb.beginTransaction();
        try {
            for (ClaimeStatus status : statuses) {
                //Фиксируем дату последнего обновления данных
                maxUpdateAt = getMaxDate(maxUpdateAt, status.getUpdateAt());
                saveStatusToDb(status, false);
            }
            saveMaxUpdateAt(STATUS_TBL, maxUpdateAt);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(MainActivity3.TAG, "Сохранение статусов в локальную БД, ошибка: " + e.getMessage());
        } finally {
            mDb.endTransaction();
            mDb.close();
            Log.d(MainActivity3.TAG, "Сохранение статусов в локальную БД завершено");
        }
    }


    public void saveStatusToDb(ClaimeStatus status, boolean closeDb) {
        if (updateStatusToDb(status, closeDb) <= 0) {
            insertStatusToDb(status, closeDb);
        }
    }

    public AddressList getAddressListFromDb() {
        AddressList result = new AddressList(mContext);
        String orderBy = " city, location";
        Cursor c = null;
        openDB();
        c = mDb.query(ADDRESS_TBL, null, null, null, null, null, orderBy);

        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    Address address = new Address();
                    address.setId(c.getInt(c.getColumnIndex("_id")));
                    address.setOsbCode(c.getInt(c.getColumnIndex("osb_code")));
                    address.setName(c.getString(c.getColumnIndex("osb_name")));
                    address.setCity(c.getString(c.getColumnIndex("city")));
                    address.setAddressName(c.getString(c.getColumnIndex("location")));
                    address.setLocation(c.getString(c.getColumnIndex("location")));
                    address.setLatitude(c.getFloat(c.getColumnIndex("latitude")));
                    address.setLongitude(c.getFloat(c.getColumnIndex("longitude")));
                    address.setUpdateAt(Utils.ConvertToDateSQLITE(c.getString(c.getColumnIndex("update_at"))));
                    address.setUpdateBy(c.getString(c.getColumnIndex("update_by")));
                    result.add(address);
                } while (c.moveToNext());
            }
            c.close();
        }
        mDb.close();
        return result;
    }

    public long insertAddressToDb(Address address, boolean closeDb) {
        openDB();
        try {
            ContentValues val = new ContentValues();
            val.put("_id", address.getId());
            val.put("osb_code", address.getOsbCode());
            val.put("osb_name", address.getName());
            val.put("city", address.getCity());
            val.put("location", address.getLocation());
            val.put("latitude", address.getLatitude());
            val.put("longitude", address.getLongitude());
            val.put("update_at", Utils.getStringFromDateSQLITE(address.getUpdateAt()));
            val.put("update_by", address.getUpdateBy() == null ? "" : address.getUpdateBy());
            long rowId = mDb.insert(ADDRESS_TBL, null, val);
            return rowId;
        } finally {
            if (closeDb)
                mDb.close();
        }
    }

    public int updateAddressToDb(Address address, boolean closeDb) {
        openDB();
        try {
            ContentValues val = new ContentValues();
            val.put("osb_code", address.getOsbCode());
            val.put("osb_name", address.getName());
            val.put("city", address.getCity());
            val.put("location", address.getLocation());
            val.put("latitude", address.getLatitude());
            val.put("longitude", address.getLongitude());
            val.put("update_at", Utils.getStringFromDateSQLITE(address.getUpdateAt()));
            val.put("update_by", address.getUpdateBy());
            return mDb.update(ADDRESS_TBL, val, " _id = ?", new String[]{Integer.toString(address.getId())});
        } finally {
            if (closeDb)
                mDb.close();
        }
    }

    public void saveAddressToDb(Address address, boolean closeDb) {
        if (updateAddressToDb(address, false) <= 0) {
            insertAddressToDb(address, false);
        }
    }

    public ServiceList getServiceListFromDb() {
        ServiceList result = new ServiceList(mContext);
        Cursor c = null;
        openDB();
        c = mDb.query(SERVICE_TBL, null, null, null, null, null, null);

        if (c != null) {
            if (c.moveToFirst()) {
                String str;
                do {
                    Service service = new Service();
                    service.setId(c.getInt(c.getColumnIndex("_id")));
                    service.setCode(c.getString(c.getColumnIndex("code")));
                    service.setName(c.getString(c.getColumnIndex("name")));
                    service.setOrder(c.getInt(c.getColumnIndex("sort_order")));
                    service.setUpdateAt(Utils.ConvertToDateSQLITE(c.getString(c.getColumnIndex("update_at"))));
                    service.setUpdateBy(c.getString(c.getColumnIndex("update_by")));
                    result.add(service);
                } while (c.moveToNext());
            }
            c.close();
        }
        mDb.close();
        return result;
    }

    public void UpdateAllServices(ServiceList services) {
        Log.d(MainActivity3.TAG, "Сохранение сервисов в локальную БД");
        openDB();
        Date maxUpdateAt = null;
        mDb.beginTransaction();
        try {
            for (Service service : services) {
                //Фиксируем дату последнего обновления данных
                maxUpdateAt = getMaxDate(maxUpdateAt, service.getUpdateAt());
                saveServiceToDb(service, false);
            }
            saveMaxUpdateAt(SERVICE_TBL, maxUpdateAt);
            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(MainActivity3.TAG, "Сохранение сервисов в локальную БД, ошибка: " + e.getMessage());

        } finally {
            mDb.endTransaction();
            mDb.close();
            Log.d(MainActivity3.TAG, "Сохранение сервисов в локальную БД завершено");
        }
    }


    public long insertServiceToDb(Service service, boolean closeDb) {
        openDB();
        try {
            ContentValues val = new ContentValues();
            val.put("_id", service.getId());
            val.put("code", service.getCode());
            val.put("name", service.getName());
            val.put("sort_order", service.getOrder());
            val.put("update_at", Utils.getStringFromDateSQLITE(service.getUpdateAt()));
            val.put("update_by", service.getUpdateBy());
            return mDb.insert(SERVICE_TBL, null, val);
        } finally {
            if (closeDb)
                mDb.close();
        }
    }

    public int updateServiceToDb(Service service, boolean closeDb) {
        openDB();
        try {
            ContentValues val = new ContentValues();
            val.put("code", service.getCode());
            val.put("name", service.getName());
            val.put("sort_order", service.getOrder());
            val.put("update_at", Utils.getStringFromDateSQLITE(service.getUpdateAt()));
            val.put("update_by", service.getUpdateBy());
            return mDb.update(SERVICE_TBL, val, " _id = ?", new String[]{Integer.toString(service.getId())});
        } finally {
            if (closeDb)
                mDb.close();
        }
    }

    public void saveServiceToDb(Service service, boolean closeDb) {
        if (updateServiceToDb(service, closeDb) <= 0) {
            insertServiceToDb(service, closeDb);
        }
    }


    public ArrayList<Photo> getPhotoListFromDb(String claimeId) {
        ArrayList<Photo> result = new ArrayList<Photo>();
        Cursor c = null;
        openDB();

        showPhotoTable();

        c = mDb.query(PHOTO_TBL, null, "claime_id = ?", new String[]{claimeId}, null, null, " _id");

        if (c != null) {
            if (c.moveToFirst()) {
                do {
                    Photo photo = new Photo(claimeId, false);
                    photo.setId(c.getInt(c.getColumnIndex("_id")));
                    photo.setFile(c.getBlob(c.getColumnIndex("photo")));
                    photo.setThumbnail(c.getBlob(c.getColumnIndex("thumbnail")));
                    result.add(photo);
                } while (c.moveToNext());
            }
            c.close();
        }
        mDb.close();
        return result;
    }

    private void showPhotoTable() {
        Cursor c;
        c = mDb.query(PHOTO_TBL, new String[]{"_id", "claime_id"}, null, null, null, null, null);
        if (c != null) {
            if (c.moveToFirst())
                do {
                    String str = "";
                    for (int i = 0; i < c.getColumnCount(); i++) {
                        str = str + c.getColumnName(i) + " = " + c.getString(i) + ";";
                    }
                    Log.d(MainActivity3.TAG, str);
                } while ((c.moveToNext()));
            c.close();
        }
    }

    public long insertPhotoToDb(Photo photo, boolean closeDb) {
        openDB();
        Log.d(MainActivity3.TAG, "До вставки");
        showPhotoTable();
        Log.d(MainActivity3.TAG, "Вставляем");
        try {
            ContentValues val = new ContentValues();
            Log.d(MainActivity3.TAG, "id =" + photo.getId());
            Log.d(MainActivity3.TAG, "claime_id =" + photo.getClaimeId());
            val.put("_id", photo.getId());
            val.put("claime_id", photo.getClaimeId());
            val.put("photo", photo.getFile());
            val.put("thumbnail", photo.getThumbnail());
            int id = (int) mDb.insert(PHOTO_TBL, null, val);
            Log.d(MainActivity3.TAG, "После вставки " + id);
            showPhotoTable();
            photo.setId(id);
            return id;
        } finally {
            if (closeDb)
                mDb.close();
        }
    }

    //Получение виртуального Id для заявки
    public String getVirtualIdForClaime() {

        return "" + getLocalId();
    }

    public int getLocalId() {
        int res = 0;
        Cursor c = null;
        openDB();
        ContentValues val = new ContentValues();
        val.put("_id", 1);
        val.put("code", "CLAIME_ID");
        String sctipt = "update " + SEQUENCE_TBL + " set _id = _id + 1 where code= \"" + SEQUENCE_TBL_CLAIME_ID + "\"";
        mDb.execSQL(sctipt);
        c = mDb.query(SEQUENCE_TBL, new String[]{"_id"}, "code = ?", new String[]{SEQUENCE_TBL_CLAIME_ID}, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                res = c.getInt(c.getColumnIndex("_id")) * -1;
            }
            c.close();
            mDb.close();
        }
        return res;
    }

    //обновление фотографий для заявки в локальной базе

    public void updatePhotos(Claime claime) {
        openDB();
        try {
            mDb.beginTransaction();
            deletePhotos(claime.getId(), false);
            for (Photo p :
                    claime.getPhotoList()) {
                insertPhotoToDb(p, false);
            }
            mDb.setTransactionSuccessful();
        } finally {
            mDb.endTransaction();
            ;
        }
    }

    //Удаление фотографий из локальной базы по заданной заявке
    public void deletePhotos(String claimeId, boolean closeDb) {
        openDB();
        try {
            mDb.delete(PHOTO_TBL, " claime_id = ?", new String[]{claimeId});
        } finally {
            if (closeDb)
                mDb.close();
        }
    }

    //обнавление всего списка адресов
    public void UpdateAllAddresses(AddressList addresses) {
        Log.d(MainActivity3.TAG, "Сохранение адресов в локальную БД");
        openDB();
        Date maxUpdateAt = null;
        mDb.beginTransaction();
        try {
            //int delCnt = mDb.delete(ADDRESS_TBL, null, null);
            for (Address a : addresses) {
                //Фиксируем дату последнего обновления данных
                maxUpdateAt = getMaxDate(maxUpdateAt, a.getUpdateAt());
                saveAddressToDb(a, false);
                //long id = insertAddressToDb(a, false);
            }

            saveMaxUpdateAt(ADDRESS_TBL, maxUpdateAt);

            mDb.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(MainActivity3.TAG, "Сохранение адресов в локальную БД, ошибка: " + e.getMessage());

        } finally {

            mDb.endTransaction();
            mDb.close();
            Log.d(MainActivity3.TAG, "Сохранение адресов в локальную БД завершено");
        }
    }

    private Date getMaxDate(Date prev, Date curr) {
        Date result = null;
        if (curr != null) {
            if (prev == null) {
                result = curr;
            } else

            {
                if (prev.compareTo(curr) < 0) {
                    result = curr;
                }
            }
        }
        return result;
    }

    //Сохраняем данные последних изменений справочника
    private void saveMaxUpdateAt(String tblName, Date maxUpdateAt) {
        if (maxUpdateAt == null) return;
        ContentValues values = new ContentValues();
        values.put("table_name", tblName);
        values.put("last_update", Utils.getStringFromDateSQLITE(maxUpdateAt));
        int rowid = mDb.update(LAST_UPDATE_TBL, values, "table_name = ?", new String[]{tblName});
        if (rowid == 0) {
            mDb.insert(LAST_UPDATE_TBL, null, values);
        }
    }

    //Получение даты последнего обновления справочника
    public Date getLastUpdateAt(String tblName) {
        Date result = null;
        Cursor c = null;
        openDB();
        c = mDb.query(LAST_UPDATE_TBL, new String[]{"last_update"}, "table_name = ?", new String[]{tblName}, null, null, null);
        if (c != null) {
            if (c.moveToFirst()) {
                result = Utils.ConvertToDateSQLITE(c.getString(0));
            }
        }
        return result;
    }

    //Удаление всего списка адресов
    private void deleteAllAddresses(boolean closeDb) {
        openDB();
        try {
            mDb.delete(ADDRESS_TBL, null, null);
        } finally {
            if (closeDb)
                mDb.close();
        }
    }

    public UserList getUserListFromDb() {

        UserList result = new UserList(mContext);
        Cursor c = null;
        openDB();
        try {
            c = mDb.query(USER_TBL, null, null, null, null, null, null);

            if (c != null) {
                try {
                    if (c.moveToFirst()) {
                        String str;
                        do {
                            User user = new User();
                            parseUserFromCursorRow(user, c);
                            result.add(user);
                        } while (c.moveToNext());
                    }
                }
                finally {
                    c.close();
                }
            }
        }
        finally {
            mDb.close();
        }
        return result;
    }

    public User getLastSignInUserFromDb(){
        User user = new User();
        Cursor c = null;
        openDB();
        try {
           c = mDb.query(USER_TBL,null,null,null,null,null,"last_login DESC","1");
            if(c.moveToFirst()){
                parseUserFromCursorRow(user, c);
            }
        }
        finally {
            mDb.close();
        }
        return user;
    }

    public User insertUserIntoDb(User user) throws UserInsertDbException{
        String email = user.getEmail();
        if (user == null || user.isEmpty()){
            throw new NullPointerException("Ошибка! Попытка создания пользователя в БД без Email");
        }
        openDB();
        try{
            mDb.beginTransaction();
            ContentValues values = new ContentValues();
            values.put("email", user.getEmail());
            values.put("first_name",user.getFirstName());
            values.put("last_name", user.getLastName());
            values.put("token", user.getToken());
            values.put("expire_token", Utils.getStringFromDateSQLITE(user.getExpireToken()));
            values.put("avatar_img", user.getAvatarImg());

            int result = (int) mDb.insertOrThrow(USER_TBL,null, values);
            mDb.setTransactionSuccessful();
            user.setId(result);
        }
        catch (Exception ex){
            Log.e("ERROR", ex.getMessage());
            throw new UserInsertDbException();
        }
        finally {
            mDb.endTransaction();
            mDb.close();
        }
        return user;
    }

    public void deleteAllUsersFromDB(){
        openDB();
        try {
            mDb.beginTransaction();
            mDb.execSQL("DELETE FROM " + USER_TBL);
            mDb.setTransactionSuccessful();
        }
        finally {
            mDb.endTransaction();
        }
    }


    private void parseUserFromCursorRow(User user, Cursor c) {
        user.setId(c.getInt(c.getColumnIndex("_id")));
        user.setEmail(c.getString(c.getColumnIndex("email")));
        user.setFirstName(c.getString(c.getColumnIndex("first_name")));
        user.setLastName(c.getString(c.getColumnIndex("last_name")));
        user.setToken(c.getString(c.getColumnIndex("token")));
        user.setExpireToken(Utils.ConvertToDateSQLITE(c.getString(c.getColumnIndex("expire_token"))));
        user.setLastLogin(Utils.ConvertToDateSQLITE(c.getString(c.getColumnIndex("last_login"))));
        user.setAvatarImg(c.getBlob(c.getColumnIndex("avatar_img")));
        user.setIsLogin( c.getInt(c.getColumnIndex("is_login")) == 1);
    }

    public void beginTransaction() {
        mDb.beginTransaction();
    }

    public void setTransactionSuccessful() {
        mDb.setTransactionSuccessful();
    }

    public void endTransaction() {
        mDb.endTransaction();
    }


    public void deleteClaime(String id) {
        mDb.delete(CLAIME_TBL, " _id = ?", new String[]{id});
    }
}
