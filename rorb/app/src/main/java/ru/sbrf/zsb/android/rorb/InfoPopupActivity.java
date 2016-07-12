package ru.sbrf.zsb.android.rorb;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by munk1 on 11.07.2016.
 */
public class InfoPopupActivity extends Activity {
    String TEXT_INFO_MESSAGE;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        TextView textView = (TextView) findViewById(R.id.txtPopupText);
        textView.setText(TEXT_INFO_MESSAGE);
    }
}
