package io.cordova.zhqy.fragment.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.RelativeLayout;
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
import com.just.agentweb.download.DownloadingService;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.activity.LoginActivity2;
import io.cordova.zhqy.activity.MyToDoMsgActivity;
import io.cordova.zhqy.bean.CountBean;
import io.cordova.zhqy.bean.NoticeInfoBean;
import io.cordova.zhqy.utils.BadgeView;
import io.cordova.zhqy.utils.BaseFragment;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.LighterHelper;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.NetState;
import io.cordova.zhqy.web.BaseWebActivity4;

import io.cordova.zhqy.web.BaseWebCloseActivity;
import io.cordova.zhqy.web.FileUtil;
import io.cordova.zhqy.web.WebLayout4;
import me.samlss.lighter.Lighter;
import me.samlss.lighter.interfaces.OnLighterListener;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.CircleShape;

import static io.cordova.zhqy.UrlRes.findLoginTypeListUrl;
import static io.cordova.zhqy.utils.MyApp.getInstance;

/**
 * Created by Administrator on 2018/11/19 0019.
 */

public class FindPreFragment extends BaseFragment {



    @BindView(R.id.swipeLayout)
    SmartRefreshLayout mSwipeRefreshLayout;

    @BindView(R.id.header)
    ClassicsHeader header;

    @BindView(R.id.rl_msg_app1)
    RelativeLayout rl_msg_app;
    protected AgentWeb mAgentWeb;

