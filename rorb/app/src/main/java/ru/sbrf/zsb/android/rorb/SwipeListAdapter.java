package ru.sbrf.zsb.android.rorb;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Oleg on 10.05.2016.
 */
public class SwipeListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ArrayList<Claime> claimeList;

    public SwipeListAdapter(Activity activity, ArrayList<Claime> items) {
        this.activity = activity;
        this.claimeList = items;
    }

    @Override
    public int getCount() {
        return claimeList.size();
    }

    @Override
    public Object getItem(int location) {
        return claimeList.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.task_item, null);

        TextView header = (TextView) convertView.findViewById(R.id.tvHeader);
        TextView info = (TextView) convertView.findViewById(R.id.tvInfo);
        ImageView status = (ImageView) convertView.findViewById(R.id.ivState);
        TextView date = (TextView) convertView.findViewById(R.id.tvDate);
        TextView time = (TextView) convertView.findViewById(R.id.tvTime);
        Claime t = claimeList.get(position);
        header.setText(t.getService().getName());
        info.setText(t.getDescription());
        date.setText(t.getDatePart());
        time.setText(t.getTimePart());
        status.setImageResource(ClaimeStatusList.getStateImage(claimeList.get(position).getStateID()));

        ImageView paperclip = (ImageView) convertView.findViewById(R.id.ivPaperclip);

        if (!t.isHasPhotos()) {
            paperclip.setVisibility(View.INVISIBLE);
        }
        else
        {
            paperclip.setVisibility(View.VISIBLE);
        }

        return convertView;
    }

}
