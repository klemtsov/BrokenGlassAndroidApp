package ru.sbrf.zsb.android.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import ru.sbrf.zsb.android.rorb.Claime;
import ru.sbrf.zsb.android.rorb.ClaimeStatusList;
import ru.sbrf.zsb.android.rorb.FullScreenViewActivity;
import ru.sbrf.zsb.android.rorb.R;
import ru.sbrf.zsb.android.rorb.TaskActivity;

/**
 * Created by Администратор on 30.05.2016.
 */
public class GridViewImageAdapter extends BaseAdapter {

    private Activity _activity;
    private Claime mClaime;
    private int imageWidth;

    public GridViewImageAdapter(Activity activity, Claime claime,
                                int imageWidth) {
        this._activity = activity;
        this.mClaime = claime;
        this.imageWidth = imageWidth;
    }

    @Override
    public int getCount() {
        return mClaime == null || mClaime.getPhotoList() == null ? 0 : mClaime.getPhotoList().size();
    }

    @Override
    public Object getItem(int position) {
        return this.mClaime.getPhotoList().get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            imageView = new ImageView(_activity);

             imageView.setOnClickListener(
                     new OnImageClickListener(position));

            imageView.setOnLongClickListener(
                    new OnImageLongClockListener(position)
            );

        } else {
            imageView = (ImageView) convertView;
        }

        Bitmap image = decodePhoto(mClaime.getPhotoList().get(position).getThumbnail(), imageWidth,
                imageWidth);

        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(imageWidth,
                imageWidth));
        imageView.setImageBitmap(image);

        return imageView;
    }

    class OnImageClickListener implements View.OnClickListener {

        int _postion;

        // constructor
        public OnImageClickListener(int position) {
            this._postion = position;
        }

        @Override
        public void onClick(View v) {
            // on selecting grid view image
            // launch full screen activity
            Intent i = new Intent(_activity, FullScreenViewActivity.class);
            i.putExtra(FullScreenViewActivity.CLAIME_ID_EXTRA, mClaime.getId());
            i.putExtra(FullScreenViewActivity.PHOTO_POSITION_EXTRA, _postion);
            _activity.startActivity(i);
        }
    }

    class OnImageLongClockListener implements View.OnLongClickListener{
        int mPosition;

        public OnImageLongClockListener(int position)
        {
            mPosition = position;
        }

        @Override
        public boolean onLongClick(View v) {
            //Выходим, если статус не "Новый"
            if (!ClaimeStatusList.isNew(mClaime.getStatus())) return false;
            AlertDialog.Builder alert = new AlertDialog.Builder(_activity);
            alert.setTitle(R.string.confirm_title);
            alert.setMessage(R.string.del_photo_question);
            alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mClaime.getPhotoList().remove(mPosition);

                    if (_activity != null && _activity instanceof TaskActivity)
                    {
                        ((TaskActivity)_activity).updatePhotoLabel();
                    }
                    notifyDataSetChanged();
                }
            });
            alert.setNegativeButton(android.R.string.cancel, null);
            alert.show();
            return true;
        }
    }

    /*
     * Resizing image size
     */

    public static Bitmap decodePhoto(byte[] file, int WIDTH, int HEIGHT)
    {
        if (file == null)
            return null;

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(new ByteArrayInputStream(file), null, o);


        final int REQUIRED_WIDTH = WIDTH;
        final int REQUIRED_HIGHT = HEIGHT;
        int scale = 1;
        while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
            scale *= 2;

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(new ByteArrayInputStream(file), null, o2);
    }

    public static Bitmap decodeFile(String filePath, int WIDTH, int HEIGHT) {
        try {

            File f = new File(filePath);

            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);


            final int REQUIRED_WIDTH = WIDTH;
            final int REQUIRED_HIGHT = HEIGHT;
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
                    && o.outHeight / scale / 2 >= REQUIRED_HIGHT)
                scale *= 2;

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}