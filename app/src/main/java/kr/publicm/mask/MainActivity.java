package kr.publicm.mask;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.appbar.AppBarLayout;


public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    InputMethodManager inputMethodManager;

    private GPS gps;
    private DataLint dataLint;

    private MaskListFragment maskListFragment;
    private MapFragment mapFragment;
    private ZeroPayFragment zeroPayFragment;

    private MaskInfo maskInfo;
    private ZeroPayInfo zeroPayInfo;

    private RecyclerView maskList;
    private MaskListAdapter maskListAdapter;
    private LinearLayoutManager maskListLayoutManager;

    private RecyclerView zeroPayList;
    private ZeroPayListAdapter zeroPayListAdapter;
    private LinearLayoutManager zeroPayListLayoutManager;

    private AppBarLayout appbarLayout;
    private SwipeRefreshLayout swipeRefresh;
    private View titlePanel;
    private TextView titleView;

    private SwipeRefreshLayout zeroPaySwipeRefresh;
    private CheckBox zeroPayChangeType;
    private EditText zeroPayInput;
    private Button zeroPayClearText;
    private LinearLayout zeroPayBtnLayout;
    private Button zeroPayBtnPrevious;
    private Button zeroPayBtnNext;

    private RadioGroup bottomTabGroup;
    private RadioButton tabMyLocation;
    private RadioButton tabMap;
    private RadioButton tabZeroPay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();
        maskListFragment = new MaskListFragment();
        mapFragment = new MapFragment();
        zeroPayFragment = new ZeroPayFragment();

        fragmentTransaction.add(R.id.fragmentFrame, maskListFragment);
        fragmentTransaction.add(R.id.fragmentFrame, mapFragment);
        fragmentTransaction.add(R.id.fragmentFrame, zeroPayFragment);

        fragmentTransaction.commitNow();

        inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        gps = new GPS(this, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (maskInfo != null) {
                    maskInfo.setLocation(location.getLatitude(), location.getLongitude(), 300);
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
        dataLint = new DataLint(this);

        Location gpsLocation = gps.getLocation();
        CustomLocation userLocation;

        if (gpsLocation != null) {
            userLocation = new CustomLocation(gpsLocation, 300);
        } else {
            userLocation = new CustomLocation(37.493046, 127.013768, 300);
            Toast.makeText(this, getResources().getText(R.string.TOAST_NO_LOCATION), Toast.LENGTH_LONG).show();
        }

        maskInfo = new MaskInfo(userLocation);
        maskInfo.refreshStores();
        zeroPayInfo = new ZeroPayInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();

        appbarLayout = findViewById(R.id.appbarLayout);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        titlePanel = findViewById(R.id.titlePanel);
        titleView = findViewById(R.id.titleText);
        bottomTabGroup = findViewById(R.id.bottomTabGroup);
        tabMyLocation = findViewById(R.id.tabMyLocation);
        tabMap = findViewById(R.id.tabMap);
        tabZeroPay = findViewById(R.id.tabZeroPay);
        maskList = findViewById(R.id.maskList);
        maskListAdapter = new MaskListAdapter(this, maskInfo);
        maskListLayoutManager = new LinearLayoutManager(this);
        zeroPayList = findViewById(R.id.zeroPayList);
        zeroPayListAdapter = new ZeroPayListAdapter(this, zeroPayInfo);
        zeroPayListLayoutManager = new LinearLayoutManager(this);
        zeroPaySwipeRefresh = findViewById(R.id.zeroPaySwipeRefresh);
        zeroPayChangeType = findViewById(R.id.zeroPayChangeType);
        zeroPayInput =  findViewById(R.id.zeroPayInput);
        zeroPayClearText = findViewById(R.id.zeroPayClearText);
        zeroPayBtnLayout = findViewById(R.id.zeroPayBtnLayout);
        zeroPayBtnPrevious = findViewById(R.id.zeroPayBtnPrevious);
        zeroPayBtnNext = findViewById(R.id.zeroPayBtnNext);

        appbarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                float percentage = (float) Math.abs(i) / appBarLayout.getTotalScrollRange();
                titlePanel.setAlpha(1.0f - percentage);
                titleView.setAlpha(percentage);
            }
        });

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                maskInfo.refreshStores();
                maskListAdapter.notifyDataSetChanged();
                swipeRefresh.setRefreshing(false);
            }
        });

        bottomTabGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.tabMyLocation :
                        inputMethodManager.hideSoftInputFromWindow(zeroPayInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        tabMyLocation.setTypeface(Typeface.DEFAULT_BOLD);
                        tabMap.setTypeface(Typeface.DEFAULT);
                        tabZeroPay.setTypeface(Typeface.DEFAULT);
                        maskListFragment.getView().setVisibility(View.VISIBLE);
                        mapFragment.getView().setVisibility(View.INVISIBLE);
                        zeroPayFragment.getView().setVisibility(View.INVISIBLE);

                        break;
                    case R.id.tabMap :
                        inputMethodManager.hideSoftInputFromWindow(zeroPayInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        tabMyLocation.setTypeface(Typeface.DEFAULT);
                        tabMap.setTypeface(Typeface.DEFAULT_BOLD);
                        tabZeroPay.setTypeface(Typeface.DEFAULT);
                        maskListFragment.getView().setVisibility(View.INVISIBLE);
                        mapFragment.getView().setVisibility(View.VISIBLE);
                        zeroPayFragment.getView().setVisibility(View.INVISIBLE);
                        mapFragment.setMarkerList(maskInfo);
                        break;
                    case R.id.tabZeroPay :
                        inputMethodManager.hideSoftInputFromWindow(zeroPayInput.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        tabMyLocation.setTypeface(Typeface.DEFAULT);
                        tabMap.setTypeface(Typeface.DEFAULT);
                        tabZeroPay.setTypeface(Typeface.DEFAULT_BOLD);
                        maskListFragment.getView().setVisibility(View.INVISIBLE);
                        mapFragment.getView().setVisibility(View.INVISIBLE);
                        zeroPayFragment.getView().setVisibility(View.VISIBLE);
                        break;
                }
            }
        });

        maskListAdapter.setOnItemClickListener(new MaskListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                MaskInfo.Store store = maskInfo.getStores()[position];
                bottomTabGroup.check(R.id.tabMap);
                mapFragment.setFocusOn(new CustomLocation(store.lat, store.lng), store.name);
            }
        });

        zeroPayChangeType.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    zeroPayInput.setHint(R.string.HINT_NAME);
                } else {
                    zeroPayInput.setHint(R.string.HINT_ADDRESS);
                }
                zeroPayClearText.callOnClick();
            }
        });

        zeroPayInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_UP) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    String inputText = ((EditText)v).getText().toString();
                    if (inputText.length() > 1) {
                        ZeroPayInfo.SearchOptions searchOptions = dataLint.getLocalCode(inputText, zeroPayChangeType.isChecked());
                        if (searchOptions == null) {
                            ((EditText) v).selectAll();
                            Toast.makeText(getApplicationContext(), getResources().getText(R.string.TOAST_WRONG), Toast.LENGTH_SHORT).show();

                            return true;
                        }
                        zeroPaySwipeRefresh.setRefreshing(true);
                        zeroPayList.requestFocus();
                        inputMethodManager.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        zeroPayInfo.setSearchOptions(searchOptions);
                        zeroPayInfo.refreshStores();
                        zeroPayListAdapter.notifyDataSetChanged();

                        Toast.makeText(getApplicationContext(), String.format("%d %s", zeroPayInfo.getTotalCnt(), getResources().getText(R.string.TOAST_FOUND)), Toast.LENGTH_SHORT).show();

                        if (zeroPayInfo.getTotalCnt() > 10) {
                            zeroPayBtnPrevious.setEnabled(false);
                            zeroPayBtnNext.setEnabled(true);
                        } else {
                            zeroPayBtnPrevious.setEnabled(false);
                            zeroPayBtnNext.setEnabled(false);
                        }

                        zeroPaySwipeRefresh.setRefreshing(false);
                    } else {
                        ((EditText) v).selectAll();
                        Toast.makeText(getApplicationContext(), getResources().getText(R.string.TOAST_MORE_TWO), Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }
                return false;
            }
        });

        zeroPayBtnPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!zeroPayInfo.decreasePage()) {
                    v.setEnabled(false);
                }
                zeroPayBtnNext.setEnabled(true);
                zeroPayInfo.refreshStores();
                zeroPayListAdapter.notifyDataSetChanged();
                zeroPayList.scrollToPosition(0);
            }
        });

        zeroPayBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!zeroPayInfo.increasePage()) {
                    v.setEnabled(false);
                }
                zeroPayBtnPrevious.setEnabled(true);
                zeroPayInfo.refreshStores();
                zeroPayListAdapter.notifyDataSetChanged();
                zeroPayList.scrollToPosition(0);
            }
        });

        zeroPayClearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                zeroPayInput.setText("");
                zeroPayInput.requestFocus();
                inputMethodManager.showSoftInput(v, InputMethodManager.SHOW_FORCED);
            }
        });

        maskList.setHasFixedSize(true);
        maskList.setAdapter(maskListAdapter);
        maskList.setLayoutManager(maskListLayoutManager);

        zeroPayList.setHasFixedSize(true);
        zeroPayList.setAdapter(zeroPayListAdapter);
        zeroPayList.setLayoutManager(zeroPayListLayoutManager);

        if (bottomTabGroup.getCheckedRadioButtonId() == RadioGroup.NO_ID) {
            bottomTabGroup.check(R.id.tabMyLocation);
        }
    }

    @Override
    public void onBackPressed() {
        if (bottomTabGroup != null) {
            if (bottomTabGroup.getCheckedRadioButtonId() != R.id.tabMyLocation) {
                bottomTabGroup.check(R.id.tabMyLocation);

                return ;
            }
        }
        super.onBackPressed();
    }
}