    private Gson mGson = new Gson();
    /**??????*/
    private static final int REQUEST_SHARE_FILE_CODE = 120;
    private BadgeView badge1;
    boolean isLogin = false;
    @Override
    public int getLayoutResID() {
        return R.layout.fragment_second_pre;
    }
    String count;
    @Override
    public void initView(View view) {
        super.initView(view);
        isLogin = !StringUtils.isEmpty((String) SPUtils.get(MyApp.getInstance(),"username",""));

        setWeb();
        rl_msg_app.setVisibility(View.VISIBLE);
        count = (String) SPUtils.get(getActivity(), "count", "");
        Log.e("Find_count",count+"");
        badge1 = new BadgeView(getActivity(), rl_msg_app);
        remind();
        if (!isLogin){
            badge1.hide();
        }else {
            if(count != null){
                if(count.equals("0")){

                    badge1.hide();
                    getBack();
                }else {
                    badge1.show();


                   getBack();
                }
            }else {
                badge1.hide();
            }



        }



        header.setEnableLastTime(false);
        rl_msg_app.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isLogin = !StringUtils.isEmpty((String) SPUtils.get(MyApp.getInstance(),"username",""));
                if (isLogin){
                    Intent intent = new Intent(MyApp.getInstance(), MyToDoMsgActivity.class);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MyApp.getInstance(), LoginActivity2.class);
                    startActivity(intent);
                }
            }
        });

        String home02 = (String) SPUtils.get(MyApp.getInstance(), "home02", "");
        if(home02.equals("")){
            setGuideView();
        }
    }


    private void getBack() {
        OkGo.<String>get(UrlRes.HOME_URL +findLoginTypeListUrl)
                .tag(this)
                .params("type","backLog")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("result_ma0bing",response.body());


                        NoticeInfoBean noticeInfoBean = JsonUtil.parseJson(response.body(),NoticeInfoBean.class);

                        List<NoticeInfoBean.Obj> obj = noticeInfoBean.getObj();
                        if(obj != null){
                            String configValue = obj.get(0).getConfigValue();
                            if(configValue.equals("1")){
                                netWorkSystemMsg();
                            }else{
                                netWorkSystemMsg2();
                            }
                        }else {
                            netWorkSystemMsg2();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        netWorkSystemMsg2();
                    }
                });


    }

    private void setGuideView() {


        CircleShape circleShape = new CircleShape(10);
        circleShape.setPaint(LighterHelper.getDashPaint()); //set custom paint
        // ????????????
        Lighter.with(getActivity()
        )
                .setBackgroundColor(0xB9000000)
                .setOnLighterListener(new OnLighterListener() {
                    @Override
                    public void onShow(int index) {


                    }

                    @Override
                    public void onDismiss() {
                        SPUtils.put(MyApp.getInstance(),"home02","1");
                    }
                })
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedView(rl_msg_app)
                        .setTipLayoutId(R.layout.fragment_home_gl3)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(150, 0, 30, 0))
                        .build()).show();
    }



    private void remind() {
        String count = (String) SPUtils.get(getActivity(), "count", "");
        if(count.equals("")){
            count = "0";
        }
        int i1 = Integer.parseInt(count);
        if(i1>99){
            badge1.setText("99+"); // ???????????????????????????
        }else {
            badge1.setText(count); // ???????????????????????????
        }
       // badge1.setText(count); // ???????????????????????????
        badge1.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);// ???????????????.?????????,BadgeView.POSITION_BOTTOM_LEFT,?????????????????????????????????
        badge1.setTextColor(Color.WHITE); // ????????????
        badge1.setBadgeBackgroundColor(Color.RED); // ??????????????????????????????????????????
        badge1.setTextSize(10); // ????????????
        badge1.setBadgeMargin(3, 3); // ??????????????????????????????
        if(count.equals("0")){
            badge1.hide();
        }else {
            badge1.show();// ????????????

        }

    }



    private void setWeb() {


        mAgentWeb = AgentWeb.with(this)//
                .setAgentWebParent(mSwipeRefreshLayout, new SwipeRefreshLayout.LayoutParams(-1,-1))
                .useDefaultIndicator(getResources().getColor(R.color.title_bar_bg),3)//?????????????????????????????????-1????????????????????????2????????????dp???
                .setAgentWebWebSettings(getSettings())//?????? IAgentWebSettings???
                .setWebViewClient(mWebViewClient)//WebViewClient ??? ??? WebView ???????????? ?????????????????????WebView??????setWebViewClient(xx)?????????,?????????AgentWeb DefaultWebClient,???????????????????????????????????????
                .setWebChromeClient(mWebChromeClient) //WebChromeClient
                //.setMainFrameErrorView(R.layout.layout_no_net, 0)
                .setWebLayout(new WebLayout4(getActivity()))
                .setPermissionInterceptor(mPermissionInterceptor) //???????????? 2.0.0 ?????????
                .setOpenOtherPageWays(DefaultWebClient.OpenOtherPageWays.DISALLOW)//???????????????????????????????????????????????????????????? AgentWeb 3.0.0 ?????????
                //.interceptUnkownUrl() //??????????????????????????????Url AgentWeb 3.0.0 ?????????
                .createAgentWeb()//??????AgentWeb???
                .ready()//?????? WebSettings???
                .go("http://info.zzuli.edu.cn/_t598/main.htm");


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
                    if (mAgentWeb.getWebCreator().getWebView().getScrollY() == 0){
                        refreshlayout.finishRefresh();
                    }else {
                        refreshlayout.finishRefresh();
                    }
                    setWeb();
                }

            }
        });
    }


    private WebViewClient mWebViewClient = new WebViewClient() {

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
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

            if (!url.equals("http://info.zzuli.edu.cn/_t598/main.htm")){
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

            return super.shouldOverrideUrlLoading(view, request);
        }

        /**????????????*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            if (!url.equals("http://info.zzuli.edu.cn/_t598/main.htm")){
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

            if(view == null){
                view.loadUrl(url);
            }

        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }

        /*Title*/
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);

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

        /**
         *
         * ????????????????????????????????????????????????????????????
         * @param url
         * @param downloadingService  ?????????????????? DownloadingService#shutdownNow ????????????
         */
        @Override
        public void onBindService(String url, DownloadingService downloadingService) {
            super.onBindService(url, downloadingService);

        }

        /**
         * ??????onUnbindService??????????????????????????? DownloadingService???
         * @param url
         * @param downloadingService
         */
        @Override
        public void onUnbindService(String url, DownloadingService downloadingService) {
            super.onUnbindService(url, downloadingService);

        }

        /**
         *
         * @param url  ????????????
         * @param loaded  ?????????????????????
         * @param length    ??????????????????
         * @param usedTime   ?????? ?????????ms
         * ????????????????????????????????? ???????????? AsyncTask #XX ?????? AgentWeb # XX
         */
        @Override
        public void onProgress(String url, long loaded, long length, long usedTime) {
            int mProgress = (int) ((loaded) / Float.valueOf(length) * 100);
            Log.i("????????????", "onProgress:" + mProgress);
            super.onProgress(url, loaded, length, usedTime);
        }

        /**
         *
         * @param path ?????????????????????
         * @param url  ????????????
         * @param throwable    ????????????????????????????????????
         * @return true ???????????????????????????????????????????????? ???false ????????????AgentWeb ??????
         */
        @Override
        public boolean onResult(String path, String url, Throwable throwable) {
            if (null == throwable) { //????????????
                //do you work
                Log.e("????????????",path);
                Log.e("????????????",url);

                Uri shareFileUrl = FileUtil.getFileUri(getActivity(),null,new File(path));
                Log.e("path2", String.valueOf(shareFileUrl));
                new Share2.Builder(getActivity())
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(shareFileUrl)
                        .setTitle("Share File")
                        .setOnActivityResult(REQUEST_SHARE_FILE_CODE)
                        .build()
                        .shareBySystem();

            } else {//????????????
                Log.e("path",path);

                Uri shareFileUrl = FileUtil.getFileUri(getActivity(),null,new File(path));
                Log.e("path2", String.valueOf(shareFileUrl));
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
            Log.i("???????????????????????? ", "mUrl:" + url + "  permission:" + mGson.toJson(permissions) + " action:" + action);
            return false;
        }
    };


    @Override
    public void onResume() {
        mAgentWeb.getWebLifeCycle().onResume();
        super.onResume();
    }

    @Override
    public void onPause() {

        mAgentWeb.getWebLifeCycle().onPause();
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        mAgentWeb.getWebLifeCycle().onDestroy();
        super.onDestroyView();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {   // ????????????????????? ??????????????????onPause();


        }else{  // ?????????????????? ??????????????????onResume();

            netInsertPortal("2");
            isLogin = !StringUtils.isEmpty((String) SPUtils.get(MyApp.getInstance(),"username",""));
            if (!isLogin){
                badge1.hide();
            }else {
                //netWorkSystemMsg();
                getBack();
            }


        }
    }
    CountBean countBean1;
    private void netWorkSystemMsg2() {

        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_countUnreadMessagesForCurrentUser)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("??????????????????",response.body());

                        countBean1 = JSON.parseObject(response.body(), CountBean.class);
                        netWorkOAToDoMsg2();//OA??????

                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
    CountBean countBean2;
    /**OA????????????*/
    private void netWorkOAToDoMsg2() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.countUnreadMessagesForCurrentUserUrl)
                .tag(this)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "1")//(1:??????,2:??????,3:??????,4:??????,5:??????)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean2 = JSON.parseObject(response.body(), CountBean.class);
                        netWorkEmailMsg2();

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
    CountBean countBeanEmail;
    private void netWorkEmailMsg2() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_emai_count)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("??????????????????",response.body());
                        countBeanEmail = JSON.parseObject(response.body(), CountBean.class);
                        netWorkDyMsg2();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }


    CountBean countBean3;
    private void netWorkDyMsg2() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.countUnreadMessagesForCurrentUserUrl)
                .tag(this)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "2")//(1:??????,2:??????,3:??????,4:??????,5:??????)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean3 = JSON.parseObject(response.body(), CountBean.class);

                        int i = Integer.parseInt(countBean1.getObj());//??????????????????
                        int count1 = Integer.parseInt(countBean2.getObj());//??????????????????
                        int i1 = Integer.parseInt(countBean3.getObj());//??????????????????
                        int count22 = countBeanEmail.getCount();//????????????????????????
                        String s = Integer.parseInt(countBean2.getObj()) + Integer.parseInt(countBean1.getObj()) + Integer.parseInt(countBean3.getObj())+countBeanEmail.getCount() + "";

                        Log.e("dsadasdsa",s);
                        if(null == s){
                            s = "0";
                        }
                        SPUtils.put(MyApp.getInstance(),"count",s+"");

                        count = (String) SPUtils.get(getActivity(), "count", "");
                        if(!count.equals("") && !"0".equals(count)){
                            remind();
                            SPUtils.get(getActivity(),"count","");
                        }else {
                            badge1.hide();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }


    private void netWorkSystemMsg() {

        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_countUnreadMessagesForCurrentUser)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean1 = JSON.parseObject(response.body(), CountBean.class);
                        netWorkOAToDoMsg();//OA?????????

                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    /**OA??????????????????*/
    private void netWorkOAToDoMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_count)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "db")
                .params("workType", "workdb")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean2 = JSON.parseObject(response.body(), CountBean.class);
                        netWorkEmailMsg();

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    private void netWorkEmailMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_emai_count)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("???????????????????????????",response.body());
                        countBeanEmail = JSON.parseObject(response.body(), CountBean.class);
                        netWorkDyMsg();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }



    private void netWorkDyMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_count)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "dy")
                .params("workType", "workdb")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean3 = JSON.parseObject(response.body(), CountBean.class);

                        //tvMyToDoMsgNum.setText(countBean2.getCount()+Integer.parseInt(countBean1.getObj())+countBean3.getCount()+"");
                        String s = countBean2.getCount() + Integer.parseInt(countBean1.getObj()) + countBean3.getCount()+countBeanEmail.getCount() + "";

                        if(null == s){
                            s = "0";
                        }
                        SPUtils.put(MyApp.getInstance(),"count",s+"");
                        String count = (String) SPUtils.get(MyApp.getInstance(), "count", "");
                       /* if (Build.MANUFACTURER.equalsIgnoreCase("huaWei")) {

                            addHuaWeiCut(count);

                        }else if(Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")){
                            xiaoMiShortCut(count);
                        }else if (Build.MANUFACTURER.equalsIgnoreCase("vivo")) {
                            vivoShortCut(count);
                        }*/

                        remind();
                        if(s.equals("0")){

                            badge1.hide();
                        }else {
                            badge1.show();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    private void netInsertPortal(final String insertPortalAccessLog) {
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

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

}
