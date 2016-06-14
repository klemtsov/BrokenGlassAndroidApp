package ru.sbrf.zsb.android.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;

import ru.sbrf.zsb.android.rorb.MainActivity;

/**
 * Created by Администратор on 27.05.2016.
 */
public class Utils {
    public static final String DB_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SS";
    public static final String SQLITE_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static String fromJsonString(JSONObject json, String tag) {
        String res = json.optString(tag);
        if (res.trim() == "null") {
            return null;
        }
        return res;
    }

    public static Date ConvertToDateWithFormat(String dateString, String format) {
        if (dateString == null || dateString.trim() == "")
            return null;
        Date convertedDate = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(format);
            //convertedDate = new Date();
            try {
                convertedDate = dateFormat.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            return convertedDate;
        }
        return convertedDate;
    }

    public static Date ConvertToDate(String dateString) {
        return ConvertToDateWithFormat(dateString, DB_DATE_FORMAT);
    }

    public static Date ConvertToDateSQLITE(String dateString) {
        return ConvertToDateWithFormat(dateString, SQLITE_DATE_FORMAT);
    }

    public static String getStringFromDateTime(Date date, String format) {
        if (date == null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String getStringFromDateTime(Date date) {
        if (date == null)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat(DB_DATE_FORMAT, Locale.getDefault());
        return dateFormat.format(date);
    }

    public static String getStringFromDateSQLITE(Date date) {
        return getStringFromDateTime(date, SQLITE_DATE_FORMAT);
    }


    public static Date getLocalDate(Date date)
    {
        if (date == null)
            return null;
        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();
        SimpleDateFormat dateFormat = new SimpleDateFormat(Utils.DB_DATE_FORMAT);
        dateFormat.setTimeZone(tz);

        try {
            return dateFormat.parse(Utils.getStringFromDateTime(date));
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static File getPhotoDirectory() {
        File result = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "brglass");
        if (!result.exists())
            result.mkdirs();
        return result;
    }

    // Check supported file extensions
    private boolean IsSupportedFile(String filePath) {
        String ext = filePath.substring((filePath.lastIndexOf(".") + 1),
                filePath.length());

        if (PhotoGridConstant.FILE_EXTN
                .contains(ext.toLowerCase(Locale.getDefault())))
            return true;
        else
            return false;

    }

    /*
     * getting screen width
     */
    public static int getScreenWidth(Context context) {
        int columnWidth;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();

        final Point point = new Point();
        try {
            display.getSize(point);
        } catch (java.lang.NoSuchMethodError ignore) { // Older device
            point.x = display.getWidth();
            point.y = display.getHeight();
        }
        columnWidth = point.x;
        return columnWidth;
    }

    public static String fromArrayToBase64(byte[] data) {
        if (data == null)
            return null;
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    public static byte[] fromBase64ToArray(String string) {
        if (string == null)
            return null;
        return Base64.decode(string, Base64.DEFAULT);
    }

    public static byte[] resizeImage(String filename, int size) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filename, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        boolean isWidthMax = photoW >= photoH;

        int scaleFactor = 1;

        if (size > 0) {

            if (isWidthMax) {
                scaleFactor = photoW / size;
            } else {
                scaleFactor = photoH / size;
            }
        }

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bm = BitmapFactory.decodeFile(filename, bmOptions);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 50, stream);
        return stream.toByteArray();
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }

    public static boolean isNullOrWhitespace(String s) {
        return s == null || s.length() == 0 || isWhitespace(s);

    }
    private static boolean isWhitespace(String s) {
        int length = s.length();
        if (length > 0) {
            for (int i = 0; i < length; i++) {
                if (!Character.isWhitespace(s.charAt(i))) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

}
