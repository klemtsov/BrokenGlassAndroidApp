package ru.sbrf.zsb.android.rorb;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import java.util.UUID;

import ru.sbrf.zsb.android.helper.FullScreenImageAdapter;

public class FullScreenViewActivity extends Activity {
        public static String CLAIME_ID_EXTRA = "ru.sbrf.zsb.android.rorb.claime_id";
        public static String PHOTO_POSITION_EXTRA = "ru.sbrf.zsb.android.rorb.photo_position";
    private FullScreenImageAdapter adapter;
    private ViewPager viewPager;
    private Claime mClaime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fullscreen_photo_view);

        viewPager = (ViewPager) findViewById(R.id.pager);


        Intent i = getIntent();
        String id = i.getStringExtra(CLAIME_ID_EXTRA);
        int position = i.getIntExtra(PHOTO_POSITION_EXTRA, 0);
        mClaime = ClaimeList.get(this).getClaimeById(id);
        if (mClaime != null) {
            adapter = new FullScreenImageAdapter(FullScreenViewActivity.this, mClaime.getPhotoList());
            viewPager.setAdapter(adapter);
        } else {
            viewPager.setAdapter(null);
        }


        // displaying selected image first
        viewPager.setCurrentItem(position);
    }
}
