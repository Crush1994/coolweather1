package com.example.coolweather.ui.fragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.coolweather.R;
import com.example.coolweather.db.City;
import com.example.coolweather.db.County;
import com.example.coolweather.db.Province;
import com.example.coolweather.ui.MainActivity;
import com.example.coolweather.ui.activity.WeatherActivity;
import com.example.coolweather.ui.adapter.ItemAdapter;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Unility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by 愤怒的艾泽拉斯 on 2018/3/7.
 */

public class ChooseAreaFragment extends Fragment {
    @InjectView(R.id.title_text)
    TextView mTitleText;
    @InjectView(R.id.back_button)
    Button mBackButton;
    @InjectView(R.id.list_view)
    ListView mListView;
    private ArrayList<String> dataList;
    private ArrayAdapter<String> mStringArrayAdapter;
    //**当前选中的级别*/
    private int currentLevel;
    public static final int LEVEL_PROVINCE=0;
    private static final int LEVEL_CITY=1;
    private static final int LEVEL_COUNTY=2;
    /**省列表*/
    private List<Province> provinceList;
    /**市列表*/
    private  List<City>cityList;
    /**县列表*/
    private  List<County>countyList;
    /**选中的省份*/
    private Province selectedProvince;
    /***选中的市级*/
    private City selectedCity;
    /**选中的县级*/
    private County selectedCounty;
    private ProgressDialog mProgressDialog;
    private String TAG="Fragment";


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choosefragment, container, false);
        ButterKnife.inject(this,view);
        dataList=new ArrayList<>();
        mStringArrayAdapter=new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        mListView.setAdapter(mStringArrayAdapter);
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }else if (currentLevel==LEVEL_COUNTY){
                    String lWeatherId = countyList.get(position).getWeatherId();
                    if (getActivity()instanceof MainActivity){
                        Intent lIntent = new Intent(getActivity(), WeatherActivity.class);
                        lIntent.putExtra("weather_id",lWeatherId);
                        startActivity(lIntent);
                        getActivity().finish();
                    }else if (getActivity()instanceof WeatherActivity){
                       WeatherActivity lWeatherActivity= (WeatherActivity) getActivity();
                        lWeatherActivity.mDrawerlayout.closeDrawers();
                        lWeatherActivity.mSwipeRefresh.setRefreshing(true);
                        lWeatherActivity.requestWeather(lWeatherId);
                    }

                }
            }
        });
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentLevel==LEVEL_COUNTY){
                    queryCities();
                }else if (currentLevel==LEVEL_CITY){
                   queryProvince();
                }
            }
        });
        Log.d(TAG,"Fragment+onActivityCreated");
        queryProvince();

    }
    /*
    查询选中的全国所有的省，优先从数据库中查找，如果没有查询到再去服务器上查询
     */
    private void queryProvince() {
        Log.d(TAG,"查询省级数据");
        mTitleText.setText("中国");
        mBackButton.setVisibility(View.GONE);
        //优先从数据库查找，如果没有就去服务器上查询
        provinceList=DataSupport.findAll(Province.class);
        Log.d(TAG,provinceList.size()+"");
        if (provinceList.size()>0){
            dataList.clear();
            for (Province province:provinceList) {
                dataList.add(province.getProvinceName());
            }
            mStringArrayAdapter.notifyDataSetChanged();
            //默认选中所有省份中的第一个，并且将当前的级别改为省
            mListView.setSelection(0);
            currentLevel=LEVEL_PROVINCE;
        }else {
            String address="http://guolin.tech/api/china";
            queryFromServer(address,"province");
        }

    }
    /*
    根据传入的地址和类型从服务器上查询省市县数据
     */
    private void queryFromServer(String address, final String type) {
        Log.d(TAG,"Fragment+下载数据");
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Log.d(TAG,"数据加载失败");
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Log.d(TAG,"数据加载成功");
                boolean result=false;
                if (type.equals("province")){
                    result=Unility.handleProvinceResponse(responseText);
                }else if (type.equals("city")){
                    result=Unility.handleCityResponse(responseText,selectedProvince.getId());
                }else if (type.equals("county")){
                    result=Unility.handleCountyResponse(responseText,selectedCity.getId());
                }
                if (result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvince();
                            }else if ("city".equals(type)){
                                queryCities();
                            }else if ("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }
    /*
    关闭进度条
     */
    private void closeProgressDialog() {
        if (mProgressDialog!=null){
            mProgressDialog.dismiss();
        }
    }

    /*
    加载进度条
     */
    private void showProgressDialog() {
        if (mProgressDialog==null){
            mProgressDialog = new ProgressDialog(getActivity());
            mProgressDialog.setMessage("正在加载");
            //dialog弹出后点击屏幕不消失，点击back消失
            mProgressDialog.setCanceledOnTouchOutside(false);
        }
        mProgressDialog.show();
    }

    /*
 查询选中的省内所有的市，优先从数据库中查找，如果没有查询到再去服务器上查询
  */
    private void queryCities() {
        mTitleText.setText(selectedProvince.getProvinceName());
        mBackButton.setVisibility(View.VISIBLE);
        cityList=DataSupport.where("provinceid=?", String.valueOf(selectedProvince.getId())).find(City.class);
        if (cityList.size()>0){
            dataList.clear();
            for (City city:cityList) {
                dataList.add(city.getCityName());
            }
            mStringArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel=LEVEL_CITY;
        }else {
            int lProvinceCode = selectedProvince.getProvinceCode();
            String address="http://guolin.tech/api/china/"+lProvinceCode;
            queryFromServer(address,"city");
        }
    }
    /*
   查询选中的市内所有的县，优先从数据库中查找，如果没有查询到再去服务器上查询
    */
    private void queryCounties() {
    mTitleText.setText(selectedCity.getCityName());
        mBackButton.setVisibility(View.VISIBLE);
        countyList=DataSupport.where("cityid=?", String.valueOf(selectedCity.getId())).find(County.class);
        if (countyList.size()>0){
            dataList.clear();
            for (County county:countyList) {
                dataList.add(county.getCountyName());
            }
            mStringArrayAdapter.notifyDataSetChanged();
            mListView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else {
            int lProvinceCode = selectedProvince.getProvinceCode();
            int lCityCode = selectedCity.getCityCode();
            String address="http://guolin.tech/api/china/"+lProvinceCode+"/"+lCityCode;
            queryFromServer(address,"county");
        }
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
