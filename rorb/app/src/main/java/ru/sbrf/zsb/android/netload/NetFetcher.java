package ru.sbrf.zsb.android.netload;


import android.content.Context;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;

import ru.sbrf.zsb.android.helper.Utils;
import ru.sbrf.zsb.android.rorb.Address;
import ru.sbrf.zsb.android.rorb.AddressList;
import ru.sbrf.zsb.android.rorb.Claime;
import ru.sbrf.zsb.android.rorb.ClaimeStatus;
import ru.sbrf.zsb.android.rorb.ClaimeStatusList;
import ru.sbrf.zsb.android.rorb.DBHelper;
import ru.sbrf.zsb.android.rorb.MainActivity;
import ru.sbrf.zsb.android.rorb.Service;
import ru.sbrf.zsb.android.rorb.ServiceList;

/**
 * Created by Администратор on 27.05.2016.
 */
public class NetFetcher {
    //public static final String ENDPOINT = "http://192.168.1.245:8050/api/";
    public static final String ENDPOINT = "http://muzychenkoaa-001-site2.ftempurl.com/api/";
    public static final String SERVICES = "Services";
    public static final String SERVICE_STATUS = "ClaimStates";
    public static final String ADDRESSES = "Adresses";
    //public static final String TAG = "NetFetcher";
    public static final String CLAIMS = "Claims";
    public static final String USER_TOKEN_PARAM = "userEmail";
    public static final String UserToken = "zsbdeveloper@gmail.com";
    private static final String LAST_UPDATE_PARAM = "lastUpdateDate";
    private Context mContext;
    private String mUsername;

    public String getLastError() {
        return mLastError;
    }

    private String mLastError;

    public static String getUserToken() {
        return "bearer e5dvKEyeypOB-0Fn7HvmsoMgtHeMJPfgab2PtqfmS1d4XLrxIBms7NwebW1Ge768KXTKv-v4QUWeKqvXj_AJsxtSC4xAEg8rTbOADa1jQyi_wyyVm7tDPXYCca_FLQbf1ZPUdwUKXOaUwwWjkwULAacYBKHrd68cetLq6zKK_NSwyXFz--4F3A3UzTQtsNLtvXOqsGKRt2xDvC2474MgkDmdLs-BkNHGibvH6wMPGino8vOsu4pPEmG-Rpdwupv4Tq8y4LkxvWZ7zNipA4vfpt4TNbfLIwgSz1SeQ0_rZVEzlzGxka9yXM9bSDBi_G2SaXzWtQUjVMqsRlu1kflYnv1i1LoNqTn0GBjXhcp7AQV5NIlhsoZfec5im4smaoBuUutNVnBGxGf0aE_-sDPrcX-M9aCFH9si1dbevb12gzyBoLf26Z06TibiT0l5DNM9LQ34kKVXMrpTHrRXDImiRbiL7rJQZmFCl4s_XIIGwjGu7XduHewbeEAYq4rb15Pb";
        //return "bearer ag50dRQOxyJdDFTMwS-EroLLWiTwIIb_zGeLEMR3JnLs03MaOgkvoqpBofW56xh6QBJZMdIrbHwmK8fQvNYlYKmiXNIRRQzFWZhjuBPcmATXD3axv44gXajfej9oZn0kbyUQnO97K8_t6qWjjxRSYAWImSYCgygnlslZ_uxlDrLwCjGSCHqNwihKluBkl5wUpIK9xDSWOI3h_tbIpsXi8hFT-W6xCV-fCLg3LBHPIaXMBhBJNO2RpM-z2Fg4u-d1Oft4yCDSkBSiO1u3D5wLuoorORsKTO0QA82XLobTFG5KAdCBolW0Vcq5OUaHKSeb3xZMw6RUqESStZzIbAcv_zSh8IpqKS7JOrkeBBvHzpDmsJrljoLOHO5aJYV_OCABgVQpzlUYMZwfEZ_1UshGX0-tdjNUdc01rmdGrD3Wa4yWh04cAGcLNnr_RRm-OBOAmqngINOFBr3Q1hXjyGUihMCsy1ACRGAdUg7PMHDtoStPbUhSFQ0Rye4jRpg0D3-W";
    }

