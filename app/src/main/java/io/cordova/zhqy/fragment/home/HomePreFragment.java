package io.cordova.zhqy.fragment.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;

import butterknife.BindView;
import butterknife.OnClick;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;

import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.activity.CAResultActivity;
import io.cordova.zhqy.activity.LoginActivity2;
import io.cordova.zhqy.activity.OaMsgActivity;
import io.cordova.zhqy.activity.OaMsgYBActivity;
import io.cordova.zhqy.activity.SystemMsgActivity;
import io.cordova.zhqy.bean.CaBean;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.LighterHelper;
import io.cordova.zhqy.utils.MobileInfoUtils;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.NetState;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;
import io.cordova.zhqy.R;
import io.cordova.zhqy.utils.BaseFragment;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.web.FileUtil;
import io.cordova.zhqy.web.WebLayout4;
import io.cordova.zhqy.zixing.OnQRCodeListener;
import io.cordova.zhqy.zixing.QRCodeManager;
import io.cordova.zhqy.zixing.activity.CaptureActivity;
import me.samlss.lighter.Lighter;
import me.samlss.lighter.interfaces.OnLighterListener;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.CircleShape;


import static android.content.Context.NOTIFICATION_SERVICE;
import static io.cordova.zhqy.UrlRes.caQrCodeVerifyUrl;
import static io.cordova.zhqy.utils.MyApp.getInstance;


/**
 * Created by Administrator on 2018/11/19 0019.
 */

