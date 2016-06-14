package ru.sbrf.zsb.android.helper;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import ru.sbrf.zsb.android.rorb.R;



import ru.sbrf.zsb.android.rorb.Address;
import ru.sbrf.zsb.android.rorb.RefObject;
import ru.sbrf.zsb.android.rorb.RefObjectList;

/**
 * Created by Администратор on 02.06.2016.
 */
public class MultilineSpinnerAdapter extends ArrayAdapter<RefObject> {

    private final RefObjectList mList;
    private LayoutInflater inflater;
    //private int oddRowColor = Color.parseColor("#E7E3D1");
    //private int evenRowColor = Color.parseColor("#F8F6E9");

    static class ViewHolder {
        public TextView nameTextView;
    }

    public MultilineSpinnerAdapter(Activity activity, int handling_layout_item, RefObjectList items) {
        super(activity, handling_layout_item, items);
        mList = items;
        this.inflater = (LayoutInflater) activity.getSystemService(activity.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return getViewInternal(position, convertView, parent, true);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return getViewInternal(position, convertView, parent, true);
    }

    private View getViewInternal(int position, View convertView, ViewGroup parent, boolean isDropdownView) {
        View view = convertView;
        if (view == null) {
            view = inflater.inflate(R.layout.multiline_spinner_item, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView) view.findViewById(R.id.multiline_spinner_tv);
            view.setTag(viewHolder);
        }
        //if (isDropdownView) {
        //    view.setBackgroundColor(position % 2 == 0 ? evenRowColor : oddRowColor);
        //}
        ViewHolder holder = (ViewHolder) view.getTag();
        RefObject model = (RefObject) mList.get(position);
        holder.nameTextView.setText(model.toString());
        return view;
    }
}
