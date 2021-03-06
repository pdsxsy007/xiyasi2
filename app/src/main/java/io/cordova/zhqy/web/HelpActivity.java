package io.cordova.zhqy.web;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;
import com.just.agentweb.AbsAgentWebSettings;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.DefaultWebClient;
import com.just.agentweb.IAgentWebSettings;
import com.just.agentweb.PermissionInterceptor;
import com.just.agentweb.WebListenerManager;
import com.just.agentweb.download.AgentWebDownloader;
import com.just.agentweb.download.DefaultDownloadImpl;
import com.just.agentweb.download.DownloadListenerAdapter;
import com.just.agentweb.download.DownloadingService;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhy.adapter.recyclerview.MultiItemTypeAdapter;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.activity.FaceYiQingActivity;
import io.cordova.zhqy.activity.LoginActivity2;
import io.cordova.zhqy.adapter.BrowserAdapter;
import io.cordova.zhqy.bean.AppOrthBean;
import io.cordova.zhqy.bean.BaseBean;
import io.cordova.zhqy.bean.DownLoadBean;
import io.cordova.zhqy.bean.ImeiBean;
import io.cordova.zhqy.bean.LocalFaceBean;
import io.cordova.zhqy.bean.LocalVersonBean;
import io.cordova.zhqy.bean.LocationBean;
import io.cordova.zhqy.bean.LocationBean2;
import io.cordova.zhqy.bean.LogInTypeBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.CookieUtils;
import io.cordova.zhqy.utils.DensityUtil;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MobileInfoUtils;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ScreenSizeUtils;
import io.cordova.zhqy.utils.SoundPoolUtils;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.widget.MyDialog;
import io.cordova.zhqy.widget.TestWebView;
import io.cordova.zhqy.zixing.QRCodeManager;
import io.cordova.zhqy.zixing.activity.CaptureActivity;
import me.samlss.lighter.Lighter;
import me.samlss.lighter.interfaces.OnLighterListener;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import okhttp3.Headers;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static io.cordova.zhqy.UrlRes.HOME_URL;
import static io.cordova.zhqy.UrlRes.addPortalReadingAccessUrl;
import static io.cordova.zhqy.UrlRes.functionInvocationLogUrl;
import static io.cordova.zhqy.activity.SplashActivity.getLocalVersionName;
import static io.cordova.zhqy.utils.AesEncryptUtile.key;
import static io.cordova.zhqy.utils.MobileInfoUtils.getIMEI;
import static io.cordova.zhqy.utils.MyApp.getInstance;
import static io.cordova.zhqy.utils.PermissionsUtil.SETTINGS_REQ_CODE;

@SuppressLint("Registered")
public class HelpActivity extends AppCompatActivity implements GestureDetector.OnGestureListener {
    protected AgentWeb mAgentWeb;

    @BindView(R.id.webView)
    TestWebView webView;

    @BindView(R.id.layout_close)
    RelativeLayout rvClose;

    @BindView(R.id.tv_title)
    TextView mTitleTextView;
    @BindView(R.id.rl_no)
    RelativeLayout rl_no;

    @BindView(R.id.rb_sc)
    ImageView rbSc;

    @BindView(R.id.ll_shoushi)
    RelativeLayout ll_shoushi;

    private LinearLayout mLinearLayout;
    String appServiceUrl, tgc,appId,search,oaMsg;
    private String time;
    boolean isFirst = true;
    String appName;
    String scan;
    private int flag = 0;

    /** Android 5.0????????????????????????????????? */
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    /** Android 5.0???????????????????????????????????? */
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;

    protected static final int REQUEST_CODE_FILE_PICKER = 51426;


    protected String mUploadableFileTypes = "*/*";
    GestureDetector gestureDetector;
    protected static final float FLIP_DISTANCE = 400;

    private PermissionsUtil permissionsUtil;
    String downLoadType;
    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        downLoadType = (String) SPUtils.get(HelpActivity.this, "downLoadType", "");
        ButterKnife.bind(this);
        mLocationClient = new LocationClient(this);
        mLocationClient.registerLocationListener(myListener);
        gestureDetector = new GestureDetector(this,this);
        mLinearLayout = (LinearLayout) findViewById(R.id.container);
        rvClose.setVisibility(View.GONE);

        tgc = (String) SPUtils.get(getApplicationContext(), "TGC", "");
        appServiceUrl = getIntent().getStringExtra("appUrl");


        appId = getIntent().getStringExtra("appId");
        search = getIntent().getStringExtra("search");//???????????????'
        oaMsg = getIntent().getStringExtra("oaMsg");//oa ??????

        appName = getIntent().getStringExtra("appName");
        scan = getIntent().getStringExtra("scan");

        if(appName != null){
            mTitleTextView.setText(appName);
        }

        if (!StringUtils.isEmpty(oaMsg)){
            if (!appServiceUrl.contains("fromnewcas=Y")){
                appServiceUrl = appServiceUrl + "&fromnewcas=Y";
            }
        }

        if(null != scan){
            if(scan.equals("scan")){
                rbSc.setVisibility(View.GONE);
            }
        }
        if(null != appId){
            rbSc.setVisibility(View.VISIBLE);
        }else {
            rbSc.setVisibility(View.GONE);
        }