public class HomePreFragment extends BaseFragment implements PermissionsUtil.IPermissionsCallback{


    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.layout_msg)
    RelativeLayout layoutMsg;

    @BindView(R.id.msg_num)
    TextView msgNum;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_qr)
    ImageView iv_qr;



    @BindView(R.id.swipeLayout)
    SmartRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.header)
    ClassicsHeader header;
    protected AgentWeb mAgentWeb;

    @BindView(R.id.linearLayout)
    LinearLayout mLinearLayout;

    private Gson mGson = new Gson();
    String tgc ,msgType;
    boolean isLogin =false;

    private static final int REQUEST_SHARE_FILE_CODE = 120;
    private PermissionsUtil permissionsUtil;

    @BindView(R.id.btn01)
    Button btn01;
    @Override
    public int getLayoutResID() {
        return R.layout.fragment_home_pre;
    }

    @SuppressLint("WrongConstant")
    @Override
    public void initView(View view) {
        super.initView(view);

        tgc = (String) SPUtils.get(MyApp.getInstance(),"TGC","");
        msgType = (String) SPUtils.get(MyApp.getInstance(),"msgType","");
        iv_qr.setVisibility(View.VISIBLE);
        tvTitle.setText("??????");

        setWeb();
        setGoPushMsg();
        header.setEnableLastTime(false);
        checkNetState();

        String home01 = (String) SPUtils.get(MyApp.getInstance(), "home01", "");
        if(home01.equals("")){
            setGuideView();
        }
        //PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE

        btn01.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationManager manager = (NotificationManager) getActivity().getSystemService(NOTIFICATION_SERVICE);

                Notification notification =new NotificationCompat.Builder(getActivity(),"chat")

                        .setContentTitle("????????????????????????")

                        .setContentText("????????????????????????")

                        .setWhen(System.currentTimeMillis())

                        .setSmallIcon(R.drawable.icon)

                        .setDefaults(Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE)
                        //.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.icon))

                        .setAutoCancel(true)

                        .build();

                manager.notify(1, notification);
            }
        });
    }




    private void setGuideView() {
        CircleShape circleShape = new CircleShape(10);
        circleShape.setPaint(LighterHelper.getDashPaint()); //set custom paint
        // ????????????
        Lighter.with(getActivity())
                .setBackgroundColor(0xB9000000)
                .setOnLighterListener(new OnLighterListener() {
                    @Override
                    public void onShow(int index) {


                    }

                    @Override
                    public void onDismiss() {
                        SPUtils.put(MyApp.getInstance(),"home01","1");




                        CircleShape circleShape = new CircleShape(10);
                        circleShape.setPaint(LighterHelper.getDashPaint()); //set custom paint
                        // ????????????
                        Lighter.with(getActivity())
                                .setBackgroundColor(0xB9000000)
                                .setOnLighterListener(new OnLighterListener() {
                                    @Override
                                    public void onShow(int index) {


                                    }

                                    @Override
                                    public void onDismiss() {
                                        SPUtils.put(MyApp.getInstance(),"home06","1");
                                    }
                                })
                                .addHighlight(new LighterParameter.Builder()
                                        .setHighlightedViewId(R.id.rb_my)
                                        .setTipLayoutId(R.layout.fragment_home_gl_new)
                                        .setTipViewRelativeDirection(Direction.TOP)
                                        .build()).show();



                    }
                })
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(iv_qr)
                        .setTipLayoutId(R.layout.fragment_home_gl)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(150, 0, 30, 0))
                        .build()).show();



    }

    private void checkNetState() {
        if (!NetState.isConnect(getActivity()) ){
            ToastUtils.showToast(getActivity(),"??????????????????!");

        }else {


        }
    }

    /**
     * ??????????????????
     * msgType ????????????(-1:?????????0:????????????,1:??????,2:??????,3:??????,4:??????,5:????????????)
     *
     * */
    private void setGoPushMsg() {
        String messageSign = (String) SPUtils.get(getActivity(), "messageSign", "");
        Log.e("homeFt-messageSign",messageSign);
        Log.e("homeFt-msgType",msgType);
        if (!StringUtils.isEmpty(msgType)){
            Intent intent;
            if (msgType.equals("0")){
                intent = new Intent(MyApp.getInstance(), SystemMsgActivity.class);
                intent.putExtra("msgType","????????????");
                startActivity(intent);
            }else if (msgType.equals("1")){
                intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                if(messageSign.equals("2")){
                    intent.putExtra("type","1");
                }else {
                    intent.putExtra("type","db");
                }
                intent.putExtra("msgType","????????????");
                startActivity(intent);
            }else if (msgType.equals("2")){
                intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                if(messageSign.equals("2")){
                    intent.putExtra("type","2");
                }else {
                    intent.putExtra("type","dy");
                }
                intent.putExtra("msgType","????????????");
                startActivity(intent);
            }else if (msgType.equals("3")){
                intent = new Intent(MyApp.getInstance(), OaMsgYBActivity.class);
                if(messageSign.equals("2")){
                    intent.putExtra("type","3");
                }else {
                    intent.putExtra("type","yb");
                }
                intent.putExtra("msgType","????????????");
                startActivity(intent);
            }else if (msgType.equals("4")){
                intent = new Intent(MyApp.getInstance(), OaMsgYBActivity.class);
                if(messageSign.equals("2")){
                    intent.putExtra("type","4");
                }else {
                    intent.putExtra("type","yy");
                }
                intent.putExtra("msgType","????????????");
                startActivity(intent);
            }else if (msgType.equals("5")){
                intent = new Intent(MyApp.getInstance(), OaMsgYBActivity.class);
                if(messageSign.equals("2")){
                    intent.putExtra("type","5");
                }else {
                    intent.putExtra("type","sq");
                }
                intent.putExtra("msgType","????????????");
                startActivity(intent);
            }else if (msgType.equals("6")){
                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                if(isOpen.equals("") || isOpen.equals("1")){
                    intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                    intent.putExtra("appUrl","http://kys.zzuli.edu.cn/cas/login?service=https://mail.zzuli.edu.cn/coremail/cmcu_addon/sso.jsp?face=hxphone");
                    startActivity(intent);
                }else {
                    intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                    intent.putExtra("appUrl","http://kys.zzuli.edu.cn/cas/login?service=https://mail.zzuli.edu.cn/coremail/cmcu_addon/sso.jsp?face=hxphone");
                    startActivity(intent);
                }

            }
            SPUtils.remove(MyApp.getInstance(),"msgType");
        }
    }

    @Override
    public void initListener() {
        super.initListener();
        mSwipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                if (!NetState.isConnect(getActivity()) ){
                    ToastUtils.showToast(getActivity(),"??????????????????!");
                    refreshlayout.finishRefresh();
                }else {
                    mAgentWeb.getWebCreator().getWebView().reload();
                    refreshlayout.finishRefresh();
                    setWeb();
                }

            }
        });

    }


    @SuppressLint("WrongConstant")
    private void setWeb() {
        layoutMsg.setVisibility(View.GONE);
        ivBack.setVisibility(View.GONE);
        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(mSwipeRefreshLayout, new SwipeRefreshLayout.LayoutParams(-1,-1))
                .useDefaultIndicator(getResources().getColor(R.color.title_bar_bg),3)//?????????????????????????????????-1????????????????????????2????????????dp???
                .setAgentWebWebSettings(getSettings())
                .setWebViewClient(mWebViewClient)//WebViewClient ??? ??? WebView ???????????? ?????????????????????WebView??????setWebViewClient(xx)?????????,?????????AgentWeb DefaultWebClient,???????????????????????????????????????
                .setWebChromeClient(mWebChromeClient)
                .setMainFrameErrorView(R.layout.layout_no_net, 0)
                .setPermissionInterceptor(mPermissionInterceptor) //???????????? 2.0.0 ?????????
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)//???????????????????????????????????????????????????????????? AgentWeb 3.0.0 ?????????
                .setWebLayout(new WebLayout4(getActivity()))
                .interceptUnkownUrl() //??????????????????????????????Url AgentWeb 3.0.0 ?????????
                .createAgentWeb()//??????AgentWeb???
                .ready()//?????? WebSettings???
                .go("http://www.zzuli.edu.cn/_t9/main.htm");


        mAgentWeb.getAgentWebSettings().getWebSettings().setUseWideViewPort(true);
        mAgentWeb.getWebCreator().getWebView().setOverScrollMode(WebView.OVER_SCROLL_NEVER);

    }

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

            //imgReset(view);
            CookieManager cookieManager = CookieManager.getInstance();
            if(Build.VERSION.SDK_INT>=21){
                cookieManager.setAcceptThirdPartyCookies(view, true);
            }

        }



        /**????????????*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url =  null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
            }

            return super.shouldOverrideUrlLoading(view, request);
        }

        /**????????????*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            Log.e("homeUrl",url);
            if (!url.equals("http://www.zzuli.edu.cn/_t9/main.htm")){
                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                if(isOpen.equals("") || isOpen.equals("1")){
                    Intent intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                    intent.putExtra("appUrl",url);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                    intent.putExtra("appUrl",url);
                    startActivity(intent);
                }


                return true;
            }
            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.e("????????????",url);
        }
    };

    //????????????????????????img?????????img?????????100%,??????????????????????????????
    private void imgReset(WebView view) {
        if(view!=null){
            view.loadUrl("javascript:(function(){" +
                    "var objs = document.getElementsByTagName('img'); " +
                    "for(var i=0;i<objs.length;i++)  " +
                    "{"
                    + "var img = objs[i];   " +
                    " img.style.maxWidth = '100%';img.style.height='auto';" +
                    "}" +
                    "})()");
        }}

    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            //   do you work
            Log.i("Info", "onProgress:" + newProgress);
        }

        /*Title*/
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//            if (mTitleTextView != null) {
//                mTitleTextView.setText(title);
//            }
        }

    };
    /**
     * @return IAgentWebSettings
     */
    public IAgentWebSettings getSettings()  {
        return new AbsAgentWebSettings() {
            private AgentWeb mAgentWeb;
            private WebSettings mWebSettings;
            @Override
            protected void bindAgentWebSupport(AgentWeb agentWeb) {
                this.mAgentWeb = agentWeb;
            }

            @Override
            public IAgentWebSettings toSetting(WebView webView) {

                return super.toSetting(webView);
            }

            @Override
            public WebListenerManager setDownloader(WebView webView, android.webkit.DownloadListener downloadListener) {
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

    protected DownloadListenerAdapter mDownloadListenerAdapter = new DownloadListenerAdapter() {

        @Override
        public boolean onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, AgentWebDownloader.Extra extra) {
            extra.setOpenBreakPointDownload(true) // ????????????????????????
                    .setIcon(R.drawable.ic_file_download_black_24dp) //???????????????icon
                    .setConnectTimeOut(6000) // ??????????????????
                    .setBlockMaxTime(10 * 60 * 1000)  // ???8KB??????????????????60s ?????????60s??????????????????????????????8KB????????????????????????
                    .setDownloadTimeOut(Long.MAX_VALUE) // ??????????????????
                    .setParallelDownload(false)  // ??????????????????????????????
                    .setEnableIndicator(true)  // false ??????????????????
                    //.addHeader("Cookie", "xx") // ??????????????????
                    //  .setAutoOpen(false) // ????????????????????????
                    .setForceDownload(true); // ???????????????????????????????????????
            return false;
        }

        @Override
        public void onProgress(String url, long loaded, long length, long usedTime) {
            int mProgress = (int) ((loaded) / Float.valueOf(length) * 100);
            Log.i("????????????", "onProgress:" + mProgress);
            super.onProgress(url, loaded, length, usedTime);
        }

        @Override
        public boolean onResult(String path, String url, Throwable throwable) {
            if (null == throwable) { //????????????

                Uri shareFileUrl = FileUtil.getFileUri(getActivity(),null,new File(path));
                new Share2.Builder(getActivity())
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(shareFileUrl)
                        .setTitle("Share File")
                        .setOnActivityResult(REQUEST_SHARE_FILE_CODE)
                        .build()
                        .shareBySystem();

            } else {//????????????
                Uri shareFileUrl = FileUtil.getFileUri(getActivity(),null,new File(path));
                new Share2.Builder(getActivity())
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


    @OnClick({R.id.msg_num, R.id.layout_msg,R.id.iv_qr})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.msg_num:

                break;
            case R.id.iv_qr:

                //cameraTask();
                permissionsUtil=  PermissionsUtil
                        .with(this)
                        .requestCode(1)
                        .isDebug(true)//??????log
                        .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                        .request();

                break;
            case R.id.layout_msg:

                break;
        }
    }

    @Override
    public void onResume() {
        if (!NetState.isConnect(getActivity())) {
            ToastUtils.showToast(getActivity(),"??????????????????!");
        }
        if(mAgentWeb != null){
            mAgentWeb.getWebLifeCycle().onResume();//??????
        }

        super.onResume();
    }
    @Override
    public void onPause() {

        if(mAgentWeb != null){
            mAgentWeb.getWebLifeCycle().onPause(); //?????????????????????WebView ??? ??????mWebView.resumeTimers();/mAgentWeb.getWebLifeCycle().onResume(); ?????????
        }

        super.onPause();
    }
    @Override
    public void onDestroyView() {
        if(mAgentWeb != null){
            mAgentWeb.getWebLifeCycle().onDestroy();
        }

        super.onDestroyView();
    }

    /**
     * ???????????????????????????
     *
     * @param
     */
    public void onScanQR() {
        isLogin = !StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""));

        QRCodeManager.getInstance()
                .with(getActivity())
                .setReqeustType(0)
                .setRequestCode(55846)
                .scanningQRCode(new OnQRCodeListener() {
                    @Override
                    public void onCompleted(String result) {
                        Log.e("QRCodeManager = ",result);
                        if(!isLogin){
                            Intent intent = new Intent(MyApp.getInstance(), LoginActivity2.class);
                            startActivity(intent);
                        }else {
                            String[] split = result.split("_");
                            String s = split[1];
                            if(s.equals("1")){
                                yanqianData(split[0]);

                            }else {
                                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                                if(isOpen.equals("") || isOpen.equals("1")){
                                    Intent intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                                    intent.putExtra("appUrl",result);
                                    intent.putExtra("scan","scan");
                                    startActivity(intent);
                                }else {
                                    Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                                    intent.putExtra("appUrl",result);
                                    intent.putExtra("scan","scan");
                                    startActivity(intent);
                                }
                            }



                        }


                    }

                    @Override
                    public void onError(Throwable errorMsg) {

                    }

                    @Override
                    public void onCancel() {

                    }


                    @Override
                    public void onManual(int requestCode, int resultCode, Intent data) {

                    }
                });
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){

        }else {
            netInsertPortal("1");
        }
    }

    private void yanqianData(String s) {
        String userId = (String) SPUtils.get(MyApp.getInstance(), "userId", "");
        String s3 = s;
        OkGo.<String>post(UrlRes.HOME_URL+caQrCodeVerifyUrl)
                .tag(this)
                .params("userId", (String) SPUtils.get(MyApp.getInstance(), "userId", ""))
                .params("content", s)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("????????????",response.body());
                            CaBean faceBean2 = JsonUtil.parseJson(response.body(),CaBean.class);

                            boolean success = faceBean2.getSuccess();
                            if(success == true){

                                Intent intent = new Intent(getActivity(),CAResultActivity.class);
                                intent.putExtra("result","0");
                                intent.putExtra("certDn",faceBean2.getObj().getCertDn());
                                intent.putExtra("signTime",faceBean2.getObj().getSignTime());
                                startActivity(intent);
                            }else {
                                Intent intent = new Intent(getActivity(),CAResultActivity.class);
                                intent.putExtra("result","1");
                                startActivity(intent);
                            }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("s",response.toString());
                    }
                });

    }

    private void netInsertPortal(final String insertPortalAccessLog) {
        String imei = MobileInfoUtils.getIMEI(getActivity());
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Four_Modules)
                .tag(this)
                .params("portalAccessLogMemberId",(String) SPUtils.get(getInstance(),"userId",""))
                .params("portalAccessLogEquipmentId",(String) SPUtils.get(getInstance(),"imei",""))//??????ID
                .params("portalAccessLogTarget", insertPortalAccessLog)//????????????
                .params("portalAccessLogVersionNumber", (String) SPUtils.get(getActivity(),"versionName", ""))//?????????
                .params("portalAccessLogOperatingSystem", "ANDROID")//?????????
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("sdsaas",response.body());
                        //getMyLocation();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //????????????onRequestPermissionsResult
        permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //???????????????????????????????????????????????????
        permissionsUtil.onActivityResult(requestCode, resultCode, intent);

        //??????onActivityResult
        if (requestCode == 55846){
            QRCodeManager.getInstance().with(getActivity()).onActivityResult(requestCode, resultCode, intent);
        }

    }

    private static final int RC_CAMERA_PERM = 123;


    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
        Log.e("????????????","????????????");

        //onScanQR();
        Intent i = new Intent(getActivity(),CaptureActivity.class);
        startActivity(i);

    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {
        Log.e("????????????","????????????");
    }
}
