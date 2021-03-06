package io.cordova.zhqy.web;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.AudioManager;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
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


import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
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
import io.cordova.zhqy.Main2Activity;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.activity.LoginActivity2;
import io.cordova.zhqy.bean.AppOrthBean;
import io.cordova.zhqy.bean.BaseBean;
import io.cordova.zhqy.bean.DownLoadBean;
import io.cordova.zhqy.bean.LoginBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.CookieUtils;
import io.cordova.zhqy.utils.DensityUtil;
import io.cordova.zhqy.utils.GpsUtils;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MobileInfoUtils;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ScreenSizeUtils;
import io.cordova.zhqy.utils.SoundPoolUtils;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.widget.CLWebView;
import io.cordova.zhqy.widget.MyDialog;
import io.cordova.zhqy.widget.TestWebView;
import io.cordova.zhqy.zixing.OnQRCodeListener;
import io.cordova.zhqy.zixing.QRCodeManager;
import me.samlss.lighter.Lighter;
import me.samlss.lighter.interfaces.OnLighterListener;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static io.cordova.zhqy.UrlRes.HOME2_URL;
import static io.cordova.zhqy.UrlRes.HOME_URL;
import static io.cordova.zhqy.UrlRes.addPortalReadingAccessUrl;
import static io.cordova.zhqy.UrlRes.functionInvocationLogUrl;
import static io.cordova.zhqy.utils.AesEncryptUtile.key;
import static io.cordova.zhqy.utils.MyApp.getInstance;


/**
 * Created by cenxiaozhong on 2017/5/26.
 * <p>
 * source code  https://github.com/Justson/AgentWeb
 */

@SuppressLint("Registered")
public class ChangeUpdatePwdWebActivity extends AppCompatActivity {
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

    @BindView(R.id.layout_back)
    RelativeLayout layout_back;

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

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        mLinearLayout = (LinearLayout) this.findViewById(R.id.container);
        rvClose.setVisibility(View.GONE);

        tgc = (String) SPUtils.get(getApplicationContext(), "TGC", "");
        appServiceUrl = getIntent().getStringExtra("appUrl");
        appId = getIntent().getStringExtra("appId");
        search = getIntent().getStringExtra("search");//???????????????'
        oaMsg = getIntent().getStringExtra("oaMsg");//oa ??????

        appName = getIntent().getStringExtra("appName");
        scan = getIntent().getStringExtra("scan");
        layout_back.setVisibility(View.GONE);
        mTitleTextView.setText(appName);
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