        if(appServiceUrl.contains("gilight://")){
            //gilight://url=weixin://123
            if(!appServiceUrl.contains("http")){
                final String endUrl = appServiceUrl.substring(10,appServiceUrl.length());
                String[] split = endUrl.split("//");
                String s = split[0];
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("????????????"+s);
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String appServiceUrl2 = UrlRes.huanxingUrl+"?"+endUrl;
                        Log.e("appServiceUrl2",appServiceUrl2);
                        Uri uri = Uri.parse(appServiceUrl2);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            // ???????????? ????????????
                            startActivity(intent);
                            //finish();
                        } else {
                            //??????????????? ???????????? ????????????
                            //Toast.makeText(MainActivity.this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                builder.create().show();
            }else {

                String endUrl = appServiceUrl.substring(10,appServiceUrl.length());
                String appServiceUrl2 = UrlRes.huanxingUrl+"?"+endUrl;
                Log.e("appServiceUrl2",appServiceUrl2);
                Uri uri = Uri.parse(appServiceUrl2);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // ???????????? ????????????
                    startActivity(intent);
                    finish();
                } else {
                    //??????????????? ???????????? ????????????
                    //Toast.makeText(MainActivity.this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show();

                }

            }



        }

        String tgc = (String) SPUtils.get(HelpActivity.this, "TGC", "");
        CookieUtils.syncCookie(UrlRes.HOME2_URL,"CASTGC="+tgc,getApplication());

        if(appServiceUrl.contains("gilight://")){
            if(!appServiceUrl.contains("http")){
                final String endUrl = appServiceUrl.substring(10,appServiceUrl.length());
                String[] split = endUrl.split("//");
                String s = split[0];
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("????????????"+s);
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String appServiceUrl2 = UrlRes.huanxingUrl+"?"+endUrl;
                        Log.e("appServiceUrl2",appServiceUrl2);
                        Uri uri = Uri.parse(appServiceUrl2);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            // ???????????? ????????????
                            startActivity(intent);
                            finish();
                        } else {
                            //??????????????? ???????????? ????????????
                            //Toast.makeText(MainActivity.this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                builder.create().show();
            }else {

                String endUrl = appServiceUrl.substring(10,appServiceUrl.length());
                String appServiceUrl2 = UrlRes.huanxingUrl+"?"+endUrl;
                Log.e("appServiceUrl2",appServiceUrl2);
                Uri uri = Uri.parse(appServiceUrl2);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // ???????????? ????????????
                    startActivity(intent);
                    finish();
                } else {
                    //??????????????? ???????????? ????????????
                    //Toast.makeText(MainActivity.this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show();

                }

            }



        }

        Log.e("appServiceUrl",appServiceUrl);
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mLinearLayout, new LinearLayout .LayoutParams(-1, -1))
                .useDefaultIndicator(-1, 3)//?????????????????????????????????-1????????????????????????2????????????dp???
                //.setAgentWebWebSettings(getSettings())//?????? IAgentWebSettings???
                .setWebChromeClient(new OpenFileChromeClient())
                .setWebViewClient(mWebViewClient)
                .setPermissionInterceptor(mPermissionInterceptor) //???????????? 2.0.0 ?????????
                //.setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                .setWebLayout(new WebLayout4(this))
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//??????????????????????????????????????????????????????????????????
                .interceptUnkownUrl() //??????????????????????????????Scheme
                .setAgentWebWebSettings(getSettings())//?????? IAgentWebSettings???
                .createAgentWeb()
                .ready()
                .go(appServiceUrl);

        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);
        mAgentWeb.getJsInterfaceHolder().addJavaObject("android",new AndroidInterface());


        netWorkIsCollection();
        initListener();

        String home05 = (String) SPUtils.get(MyApp.getInstance(), "home05", "");
        if(home05.equals("")){
            setGuideView();
        }

        sBegin = Calendar.getInstance().getTimeInMillis() + "";

        s1 = stringToDate(sBegin);



        registerBoradcastReceiver();
        registerBoradcastReceiver2();

    }

    private void registerBoradcastReceiver2() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("facereYiQing2");
        //????????????
        registerReceiver(broadcastReceiver2, myIntentFilter);
    }


    private BroadcastReceiver broadcastReceiver2 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("facereYiQing2")){
                LocalFaceBean localVersonBean = new LocalFaceBean();
                localVersonBean.setSuccess(false);
                localVersonBean.setMessage("???????????????????????????");
                Gson gson = new Gson();
                String s = gson.toJson(localVersonBean);
                String jsonParams = s;
                String url2 = "javascript:getFaceAndroidParams('"+jsonParams+"')";
                mAgentWeb.getWebCreator().getWebView().evaluateJavascript(url2, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //????????? js ???????????????
                        Log.e("value",value);
                    }
                });
                SPUtils.put(getApplicationContext(),"isloading2","");
            }
        }
    };
    private BroadcastReceiver broadcastReceiver3 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("facereYiQing3")){
                LocalFaceBean localVersonBean = new LocalFaceBean();
                localVersonBean.setSuccess(false);
                localVersonBean.setMessage("?????????????????????????????????");
                Gson gson = new Gson();
                String s = gson.toJson(localVersonBean);
                String jsonParams = s;
                String url2 = "javascript:getFaceAndroidParams('"+jsonParams+"')";
                mAgentWeb.getWebCreator().getWebView().evaluateJavascript(url2, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        //????????? js ???????????????
                        Log.e("value",value);
                    }
                });
                SPUtils.put(getApplicationContext(),"isloading2","");
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("facereYiQing");
        registerReceiver(broadcastReceiver, myIntentFilter);

    }


    public LocationClient mLocationClient;
    private MyLocationListener myListener = new MyLocationListener();



    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){
            double latitude = location.getLatitude();    //??????????????????
            double longitude = location.getLongitude();    //??????????????????
            float radius = location.getRadius();    //?????????????????????????????????0.0f

            String coorType = location.getCoorType();
            //?????????????????????????????????LocationClientOption?????????????????????????????????

            int errorCode = location.getLocType();
            boolean b = location.hasAltitude();
            double altitude = location.getAltitude();
            float speed = location.getSpeed();
            float direction = location.getDirection();
            int locationWhere = location.getLocationWhere();

            Log.e("errorCode",errorCode+"");
            Log.e("latitude",latitude+"");
            Log.e("longitude",longitude+"");
            Log.e("altitude??????base",altitude+"");
            Log.e("speed",speed+"");
            Log.e("direction",direction+"");
            Log.e("locationWhere",locationWhere+"");

            com.baidu.location.Address address = location.getAddress();
            String addressLine = address.address;
            String country = address.country;
            String countryCode = address.countryCode;
            String province = address.province;
            String city = address.city;
            String cityCode = address.cityCode;
            String district = address.district;
            String town = address.town;
            String adcode = address.adcode;
            String street = address.street;
            String streetNumber = address.streetNumber;
            if(addressLine != null){
                SPUtils.put(HelpActivity.this,"addressLine",addressLine);
            }
            if(country != null){
                SPUtils.put(HelpActivity.this,"country",country);
            }

            if(countryCode != null){
                SPUtils.put(HelpActivity.this,"countryCode",countryCode);
            }

            if(province != null){
                SPUtils.put(HelpActivity.this,"province",province);
            }

            if(city != null){
                SPUtils.put(HelpActivity.this,"city",city);
            }

            if(cityCode != null){
                SPUtils.put(HelpActivity.this,"cityCode",cityCode);
            }

            if(district != null){
                SPUtils.put(HelpActivity.this,"district",district);
            }

            if(adcode != null){
                SPUtils.put(HelpActivity.this,"adcode",adcode);
            }

            if(street != null){
                SPUtils.put(HelpActivity.this,"street",street);
            }

            if(streetNumber != null){
                SPUtils.put(HelpActivity.this,"streetNumber",streetNumber);
            }
            if(town != null){
                SPUtils.put(HelpActivity.this,"town",town);
            }

            SPUtils.put(HelpActivity.this,"latitude",latitude+"");
            SPUtils.put(HelpActivity.this,"longitude",longitude+"");
            SPUtils.put(HelpActivity.this,"altitude",altitude+"");
            SPUtils.put(HelpActivity.this,"speed",speed+"");
            SPUtils.put(HelpActivity.this,"direction",direction+"");
            SPUtils.put(HelpActivity.this,"locationWhere",locationWhere+"");
        }
    }

    private void getMyLocation() {

/*
        //1.?????????????????????
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //2.????????????????????????GPS??????NetWork
        locationProvider = LocationManager.GPS_PROVIDER;

        if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(locationProvider);
            if (location!=null){
                //showLocation(location);
                double latitude = location.getLatitude();//??????
                double longitude = location.getLongitude();//??????
                Log.e("???????????????",latitude+ "");
                Log.e("???????????????",longitude+ "");
                SPUtils.put(BaseWebActivity4.this,"latitude",latitude+"");
                SPUtils.put(BaseWebActivity4.this,"longitude",longitude+"");
                Geocoder gc = new Geocoder(this, Locale.getDefault());
                List<Address> locationList = null;
                try {
                    locationList = gc.getFromLocation(latitude, longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(locationList != null && locationList.size() > 0){
                    Address address = locationList.get(0);//??????Address??????
                    String countryName = address.getCountryName();//????????????????????????????????????
                    String locality = address.getLocality();//???????????????????????????????????????
                    for (int i = 0; address.getAddressLine(i) != null; i++) {
                        String addressLine = address.getAddressLine(i);//???????????????????????????????????????i=0?????????????????????
                        SPUtils.put(BaseWebActivity4.this,"addressLine",addressLine);
                    }
                }else {
                    SPUtils.put(BaseWebActivity4.this,"latitude","");
                    SPUtils.put(BaseWebActivity4.this,"longitude","");
                    SPUtils.put(BaseWebActivity4.this,"addressLine","");
                }

            }else{
                SPUtils.put(BaseWebActivity4.this,"latitude","");
                SPUtils.put(BaseWebActivity4.this,"longitude","");
                SPUtils.put(BaseWebActivity4.this,"addressLine","");
                //locationManager.requestLocationUpdates(locationProvider, 300000, 0,mListener);
            }
            //locationManager.requestLocationUpdates(locationProvider, 300000, 0,mListener);

        }else {
            SPUtils.put(BaseWebActivity4.this,"latitude","");
            SPUtils.put(BaseWebActivity4.this,"longitude","");
            SPUtils.put(BaseWebActivity4.this,"addressLine","");
        }*/

        LocationClientOption option = new LocationClientOption();

        //??????????????????
        option.setCoorType("bd09ll");
        option.setIgnoreKillProcess(true);
        option.SetIgnoreCacheException(false);
        //???????????????????????????????????????????????????
        option.setIsNeedAddress(true);
        //??????????????????gps????????????
        //option.setOpenGps(true);
        //option.setWifiCacheTimeOut(20);
        option.setIsNeedAltitude(true);
        option.setIsNeedLocationDescribe(true);
        option.setNeedDeviceDirect(true);
        //?????????????????????1???
        option.setScanSpan(300000);
        //option.setScanSpan(1000);

        //????????????????????????
        mLocationClient.setLocOption(option);

        mLocationClient.start();


    }

    private void setGuideView() {

        // ????????????
        Lighter.with(this)
                .setBackgroundColor(0xB9000000)
                .setOnLighterListener(new OnLighterListener() {
                    @Override
                    public void onShow(int index) {


                    }

                    @Override
                    public void onDismiss() {
                        SPUtils.put(MyApp.getInstance(),"home05","1");
                    }
                })
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedViewId(R.id.ll_shoushi)
                        .setTipLayoutId(R.layout.fragment_home_gl2)
                        //.setLighterShape(new RectShape(80, 80, 50))
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(0, 10, 0, 20))
                        .build()).show();
    }

    BaseBean appTime;
    private void networkAppStatTime() {
        OkGo.<String>post(HOME_URL+ UrlRes.APP_Time)
                .params( "responseTime",time )
                .params( "responseAppId",appId)
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        //handleResponse(response);
                        Log.e("Tag",response.body());
                        appTime = JSON.parseObject(response.body(),BaseBean.class);
                        if (appTime.isSuccess()) {
                            isFirst = false;
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        isFirst = true;
                    }
                });


    }

    BaseBean baseBean;
    private void netWorkIsCollection() {
        OkGo.<String>post(HOME_URL+ UrlRes.Query_IsCollection)
                .params( "version","1.0" )
                .params( "collectionAppId",appId )
                .params( "userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        //handleResponse(response);
                        Log.e("tag",response.body());
                        baseBean = JSON.parseObject(response.body(), BaseBean.class);

                        if (baseBean.isSuccess()){
                            rbSc.setBackgroundResource(R.mipmap.sc_hover_icon);
                            flag = 1;
                        }else {
                            rbSc.setBackgroundResource(R.mipmap.sc_icon);
                            flag = 0;
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });

    }

    private MyDialog m_Dialog_browser;
    private LinearLayoutManager mLinearLayoutManager;
    private void initListener() {
        rbSc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (flag == 0){
                    //????????????
                    networkCollection();
                }else {
                    //????????????
                    cancelCollection();
                }
            }
        });

        rbSc.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                m_Dialog_browser = new MyDialog(HelpActivity.this, R.style.DialogTheme);

                View view1 = LayoutInflater.from(HelpActivity.this).inflate(R.layout.dialog_browser, null);
                m_Dialog_browser.setContentView(view1);
                Window window = m_Dialog_browser.getWindow();
                window.setGravity(Gravity.BOTTOM);
                window.setWindowAnimations(R.style.main_menu_animStyle);
                window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                m_Dialog_browser.show();
                m_Dialog_browser.setCanceledOnTouchOutside(true);
                LinearLayout ll_01 = view1.findViewById(R.id.ll_01);
                LinearLayout ll_02 = view1.findViewById(R.id.ll_02);
                LinearLayout ll_03 = view1.findViewById(R.id.ll_03);
                RecyclerView rv_content = view1.findViewById(R.id.rv_content);
                mLinearLayoutManager = new LinearLayoutManager(HelpActivity.this, LinearLayout.HORIZONTAL,false);
                rv_content.setLayoutManager(mLinearLayoutManager);
                List<LogInTypeBean> list = new ArrayList<>();
                list.clear();
                list.add(new LogInTypeBean("??????????????????",R.mipmap.b_browser));
                list.add(new LogInTypeBean("????????????",R.mipmap.b_copy));
                list.add(new LogInTypeBean("??????",R.mipmap.b_refresh));
                BrowserAdapter browserAdapter = new BrowserAdapter(HelpActivity.this,R.layout.item_browser,list);
                rv_content.setAdapter(browserAdapter);
                browserAdapter.setOnItemClickListener(new MultiItemTypeAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, RecyclerView.ViewHolder holder, int position) {
                        switch (position){
                            case 0:
                                m_Dialog_browser.dismiss();
                                Uri uri = Uri.parse(appServiceUrl);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                if (intent.resolveActivity(getPackageManager()) != null) {
                                    // ???????????? ????????????
                                    startActivity(intent);

                                } else {


                                }
                                break;

                            case 1:
                                m_Dialog_browser.dismiss();
                                copyString(appServiceUrl);
                                break;

                            case 2:
                                m_Dialog_browser.dismiss();
                                mAgentWeb.getWebCreator().getWebView().reload();
                                break;

                        }
                    }

                    @Override
                    public boolean onItemLongClick(View view, RecyclerView.ViewHolder holder, int position) {
                        return false;
                    }
                });
                TextView tv_cancel = view1.findViewById(R.id.tv_cancel);

                tv_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        m_Dialog_browser.dismiss();

                    }
                });


                return false;
            }
        });

    }

    private void copyString(String appServiceUrl) {
        ClipboardManager clipManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);//???????????????????????????
        ClipData clipData = ClipData.newPlainText("copy text", appServiceUrl);//???????????????clip??????
        clipManager.setPrimaryClip(clipData);//???clip?????????????????????

        //?????????????????????????????????
        if (!clipManager.hasPrimaryClip()) {
            return;
        }
        ClipData clip = clipManager.getPrimaryClip();
        //?????? ClipDescription
        ClipDescription description = clip.getDescription();
        //?????? label
        String label = description.getLabel().toString();
        //?????? text
        String copyText = clip.getItemAt(0).getText().toString();
        ToastUtils.showToast(HelpActivity.this,"????????????!");
    }

    /**????????????*/
    private void networkCollection() {
        OkGo.<String>post(HOME_URL+ UrlRes.Add_Collection)
                .params( "version","1.0" )
                .params( "collectionAppId",appId )
                .params( "userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("tag",response.body());
                        baseBean = JSON.parseObject(response.body(), BaseBean.class);
                        if (baseBean.isSuccess()){
                            rbSc.setBackgroundResource(R.mipmap.sc_hover_icon);
                            flag = 1;
                            ToastUtils.showToast(MyApp.getInstance(),baseBean.getMsg());
                            Intent intent = new Intent();
                            intent.putExtra("refreshService","dongtai");
                            intent.setAction("refresh2");
                            sendBroadcast(intent);
                        }else {
                            rbSc.setBackgroundResource(R.mipmap.sc_icon);
                            flag = 0;
                            ToastUtils.showToast(MyApp.getInstance(),baseBean.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        rbSc.setBackgroundResource(R.mipmap.sc_icon);
                    }
                });
    }

    /**????????????*/
    private void cancelCollection() {
        OkGo.<String>post(HOME_URL+ UrlRes.Cancel_Collection)
                .params( "version","1.0" )
                .params( "collectionAppId",appId )
                .params( "userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("tag",response.body());
                        baseBean = JSON.parseObject(response.body(), BaseBean.class);
                        if (baseBean.isSuccess()){
                            rbSc.setBackgroundResource(R.mipmap.sc_icon);
                            flag = 0;
                            ToastUtils.showToast(MyApp.getInstance(),baseBean.getMsg());
                            Intent intent = new Intent();
                            intent.putExtra("refreshService","dongtai");
                            intent.setAction("refresh2");
                            sendBroadcast(intent);
                        }else {
                            rbSc.setBackgroundResource(R.mipmap.sc_hover_icon);
                            flag = 1;
                            ToastUtils.showToast(MyApp.getInstance(),baseBean.getMsg());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        rbSc.setBackgroundResource(R.mipmap.sc_hover_icon);
                    }
                });

    }
    /**
     * IEventHandler ??????WebView??????????????????
     */
    @OnClick({R.id.iv_back, R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                if (!mAgentWeb.back()){
                    HelpActivity.this.finish();
                }
                break;
            case R.id.iv_close:
                HelpActivity.this.finish();
                break;
        }
    }
    long start;
    long end;
    String urldown = "";
    String sBegin;//???????????????
    String s1;//????????????
    @SuppressLint("WrongConstant")
    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageCommitVisible(WebView view, String url) {

            super.onPageCommitVisible(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                handler.cancel(); // ???????????????????????????
            }


        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.e("????????????url",url);
            boolean b = mAgentWeb.getWebCreator().getWebView().canGoBack();
            if(b){
                rvClose.setVisibility(View.VISIBLE);

            }else {
                rvClose.setVisibility(View.GONE);
            }

            WebSettings webSettings = view.getSettings();
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//???html??????????????????webview??????????????????
            webSettings.setJavaScriptEnabled(true);//??????js
            webSettings.setBuiltInZoomControls(true); // ??????????????????
            webSettings.setSupportZoom(true); // ????????????
            webSettings.setUseWideViewPort(true);  //?????????????????????????????????

            end =   Calendar.getInstance().getTimeInMillis();
            time =(end - start) +"";
            if (!StringUtils.isEmpty(time) && !StringUtils.isEmpty(appId) && isFirst){
                networkAppStatTime();
            }

            sBegin = Calendar.getInstance().getTimeInMillis() + "";

            s1 = stringToDate(sBegin);


        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url =  null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
            }



            if (url.contains("http://kys.zzuli.edu.cn/cas/login")) {
                if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username","")) || tgc.equals("")){
                    Intent intent = new Intent(getApplicationContext(),LoginActivity2.class);
                    startActivity(intent);
                    finish();

                    return true;
                }
            }


            urldown = "";
            urldown =url;



            return super.shouldOverrideUrlLoading(view, request);
        }

        /**????????????*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {


            Log.e("????????????1",url);

            String downLoadType = (String) SPUtils.get(HelpActivity.this, "downLoadType", "");
            DownLoadBean downLoadBean = JsonUtil.parseJson(downLoadType,DownLoadBean.class);
            List<String> downLoadTypeList = downLoadBean.getString();
            for (int i = 0; i < downLoadTypeList.size(); i++) {
                if(url.contains(downLoadTypeList.get(i))){
                    Log.e("tag",downLoadTypeList.get(i));
                    logOut(url);
                    //getSettings();
                    return true;
                }else {

                }
            }


            if(url.contains("gilight://")){

                String endUrl = url.substring(10,url.length());
                String fullUrl = UrlRes.huanxingUrl+"?"+endUrl;

                //view.loadUrl(endUrl);
                Uri uri = Uri.parse(fullUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // ???????????? ????????????
                    startActivity(intent);
                    finish();
                } else {
                    //??????????????? ???????????? ????????????
                    //Toast.makeText(MainActivity.this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show();

                }

                return true;

            }else {
                if (url.contains("http://kys.zzuli.edu.cn/cas/login")) {
                    if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username","")) || tgc.equals("")){
                        Intent intent = new Intent(getApplicationContext(),LoginActivity2.class);
                        startActivity(intent);
                        finish();

                        return true;
                    }
                }
                upLoadWebInfo();
              /*  urldown = "";
                urldown =url;
                Log.e("urldown",urldown);
                webView.loadUrl(urldown);
                webView.setWebViewClient(new WebViewClient(){
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                        view.loadUrl(url);
                        Log.e("myurl",url);
                        return true;
                    }

                    @Override
                    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                        //super.onReceivedSslError(view, handler, error);

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                            handler.proceed(); // ???????????????????????????
                        }


                    }


                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                        Log.e("myurl",request.getUrl().toString());
                        return super.shouldOverrideUrlLoading(view, request);

                    }

                    @Override
                    public void onPageFinished(WebView view, String url) {
                        super.onPageFinished(view, url);

                        Log.e("myurl","?????????");
                        Log.e("myurl",url);
                    }

                    @Override
                    public void onPageStarted(WebView view, String url, Bitmap favicon) {
                        super.onPageStarted(view, url, favicon);
                        //Log.e("myurl","?????????");
                        Log.e("myurlonPageStarted",url);
                    }
                });
*/
           /*     webView.setLoadFinishListener(new CLWebView.LoadFinishListener() {
                    @Override
                    public void onLoadFinish(WebView webView) {
                        Log.e("urldownonLoadFinish",urldown);


                        upLoadWebInfo();
                     *//*   String url1 = webView.getUrl();
                        urldown = url1;
                        sBegin = Calendar.getInstance().getTimeInMillis() + "";

                        s1 = stringToDate(sBegin);
                        Log.e("myurl???????????????",url1);
                        String downLoadType = (String) SPUtils.get(BaseWebActivity4.this, "downLoadType", "");
                        DownLoadBean downLoadBean = JsonUtil.parseJson(downLoadType,DownLoadBean.class);
                        List<String> downLoadTypeList = downLoadBean.getString();
                        for (int i = 0; i < downLoadTypeList.size(); i++) {
                            if(urldown.contains(downLoadTypeList.get(i))){
                                Log.e("tag",downLoadTypeList.get(i));
                                logOut(urldown);
                                break;
                            }else {

                            }
                        }*//*
                    }
                });*/
            }


            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            CookieUtils.syncCookie("http://kys.zzuli.edu.cn","CASTGC="+tgc,getApplication());
            if (!StringUtils.isEmpty(appId)){
                start =  Calendar.getInstance().getTimeInMillis() ;
                Log.i("Info", "start:  " + start );
            }

        }


        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, final String url) {
            Log.e("??????????????????",url);
            return null;

        }
    };

    @SuppressLint("WrongConstant")
    private WebViewClient mWebViewClient2 = new WebViewClient() {
        @Override
        public void onPageCommitVisible(WebView view, String url) {

            super.onPageCommitVisible(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                handler.cancel(); // ???????????????????????????
            }


        }


        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            boolean b = mAgentWeb.getWebCreator().getWebView().canGoBack();
            if(b){
                rvClose.setVisibility(View.VISIBLE);

            }else {
                rvClose.setVisibility(View.GONE);
            }



            WebSettings webSettings = view.getSettings();
            webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);//???html??????????????????webview??????????????????
            webSettings.setJavaScriptEnabled(true);//??????js
            webSettings.setBuiltInZoomControls(true); // ??????????????????
            webSettings.setSupportZoom(true); // ????????????
            webSettings.setUseWideViewPort(true);  //?????????????????????????????????

            end =   Calendar.getInstance().getTimeInMillis();
            time =(end - start) +"";
            if (!StringUtils.isEmpty(time) && !StringUtils.isEmpty(appId) && isFirst){
                networkAppStatTime();
            }

            sBegin = Calendar.getInstance().getTimeInMillis() + "";

            s1 = stringToDate(sBegin);

        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url =  null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
            }



            if (url.contains("http://kys.zzuli.edu.cn/cas/login")) {
                if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username","")) || tgc.equals("")){
                    Intent intent = new Intent(getApplicationContext(),LoginActivity2.class);
                    startActivity(intent);
                    finish();

                    return true;
                }
            }


            urldown = "";
            urldown =url;



            return super.shouldOverrideUrlLoading(view, request);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {



            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            CookieUtils.syncCookie("http://kys.zzuli.edu.cn","CASTGC="+tgc,getApplication());
            if (!StringUtils.isEmpty(appId)){
                start =  Calendar.getInstance().getTimeInMillis() ;
                Log.i("Info", "start:  " + start );
            }

        }


    };



    public static String stringToDate(String lo){
        long time = Long.parseLong(lo);
        Date date = new Date(time);
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sd.format(date);
    }

    /**
     * ????????????
     */
    private void upLoadWebInfo() {
        String s = Calendar.getInstance().getTimeInMillis() + "";


        long sCha = Long.parseLong(s) - Long.parseLong(sBegin);
        String userId = (String) SPUtils.get(MyApp.getInstance(), "userId", "");
        try {
            String decrypt = AesEncryptUtile.decrypt(userId, key);
            Map<String,String> map=new HashMap<String,String>();
            map.put("portalReadingAccessEquipmentId",(String) SPUtils.get(MyApp.getInstance(),"imei",""));
            map.put("portalReadingAccessMemberId",decrypt);
            map.put("portalReadingAccessLongitude",(String) SPUtils.get(MyApp.getInstance(),"longitude",""));
            map.put("portalReadingAccessLatitude",(String) SPUtils.get(MyApp.getInstance(),"latitude",""));
            map.put("portalReadingAccessAddress",(String) SPUtils.get(MyApp.getInstance(),"addressLine",""));
            map.put("portalReadingAccessTime",s1);
            map.put("portalReadingAccessUrl",urldown);
            map.put("portalReadingAccessReadTime",sCha/1000+"");
            JSONObject json =new JSONObject(map);
            String s3 = json.toString();
            Log.e("content",json.toString());
            String content = AesEncryptUtile.encrypt(s3, key);

            OkGo.<String>post(HOME_URL+addPortalReadingAccessUrl)
                    .params("json", content)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("s",response.toString());

                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.e("s",response.toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



    private MyDialog m_Dialog;
    private int tag = 0;
    private void logOut(final String urldown) {
        m_Dialog = new MyDialog(this, R.style.dialogdialog);
        Window window = m_Dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_down, null);
        RelativeLayout rl_sure = view.findViewById(R.id.rl_sure);
        RelativeLayout rl_sure1 = view.findViewById(R.id.rl_sure1);

        int width = ScreenSizeUtils.getWidth(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width - DensityUtil.dip2px(this,24),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        m_Dialog.setContentView(view, layoutParams);
        m_Dialog.show();
        m_Dialog.setCanceledOnTouchOutside(true);
        rl_sure1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_Dialog.dismiss();

            }
        });
        rl_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_Dialog.dismiss();

                tag = 1;
                mAgentWeb = AgentWeb.with(HelpActivity.this)
                        .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                        .useDefaultIndicator(-1, 3)//?????????????????????????????????-1????????????????????????2????????????dp???
                        .setWebChromeClient(mWebChromeClient)
                        .setWebViewClient(mWebViewClient2)
                        .setPermissionInterceptor(mPermissionInterceptor) //???????????? 2.0.0 ?????????
                        //.setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                        .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                        .setWebLayout(new WebLayout4(HelpActivity.this))
                        .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//??????????????????????????????????????????????????????????????????
                        .interceptUnkownUrl() //??????????????????????????????Scheme
                        .setAgentWebWebSettings( getSettings2())//?????? IAgentWebSettings???
                        .createAgentWeb()
                        .ready()
                        .go(urldown);

                mAgentWeb = AgentWeb.with(HelpActivity.this)
                        .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                        .useDefaultIndicator(-1, 3)//?????????????????????????????????-1????????????????????????2????????????dp???
                        .setWebChromeClient(mWebChromeClient)
                        .setWebViewClient(mWebViewClient)
                        .setPermissionInterceptor(mPermissionInterceptor) //???????????? 2.0.0 ?????????
                        //.setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                        .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                        .setWebLayout(new WebLayout4(HelpActivity.this))
                        .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//??????????????????????????????????????????????????????????????????
                        .interceptUnkownUrl() //??????????????????????????????Scheme
                        .setAgentWebWebSettings( getSettings())//?????? IAgentWebSettings???
                        .createAgentWeb()
                        .ready()
                        .go(appServiceUrl);
            }
        });
    }


    private WebChromeClient mWebChromeClient = new WebChromeClient() {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //   do you work

            if (newProgress == 100 && start > 0){
                end =   Calendar.getInstance().getTimeInMillis();
                time =(end - start) +"";
            }

            if (!StringUtils.isEmpty(time) && !StringUtils.isEmpty(appId) && isFirst){
                networkAppStatTime();
            }

        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mTitleTextView != null) {
                mTitleTextView.setText(title);
            }
            if(appName != null){
                mTitleTextView.setText(appName);
            }
        }


    };
    protected PermissionInterceptor mPermissionInterceptor = new PermissionInterceptor() {

        /**
         * PermissionInterceptor ????????? url1 ??????????????? url2 ????????????????????????
         * @param url
         * @param permissions
         * @param action
         * @return true ???Url???????????????????????????????????? ???false ??????????????????
         */
        @Override
        public boolean intercept(String url, String[] permissions, String action) {

            return false;
        }
    };


    DownloadListener downloadListener2 = new DownloadListener() {
        @Override
        public void onDownloadStart(final String url, String s1, String s2, String s3, long l) {
            Log.e("onDownloadStart",url);

            if(url.contains(".png")|| url.contains(".jpg") || url.contains(".jpeg")){

                OkGo.<String>get(url)
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {


                                Headers headers = response.getRawResponse().headers();
                                String Disposition = headers.get("Content-Disposition");
                                if(Disposition != null){

                                    if(Disposition.contains("attachment")){
                                        logOut(url);
                                        return;
                                    }
                                }
                            }
                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);

                            }
                        });


            }else {
                logOut(url);
            }





        }


    };

    protected DownloadListenerAdapter mDownloadListenerAdapter = new DownloadListenerAdapter() {

        @Override
        public void onProgress(String url, long downloaded, long length, long usedTime) {
            super.onProgress(url, downloaded, length, usedTime);
            Log.e("onProgress??????",url);
        }

        @Override
        public void onBindService(String url, DownloadingService downloadingService) {
            super.onBindService(url, downloadingService);
            Log.e("onBindService??????",url);
            // downloadingService.shutdownNow();
        }

        @Override
        public void onUnbindService(String url, DownloadingService downloadingService) {
            super.onUnbindService(url, downloadingService);

        }

        @Override
        public boolean onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, AgentWebDownloader.Extra extra) {
            Log.e("onStart??????",url);

            ViewUtils.createLoadingDialog(HelpActivity.this);
            extra.setOpenBreakPointDownload(true) // ????????????????????????
                    .setIcon(R.drawable.ic_file_download_black_24dp) //???????????????icon
                    .setConnectTimeOut(6000) // ??????????????????
                    .setBlockMaxTime(10 * 60 * 1000)  // ???8KB??????????????????60s ?????????60s??????????????????????????????8KB????????????????????????
                    .setDownloadTimeOut(Long.MAX_VALUE) // ??????????????????
                    .setParallelDownload(false)  // ??????????????????????????????
                    .setEnableIndicator(false)  // false ??????????????????
                    //.addHeader("Cookie", "xx") // ??????????????????
                    .setAutoOpen(true) // ????????????????????????
                    .setForceDownload(true); // ???????????????????????????????????????
            return false;
        }




        @Override
        public boolean onResult(final String path, String url, final Throwable throwable) {

            ViewUtils.cancelLoadingDialog();
            if (null == throwable) { //????????????

                Uri shareFileUrl = FileUtil.getFileUri(getApplicationContext(), null, new File(path));
                new Share2.Builder(HelpActivity.this)
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(shareFileUrl)
                        .setTitle("Share File")
                        .setOnActivityResult(REQUEST_SHARE_FILE_CODE)
                        .build()
                        .shareBySystem();

            } else {//????????????
                Uri shareFileUrl = FileUtil.getFileUri(getApplicationContext(), null, new File(path));
                Log.e("path2", String.valueOf(shareFileUrl));
                new Share2.Builder(HelpActivity.this)
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(shareFileUrl)
                        .setTitle("Share File")
                        .setOnActivityResult(REQUEST_SHARE_FILE_CODE)
                        .build()
                        .shareBySystem();

            }
            return false; // true  ????????????????????????????????? , ??????????????????
        }
    };


    public IAgentWebSettings getSettings() {
        return new AbsAgentWebSettings() {
            private AgentWeb mAgentWeb;


            @Override
            protected void bindAgentWebSupport(AgentWeb agentWeb) {
                this.mAgentWeb = agentWeb;
            }

            @Override
            public WebListenerManager setDownloader(WebView webView, DownloadListener downloadListener) {
                return super.setDownloader(webView, downloadListener2);
            }


        };
    }

    public IAgentWebSettings getSettings2() {
        return new AbsAgentWebSettings() {
            private AgentWeb mAgentWeb;


            @Override
            protected void bindAgentWebSupport(AgentWeb agentWeb) {
                this.mAgentWeb = agentWeb;
            }

            @Override
            public WebListenerManager setDownloader(WebView webView, DownloadListener downloadListener) {

                return super.setDownloader(webView,
                        DefaultDownloadImpl
                                .create((Activity) webView.getContext(),
                                        webView,
                                        mDownloadListenerAdapter,
                                        mDownloadListenerAdapter,
                                        this.mAgentWeb.getPermissionInterceptor()));
            }


        };
    }
    private static final int REQUEST_SHARE_FILE_CODE = 120;

    @SuppressLint("WrongConstant")
    private void toCleanWebCache() {

        if (this.mAgentWeb != null) {
            //???????????????WebView??????????????? ??????????????? ???????????? ??????
            this.mAgentWeb.clearWebCache();
            Toast.makeText(getApplicationContext(), "???????????????", Toast.LENGTH_SHORT).show();
            //???????????? AgentWeb ????????????????????? WebView ????????? , AgentWeb ??????????????? ????????? ???apk ????????????
//            AgentWebConfig.clearDiskCache(this.getContext());
        }

    }

    /**????????????*/
    private Handler handler = new MyHandler(this);




    @SuppressLint("HandlerLeak")
    public class MyHandler extends Handler {
        private WeakReference<Context> reference;
        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }
        @Override
        public void handleMessage(Message msg) {
            //????????????
            SoundPoolUtils.stopRing();
            //????????????
            SoundPoolUtils.virateCancle(HelpActivity.this);

        }
    }

    private static final int QR_CODE = 55846;
    private Handler deliver = new Handler(Looper.getMainLooper());
    public class AndroidInterface {


        /**????????????*/
        @JavascriptInterface
        public void playSoundAndVibration() {
            deliver.post(new Runnable() {
                @Override
                public void run() {
                    setAlarmParams();
                    handler.sendEmptyMessageDelayed(0, 1100);
                }
            });

        }

        /**????????????*/
        @JavascriptInterface
        public void cleanUpAppCache() {
            deliver.post(new Runnable() {
                @Override
                public void run() {
                    toCleanWebCache();
                }
            });

        }
        /**???????????????*/
        @JavascriptInterface
        public void backToLastUrl() {
            deliver.post(new Runnable() {
                @Override
                public void run() {
                    if (!mAgentWeb.back()){
                        finish();
                    }else {
                        mAgentWeb.back();
                    }

                }
            });

        }


        /**???????????????????????????*/
        @JavascriptInterface
        public void nativeScanQRCode(final String invocationLogAppId,final String invocationLogFunction) {
            ceshiData(invocationLogAppId,invocationLogFunction,"nativeScanQRCode");

        }

        @JavascriptInterface
        public void nativeOpenSystemSetting(final String invocationLogAppId,final String invocationLogFunction) {
            Log.e("nativeOpenSystemSetting",invocationLogAppId+"");
            ceshiData(invocationLogAppId,invocationLogFunction,"nativeOpenSystemSetting");

        }


        /**??????????????????*/
        @JavascriptInterface
        public void nativeGetLocation(final String invocationLogAppId,final String invocationLogFunction,final String needExtraInfo) {

                if (EasyPermissions.hasPermissions(HelpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    ceshiData2(invocationLogAppId,invocationLogFunction, "nativeGetLocation",needExtraInfo);

                } else {//???????????????????????????????????????

                    //TestShowDig.AskForPermission(BaseWebActivity4.this,"??????");
                    permissionsUtil = PermissionsUtil
                            .with(HelpActivity.this)
                            .requestCode(0)
                            .isDebug(true)
                            .permissions(PermissionsUtil.Permission.Location.ACCESS_FINE_LOCATION)
                            .request();
                }





        }


        @JavascriptInterface
        public void nativeGetLocation(final String invocationLogAppId,final String invocationLogFunction) {
            if (EasyPermissions.hasPermissions(HelpActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                ceshiData(invocationLogAppId,invocationLogFunction, "nativeGetLocation");
            } else {
                permissionsUtil = PermissionsUtil
                        .with(HelpActivity.this)
                        .requestCode(0)
                        .isDebug(true)
                        .permissions(PermissionsUtil.Permission.Location.ACCESS_FINE_LOCATION)
                        .request();
            }

        }

        @JavascriptInterface
        public void nativeVerifyUserAndFace(final String invocationLogAppId,final String invocationLogFunction) {
            Log.e("invocationLogAppId",invocationLogAppId);

                if (EasyPermissions.hasPermissions(HelpActivity.this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    ceshiFaceData(invocationLogAppId,invocationLogFunction,"nativeVerifyUserAndFace");
                } else {
                    permissionsUtil = PermissionsUtil
                            .with(HelpActivity.this)
                            .requestCode(0)
                            .isDebug(true)
                            .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                            .request();
                }



        }


        /**??????????????????*/
        @JavascriptInterface
        public void nativeCloseCurrentPage(final String invocationLogAppId,final String invocationLogFunction) {
            ceshiData(invocationLogAppId,invocationLogFunction, "nativeCloseCurrentPage");


        }

        /**????????????imei*/
        @JavascriptInterface
        public void nativeGetEquipmentId(final String invocationLogAppId,final String invocationLogFunction) {
            if (EasyPermissions.hasPermissions(HelpActivity.this, Manifest.permission.READ_PHONE_STATE)) {
                imeiTask(invocationLogAppId,invocationLogFunction);

            } else {//???????????????????????????????????????

                //TestShowDig.AskForPermission(BaseWebActivity4.this,"??????");
                permissionsUtil = PermissionsUtil
                        .with(HelpActivity.this)
                        .requestCode(2)
                        .isDebug(true)
                        .permissions(PermissionsUtil.Permission.Phone.READ_PHONE_STATE)
                        .request();
            }


        }


        @JavascriptInterface
        public void nativeGetLocalVersion(final String invocationLogAppId,final String invocationLogFunction) {
            ceshiData(invocationLogAppId,invocationLogFunction, "nativeGetLocalVersion");
        }


        /**??????????????????*/
        @JavascriptInterface
        public void closeCurrentPage() {
            deliver.post(new Runnable() {
                @Override
                public void run() {
                    finish();
                }
            });
            Log.i("Info", "Thread:" + Thread.currentThread());
        }


        /**??????????????????*/
        @JavascriptInterface
        public void getLocation() {
            deliver.post(new Runnable() {
                @Override
                public void run() {
                    onLoctionCoordinate();
                }
            });
            Log.i("Info", "Thread:" + Thread.currentThread());
        }



        /**???????????????????????????*/
        @JavascriptInterface
        public void ScanQRCode() {


            deliver.post(new Runnable() {
                @Override
                public void run() {
                    qrPermission();
                    if (allowedScan){
                        onScanQR();
                    }else {
                        Toast.makeText(getApplicationContext(),"????????????????????????",Toast.LENGTH_SHORT).show();
                        qrPermission();
                    }


                }
            });

        }
    }
    private int imageid = 0;
    /**
     * ????????????
     * @param
     * @param invocationLogAppId
     * @param invocationLogFunction
     * @param
     */
    private void ceshiFaceData(String invocationLogAppId, String invocationLogFunction, final String typeName) {

        String username = (String) SPUtils.get(MyApp.getInstance(), "personName", "");
        String userId  = null;
        try {
            userId = AesEncryptUtile.encrypt(username+ "_"+ Calendar.getInstance().getTimeInMillis(),key);
            Log.e("userId",userId);
            OkGo.<String>post(HOME_URL+functionInvocationLogUrl)
                    .params("invocationLogAppId",invocationLogAppId)
                    .params("invocationLogMember",userId)
                    .params("invocationLogFunction",invocationLogFunction)
                    .params("domainName",appServiceUrl)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {

                            Log.e("js??????????????????",response.body());

                            AppOrthBean appOrthBean = JsonUtil.parseJson(response.body(),AppOrthBean.class);
                            boolean success = appOrthBean.getSuccess();
                            if(success == true){
                                String invocationLogFunction = appOrthBean.getObj().getInvocationLogFunction();
                                if(typeName.equals(invocationLogFunction)){
                                    if(invocationLogFunction.equals("nativeVerifyUserAndFace")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                SPUtils.put(HelpActivity.this,"bitmap","");
                                                Intent intent = new Intent(HelpActivity.this,FaceYiQingActivity.class);
                                                startActivityForResult(intent,99);
                                                imageid = 0;
                                            }
                                        });
                                    }

                                }
                            }else {
                                ToastUtils.showToast(HelpActivity.this,"??????????????????????????????!");
                                /*deliver.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        onLoctionCoordinate();
                                    }
                                });*/
                            }
                        }
                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.e("s",response.toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("facereYiQing")){
                Log.e("imageid",imageid+"");
                String FaceActivity = intent.getStringExtra("FaceActivity");
                if(imageid == 0){
                    if(FaceActivity != null){
                        imageid = 1;

                        String s = (String)SPUtils.get(HelpActivity.this, "bitmap", "");
                        String imei = (String) SPUtils.get(HelpActivity.this, "imei", "");
                        String username = (String) SPUtils.get(HelpActivity.this, "phone", "");

                        try {
                            //String secret  = AesEncryptUtile.encrypt(Calendar.getInstance().getTimeInMillis()+ "_"+"123456",key);
                            String secret = AesEncryptUtile.encrypt(username,key);
                            OkGo.<String>post(UrlRes.HOME2_URL+UrlRes.distinguishFaceUrl)
                            //OkGo.<String>post("http://192.168.30.68:8084/authentication/api/face/distinguishFace")
                                    .params( "openId",AesEncryptUtile.openid)
                                    .params( "memberId",secret)
                                    .params( "img",s )
                                    .params( "equipmentId",imei)
                                    .execute(new StringCallback(){

                                        @Override
                                        public void onStart(Request<String, ? extends Request> request) {
                                            super.onStart(request);
                                            //ViewUtils.createLoadingDialog2(LoginActivity2.this,true,"???????????????");

                                        }

                                        @Override
                                        public void onSuccess(Response<String> response) {
                                            SPUtils.put(getApplicationContext(),"isloading2","");
                                            ViewUtils.cancelLoadingDialog();
                                            Log.e("tag",response.body());
                                            BaseBean baseBean = JsonUtil.parseJson(response.body(),BaseBean.class);
                                            boolean success = baseBean.isSuccess();

                                            LocalFaceBean localVersonBean = new LocalFaceBean();
                                            localVersonBean.setSuccess(success);
                                            localVersonBean.setMessage(baseBean.getMsg());
                                            Gson gson = new Gson();
                                            String s = gson.toJson(localVersonBean);
                                            String jsonParams = s;
                                            String url2 = "javascript:getFaceAndroidParams('"+jsonParams+"')";
                                            Log.e("url",url2);
                                            mAgentWeb.getWebCreator().getWebView().evaluateJavascript(url2, new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String value) {
                                                    //????????? js ???????????????
                                                    Log.e("value",value);
                                                }
                                            });

                                        }

                                        @Override
                                        public void onError(Response<String> response) {
                                            super.onError(response);
                                            SPUtils.put(getApplicationContext(),"isloading2","");
                                            LocalFaceBean localVersonBean = new LocalFaceBean();
                                            localVersonBean.setSuccess(false);
                                            localVersonBean.setMessage("?????????????????????????????????");
                                            Gson gson = new Gson();
                                            String s = gson.toJson(localVersonBean);
                                            String jsonParams = s;
                                            String url2 = "javascript:getFaceAndroidParams('"+jsonParams+"')";
                                            mAgentWeb.getWebCreator().getWebView().evaluateJavascript(url2, new ValueCallback<String>() {
                                                @Override
                                                public void onReceiveValue(String value) {
                                                    //????????? js ???????????????
                                                    Log.e("value",value);
                                                }
                                            });
                                            ViewUtils.cancelLoadingDialog();
                                            imageid = 0;
                                        }
                                    });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        imageid = 0;
                    }
                }

            }
        }
    };


    public void imeiTask(String invocationLogAppId, String invocationLogFunction) {
        ceshiData(invocationLogAppId,invocationLogFunction, "nativeGetEquipmentId");
    }



    /**
     * ???????????????????????????
     * ???????????????????????????*/
    boolean allowedScan = false;
    private void qrPermission() {
        //????????????????????????
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .requestEach(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .subscribe(new io.reactivex.functions.Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // ???????????????????????????
                            Log.e("???????????????????????????", permission.name + " is granted.");
                            //   Log.d(TAG, permission.name + " is granted.");
                            allowedScan =true;

                        } else if (permission.shouldShowRequestPermissionRationale) {
                            allowedScan =false;
                            Log.e("????????????????????????", permission.name + " is denied. More info should be provided.");
                            // ????????????????????????????????????????????????????????????Never ask again???,??????????????????????????????????????????????????????????????????
                            //   Log.d(TAG, permission.name + " is denied. More info should be provided.");

                        } else {
                            allowedScan =false;
                            // ?????????????????????????????????????????????????????????
                            //   Log.d(TAG, permission.name + " is denied.");
                            Log.e("????????????????????????", permission.name + permission.name + " is denied.");
//                            BaseWebActivity.this.finish();
                        }
                    }
                });

    }


    /**
     * ???????????????????????????
     * @param
     */
    String loginQrUrl;
    public void onScanQR() {
        Intent i = new Intent(this,CaptureActivity.class);
        startActivity(i);
       /* QRCodeManager.getInstance()
                .with(this)
                .setReqeustType(0)
                .setRequestCode(QR_CODE)
                .scanningQRCode(new OnQRCodeListener() {
                    @Override
                    public void onCompleted(String result) {
                        //controlLog.append("\n\n(??????)" + result);
                        Log.e("QRCodeManager = ",result);
                        loginQrUrl = result;
                        mAgentWeb.getWebCreator().getWebView().loadUrl(result);

                    }

                    @Override
                    public void onError(Throwable errorMsg) {
                        //   controlLog.append("\n\n(??????)" + errorMsg.toString());
                        Log.e("QRCodeManager = = ",errorMsg.toString());
                    }

                    @Override
                    public void onCancel() {
                        //controlLog.append("\n\n(??????)?????????????????????");
                        Log.e("QRCodeManager = = = ","?????????????????????");
                    }

                    *//**
                     * ??????????????????????????????
                     *
                     * @param requestCode
                     * @param resultCode
                     * @param data
                     *//*
                    @Override
                    public void onManual(int requestCode, int resultCode, Intent data) {
                        Log.e("QRCodeManager","????????????????????????");
                    }
                });*/

    }
    private Location location;
    private LocationManager locationManager;
    private String locationProvider;       //???????????????

    /**??????????????????*/
    @SuppressLint("WrongConstant")
    public void onLoctionCoordinate(){
        String latitude =(String) SPUtils.get(MyApp.getInstance(), "latitude", "");
        Log.e("????????????",latitude);

        String longitude =(String) SPUtils.get(MyApp.getInstance(), "longitude", "");
        String direction =(String) SPUtils.get(MyApp.getInstance(), "direction", "");
        String altitude =(String) SPUtils.get(MyApp.getInstance(), "altitude", "");
        String speed =(String) SPUtils.get(MyApp.getInstance(), "speed", "");
        String locationWhere =(String) SPUtils.get(MyApp.getInstance(), "locationWhere", "");

        String countryName =(String) SPUtils.get(MyApp.getInstance(), "country", "");
        String countryCode =(String) SPUtils.get(MyApp.getInstance(), "countryCode", "");
        String province =(String) SPUtils.get(MyApp.getInstance(), "province", "");
        String city =(String) SPUtils.get(MyApp.getInstance(), "city", "");
        String cityCode =(String) SPUtils.get(MyApp.getInstance(), "cityCode", "");
        String district =(String) SPUtils.get(MyApp.getInstance(), "district", "");
        String town =(String) SPUtils.get(MyApp.getInstance(), "town", "");
        String adcode =(String) SPUtils.get(MyApp.getInstance(), "adcode", "");
        String street =(String) SPUtils.get(MyApp.getInstance(), "street", "");
        String streetNumber =(String) SPUtils.get(MyApp.getInstance(), "streetNumber", "");

        Log.e("????????????2",longitude);
        LocationBean locationBean = new LocationBean();
        locationBean.setSuccess(true);
        locationBean.setMessage("??????");
        locationBean.setIsBaidu("1");
        locationBean.setAltitude(altitude);
        locationBean.setSignRecordEquipmentId(MobileInfoUtils.getIMEI(HelpActivity.this));
        //locationBean.setLatitude(location.getLatitude()+"");
        locationBean.setLatitude(latitude+"");
        //locationBean.setLongitude(location.getLongitude()+"");
        locationBean.setLongitude(longitude+"");
        locationBean.setAddress((String) SPUtils.get(MyApp.getInstance(), "addressLine", ""));

        locationBean.setCountryName(countryName);
        locationBean.setCityCode(countryCode);
        locationBean.setProvince(province);
        locationBean.setCity(city);
        locationBean.setCityCode(cityCode);
        locationBean.setDistrict(district);
        locationBean.setTown(town);
        locationBean.setAdcode(adcode);
        locationBean.setStreet(street);
        locationBean.setStreetNumber(streetNumber);

        Gson gson = new Gson();
        String s = gson.toJson(locationBean);
        String jsonParams = s;
        String url2 = "javascript:getAndroidParams('"+jsonParams+"')";
        Log.e("url",url2);
        mAgentWeb.getWebCreator().getWebView().evaluateJavascript(url2, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //????????? js ???????????????
                Log.e("value",value);
            }
        });


    }

    @SuppressLint("WrongConstant")
    public void onLoctionCoordinate2(){
        String latitude =(String) SPUtils.get(MyApp.getInstance(), "latitude", "");
        Log.e("????????????",latitude);

        String longitude =(String) SPUtils.get(MyApp.getInstance(), "longitude", "");
        String direction =(String) SPUtils.get(MyApp.getInstance(), "direction", "");
        String altitude =(String) SPUtils.get(MyApp.getInstance(), "altitude", "");
        String speed =(String) SPUtils.get(MyApp.getInstance(), "speed", "");
        String locationWhere =(String) SPUtils.get(MyApp.getInstance(), "locationWhere", "");

        String countryName =(String) SPUtils.get(MyApp.getInstance(), "country", "");
        String countryCode =(String) SPUtils.get(MyApp.getInstance(), "countryCode", "");
        String province =(String) SPUtils.get(MyApp.getInstance(), "province", "");
        String city =(String) SPUtils.get(MyApp.getInstance(), "city", "");
        String cityCode =(String) SPUtils.get(MyApp.getInstance(), "cityCode", "");
        String district =(String) SPUtils.get(MyApp.getInstance(), "district", "");
        String town =(String) SPUtils.get(MyApp.getInstance(), "town", "");
        String adcode =(String) SPUtils.get(MyApp.getInstance(), "adcode", "");
        String street =(String) SPUtils.get(MyApp.getInstance(), "street", "");
        String streetNumber =(String) SPUtils.get(MyApp.getInstance(), "streetNumber", "");

        Log.e("????????????2",longitude);
        LocationBean2 locationBean = new LocationBean2();
        locationBean.setSuccess(true);
        locationBean.setMessage("??????");
        locationBean.setIsBaidu("1");
        locationBean.setAltitude(altitude);
        locationBean.setCourse(direction);
        if(locationWhere.equals("1")){
            locationBean.setChina(true);
        }else {
            locationBean.setChina(false);
        }
        locationBean.setSpeed(speed);
        locationBean.setSignRecordEquipmentId(MobileInfoUtils.getIMEI(HelpActivity.this));
        //locationBean.setLatitude(location.getLatitude()+"");
        locationBean.setLatitude(latitude+"");
        //locationBean.setLongitude(location.getLongitude()+"");
        locationBean.setLongitude(longitude+"");
        locationBean.setAddress((String) SPUtils.get(MyApp.getInstance(), "addressLine", ""));

        locationBean.setCountryName(countryName);
        locationBean.setCountryCode(countryCode);
        locationBean.setProvince(province);
        locationBean.setCity(city);
        locationBean.setCityCode(cityCode);
        locationBean.setDistrict(district);
        locationBean.setTown(town);
        locationBean.setAdcode(adcode);
        locationBean.setStreet(street);
        locationBean.setStreetNumber(streetNumber);

        Gson gson = new Gson();
        String s = gson.toJson(locationBean);
        String jsonParams = s;
        String url2 = "javascript:getAndroidParams('"+jsonParams+"')";
        Log.e("url",url2);
        mAgentWeb.getWebCreator().getWebView().evaluateJavascript(url2, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //????????? js ???????????????
                Log.e("value",value);
            }
        });


    }


    /** ??????????????????*/
    @SuppressLint("WrongConstant")
    private void setAlarmParams() {
        //AudioManager provides access to volume and ringer mode control.
        AudioManager volMgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        switch (volMgr.getRingerMode()) {//?????????????????????????????????
            case AudioManager.RINGER_MODE_SILENT://?????????????????????0?????????????????????????????????
                break;
            case AudioManager.RINGER_MODE_VIBRATE://?????????????????????1??????????????????????????????
                SoundPoolUtils.vibrate(this, new long[]{1000, 500, 1000, 500}, 0);
                break;
            case AudioManager.RINGER_MODE_NORMAL://?????????????????????2?????????????????????1_?????????????????????2_??????+??????
                SoundPoolUtils.playRing(this);
                SoundPoolUtils.vibrate(this, new long[]{500, 1000, 500, 1000}, 0);
                break;
            default:
                break;
        }
    }


    private void ceshiData(String appid, String function, final String typeName) {
        Log.e("function",function);
        String username = (String) SPUtils.get(MyApp.getInstance(), "personName", "");
        String userId  = null;
        try {
            userId = AesEncryptUtile.encrypt(username+ "_"+ Calendar.getInstance().getTimeInMillis(),key);
            OkGo.<String>post(HOME_URL+functionInvocationLogUrl)
                    .params("invocationLogAppId",appid)
                    .params("invocationLogMember",userId)
                    .params("invocationLogFunction",function)
                    .params("domainName",appServiceUrl)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {

                            Log.e("js????????????",response.body());

                            AppOrthBean appOrthBean = JsonUtil.parseJson(response.body(),AppOrthBean.class);
                            boolean success = appOrthBean.getSuccess();
                            if(success == true){
                                String invocationLogFunction = appOrthBean.getObj().getInvocationLogFunction();
                                if(typeName.equals(invocationLogFunction)){
                                    if(invocationLogFunction.equals("nativeScanQRCode")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                cameraTask();



                                            }
                                        });
                                    }  else if(invocationLogFunction.equals("nativeGetLocation")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                onLoctionCoordinate();
                                            }
                                        });
                                    }else if(invocationLogFunction.equals("nativeCloseCurrentPage")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        });
                                    }else if(invocationLogFunction.equals("nativeGetEquipmentId")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                onNativeGetImei();
                                            }
                                        });
                                    }else if(invocationLogFunction.equals("nativeGetLocalVersion")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                onNativeGetLocalVersion();
                                            }
                                        });
                                    }else if(invocationLogFunction.equals("nativeOpenSystemSetting")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                                intent.setData(uri);
                                                startActivityForResult(intent, SETTINGS_REQ_CODE);
                                            }
                                        });
                                    }

                                }
                            }else {
                                ToastUtils.showToast(HelpActivity.this,"??????????????????????????????!");
                                /*deliver.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        onLoctionCoordinate();
                                    }
                                });*/
                            }
                        }
                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.e("s",response.toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void ceshiData2(String appid, String function, final String typeName, final String needExtraInfo) {
        Log.e("function",function);
        String username = (String) SPUtils.get(MyApp.getInstance(), "personName", "");
        String userId  = null;
        try {
            userId = AesEncryptUtile.encrypt(username+ "_"+ Calendar.getInstance().getTimeInMillis(),key);
            OkGo.<String>post(HOME_URL+functionInvocationLogUrl)
                    .params("invocationLogAppId",appid)
                    .params("invocationLogMember",userId)
                    .params("invocationLogFunction",function)
                    .params("domainName",appServiceUrl)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {

                            Log.e("js????????????",response.body());

                            AppOrthBean appOrthBean = JsonUtil.parseJson(response.body(),AppOrthBean.class);
                            boolean success = appOrthBean.getSuccess();
                            if(success == true){
                                String invocationLogFunction = appOrthBean.getObj().getInvocationLogFunction();
                                if(typeName.equals(invocationLogFunction)){
                                    if(invocationLogFunction.equals("nativeScanQRCode")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                cameraTask();



                                            }
                                        });
                                    }  else if(invocationLogFunction.equals("nativeGetLocation")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(needExtraInfo!= null){
                                                    if(needExtraInfo.equals("0")){
                                                        onLoctionCoordinate();
                                                    }else {
                                                        onLoctionCoordinate2();
                                                    }
                                                }else {
                                                    onLoctionCoordinate();
                                                }


                                            }
                                        });
                                    }else if(invocationLogFunction.equals("nativeCloseCurrentPage")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                finish();
                                            }
                                        });
                                    }else if(invocationLogFunction.equals("nativeGetEquipmentId")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                onNativeGetImei();
                                            }
                                        });
                                    }else if(invocationLogFunction.equals("nativeGetLocalVersion")){
                                        deliver.post(new Runnable() {
                                            @Override
                                            public void run() {
                                                onNativeGetLocalVersion();
                                            }
                                        });
                                    }

                                }
                            }else {
                                ToastUtils.showToast(HelpActivity.this,"??????????????????????????????!");
                                /*deliver.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        onLoctionCoordinate();
                                    }
                                });*/
                            }
                        }
                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.e("s",response.toString());
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void onNativeGetLocalVersion() {
        String localVersionName = getLocalVersionName(this);
        LocalVersonBean localVersonBean = new LocalVersonBean();
        localVersonBean.setSuccess(1);
        localVersonBean.setMessage("??????");
        localVersonBean.setLocalVersionName(localVersionName);
        Gson gson = new Gson();
        String s = gson.toJson(localVersonBean);
        String jsonParams = s;
        String url2 = "javascript:getAndroidVersionParams('"+jsonParams+"')";
        Log.e("url",url2);
        mAgentWeb.getWebCreator().getWebView().evaluateJavascript(url2, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //????????? js ???????????????
                Log.e("value",value);
            }
        });
    }

    private void onNativeGetImei() {

        String imei = getIMEI(this);
        ImeiBean imeiBean = new ImeiBean();
        imeiBean.setSuccess(1);
        imeiBean.setMessage("??????");
        imeiBean.setEquipmentId(imei);
        Gson gson = new Gson();
        String s = gson.toJson(imeiBean);
        String jsonParams = s;
        String url2 = "javascript:getAndroidImeiParams('"+jsonParams+"')";
        Log.e("url",url2);
        mAgentWeb.getWebCreator().getWebView().evaluateJavascript(url2, new ValueCallback<String>() {
            @Override
            public void onReceiveValue(String value) {
                //????????? js ???????????????
                Log.e("value",value);
            }
        });
    }


    private static final int RC_CAMERA_PERM = 123;

    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraTask() {
        if (EasyPermissions.hasPermissions(HelpActivity.this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            onScanQR();

        } else {//???????????????????????????????????????
         /*
            EasyPermissions.requestPermissions(this, "?????????????????????",
                    RC_CAMERA_PERM, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);*/
            permissionsUtil=  PermissionsUtil
                    .with(this)
                    .requestCode(1)
                    .isDebug(true)//??????log
                    .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                    .request();
        }
    }


    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
        SPUtils.put(getApplicationContext(),"isloading2","");
        //upLoadWebInfo();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
        netInsertPortal("5");
        String isLoading2 = (String) SPUtils.get(HelpActivity.this, "isloading2", "");
        Log.e("isLoading2",isLoading2);
        if(!isLoading2 .equals("")){
            ViewUtils.createLoadingDialog2(HelpActivity.this,true,"???????????????");
            SPUtils.put(getApplicationContext(),"isloading2","");


        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
        mLocationClient.stop();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiver2);
    }

    private class OpenFileChromeClient extends WebChromeClient {

        //  Android 2.2 (API level 8)???Android 2.3 (API level 10)?????????????????????????????????????????????
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, null);
        }

        // Android 3.0 (API level 11)??? Android 4.0 (API level 15))?????????????????????????????????????????????????????????
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, acceptType, null);
        }

        // Android 4.1 (API level 16) -- Android 4.3 (API level 18)?????????????????????????????????????????????????????????
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileInput(uploadMsg, null, false);
        }

        // Android 5.0 (API level 21)?????????????????????????????????????????????????????????
        @SuppressWarnings("all")
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (Build.VERSION.SDK_INT >= 21) {
                final boolean allowMultiple = fileChooserParams.getMode() == FileChooserParams.MODE_OPEN_MULTIPLE;//??????????????????
                openFileInput(null, filePathCallback, allowMultiple);
                return true;
            }
            else {
                return false;
            }
        }

        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }

        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
            if (mTitleTextView != null) {
                mTitleTextView.setText(title);
            }
            if(appName != null){
                mTitleTextView.setText(appName);
            }
        }


    }


    @SuppressLint("NewApi")
    protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst, final ValueCallback<Uri[]> fileUploadCallbackSecond, final boolean allowMultiple) {
        //Android 5.0????????????
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;

        //Android 5.0???????????????
        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;

        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);

        if (allowMultiple) {
            if (Build.VERSION.SDK_INT >= 18) {
                i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            }
        }

        i.setType(mUploadableFileTypes);

        startActivityForResult(Intent.createChooser(i, "????????????"), REQUEST_CODE_FILE_PICKER);

    }




    private void netInsertPortal(final String insertPortalAccessLog) {
        String imei = getIMEI(this);
        OkGo.<String>post(HOME_URL + UrlRes.Four_Modules)
                .params("portalAccessLogMemberId",(String) SPUtils.get(getInstance(),"userId",""))
                .params("portalAccessLogEquipmentId",(String) SPUtils.get(getInstance(),"imei",""))//??????ID
                .params("portalAccessLogTarget", insertPortalAccessLog)//????????????
                .params("portalAccessLogVersionNumber", (String) SPUtils.get(this,"versionName", ""))//?????????
                .params("portalAccessLogOperatingSystem", "ANDROID")//?????????
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("sdsaas",response.body());

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }


    @Override
    public boolean onDown(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent motionEvent) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent motionEvent) {

    }

    @Override
    public boolean onFling(MotionEvent motionEvent, MotionEvent motionEvent1, float v, float v1) {
       /* if(motionEvent.getX() - motionEvent1.getX() > FLIP_DISTANCE)
        {
            Toast.makeText(this, "??????", Toast.LENGTH_SHORT).show();
            return true;
        }*/
        float x = motionEvent1.getX();
        float x1 = motionEvent.getX();
        Log.e("x",x+"");
        Log.e("x1",x1+"");
        if(motionEvent1.getX() - motionEvent.getX() > FLIP_DISTANCE)
        {
            /*Toast.makeText(this, "??????", Toast.LENGTH_SHORT).show();
            onBackPressed();*/
            boolean b = mAgentWeb.getWebCreator().getWebView().canGoBack();
            if(b){
                mAgentWeb.back();

                Log.e("ACTION_MOVE","ACTION_MOVE");

            }else {
                finish();
                Log.e("ACTION_MOVE","ACTION_MOVE---finish");
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        //TouchEvent dispatcher.
        if (gestureDetector != null) {
            if (gestureDetector.onTouchEvent(ev))
                //If the gestureDetector handles the event, a swipe has been executed and no more needs to be done.
                return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //????????????onRequestPermissionsResult
        if(permissionsUtil != null){
            permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
        QRCodeManager.getInstance().with(this).onActivityResult(requestCode, resultCode, intent);
        if(permissionsUtil != null){
            permissionsUtil.onActivityResult(requestCode, resultCode, intent);
        }

        if (requestCode == REQUEST_CODE_FILE_PICKER) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    //Android 5.0????????????
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    }
                    else if (mFileUploadCallbackSecond != null) {//Android 5.0???????????????
                        Uri[] dataUris = null;

                        try {
                            if (intent.getDataString() != null) {
                                dataUris = new Uri[] { Uri.parse(intent.getDataString()) };
                            }
                            else {
                                if (Build.VERSION.SDK_INT >= 16) {
                                    if (intent.getClipData() != null) {
                                        final int numSelectedFiles = intent.getClipData().getItemCount();

                                        dataUris = new Uri[numSelectedFiles];

                                        for (int i = 0; i < numSelectedFiles; i++) {
                                            dataUris[i] = intent.getClipData().getItemAt(i).getUri();
                                        }
                                    }
                                }
                            }
                        }
                        catch (Exception ignored) { }
                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            }
            else {
                //??????mFileUploadCallbackFirst???mFileUploadCallbackSecond???????????????????????????????????????
                //WebView????????????????????????????????????????????????????????????onReceiveValue???null?????????
                //??????WebView???????????????????????????????????????????????????????????????????????????????????????
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                }
                else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }


    }

}
