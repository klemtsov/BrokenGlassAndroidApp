package ru.sbrf.zsb.android.rorb;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import ru.sbrf.zsb.android.fragments.MainFragment;
import ru.sbrf.zsb.android.netload.NetFetcher;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SwipeRefreshLayout.OnRefreshListener {
    public static final String USER_TOKEN = "UserLogin";
    private static final int SHOW_PROGRESS = 1;
    private static final int STOP_PROGRESS = 2;
    private static final int NETWORK_FAILURE = 3;
    private static final String SETTING_FILE = "settings";
    private static final String SELECTED_GROUP = "selected_group";

    private ClaimeList mClaimeList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView mListView;
    private SwipeListAdapter adapter;
    public static final String TAG = "MainActivity";
    private MyHandler mHandler;

    // initially offset will be 0, later will be updated while parsing the json
    private int offSet = 0;
    private ProgressBar mPropgress;
    private boolean mLoading;
    private CharSequence mTitle;
    private NavigationView mNavView;
    private HashMap<MenuItem, Integer> myDrawerMenuItemMap;
    private SharedPreferences mProgPrefs;

    public boolean isLoading() {
        return mLoading;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  if (isLoading()) {
                    Toast.makeText(MainActivity.this, getString(R.string.wait_loading_ens), Toast.LENGTH_SHORT).show();
                } else {
                    if (existsAllRefs()) {
                        Intent i = new Intent(MainActivity.this,
                                TaskActivity.class);
                        i.putExtra(TaskActivity.EXTRA_TASK_ID, ClaimeConstant.NEW_CLAIME_ID);
                        startActivity(i);
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "Отсуствуют справочники, потяните экран вниз для обновления!", Toast.LENGTH_LONG).show();
                    }
                }
                */
            }

        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        mNavView = (NavigationView) findViewById(R.id.nav_view);
        final Menu menu = mNavView.getMenu();
        SubMenu claimeGroup = menu.addSubMenu("Обращения");
        ClaimeStatusList claimeStatusList = ClaimeStatusList.get(this);
        myDrawerMenuItemMap = new HashMap<>();
        for (int i = 0; i < claimeStatusList.size(); i++) {
            ClaimeStatus claimeStatus = claimeStatusList.get(i);
            MenuItem menuItem = claimeGroup.add(claimeStatus.getName());
            setMenuItemTag(menuItem, claimeStatus.getId());
        }
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        //mPropgress = (ProgressBar) findViewById(R.id.main_activity_progressBar);
        //mHandler = new MyHandler();

        //AddressList addressList = AddressList.get(this);
        //ServiceList serviceList = ServiceList.get(this);
        //ClaimeStatusList claimeStatusList = ClaimeStatusList.get(this);

        //mClaimeList = ClaimeList.get(this);
        //reloadTasksFromLocal();


       // swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        /*mListView = (ListView) findViewById(android.R.id.list);

        setupAdapter();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Claime task = (Claime) parent.getAdapter().getItem(position);
                Intent i = new Intent(MainActivity.this,
                        TaskActivity.class);
                i.putExtra(TaskActivity.EXTRA_TASK_ID, task.getId());
                startActivity(i);
            }
        });

        mListView.setOnItemLongClickListener(new ListViewOnItemLongClickListener());
        */
        //swipeRefreshLayout.setOnRefreshListener(this);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        /*swipeRefreshLayout.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        //reloadTasksFromLocal();
                                    }
                                }
        );
        */

        //if (savedInstanceState == null) {
            mProgPrefs = getSharedPreferences(SETTING_FILE, Context.MODE_PRIVATE);
            int selectedGroupId = mProgPrefs.getInt(SELECTED_GROUP, 0);
            ClaimeStatus claimeStatus = ClaimeStatusList.get(this).getStateByID(selectedGroupId);
            if (claimeStatus != null) {
                selectStatusMenuItem(selectedGroupId);
            }
            else
            {
                selectStatusMenuItem(ClaimeStatusList.STATUS_ALL_ID);
            }
        //}
    }

    public void setMenuItemTag(MenuItem item, Integer tag)
    {
        myDrawerMenuItemMap.put(item, tag);
    }

    // returns null if tag has not been set(or was set to null)
    public Integer getMenuItemTag(MenuItem item)
    {
        return myDrawerMenuItemMap.get(item);
    }

    //Проверка наличия значений в справочниках
    private boolean existsAllRefs() {
        return ClaimeStatusList.get(this).size() > 0 && AddressList.get(this).size() > 0
                && ServiceList.get(this).size() > 0;
    }

    //класс реализует обработчик долгого нажатие на список
    private class ListViewOnItemLongClickListener implements AdapterView.OnItemLongClickListener {
        private Claime mClaime;

        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            mClaime = (Claime) parent.getAdapter().getItem(position);
            if (isLoading() || mClaime == null || !ClaimeStatusList.isNew(mClaime.getStatus()))
                return false;
            AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.this);
            alert.setTitle(R.string.confirm_title);
            alert.setMessage(R.string.del_claime_question);

            alert.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Удаляем несохраненную заявку
                    if (mClaime != null) {
                        ClaimeList.get(MainActivity.this).deleteClaime(mClaime);
                        if (MainActivity.this.adapter != null)
                        {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            });
            alert.setNegativeButton(android.R.string.cancel, null);
            alert.show();
            return true;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void reloadTasksFromLocal() {
        if (mClaimeList.getItems().size() == 0) {
            loadTasksFromLocal();
        }
    }

    //Настройка адаптера
    private void setupAdapter() {
        ClaimeList list = ClaimeList.get(MainActivity.this);
        adapter = new SwipeListAdapter(this, list.getItems());
        mListView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        try {
            loadTasks();
            loadTasksFromLocal();
        } catch (Exception e) {
            Log.d(TAG, "loadTasks ошибка: " + e.getMessage(), e);
        } finally {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private void loadTasks() {
        if (!mLoading) {
            new FetchTasks().execute(true);
        }
    }

    private void loadTasksFromLocal() {
        if (!mLoading) {
            new FetchTasks().execute(false);
        }
    }

    public void selectStatusMenuItem(int statusId)
    {
        Fragment fragment = MainFragment.createInstance(statusId);
        if (fragment != null) {
            android.app.FragmentManager fragmentManager = getFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.content_frame, fragment).commit();

            mProgPrefs.edit().putInt(SELECTED_GROUP, statusId);

            // Highlight the selected item, update the title, and close the drawer
            //item.setItemChecked(position, true);
            //setTitle(mScreenTitles[position]);
            //mDrawerLayout.closeDrawer(mDrawerList);
        } else {
            // Error
            Log.e(this.getClass().getName(), "Error. Fragment is not created");
        }
    }





    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        int id = item.getItemId();

        Integer tag = getMenuItemTag(item);
        if (tag != null)
        {
            selectStatusMenuItem(tag);

        }



        switch (id)
        {

           // case R.id.nav_all:
           //     fragment = new MainFragment();
           //     break;
           // case R.id.nav_new:
           //     fragment = new MainFragment();
        }

      /*  if (id == R.id.my_profile) {
            // Окно настроек профиля
        } else
        if (id == R.id.prog_settings) {
            // Окно настроек программы
        }
    */

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        super.setTitle(title);
    }

    private class FetchTasks extends AsyncTask<Boolean, Void, Void> {
    private String error;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mLoading = true;
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
    }

    @Override
    protected Void doInBackground(Boolean... params) {
        ClaimeList claimeList = ClaimeList.get(MainActivity.this);
        try {
            if (!params[0]) {
                //Загрузка из локальной БД
                DBHelper db = new DBHelper(MainActivity.this);
                claimeList.setItems(db.getClameListFromDb());
            } else {
                //Загрузка с сервера
                NetFetcher nf = new NetFetcher(MainActivity.this);
                ClaimeStatusList cslist = nf.fetchStatuses();
                cslist.saveToDb();
                cslist.loadFromDb();
                //ClaimeStatusList.set(nf.fetchStatuses(), MainActivity.this);
                AddressList addressList = nf.fetchAddresses();
                addressList.saveToDb();
                addressList.loadFromDb();
                //AddressList.set(nf.fetchAddresses(), MainActivity.this);
                ServiceList serviceList = nf.fetchServices();
                serviceList.saveToDb();
                serviceList.loadFromDb();
                //ServiceList.set(nf.fetchServices(), MainActivity.this);

                claimeList.setItems(nf.fetchClaims());
                claimeList.saveToDb();
                DBHelper db = new DBHelper(MainActivity.this);
                claimeList.setItems(db.getClameListFromDb());


            }

        } catch (JSONException e) {
            error = "Ошибка :" + e.getMessage();
            Log.d(TAG, error, e);
        } catch (IOException e) {
            error = "Ошибка :" + e.getMessage();
            Log.d(TAG, error, e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        try {
            if (error != null) {
                Toast.makeText(MainActivity.this, error, Toast.LENGTH_LONG);
            }

            setupAdapter();
            mHandler.sendEmptyMessage(STOP_PROGRESS);
        } finally {
            mLoading = false;
        }

    }
}


private class MyHandler extends android.os.Handler {
    @Override
    public void handleMessage(Message msg) {
        switch (msg.what) {
            case SHOW_PROGRESS:
                mPropgress.setVisibility(View.VISIBLE);
                mHandler.removeMessages(SHOW_PROGRESS);
                break;

            case STOP_PROGRESS:
                mPropgress.setVisibility(View.INVISIBLE);
                mHandler.removeMessages(STOP_PROGRESS);
                break;

            case NETWORK_FAILURE:
                Toast.makeText(MainActivity.this, "Ошибка соединения!", Toast.LENGTH_LONG).show();
                mHandler.removeMessages(NETWORK_FAILURE);
                break;
        }
        super.handleMessage(msg);
    }
}
}
