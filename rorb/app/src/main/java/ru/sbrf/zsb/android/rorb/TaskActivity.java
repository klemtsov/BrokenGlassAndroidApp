package ru.sbrf.zsb.android.rorb;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.sbrf.zsb.android.helper.ExpandableHeightGridView;
import ru.sbrf.zsb.android.helper.MultilineSpinnerAdapter;
import ru.sbrf.zsb.android.helper.ClaimeConstant;
import ru.sbrf.zsb.android.helper.GridViewImageAdapter;
import ru.sbrf.zsb.android.helper.PhotoGridConstant;
import ru.sbrf.zsb.android.helper.Utils;
import ru.sbrf.zsb.android.netload.NetFetcher;

public class TaskActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "ru.sbrf.zsb.rorb.task_id";
    private static final String TAG = "TaskActivity";
    private static final int REQUEST_PHOTO_FROM_CAM = 1;
    private static final int REQUEST_PHOTO_FROM_GALLERY = 2;

    private Claime mClaime;
    private TextView mStatus;
    private ImageView mStatusIcon;
    private ClaimeList mClaimeList;
    private EditText mInfo;
    private TextView mService;
    private Spinner mServiceSpinner;
    private Spinner mAddressSpinner;
    private TextView mAddress;
    private Uri mPhotoFile;
    private boolean mIsNew;

    private GridViewImageAdapter gridPhotoAdapter;
    private ExpandableHeightGridView gridView;
    private int columnWidth;
    private AutoCompleteTextView mAddressAutocomplite;
    private TextView mInfoLabel;
    private boolean mSending;
    private TextView mPhotoLabel;
    private TextView mStatusDate;
    private TextView mStatusTime;
    private ScrollView mScroll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (NavUtils.getParentActivityName(TaskActivity.this) != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            }
        }

        String taskID =  getIntent().getStringExtra(EXTRA_TASK_ID);
        mClaimeList = ClaimeList.get(this);
        mStatus = (TextView) findViewById(R.id.task_activity_tvStatus);
        mStatusIcon = (ImageView) findViewById(R.id.task_activity_ivStatus);

        LinearLayout linService = (LinearLayout) findViewById(R.id.task_activity_linLayService);
        LayoutParams lpViewService = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        LinearLayout linAddress = (LinearLayout) findViewById(R.id.task_activity_linLayAddress);
        LayoutParams lpViewAddress = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        LinearLayout linInfo = (LinearLayout) findViewById(R.id.task_activity_linLayInfo);
        LayoutParams lpViewInfo = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);



        if (Integer.parseInt(taskID) > 0) {
            //Открытие заявки
            mClaime = mClaimeList.getClaimeById(taskID);

            if (mClaime.getPhotoList().size()  == 0)
            {
                mClaime.loadPhotosFromLocal();
            }

            if (mClaime.getPhotoList().size() == 0) {
                loadPhotos();
            }

            mService = new TextView(this);
            mService.setText(mClaime.getService().getName());
            mService.setLayoutParams(lpViewService);
            mService.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            linService.addView(mService);
            mAddress = new TextView(this);
            mAddress.setText(mClaime.getAddress().toString());
            mAddress.setLayoutParams(lpViewAddress);
            mAddress.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            linAddress.addView(mAddress);

            mInfoLabel = new TextView(this);
            linInfo.addView(mInfoLabel);
            mInfoLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            mInfoLabel.setText(mClaime.getDescription());
            mInfoLabel.setLayoutParams(lpViewInfo);
        } else {
            if (taskID.equals(ClaimeConstant.NEW_CLAIME_ID)) {

                mClaime = new Claime(this);
                ClaimeList.get(this).getItems().add(mClaime);
                mIsNew = true;
            } else {
                mClaime = ClaimeList.get(this).getClaimeById(taskID);
                if (mClaime.getPhotoList() != null &&  mClaime.getPhotoList().size() == 0)
                {
                    mClaime.loadPhotosFromLocal();
                }
            }
            mClaime.setStatus(new ClaimeStatus(ClaimeStatusList.get(this).getStateByID(1)));

            //Новая заявка
            MultilineSpinnerAdapter serviceArrayAdapter = new MultilineSpinnerAdapter(this, R.layout.multiline_spinner_item,  ServiceList.get(this));
            mServiceSpinner = new Spinner(this);
            mServiceSpinner.setLayoutParams(lpViewService);
            linService.addView(mServiceSpinner);
            if (mClaime.getService() != null)
            {
                ServiceList list = ServiceList.get(this);
                mServiceSpinner.setSelection(list.getServiceIndexById(mClaime.getService().getId()));
            }

            mServiceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Service service = (Service) parent.getSelectedItem();
                    mClaime.setService(service);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            mServiceSpinner.setAdapter(serviceArrayAdapter);

            mServiceSpinner.post(new Runnable() {
                @Override
                public void run() {
                    if (mClaime.getService() != null) {
                        ServiceList list = ServiceList.get(TaskActivity.this);
                        for (int i = 0; i < list.size(); i++) {
                            if (mClaime.getService().getId() == list.get(i).getId()) {
                                mServiceSpinner.setSelection(i);
                            }
                        }
                    }
                }
            });


            MultilineSpinnerAdapter addressArrayAdapter =
                    new MultilineSpinnerAdapter(this, R.layout.multiline_spinner_item,  AddressList.get(this));
            //android.R.layout. expandable_list_item_1

            mAddressSpinner = new Spinner(this);
            mAddressSpinner.setLayoutParams(lpViewAddress);
            linAddress.addView(mAddressSpinner);

            mAddressSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Address address = (Address) parent.getSelectedItem();
                    mClaime.setAddress(new Address(address));
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

            mAddressSpinner.setAdapter(addressArrayAdapter);

            mAddressSpinner.post(new Runnable() {
                @Override
                public void run() {
                    if (mClaime.getAddress() != null) {
                        AddressList list = AddressList.get(TaskActivity.this);
                        for (int i = 0; i < list.size(); i++) {
                            if (mClaime.getAddress().getId() == list.get(i).getId()) {
                                mAddressSpinner.setSelection(i);
                            }
                        }
                    }
                }
            });

            mInfo = new  EditText(this);// findViewById(R.id.task_activity_tvInfo);
            mInfo.setEms(10);
            mInfo.setSingleLine(false);
            mInfo.setImeOptions(EditorInfo.IME_FLAG_NO_ENTER_ACTION);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
            params.gravity = Gravity.CENTER_HORIZONTAL;
            mInfo.setLayoutParams(params);
            mInfo.setLines(3);

            mInfo.setText(mClaime.getDescription());
            mInfo.setLayoutParams(lpViewInfo);
            mInfo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    mClaime.setDescription(s.toString());
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            linInfo.addView(mInfo);

            mStatusDate = (TextView) findViewById(R.id.task_activity_tvDate);
            mStatusDate.setText(mClaime.getDatePart());
            mStatusTime = (TextView) findViewById(R.id.task_activity_tvTime);
            mStatusTime.setText(mClaime.getTimePart());


        }

        mStatusIcon.setImageResource(ClaimeStatusList.getStateImage(mClaime.getStateID()));
        mStatus.setText(mClaime.getStatus().getName());

        gridView = (ExpandableHeightGridView) findViewById(R.id.task_activity_photo_grid);

        InitilizeGridLayout();
        gridPhotoAdapter = new GridViewImageAdapter(TaskActivity.this, mClaime, columnWidth);
        gridView.setAdapter(gridPhotoAdapter);
        mPhotoLabel = (TextView) findViewById(R.id.task_activity_photo_cnt_tv);
        updatePhotoLabel();

        mScroll = (ScrollView) findViewById(R.id.task_activity_scroll);

        mScroll.post(new Runnable() {
            public void run() {
                mScroll.scrollTo(0, 0);
            }
        });
    }

    public void updatePhotoLabel() {
        String text = getString(R.string.photo_cnt_label) + (mClaime.getPhotoList() == null? 0: mClaime.getPhotoList().size());
        mPhotoLabel.setText(text.toString());
    }


    //Блокировка кот
    private void lockControls(boolean locked)
    {
        if (mServiceSpinner != null)
        {
            mServiceSpinner.setEnabled(!locked);
        }

        if (mAddressSpinner != null)
        {
            mAddressSpinner.setEnabled(!locked);
        }

        if (mInfo != null)
        {
            mInfo.setEnabled(!locked);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean visible = mClaime != null && ClaimeStatusList.isNew(mClaime.getStatus());
        menu.setGroupVisible(R.id.task_activity_menu_group_edit, visible);
        MenuItem AddPhotoItem = (MenuItem) menu.findItem(R.id.add_photo_from_cam);
        AddPhotoItem.setEnabled(hasCamera());
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isSending())
        {
            Toast.makeText(this, R.string.wait_sending_message, Toast.LENGTH_SHORT).show();
            return super.onOptionsItemSelected(item);
        }
        switch (item.getItemId()) {
            case android.R.id.home:
                boolean res = false;
                if (!mIsNew) {
                    if (NavUtils.getParentActivityName(this) != null) {
                        NavUtils.navigateUpFromSameTask(this);
                    }
                } else {
                    AlertDialog.Builder alert = new AlertDialog.Builder(this);
                    alert.setTitle("Заявка не сохранена");
                    alert.setMessage("Выйти без сохранения заявки?");

                    alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Удаляем несохраненную заявку
                            ClaimeList.get(TaskActivity.this).getItems().remove(mClaime);
                            if (NavUtils.getParentActivityName(TaskActivity.this) != null) {
                                NavUtils.navigateUpFromSameTask(TaskActivity.this);
                            }
                        }
                    });
                    alert.setNegativeButton(android.R.string.cancel, null);
                    alert.show();
                }
                return true;
            case R.id.add_photo_from_cam: {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                mPhotoFile = generateFileUri();
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoFile);
                startActivityForResult(intent, REQUEST_PHOTO_FROM_CAM);
                return true;
            }

            case R.id.add_photo_from_gallery: {
                Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_PHOTO_FROM_GALLERY);
                return true;
            }

            case R.id.save_task:
                //Сохранение заявки в локальную базу
                String clId = mClaime.SaveNewClame();
                if (NavUtils.getParentActivityName(TaskActivity.this) != null) {
                    NavUtils.navigateUpFromSameTask(TaskActivity.this);
                }
                Toast.makeText(TaskActivity.this, R.string.claime_saved_local, Toast.LENGTH_SHORT).show();
                return true;
            case R.id.send_task:
                mClaime.SaveNewClame();
                new SaveClaimeToServer().execute(mClaime);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_PHOTO_FROM_CAM) {
            if (resultCode == Activity.RESULT_OK) {

                Photo p = new Photo(mClaime.getId(), true);
                p.setFilename(mPhotoFile.getPath());
                mClaime.getPhotoList().add(p);
                updatePhotoLabel();
                gridPhotoAdapter.notifyDataSetChanged();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Отмена фото");
            }
            mPhotoFile = null;
        }

        if (requestCode == REQUEST_PHOTO_FROM_GALLERY)
        {
            if (resultCode == Activity.RESULT_OK && null != data) {
                Uri selectedImage = data.getData();

                String picturePath = null;

                if (new File(selectedImage.getPath()).isFile())
                {
                    picturePath = selectedImage.getPath();
                }
                else {
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);

                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                }
                Photo p = new Photo(mClaime.getId(), false);
                p.setFilename(picturePath);
                mClaime.getPhotoList().add(p);
                updatePhotoLabel();
                gridPhotoAdapter.notifyDataSetChanged();
            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.d(TAG, "Отмена фото");
            }
        }
    }

    private Uri generateFileUri() {
        File file = null;
        File dir = Utils.getPhotoDirectory();
        file = new File(dir.getPath() + "/" + "photo_"
                + System.currentTimeMillis() + ".jpg");

        Log.d(TAG, "fileName = " + file);
        return Uri.fromFile(file);
    }

    private boolean hasCamera() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA) &&
                !getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return false;
        }
        return true;
    }

    private void InitilizeGridLayout() {
        Resources r = getResources();
        float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                PhotoGridConstant.GRID_PADDING, r.getDisplayMetrics());

        columnWidth = (int) ((Utils.getScreenWidth(this) - ((PhotoGridConstant.NUM_OF_COLUMNS + 1) * padding)) / PhotoGridConstant.NUM_OF_COLUMNS);

        gridView.setNumColumns(PhotoGridConstant.NUM_OF_COLUMNS);
        gridView.setColumnWidth(columnWidth);
        gridView.setStretchMode(GridView.NO_STRETCH);
        gridView.setPadding((int) padding, (int) padding, (int) padding,
                (int) padding);
        gridView.setHorizontalSpacing((int) padding);
        gridView.setVerticalSpacing((int) padding);
        gridView.setExpanded(true);
    }

    public void loadPhotos() {
        if (mClaime.getPhotoList().size() == 0) {
            LoadPhotosTask task = new LoadPhotosTask();
            task.execute(mClaime);
        }
    }

    public void setSending(boolean sending) {
        mSending = sending;
        lockControls(sending);
    }

    public boolean isSending() {
        return mSending;
    }

    private class LoadPhotosTask extends AsyncTask<Claime, Void, Claime> {

        @Override
        protected Claime doInBackground(Claime... params) {
            NetFetcher nf = new NetFetcher(TaskActivity.this);
            try {
                nf.fetchPhotos(params[0]);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Claime claime) {
            super.onPostExecute(claime);
            gridPhotoAdapter.notifyDataSetChanged();
        }
    }


    private class SaveClaimeToServer extends AsyncTask<Claime, Void, String> {
        @Override
        protected String doInBackground(Claime... params) {
            String result = null;
            try {
                JSONObject json = params[0].toJson();
                JSONObject innerJson = new JSONObject();
                innerJson.put("Email", NetFetcher.UserToken);
                json.put("User", innerJson);
                String uri = Uri.parse(NetFetcher.ENDPOINT_API).buildUpon()
                        .appendPath(NetFetcher.CLAIMS)
                        .build().toString();

                result = makeRequest(uri, json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
                return e.getMessage();
            }
            return  result;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            TaskActivity.this.setSending(true);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s == null || s.trim() == "") {
                Toast.makeText(TaskActivity.this, "Заявка отправлена!", Toast.LENGTH_SHORT).show();
                if (NavUtils.getParentActivityName(TaskActivity.this) != null) {
                    NavUtils.navigateUpFromSameTask(TaskActivity.this);
                }
            } else {
                Toast.makeText(TaskActivity.this, s, Toast.LENGTH_SHORT).show();
            }
            TaskActivity.this.setSending(false);
        }

        private String makeRequest(String uri, String json) {
            HttpURLConnection urlConnection;
            String url;
            String data = json;
            String result = null;
            try {
                //Connect
                urlConnection = (HttpURLConnection) ((new URL(uri).openConnection()));
                urlConnection.setConnectTimeout(60000);
                urlConnection.setRequestProperty("Authorization", NetFetcher.getUserToken());
                urlConnection.setDoOutput(true);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                urlConnection.setRequestProperty("Accept", "application/json");
                urlConnection.setRequestMethod("POST");
                urlConnection.connect();
                //Write
                OutputStream outputStream = urlConnection.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                writer.write(data);
                writer.close();
                outputStream.close();
                //Read
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                String line = null;
                StringBuilder sb = new StringBuilder();

                while ((line = bufferedReader.readLine()) != null) {
                    sb.append(line);
                }

                bufferedReader.close();
                JSONObject savedjson = new JSONObject(sb.toString());
                mClaime.deleteFromDb();
                mClaime.updateFromJson(savedjson);
                mClaime.saveToLocalDb();

            } catch (Exception e){
                Log.d(MainActivity3.TAG, e.getMessage());
                result = "Ошибка при отправке заявки на сервер";
            }
            return result;
        }


    }
}