    public NetFetcher(Context context) {
        mContext = context;
        mUsername = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext())
                .getString(MainActivity.USER_TOKEN, NetFetcher.UserToken);
    }

    //Возвращает массив байт, принятых по сети
    byte[] getUrlBytes(String urlSpec) throws IOException {
        mLastError = null;
        URL url = new URL(urlSpec);
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("Authorization", getUserToken());
            connection.setConnectTimeout(30000);
            try {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                InputStream in = new BufferedInputStream(connection.getInputStream());
                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.d(MainActivity.TAG, "Ошибка соединения: код " + connection.getResponseCode());
                    return null;
                }
                int bytesRead = 0;
                byte[] buffer = new byte[1024];
                while ((bytesRead = in.read(buffer)) > 0) {
                    out.write(buffer, 0, bytesRead);
                }
                out.close();
                return out.toByteArray();
            } finally {
                connection.disconnect();
            }
        } catch (Exception e) {
            Log.d(MainActivity.TAG, e.getMessage());
            return null;
        }
    }

    //Возвращает запрос в виде строки
    public String getUrl(String urlSpec) throws IOException {
        byte[] arr = getUrlBytes(urlSpec);
        String res = "";
        if (arr != null) {
            res = new String(arr);
        }
        return res;
    }


    //Читаем заявки из сети
    public ArrayList<Claime> fetchClaims() throws JSONException {
        Log.d(MainActivity.TAG, "Считывание заявок по сети");
        ArrayList<Claime> result = new ArrayList<>();
        try {
            String url = Uri.parse(NetFetcher.ENDPOINT).buildUpon()
                    .appendPath(NetFetcher.CLAIMS)
                    .appendQueryParameter(NetFetcher.USER_TOKEN_PARAM, mUsername)
                    .build().toString();
            String xmlString = getUrl(url);
            if (!Utils.isNullOrWhitespace(xmlString)) {
                JSONArray arr = (JSONArray) new JSONTokener(xmlString).nextValue();
                for (int i = 0; i < arr.length(); i++) {
                    result.add(new Claime(arr.getJSONObject(i), mContext));
                }
            }
            Log.d(MainActivity.TAG, "Считывание заявок по сети завершено");

        } catch (IOException ioe) {

            Log.e(MainActivity.TAG, "Ошибка при чтении заявок: " + ioe.getMessage(), ioe);
        }
        return result;
    }

    //Читаем заявки из сети
    public Claime fetchPhotos(Claime claime) throws JSONException {
        Log.d(MainActivity.TAG, "Считывание фоток по сети");
        try {
            try {
                String url = Uri.parse(NetFetcher.ENDPOINT).buildUpon()
                        .appendPath(NetFetcher.CLAIMS)
                        .appendPath(claime.getId())
                        .build().toString();
                String xmlString = getUrl(url);
                if (!Utils.isNullOrWhitespace(xmlString)) {
                    JSONObject obj = new JSONObject(xmlString);
                    Claime cl = new Claime(obj, mContext);
                    claime.getPhotoList().clear();
                    claime.getPhotoList().addAll(cl.getPhotoList());
                    Log.d(MainActivity.TAG, "Считывание фоток по сети завершено, считано: " + claime.getPhotoList().size());
                    claime.updatePhotosInLocal();
                }

            } catch (IOException ioe) {

                Log.e(MainActivity.TAG, "Ошибка при чтении фоток: " + ioe.getMessage(), ioe);
            }
        } catch (Exception e) {
            Log.e(MainActivity.TAG, "Ошибка при чтении фоток: " + e.getMessage(), e);
        }
        return claime;
    }

    //Читаем адреса с сервера
    public AddressList fetchAddresses() throws JSONException, IOException {
        Log.d(MainActivity.TAG, "Считывание адресов по сети");
        AddressList result = new AddressList(mContext);
        Date lastUpdate = getLastDate(DBHelper.ADDRESS_TBL);

        try {
            String url = Uri.parse(NetFetcher.ENDPOINT).buildUpon()
                    .appendPath(NetFetcher.ADDRESSES)
                    .build().toString();

            if (lastUpdate != null) {
                url = Uri.parse(NetFetcher.ENDPOINT).buildUpon()
                        .appendPath(NetFetcher.ADDRESSES)
                        .appendQueryParameter(NetFetcher.LAST_UPDATE_PARAM, Utils.getStringFromDateTime(lastUpdate))
                        .build().toString();
            }

            String xmlString = getUrl(url);
            if (!Utils.isNullOrWhitespace(xmlString)) {
                JSONArray arr = (JSONArray) new JSONTokener(xmlString).nextValue();
                for (int i = 0; i < arr.length(); i++) {
                    Address item = new Address(arr.getJSONObject(i));
                    result.add(item);
                }
            }
            Log.d(MainActivity.TAG, "Считывание адресов по сети завершено");
        } catch (IOException ioe) {
            Log.e(MainActivity.TAG, "Ошибка при чтении адресов: " + ioe.getMessage(), ioe);
            throw ioe;
        }
        return result;
    }

    ;

    public ClaimeStatusList fetchStatuses() throws JSONException {
        Log.d(MainActivity.TAG, "Считывание статусов по сети");
        ClaimeStatusList result = new ClaimeStatusList(mContext);
        Date lastUpdate = getLastDate(DBHelper.STATUS_TBL);

        try {
            String url = Uri.parse(NetFetcher.ENDPOINT).buildUpon()
                    .appendPath(NetFetcher.SERVICE_STATUS)
                    .build().toString();

            if (lastUpdate != null) {
                url = Uri.parse(NetFetcher.ENDPOINT).buildUpon()
                        .appendPath(NetFetcher.SERVICE_STATUS)
                        .appendQueryParameter(NetFetcher.LAST_UPDATE_PARAM, Utils.getStringFromDateTime(lastUpdate))
                        .build().toString();
            }

            String xmlString = getUrl(url);
            if (!Utils.isNullOrWhitespace(xmlString)) {
                JSONArray arr = (JSONArray) new JSONTokener(xmlString).nextValue();
                for (int i = 0; i < arr.length(); i++) {
                    result.add(new ClaimeStatus(arr.getJSONObject(i)));

                }
            }
            Log.d(MainActivity.TAG, "Считывание статусов по сети завершено!");

        } catch (IOException ioe) {
            Log.e(MainActivity.TAG, "Ошибка при чтении статусов: " + ioe.getMessage(), ioe);
        }
        return result;
    }

    private Date getLastDate(String tbl) {
        DBHelper db = new DBHelper(mContext);
        Date lastUpdate = null;
        try {
            lastUpdate = db.getLastUpdateAt(tbl);
        } finally {
            db.close();
        }
        return lastUpdate;
    }

    public ServiceList fetchServices() throws JSONException {
        Log.d(MainActivity.TAG, "Считывание сервисов по сети");
        ServiceList result = new ServiceList(mContext);
        Date lastUpdate = getLastDate(DBHelper.SERVICE_TBL);
        try {
            String url = Uri.parse(NetFetcher.ENDPOINT).buildUpon()
                    .appendPath(NetFetcher.SERVICES)
                    .build().toString();

            if (lastUpdate != null) {
                url = Uri.parse(NetFetcher.ENDPOINT).buildUpon()
                        .appendPath(NetFetcher.SERVICES)
                        .appendQueryParameter(NetFetcher.LAST_UPDATE_PARAM, Utils.getStringFromDateTime(lastUpdate))
                        .build().toString();
            }

            String xmlString = getUrl(url);
            if (!Utils.isNullOrWhitespace(xmlString)) {
                JSONArray arr = (JSONArray) new JSONTokener(xmlString).nextValue();
                for (int i = 0; i < arr.length(); i++) {
                    result.add(new Service(arr.getJSONObject(i)));
                }
            }
            Log.d(MainActivity.TAG, "Считывание сервисов по сети завершено");
        } catch (IOException ioe) {
            Log.e(MainActivity.TAG, "Ошибка при чтении статусов: " + ioe.getMessage(), ioe);
        }
        return result;
    }

}
