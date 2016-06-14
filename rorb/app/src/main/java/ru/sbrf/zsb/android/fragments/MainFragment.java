package ru.sbrf.zsb.android.fragments;

import android.app.ListFragment;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.sbrf.zsb.android.helper.SlidingTabLayout;
import ru.sbrf.zsb.android.rorb.Claime;
import ru.sbrf.zsb.android.rorb.ClaimeList;
import ru.sbrf.zsb.android.rorb.ClaimeStatus;
import ru.sbrf.zsb.android.rorb.ClaimeStatusList;
import ru.sbrf.zsb.android.rorb.R;


/**
 * Created by Администратор on 07.06.2016.
 */
public class MainFragment extends ListFragment {

    private SlidingTabLayout mSlidingTabLayout;
    private ViewPager mViewPager;
    private int mStatusId;
    private ListView mList;

    public int getStatusId()
    {
        return mStatusId;
    }


    public MainFragment()
    {
    }

    public static MainFragment createInstance(int id)
    {

        MainFragment fragment = new MainFragment();
        fragment.mStatusId = id;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.screen_one, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        // Give the SlidingTabLayout the ViewPager, this must be
        // done AFTER the ViewPager has had it's PagerAdapter set.
        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        mList = (ListView) view.findViewById(android.R.id.list);
        mList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
               mList.getParent().requestDisallowInterceptTouchEvent(
                        true);

                int action = event.getActionMasked();

                switch (action) {
                    case MotionEvent.ACTION_MOVE:
                        mList.getParent()
                                .requestDisallowInterceptTouchEvent(false);
                        break;
                }

                return false;
            }
        });
    }

    // Adapter
    class SamplePagerAdapter extends PagerAdapter {

        /**
         * Return the number of pages to display
         */
        @Override
        public int getCount() {
            ClaimeStatusList claimeStatusList = ClaimeStatusList.get(getActivity());
            return claimeStatusList == null? 0: claimeStatusList.size();
        }

        /**
         * Return true if the value returned from is the same object as the View
         * added to the ViewPager.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        /**
         * Return the title of the item at position. This is important as what
         * this method returns is what is displayed in the SlidingTabLayout.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            ClaimeStatusList claimeStatusList = getStatusList();
            return claimeStatusList.get(position).getName();
        }

        private ClaimeStatusList getStatusList()
        {
            return ClaimeStatusList.get(getActivity());
        }

        /**
         * Instantiate the View which should be displayed at position. Here we
         * inflate a layout from the apps resources and then change the text
         * view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getActivity().getLayoutInflater().inflate(R.layout.screen_one,
                    container, false);
            // Add the newly created View to the ViewPager
            container.addView(view);

            // Retrieve a TextView from the inflated View, and update it's text
            //TextView title = (TextView) view.findViewById(R.id.item_title);
            ClaimeList.get(getActivity()).getItems().add(new Claime(getActivity()));
            ClaimeList.get(getActivity()).getItems().add(new Claime(getActivity()));
            ClaimeList.get(getActivity()).getItems().add(new Claime(getActivity()));

            ArrayList<Claime> list = ClaimeList.get(getActivity()).getItems();
            //ClaimeStatus claimeStatus = ClaimeStatusList.get(getActivity()).get(position);
            //ArrayList<Claime> newlist = new ArrayList<>();
            //for (Claime c :
            //        list) {
            //    if (c.getStatus().getId() == claimeStatus.getId() )
            //        newlist.add(c);
            //}


            ArrayAdapter<Claime> adapter =
                    new ArrayAdapter<Claime>(getActivity(),
                            android.R.layout.simple_list_item_1,
                            list);
            setListAdapter(adapter);

            //title.setText(getStatusList().get(position).getName());

            // Return the View
            return view;
        }

        /**
         * Destroy the item from the ViewPager. In our case this is simply
         * removing the View.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
