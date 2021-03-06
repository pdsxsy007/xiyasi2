package io.cordova.zhqy.fragment.home;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.activity.AppSearchActivity;
import io.cordova.zhqy.activity.LoginActivity2;
import io.cordova.zhqy.bean.MyCollectionBean;
import io.cordova.zhqy.bean.ServiceAppListBean;
import io.cordova.zhqy.bean.UserMsgBean;
import io.cordova.zhqy.db.MyDatabaseHelper;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseFragment;
import io.cordova.zhqy.utils.CookieUtils;
import io.cordova.zhqy.utils.DargeFaceUtils;
import io.cordova.zhqy.utils.LighterHelper;
import io.cordova.zhqy.utils.MobileInfoUtils;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.utils.NetState;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;
import me.samlss.lighter.Lighter;
import me.samlss.lighter.interfaces.OnLighterListener;
import me.samlss.lighter.parameter.Direction;
import me.samlss.lighter.parameter.LighterParameter;
import me.samlss.lighter.parameter.MarginOffset;
import me.samlss.lighter.shape.CircleShape;

import static io.cordova.zhqy.utils.MyApp.getInstance;

/**
 * Created by Administrator on 2018/11/19 0019.
 */

public class ServicePreFragment extends BaseFragment implements PermissionsUtil.IPermissionsCallback {
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_searcch)
    ImageView ivSearch;
    @BindView(R.id.rv_content)
    RecyclerView rvConent;
    @BindView(R.id.tablayout)
    TabLayout tablayout;

    @BindView(R.id.rl_no)
    RelativeLayout rl_no;

    @BindView(R.id.rl_load)
    RelativeLayout rl_load;
    @BindView(R.id.webView)
    WebView webView;
    //???????????????recyclerView????????????????????????true- ??????false- ?????????tablayout?????????
    private boolean isRecyclerScroll;
    private LinearLayoutManager manager;
    //????????????????????????????????????????????????????????? ???????????????tablayout
    private int lastPos;
    //??????recyclerView????????????????????????
    private boolean canScroll;
    private int scrollToPosition;
    boolean isLogin =false;


    private int flag = 0;

    private PermissionsUtil permissionsUtil;

    MyDatabaseHelper databaseHelper;

    @Override
    public int getLayoutResID() {
        return R.layout.fragment_service_pre;
    }

    @Override
    public void initView(View view) {
        super.initView(view);
        tvTitle.setText("????????????");
        ivBack.setVisibility(View.GONE);
        ivSearch.setVisibility(View.VISIBLE);
        isLogin = !StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""));
        databaseHelper = new MyDatabaseHelper(getActivity(),"serviceInfo.db",null,1);

        checkNetState();
        rl_load.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkNetState();
            }
        });
        flag = 0;
        String home03 = (String) SPUtils.get(MyApp.getInstance(), "home03", "");
        if(home03.equals("")){
            setGuideView();
        }
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
                        SPUtils.put(MyApp.getInstance(),"home03","1");
                    }
                })
                .addHighlight(new LighterParameter.Builder()
                        .setHighlightedViewId(R.id.msg_hint)
                        .setTipLayoutId(R.layout.fragment_home_gl4)
                        .setTipViewRelativeDirection(Direction.BOTTOM)
                        .setTipViewRelativeOffset(new MarginOffset(150, 0, 30, 0))
                        .build()).show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void checkNetState() {
        if (!NetState.isConnect(getActivity()) ){
            ToastUtils.showToast(getActivity(),"??????????????????!");
            SQLiteDatabase db = databaseHelper.getReadableDatabase();
            String personName = (String) SPUtils.get(MyApp.getInstance(), "personName", "");
            Cursor cursor = db.rawQuery("select * from serviceInfo where userId = ?", new String[]{personName});
            if(cursor.moveToNext()) {

                collectContentSelect = cursor.getString(cursor.getColumnIndex("collectContent"));

                contentSelect = cursor.getString(cursor.getColumnIndex("content"));
                String userId = cursor.getString(cursor.getColumnIndex("userId"));
                serviceAppListBean1 = JSON.parseObject(collectContentSelect, ServiceAppListBean.class);
                obj1 = serviceAppListBean1.getObj();
                serviceAppListBean = JSON.parseObject(contentSelect, ServiceAppListBean.class);
                List<ServiceAppListBean.ObjBean> obj = serviceAppListBean.getObj();
                obj1.addAll(obj);
                setTap();
                setRvServiceList();
                getMyCollectDatas();
            }else {
                rl_no.setVisibility(View.VISIBLE);
            }

        }else {
            registerBoradcastReceiver();
            registerBoradcastReceiver2();
            registerBoradcastReceiver3();
            registerBoradcastReceiver4();
            rl_no.setVisibility(View.GONE);
            initShowPage();
        }
    }

    private void registerBoradcastReceiver4() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("facereYiQingClose01");
        //????????????
        getActivity().registerReceiver(broadcastReceiverClose01, myIntentFilter);
    }

    private void registerBoradcastReceiver3() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("faceService");
        //????????????
        getActivity().registerReceiver(broadcastReceiverFace, myIntentFilter);
    }

    private void registerBoradcastReceiver2() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("refresh2");
        //????????????
        getActivity().registerReceiver(broadcastReceiver, myIntentFilter);
    }


    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("refresh");
        //????????????
        getActivity().registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("refresh")){
                isLogin = false;
                flag = 1;
                initShowPage();
            }else if(action.equals("refresh2")){
                isLogin = true;
                flag = 1;
                initShowPage();
            }
        }
    };

    String collectContentSelect;//????????????????????????
    String contentSelect;//??????????????????????????????
    @TargetApi(Build.VERSION_CODES.M)
    private void initShowPage() {
        Log.e("initShowPage","initShowPage");
        if (isLogin){
            if(obj1 != null){
                obj1.clear();
            }
            if (!StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"rolecodes",""))){

                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                String personName = (String) SPUtils.get(MyApp.getInstance(), "personName", "");
                Cursor cursor = db.rawQuery("select * from serviceInfo where userId = ?", new String[]{personName});
                if (cursor.moveToNext()){

                    collectContentSelect = cursor.getString(cursor.getColumnIndex("collectContent"));

                    contentSelect = cursor.getString(cursor.getColumnIndex("content"));
                    String userId = cursor.getString(cursor.getColumnIndex("userId"));
                    serviceAppListBean1 = JSON.parseObject(collectContentSelect, ServiceAppListBean.class);
                    obj1 = serviceAppListBean1.getObj();
                    serviceAppListBean = JSON.parseObject(contentSelect, ServiceAppListBean.class);
                    List<ServiceAppListBean.ObjBean> obj = serviceAppListBean.getObj();
                    obj1.addAll(obj);
                    setTap();
                    setRvServiceList();
                    getMyCollectDatas();
                   /* Log.e("collectContentSelect",collectContentSelect);
                    Log.e("contentSelect",contentSelect);
                    getMyCollectDatas();*/
                }else {
                    getMyCollectDatas();
                }



            }else {
                if(obj1 != null){
                    obj1.clear();
                }
                netWorkUserMsg();
            }
        }else {
            if(obj1 != null){
                obj1.clear();
            }
//            ????????????app??????
            netWorkServiceAPPListYou();
        }
    }

    String collectContent;

    ServiceAppListBean serviceAppListBean1;
    private void getMyCollectDatas() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.My_Collection)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("????????????",response.toString());
                        serviceAppListBean1 = new ServiceAppListBean();

                        MyCollectionBean collectionBean = JSON.parseObject(response.body(), MyCollectionBean.class);
                        if (collectionBean.isSuccess()) {
                            if(collectionBean.getObj() != null){
                                if (collectionBean.getObj().size() > 0) {
                                    List<MyCollectionBean.ObjBean> obj = collectionBean.getObj();
                                    List<ServiceAppListBean.ObjBean.AppsBean> listsApp = new ArrayList<>();
                                    for (int i = 0; i < obj.size(); i++) {
                                        ServiceAppListBean.ObjBean.AppsBean appsBean = new ServiceAppListBean.ObjBean.AppsBean();
                                        int appLoginFlag = obj.get(i).getAppLoginFlag();
                                        int appId = obj.get(i).getAppId();
                                        String appAndroidSchema = obj.get(i).getAppAndroidSchema();
                                        String appName = obj.get(i).getAppName();
                                        String appUrl = obj.get(i).getAppUrl();
                                        String appImages = obj.get(i).getAppImages();
                                        int appIntranet = obj.get(i).getAppIntranet();
                                        Object appSecret = obj.get(i).getAppSecret();
                                        MyCollectionBean.ObjBean.PortalAppIconBean portalAppIcon = obj.get(i).getPortalAppIcon();
                                        if(appAndroidSchema != null){
                                            appsBean.setAppAndroidSchema(appAndroidSchema);
                                        }
                                        appsBean.setAppLoginFlag(appLoginFlag);
                                        appsBean.setAppId(appId);
                                        appsBean.setAppName(appName);
                                        appsBean.setAppUrl(appUrl);
                                        appsBean.setAppIntranet(appIntranet);
                                        appsBean.setAppSecret(appSecret);
                                        if(appImages != null){
                                            appsBean.setAppImages(appImages);
                                        }
                                        ServiceAppListBean.ObjBean.AppsBean.PortalAppIconBean portalAppIconBean = new ServiceAppListBean.ObjBean.AppsBean.PortalAppIconBean();
                                        if(portalAppIcon != null){
                                            String templetAppImage = obj.get(i).getPortalAppIcon().getTempletAppImage();
                                            if(templetAppImage != null){
                                                portalAppIconBean.setTempletAppImage(templetAppImage);
                                            }
                                        }
                                        appsBean.setPortalAppIcon(portalAppIconBean);
                                        ServiceAppListBean.ObjBean.AppsBean.PortalAppAuthentication portalAppAuthentication = new ServiceAppListBean.ObjBean.AppsBean.PortalAppAuthentication();
                                        ServiceAppListBean.ObjBean.AppsBean.PortalAppAuthentication portalAppAuthentication1 = obj.get(i).getPortalAppAuthentication();
                                        if(portalAppAuthentication1 != null){
                                            String appAuthenticationFace1 = obj.get(i).getPortalAppAuthentication().getAppAuthenticationFace();
                                            if(appAuthenticationFace1 != null){
                                                portalAppAuthentication.setAppAuthenticationFace(appAuthenticationFace1);
                                            }
                                            appsBean.setPortalAppAuthentication(portalAppAuthentication);
                                        }

                                        listsApp.add(appsBean);
                                    }
                                    List<ServiceAppListBean.ObjBean> objBeans = new ArrayList<>();
                                    ServiceAppListBean.ObjBean obj1 = new ServiceAppListBean.ObjBean();
                                    obj1.setModulesName("????????????");
                                    obj1.setApps(listsApp);
                                    objBeans.add(obj1);
                                    serviceAppListBean1.setObj(objBeans);
                                    Gson gson = new Gson();

                                    collectContent = gson.toJson(serviceAppListBean1);
                                    netWorkServiceAPPList();
                                }
                            }else {
                                List<ServiceAppListBean.ObjBean> objBeans = new ArrayList<>();
                                ServiceAppListBean.ObjBean obj1 = new ServiceAppListBean.ObjBean();
                                obj1.setModulesName("????????????");
                                serviceAppListBean1.setObj(objBeans);
                                Gson gson = new Gson();
                                collectContent = gson.toJson(serviceAppListBean1);
                                netWorkServiceAPPList();
                            }

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);


                    }
                });
    }

    private void netWorkServiceAPPListYou() {

        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Service_APP_List)
                .tag(this)
                .params("Version", "1.0")
                .params("rolecodes","tourists")
                .execute(new StringCallback() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("??????????????????",response.body());
                        obj1.clear();
                        serviceAppListBean1 = new ServiceAppListBean();
                        serviceAppListBean = JSON.parseObject(response.body(),ServiceAppListBean.class);
                        if (serviceAppListBean.isSuccess()){
                            obj1.addAll(serviceAppListBean.getObj());
                            setTap();
                            setRvServiceList();

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }


    /**????????????*/
    UserMsgBean userMsgBean;
    private void netWorkUserMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.User_Msg)
                .params("userId", (String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("result1",response.body()+"   --??????");
                        userMsgBean = JSON.parseObject(response.body(), UserMsgBean.class);
                        if (userMsgBean.isSuccess()) {

                            StringBuilder sb = new StringBuilder();
                            if(userMsgBean.getObj() != null){
                                if (userMsgBean.getObj().getModules().getRolecodes().size() > 0){
                                    for (int i = 0; i < userMsgBean.getObj().getModules().getRolecodes().size(); i++) {
                                        sb.append(userMsgBean.getObj().getModules().getRolecodes().get(i).getRoleCode()).append(",");
                                    }
                                }
                                String ss = sb.substring(0, sb.lastIndexOf(","));
                                Log.e("TAG",ss);
                                SPUtils.put(MyApp.getInstance(),"rolecodes",ss);
                                SQLiteDatabase db = databaseHelper.getReadableDatabase();
                                String personName = (String) SPUtils.get(MyApp.getInstance(), "personName", "");
                                Cursor cursor = db.rawQuery("select * from serviceInfo where userId = ?", new String[]{personName});
                                if(cursor.moveToNext()){

                                    collectContentSelect = cursor.getString(cursor.getColumnIndex("collectContent"));

                                    contentSelect = cursor.getString(cursor.getColumnIndex("content"));
                                    String userId = cursor.getString(cursor.getColumnIndex("userId"));
                                    serviceAppListBean1 = JSON.parseObject(collectContentSelect, ServiceAppListBean.class);
                                    obj1 = serviceAppListBean1.getObj();
                                    serviceAppListBean = JSON.parseObject(contentSelect, ServiceAppListBean.class);
                                    List<ServiceAppListBean.ObjBean> obj = serviceAppListBean.getObj();
                                    obj1.addAll(obj);
                                    setTap();
                                    setRvServiceList();
                                    Log.e("collectContentSelect",collectContentSelect);
                                    Log.e("contentSelect",contentSelect);
                                    getMyCollectDatas();
                                }else {
                                    getMyCollectDatas();
                                }

                            }else {

                                netWorkServiceAPPList();
                            }



                        }
                    }
                });

    }

    String content;
    ServiceAppListBean serviceAppListBean;
    List<ServiceAppListBean.ObjBean> obj1 = new ArrayList<>();
    private void netWorkServiceAPPList() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Service_APP_List)
                .params("Version", "1.0")
                .params("userId", (String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("rolecodes", (String) SPUtils.get(MyApp.getInstance(),"rolecodes",""))
                .execute(new StringCallback() {
                    @TargetApi(Build.VERSION_CODES.M)
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("????????????",response.body());
                        serviceAppListBean = JSON.parseObject(response.body(),ServiceAppListBean.class);

                        content = response.body();
                        if (serviceAppListBean.isSuccess()){


                            if(collectContentSelect== null && contentSelect== null){
                                Log.e("??????","??????");
                                SQLiteDatabase db = databaseHelper.getWritableDatabase();
                                ContentValues values = new ContentValues();
                                values.put("collectContent", collectContent);
                                values.put("content", content);
                                values.put("userId", (String) SPUtils.get(MyApp.getInstance(),"personName",""));
                                db.insert("serviceInfo",null,values);
                                obj1 = serviceAppListBean1.getObj();
                                List<ServiceAppListBean.ObjBean> obj = serviceAppListBean.getObj();
                                obj1.addAll(obj);
                                setTap();
                                setRvServiceList();
                            }

                            if(collectContentSelect != null && contentSelect != null){
                                if(collectContentSelect.equals(collectContent) && contentSelect.equals(content)){//?????????
                                    Log.e("??????","?????????");

                                }else {
                                    Log.e("??????","??????");
                                    SQLiteDatabase db = databaseHelper.getWritableDatabase();
                                    ContentValues values = new ContentValues();
                                    values.put("collectContent", collectContent);
                                    values.put("content", content);
                                    values.put("userId", (String) SPUtils.get(MyApp.getInstance(),"personName",""));
                                    //db.execSQL("update serviceInfo set collectContent = '"+collectContent+"',content = '"+content+"' where userId = "+(String) SPUtils.get(MyApp.getInstance(),"personName",""));
                                    db.update("serviceInfo",values,"userId = ?",new String[]{(String) SPUtils.get(getInstance(), "personName", "")});
                                    obj1 = serviceAppListBean1.getObj();
                                    List<ServiceAppListBean.ObjBean> obj = serviceAppListBean.getObj();
                                    obj1.addAll(obj);
                                    setTap();
                                    setRvServiceList();
                                }
                            }





                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

    }
    CommonAdapter<ServiceAppListBean.ObjBean> adapterSysApp;
    CommonAdapter<ServiceAppListBean.ObjBean.AppsBean> adapterApp;
    int lastItemPosition;
    int firstItemPosition;

    private void setRvServiceList() {
        rvConent.setVisibility(View.VISIBLE);
        manager  = new LinearLayoutManager(getActivity());
        rvConent.setLayoutManager(manager);

        try {
            adapterSysApp = new CommonAdapter<ServiceAppListBean.ObjBean>(getActivity(),R.layout.itme_service_app_list,obj1) {
                @Override
                protected void convert(ViewHolder holder, ServiceAppListBean.ObjBean objBean, int position) {

                    holder.setText(R.id.tv_content,objBean.getModulesName());
                    RecyclerView recyclerView = holder.getView(R.id.rv_app_list);
                    recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
                    adapterApp = new CommonAdapter<ServiceAppListBean.ObjBean.AppsBean>(getActivity(), R.layout.item_service_app,objBean.getApps()) {
                        @Override
                        protected void convert(ViewHolder holder, final ServiceAppListBean.ObjBean.AppsBean appsBean, final int position) {
                            holder.setText(R.id.tv_app_name, appsBean.getAppName());

                            if (null != appsBean.getPortalAppIcon() && null != appsBean.getPortalAppIcon().getTempletAppImage()){

                                Glide.with(getActivity())
                                        .load(UrlRes.HOME3_URL + appsBean.getPortalAppIcon().getTempletAppImage())
                                        .error(getResources().getColor(R.color.app_bg))
                                        .placeholder(R.mipmap.zwt)
                                        .into((ImageView) holder.getView(R.id.iv_app_icon));
                            }else {
                                Glide.with(getActivity())
                                        .load(UrlRes.HOME3_URL + appsBean.getAppImages())
                                        .error(getResources().getColor(R.color.app_bg))
                                        .placeholder(R.mipmap.zwt)
                                        //.signature(new StringSignature(UUID.randomUUID().toString()))
                                        .into((ImageView) holder.getView(R.id.iv_app_icon));
                            }

                            /*appIntranet  1 ????????????*/
                            if (appsBean.getAppIntranet()==1){
                                holder.setVisible(R.id.iv_del,true);
                                Glide.with(getActivity())
                                        .load(R.mipmap.nei_icon)
                                        .error(R.mipmap.nei_icon)
                                        .placeholder(R.mipmap.zwt)
                                        .into((ImageView) holder.getView(R.id.iv_del));
                            }else {
                                holder.setVisible(R.id.iv_del,false);
                            }
                            /*appLoginFlag  0 ????????????*/
                            if (!isLogin) {
                                if (appsBean.getAppLoginFlag()==0){
                                    holder.setVisible(R.id.iv_lock_close,true);
                                    Glide.with(getActivity())
                                            .load(R.mipmap.lock_icon)
                                            .error(R.mipmap.lock_icon)
                                            .placeholder(R.mipmap.zwt)
                                            .into((ImageView) holder.getView(R.id.iv_lock_close));
                                }else {
                                    holder.setVisible(R.id.iv_lock_close,false);
                                }
                            }
                            holder.setOnClickListener(R.id.ll_click, new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    long nowTime = System.currentTimeMillis();
                                    if (nowTime - mLastClickTime > TIME_INTERVAL) {
                                        mLastClickTime = nowTime;
                                        if (null != appsBean.getAppAndroidSchema() && appsBean.getAppAndroidSchema().trim().length() != 0){
                                            if (!isLogin){
                                                Intent intent = new Intent(getActivity(),LoginActivity2.class);
                                                startActivity(intent);
                                            }else {

                                                String appUrl =  appsBean.getAppAndroidSchema()+"";
                                                String intercept = appUrl.substring(0,appUrl.indexOf(":")+3);

                                                Log.e("TAG", hasApplication(intercept)+"");
                                                if (hasApplication(intercept)){
                                                    try {
                                                        //????????????Scheme????????????  ????????????
                                                        if (appUrl.contains("{memberid}")){
                                                            String s1=  URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"personName",""), "UTF-8");
                                                            appUrl =  appUrl.replace("{memberid}", s1);
                                                        }
                                                        if (appUrl.contains("{memberAesEncrypt}")){
                                                            String memberAesEncrypt = AesEncryptUtile.encrypt((String) SPUtils.get(MyApp.getInstance(),"personName",""), String.valueOf(appsBean.getAppSecret()));
                                                            String s2=  URLEncoder.encode(memberAesEncrypt, "UTF-8");
                                                            appUrl =  appUrl.replace("{memberAesEncrypt}", s2);
                                                        }
                                                        if (appUrl.contains("{quicklyTicket}")){
                                                            String s3 =  URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"TGC",""), "UTF-8");
                                                            appUrl = appUrl.replace("{quicklyTicket}",s3);
                                                        }
                                                        Log.e("TAG", appUrl+"");
                                                        Uri uri = Uri.parse(appUrl);
                                                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                                                        intent.addCategory(Intent.CATEGORY_DEFAULT);

                                                        startActivity(intent);

                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                }else {
                                                    //?????????????????? ????????????????????????
                                                    if(null!= appsBean.getAppAndroidDownloadLink()){
                                                        String dwon = appsBean.getAppAndroidDownloadLink()+"";
                                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(dwon));
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                                                        startActivity(intent);
                                                    }
                                                }
                                            }


                                        }else if (!appsBean.getAppUrl().isEmpty()){
                                            if (!isLogin){
                                                if(appsBean.getAppLoginFlag()==0){
                                                    Intent intent = new Intent(getActivity(),LoginActivity2.class);
                                                    startActivity(intent);
                                                }else {

                                                    String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                                                    if(isOpen.equals("") || isOpen.equals("1")){
                                                        Intent intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);

                                                        if (NetState.isConnect(getActivity())) {
                                                            netWorkAppClick(appsBean.getAppId());
                                                        }
                                                        Log.e("url  ==",appsBean.getAppUrl() + "");
                                                        intent.putExtra("appUrl",appsBean.getAppUrl());
                                                        intent.putExtra("appId",appsBean.getAppId()+"");
                                                        intent.putExtra("appName",appsBean.getAppName()+"");
                                                        startActivity(intent);
                                                    }else {
                                                        Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);

                                                        if (NetState.isConnect(getActivity())) {
                                                            netWorkAppClick(appsBean.getAppId());
                                                        }
                                                        Log.e("url  ==",appsBean.getAppUrl() + "");
                                                        intent.putExtra("appUrl",appsBean.getAppUrl());
                                                        intent.putExtra("appId",appsBean.getAppId()+"");
                                                        intent.putExtra("appName",appsBean.getAppName()+"");
                                                        startActivity(intent);
                                                    }


                                                }

                                            }else {
                                                ServiceAppListBean.ObjBean.AppsBean.PortalAppAuthentication portalAppAuthentication = appsBean.getPortalAppAuthentication();
                                                if(portalAppAuthentication != null){
                                                    String appAuthenticationFace = appsBean.getPortalAppAuthentication().getAppAuthenticationFace();
                                                    if(appAuthenticationFace != null ){
                                                        if(!appAuthenticationFace.equals("0")){
                                                            permissionsUtil=  PermissionsUtil
                                                                    .with(ServicePreFragment.this)
                                                                    .requestCode(1)
                                                                    .isDebug(true)//??????log
                                                                    .permissions(PermissionsUtil.Permission.Camera.CAMERA,PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                                                                    .request();

                                                            if(isOpen == 1){
                                                                DargeFaceUtils.cameraTask(appsBean,getActivity());
                                                            }
                                                        }else {
                                                            DargeFaceUtils.cameraTask(appsBean,getActivity());
                                                        }

                                                    }else {
                                                        DargeFaceUtils.cameraTask(appsBean,getActivity());
                                                    }
                                                }else {
                                                    DargeFaceUtils.cameraTask(appsBean,getActivity());
                                                }


                                            }


                                        }
                                    }
                                }

                            });
                        }
                    };
                    recyclerView.setAdapter(adapterApp);
                    adapterApp.notifyDataSetChanged();

                }
            };

        }catch (Exception e){

        }

        rvConent.setAdapter(adapterSysApp);

        manager.scrollToPositionWithOffset(firstItemPosition, 0);


        rvConent.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                if (layoutManager instanceof LinearLayoutManager) {
                    LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                    //????????????????????????view?????????

                    lastItemPosition = linearManager.findLastVisibleItemPosition();
                    //?????????????????????view?????????

                    firstItemPosition = linearManager.findFirstVisibleItemPosition();

                }
            }
        });

    }
    private int imageid = 0;

    private BroadcastReceiver broadcastReceiverFace = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("faceService")){
                String FaceActivity = intent.getStringExtra("FaceActivity");
                if(imageid == 0){
                    if(FaceActivity != null){
                        imageid = 1;

                        DargeFaceUtils.jargeFaceResult(getActivity());
                        imageid = 0;
                    }else {
                        imageid = 0;
                    }
                }

            }
        }
    };

    private BroadcastReceiver broadcastReceiverClose01 = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("facereYiQingClose01")){

                SPUtils.put(getActivity(),"isloading2","");
            }
        }
    };

    @Override
    public void onPause() {
        super.onPause();
        SPUtils.put(getActivity(),"isloading2","");
    }

    /**
     * ???????????????????????????
     * @return true ???????????????
     */
    private boolean hasApplication(String scheme) {
        PackageManager manager = getActivity().getPackageManager();
        Intent action = new Intent(Intent.ACTION_VIEW);
        action.setData(Uri.parse(scheme));
        List list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
        return list != null && list.size() > 0;
    }

    /**
     * ?????????????????????????????????
     * @param appId
     *
     * */
    private void netWorkAppClick(int appId) {
        OkGo.<String>get(UrlRes.HOME_URL +UrlRes.APP_Click_Number)

                .params("appId",appId)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))

                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("result1",response.body());
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("??????",response.body());
                    }
                });
    }

    /**??????Tab  ????????????
     * @param
     * */

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setTap() {

        if (tablayout.getTabCount() > 0) {
            tablayout.removeAllTabs();
        }

        for (int i = 0; i < obj1.size(); i++) {
            tablayout.addTab(tablayout.newTab().setText(obj1.get(i).getModulesName()));
        }
        //???????????????
        LinearLayout linearLayout = (LinearLayout) tablayout.getChildAt(0);
        if(linearLayout != null){
            linearLayout.setShowDividers(LinearLayout.SHOW_DIVIDER_MIDDLE);
            try {
                Drawable drawable = ContextCompat.getDrawable(getActivity(), R.drawable.divider);
                if(drawable != null){
                    linearLayout.setDividerDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.divider)); //????????????????????????
                }

                linearLayout.setDividerPadding(dip2px(10)); //?????????????????????
            }catch (Exception e){
                e.getMessage();
            }

        }



        rvConent.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //????????????recyclerView????????????isRecyclerScroll ???true
                if (event.getAction() == MotionEvent.ACTION_MOVE) {
                    isRecyclerScroll = true;
                }
                return false;
            }
        });

        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int pos = tab.getPosition();
                isRecyclerScroll = false;
                moveToPosition(manager, rvConent, pos);
                tablayout.setScrollPosition(pos,0,true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });


        rvConent.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                isRecyclerScroll =false;
                if (canScroll) {
                    canScroll = false;
                    moveToPosition(manager, recyclerView, scrollToPosition);

                }

            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                isRecyclerScroll = true;
                if (isRecyclerScroll) {
                    int position = manager.findFirstVisibleItemPosition();

                    tablayout.setScrollPosition(position, 0, true);
                    if (lastPos != position) {
                        tablayout.setScrollPosition(position, 0, true);
                    }
                    lastPos = position;
                }else {
                    tablayout.setScrollPosition(firstItemPosition, 0, true);

                }
            }
        });

    }
    //??????????????????
    public int dip2px(int dip) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dip * density + 0.5);
    }


    @OnClick(R.id.iv_searcch)
    public void onViewClicked() {
        //?????????????????????
        Intent intent = new Intent(MyApp.getInstance(), AppSearchActivity.class);
        startActivity(intent);
    }

    public void moveToPosition(LinearLayoutManager manager, RecyclerView mRecyclerView, int position) {
        // ??????????????????view?????????
        int firstItem = manager.findFirstVisibleItemPosition();
        // ?????????????????????view?????????
        int lastItem = manager.findLastVisibleItemPosition();
        if (position <= firstItem) {
            // ??????????????????firstItem ??????(?????????????????????)??????smoothScrollToPosition?????????????????????
            mRecyclerView.smoothScrollToPosition(position);
        } else if (position <= lastItem) {
            // ???????????????firstItem ?????????lastItem ????????????????????????????????????smoothScrollBy????????????????????????
            int top = mRecyclerView.getChildAt(position - firstItem).getTop();
            mRecyclerView.smoothScrollBy(0, top);
        } else {
            // ???????????????????????????lastItem ?????????????????????smoothScrollToPosition??????????????????????????????????????????
            // ?????????onScrollStateChanged????????????????????????moveToPosition??????????????????????????????????????????
            mRecyclerView.smoothScrollToPosition(position);
            scrollToPosition = position;
            canScroll = true;
        }
    }


    @Override
    public void onResume() {

        super.onResume();

        String isLoading2 = (String) SPUtils.get(getActivity(), "isloading2", "");
        if(!isLoading2 .equals("")){
            ViewUtils.createLoadingDialog2(getActivity(),true,"???????????????");
            SPUtils.put(getActivity(),"isloading2","");


        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(broadcastReceiver);
        getActivity().unregisterReceiver(broadcastReceiverFace);
        getActivity().unregisterReceiver(broadcastReceiverClose01);
    }
    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 500L;
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {

            if (isLogin){
                Log.e("??????","??????");
                if (!StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"rolecodes",""))){
                    long nowTime = System.currentTimeMillis();
                    if (nowTime - mLastClickTime > TIME_INTERVAL) {
                        // do something
                        mLastClickTime = nowTime;
                        SQLiteDatabase db = databaseHelper.getReadableDatabase();
                        String personName = (String) SPUtils.get(MyApp.getInstance(), "personName", "");
                        Cursor cursor = db.rawQuery("select * from serviceInfo where userId = ?", new String[]{personName});
                        if (cursor.moveToNext()) {

                            collectContentSelect = cursor.getString(cursor.getColumnIndex("collectContent"));

                            contentSelect = cursor.getString(cursor.getColumnIndex("content"));
                            String userId = cursor.getString(cursor.getColumnIndex("userId"));
                            serviceAppListBean1 = JSON.parseObject(collectContentSelect, ServiceAppListBean.class);
                            obj1 = serviceAppListBean1.getObj();
                            serviceAppListBean = JSON.parseObject(contentSelect, ServiceAppListBean.class);
                            List<ServiceAppListBean.ObjBean> obj = serviceAppListBean.getObj();
                            getMyCollectDatas();
                        }
                    }

                }else {
                    netWorkUserMsg();
                }

            }else {

                netWorkServiceAPPListYou();
            }


            return;
        }else{  // ?????????????????? ??????????????????onResume();
            netInsertPortal("3");
            registerBoradcastReceiver();
            //registerBoradcastReceiver2();
            if (isLogin){
                webView.setWebViewClient(mWebViewClient);
                webView.loadUrl(UrlRes.HOME_URL +"/portal/login/appLogin");
            }

        }

    }

    private void netInsertPortal(final String insertPortalAccessLog) {
        String imei = MobileInfoUtils.getIMEI(getActivity());
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Four_Modules)
                .tag(this)
                .params("portalAccessLogMemberId",(String) SPUtils.get(getInstance(),"userId",""))
                .params("portalAccessLogEquipmentId",(String) SPUtils.get(getInstance(),"imei",""))//??????ID
                .params("portalAccessLogTarget", insertPortalAccessLog)//????????????
                .params("portalAccessLogVersionNumber", (String) SPUtils.get(getActivity(),"versionName", ""))//?????????
                .params("portalAccessLogOperatingSystem", "ANDROID")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("????????????",response.body());

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }


        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url =  null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
            }


            if (url.contains(UrlRes.HOME2_URL+"/cas/login")) {
                if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""))){
                    Intent intent = new Intent(getActivity(),LoginActivity2.class);
                    startActivity(intent);

                    return true;
                }
            }

            return super.shouldOverrideUrlLoading(view, request);
        }

        /**????????????*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(UrlRes.HOME2_URL+"/cas/login")) {
                if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""))){
                    Intent intent = new Intent(getActivity(),LoginActivity2.class);
                    startActivity(intent);
                    return true;
                }
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            CookieUtils.syncCookie(UrlRes.HOME2_URL,"CASTGC="+SPUtils.get(getActivity(),"TGC",""),getActivity());


        }

    };


    private int isOpen = 0;
    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
        isOpen = 1;
    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //????????????onRequestPermissionsResult
        if(permissionsUtil != null){
            permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //???????????????????????????????????????????????????
        if(permissionsUtil != null){
            permissionsUtil.onActivityResult(requestCode, resultCode, intent);
        }


    }


}
