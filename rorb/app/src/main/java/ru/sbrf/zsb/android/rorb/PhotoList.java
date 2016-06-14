package ru.sbrf.zsb.android.rorb;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

/**
 * Created by Администратор on 30.05.2016.
 */
public class PhotoList extends ArrayList<Photo> {

    public PhotoList(){
    }

    public JSONArray getJSONArray() throws JSONException {
        JSONArray result = new JSONArray();
        for (Photo p :
                this) {
            result.put(p.toJSON());
        }

        return result;
    }
}