        String tgc = (String) SPUtils.get(ChangeUpdatePwdWebActivity.this, "TGC", "");
        CookieUtils.syncCookie(HOME2_URL,"CASTGC="+tgc,getApplication());
        mAgentWeb = AgentWeb.with(this)
                .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
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
        //ceshiData();

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
    /**
     * ??????????????????????????????
     * */
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
                    ChangeUpdatePwdWebActivity.this.finish();
                }
                break;
            case R.id.iv_close:
                ChangeUpdatePwdWebActivity.this.finish();
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
            // super.onReceivedSslError(view, handler, error);
            /**
             *  Webview?????????5.0???????????????????????????????????????????????????
             *  ?????????5.0??????????????????????????????http???https???????????????????????????webview???????????????????????????????????????
             */
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



            if (url.contains(UrlRes.HOME2_URL+"/cas/login")) {
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
            if (url.contains(UrlRes.HOME2_URL+"/cas/login")) {
                if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username","")) || tgc.equals("")){
                    Intent intent = new Intent(getApplicationContext(),LoginActivity2.class);
                    startActivity(intent);
                    finish();

                    return true;
                }
            }

            urldown = "";
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
                public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {

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
                    Log.e("myurl","?????????");
                    Log.e("myurl",url);
                }
            });

            webView.setLoadFinishListener(new CLWebView.LoadFinishListener() {
                @Override
                public void onLoadFinish(WebView webView) {
                    Log.e("urldown",urldown);


                    upLoadWebInfo();
                    String url1 = webView.getUrl();
                    urldown = url1;
                    sBegin = Calendar.getInstance().getTimeInMillis() + "";

                    s1 = stringToDate(sBegin);
                    Log.e("urldown",urldown);
                    Log.e("myurl","???????????????");
                    Log.e("myurl",url1);
                    String downLoadType = (String) SPUtils.get(ChangeUpdatePwdWebActivity.this, "downLoadType", "");
                    DownLoadBean downLoadBean = JsonUtil.parseJson(downLoadType,DownLoadBean.class);
                    List<String> downLoadTypeList = downLoadBean.getString();
                    for (int i = 0; i < downLoadTypeList.size(); i++) {
                        if(urldown.contains(downLoadTypeList.get(i))){
                            Log.e("tag",downLoadTypeList.get(i));
                            logOut(urldown);
                            break;
                        }else {

                        }
                    }
                }
            });

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            CookieUtils.syncCookie(UrlRes.HOME2_URL,"CASTGC="+tgc,getApplication());
            if (!StringUtils.isEmpty(appId)){
                start =  Calendar.getInstance().getTimeInMillis() ;
                Log.i("Info", "start:  " + start );
            }

        }




    };

    /**
     * @Description: String??????????????????????????????
     * [@param](http://my.oschina.net/param) lo ?????????
     * @return String yyyy-MM-dd HH:mm:ss
     */
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
                //setDownloader(webView, defaultDownload);
                tag = 1;
                mAgentWeb = AgentWeb.with(ChangeUpdatePwdWebActivity.this)
                        .setAgentWebParent(mLinearLayout, new LinearLayout.LayoutParams(-1, -1))
                        .useDefaultIndicator(-1, 3)//?????????????????????????????????-1????????????????????????2????????????dp???
                        .setWebChromeClient(mWebChromeClient)
                        .setWebViewClient(mWebViewClient)
                        .setPermissionInterceptor(mPermissionInterceptor) //???????????? 2.0.0 ?????????
                        .setMainFrameErrorView(R.layout.agentweb_error_page, -1)
                        .setSecurityType(AgentWeb.SecurityType.STRICT_CHECK)
                        .setWebLayout(new WebLayout4(ChangeUpdatePwdWebActivity.this))
                        .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.ASK)//??????????????????????????????????????????????????????????????????
                        .interceptUnkownUrl() //??????????????????????????????Scheme
                        .setAgentWebWebSettings( getSettings())//?????? IAgentWebSettings???
                        .createAgentWeb()
                        .ready()
                        .go(urldown);

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
            Log.i("Info", "onProgress:" + newProgress);
            Log.i("Info", "onProgress:" +  time);

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
            Log.i("???????????????????????? ", "mUrl:" + url + "  permission:" + mGson.toJson(permissions) + " action:" + action);
            return false;
        }
    };

    public String getUrl() {
        return "http://iapp.zzuli.edu.cn/portal/portal-app/app-5/index.html?isAPP=true";
    }

    private Gson mGson = new Gson();

    /**
     * ????????? AgentWeb  4.0.0
     */
    protected DownloadListenerAdapter mDownloadListenerAdapter = new DownloadListenerAdapter() {

        /**
         *
         * @param url                ????????????
         * @param userAgent          UserAgent
         * @param contentDisposition ContentDisposition
         * @param mimetype           ?????????????????????
         * @param contentLength      ????????????
         * @param extra              ???????????? ??? ?????????????????? Extra ????????????icon ??? ??????????????? ??? ?????????????????????
         * @return true ???????????????????????????????????? ??? false ?????? AgentWeb ??????
         */
        @Override
        public boolean onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, AgentWebDownloader.Extra extra) {
            Log.i("????????????", "onStart:" + url);
            ViewUtils.createLoadingDialog(ChangeUpdatePwdWebActivity.this);
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
        public boolean onResult(String path, String url, Throwable throwable) {
            ViewUtils.cancelLoadingDialog();
            if (null == throwable) { //????????????
                //do you work
                Log.e("????????????", path);
                Log.e("????????????", url);

                Uri shareFileUrl = FileUtil.getFileUri(getApplicationContext(), null, new File(path));
                Log.e("path2", String.valueOf(shareFileUrl));
                new Share2.Builder(ChangeUpdatePwdWebActivity.this)
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(shareFileUrl)
                        .setTitle("Share File")
                        .setOnActivityResult(REQUEST_SHARE_FILE_CODE)
                        .build()
                        .shareBySystem();

            } else {//????????????
                Log.e("path", path);

                Uri shareFileUrl = FileUtil.getFileUri(getApplicationContext(), null, new File(path));
                Log.e("path2", String.valueOf(shareFileUrl));
                new Share2.Builder(ChangeUpdatePwdWebActivity.this)
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

    /**
     * @return IAgentWebSettings
     */
    public IAgentWebSettings getSettings() {
        return new AbsAgentWebSettings() {
            private AgentWeb mAgentWeb;


            @Override
            protected void bindAgentWebSupport(AgentWeb agentWeb) {
                this.mAgentWeb = agentWeb;
            }

            /**
             * AgentWeb 4.0.0 ??????????????? DownloadListener ?????? ???????????????API ?????? Download ??????????????????????????????????????????
             * ????????????????????? AgentWeb Download ?????? ??? ???????????? compile 'com.just.agentweb:download:4.0.0 ???
             * ???????????????????????????????????????????????? AgentWebSetting ??? New ??? DefaultDownloadImpl?????????DownloadListenerAdapter
             * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? setDownloader ????????????????????????
             * @param webView
             * @param downloadListener
             * @return WebListenerManager
             */
            @Override
            public WebListenerManager setDownloader(WebView webView, DownloadListener downloadListener) {

              /*  return super.setDownloader(webView,
                        DefaultDownloadImpl
                                .create((Activity) webView.getContext(),
                                        webView,
                                        mDownloadListenerAdapter,
                                        mDownloadListenerAdapter,
                                        this.mAgentWeb.getPermissionInterceptor()));*/


                if(tag == 0){
                    Log.e("tag---------","111111");
                    return super.setDownloader(webView,downloadListener);

                }else {
                    tag = 0;
                    Log.e("tag---------","222222");
                    return super.setDownloader(webView,
                            DefaultDownloadImpl
                                    .create((Activity) webView.getContext(),
                                            webView,
                                            mDownloadListenerAdapter,
                                            null,
                                            this.mAgentWeb.getPermissionInterceptor()));
                }

                //return super.setDownloader(webView,downloadListener);
            }


        };
    }

    private static final int REQUEST_SHARE_FILE_CODE = 120;



    /**
     * ?????? WebView ??????
     */
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
            SoundPoolUtils.virateCancle(ChangeUpdatePwdWebActivity.this);

        }
    }

    /**??????*/
    private static final int QR_CODE = 55846;
    private Handler deliver = new Handler(Looper.getMainLooper());
    /**Js???????????????*/
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
            Log.i("Info", "Thread:" + Thread.currentThread());
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
            Log.i("Info", "Thread:" + Thread.currentThread());
        }


        /**???????????????????????????*/
        @JavascriptInterface
        public void nativeScanQRCode(final String invocationLogAppId,final String invocationLogFunction) {
            ceshiData(invocationLogAppId,invocationLogFunction,"nativeScanQRCode");

        }
        /**??????????????????*/
        @JavascriptInterface
        public void nativeGetLocation(final String invocationLogAppId,final String invocationLogFunction) {
            ceshiData(invocationLogAppId,invocationLogFunction, "nativeGetLocation");

            Log.i("Info", "Thread:" + Thread.currentThread());
        }
        /**??????????????????*/
        @JavascriptInterface
        public void nativeCloseCurrentPage(final String invocationLogAppId,final String invocationLogFunction) {
            ceshiData(invocationLogAppId,invocationLogFunction, "nativeCloseCurrentPage");

            Log.i("Info", "Thread:" + Thread.currentThread());
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
//        QRCodeManager.getInstance().with(getActivity()).scanningQRCode(1);
        Log.e("tag  = ","?????????");
        QRCodeManager.getInstance()
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

                    /**
                     * ??????????????????????????????
                     *
                     * @param requestCode
                     * @param resultCode
                     * @param data
                     */
                    @Override
                    public void onManual(int requestCode, int resultCode, Intent data) {
                        Log.e("QRCodeManager","????????????????????????");
                    }
                });

    }
    private Location location;
    /**??????????????????*/
    @SuppressLint("WrongConstant")
    public void onLoctionCoordinate(){
        location = GpsUtils.getInstance(this).showLocation();
        if (location != null) {
            String address = "?????????" + location.getLatitude() + "?????????" + location.getLongitude();
            Log.d( "??????????????????",address );
            Toast.makeText(getApplication(),address,Toast.LENGTH_SHORT).show();
            GpsUtils.getInstance(this).removeLocationUpdatesListener();
        }
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
        String username = (String) SPUtils.get(MyApp.getInstance(), "personName", "");
        String userId  = null;
        try {
            userId = AesEncryptUtile.encrypt(username+ "_"+ Calendar.getInstance().getTimeInMillis(),key);
            OkGo.<String>post(HOME_URL+functionInvocationLogUrl)
                    .params("invocationLogAppId",appid)
                    .params("invocationLogMember",userId)
                    .params("invocationLogFunction",function)
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
                                                /*qrPermission();
                                                if (allowedScan){
                                                    onScanQR();
                                                }else {
                                                    Toast.makeText(getApplicationContext(),"????????????????????????",Toast.LENGTH_SHORT).show();
                                                    qrPermission();
                                                }*/


                                            }
                                        });
                                    }else if(invocationLogFunction.equals("nativeGetLocation")){
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
                                    }

                                }
                            }else {
                                ToastUtils.showToast(ChangeUpdatePwdWebActivity.this,"??????????????????????????????!");
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

    private static final int RC_CAMERA_PERM = 123;

    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraTask() {
        if (EasyPermissions.hasPermissions(ChangeUpdatePwdWebActivity.this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Have permission, do the thing!

            onScanQR();
            ;//??????????????????
        } else {//???????????????????????????????????????
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "?????????????????????",
                    RC_CAMERA_PERM, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }


    @Override
    protected void onPause() {
        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
        upLoadWebInfo();
    }

    @Override
    protected void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
        netInsertPortal("5");
        //netWorkLogin();
    }

    LoginBean loginBean;
    String sName;
    String sPwd;
    private void netWorkLogin() {
        sName = (String) SPUtils.get(MyApp.getInstance(),"username","");
        sPwd =  (String) SPUtils.get(MyApp.getInstance(),"password","");

        try {
            String imei = AesEncryptUtile.encrypt((String) SPUtils.get(this, "imei", ""), key);
            OkGo.<String>get(UrlRes.HOME2_URL +UrlRes.loginUrl)
                    .params("openid",AesEncryptUtile.openid)
                    .params("username",sName)
                    .params("password",sPwd)
                    .params("type","10")
                    .params("equipmentId",imei)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("????????????hha",response.body());
                            loginBean = JSON.parseObject(response.body(),LoginBean.class);
                            if (loginBean.isSuccess() ) {

                            }else {
                                SPUtils.put(MyApp.getInstance(),"userId","");
//                                SPUtils.put(MyApp.getInstance(),"tgt",tgt);
                                SPUtils.put(getApplicationContext(),"TGC","");
                                SPUtils.put(getApplicationContext(),"username","");
                                SPUtils.put(getApplicationContext(),"password","");
                                Intent intent = new Intent(MyApp.getInstance(),LoginActivity2.class);
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.e("??????????????????",response.body());

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAgentWeb.getWebLifeCycle().onDestroy();
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
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
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


    public void onActivityResult(final int requestCode, final int resultCode, final Intent intent) {
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


    private void netInsertPortal(final String insertPortalAccessLog) {
        String imei = MobileInfoUtils.getIMEI(this);
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
    public void onBackPressed() {
        //super.onBackPressed();
    }
}
