package io.cordova.zhqy.activity;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.WebView;
import android.widget.ImageView;
import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.lzy.okgo.model.Response;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import butterknife.BindView;
import cn.jiguang.verifysdk.api.JVerificationInterface;
import cn.jpush.android.api.JPushInterface;
import io.cordova.zhqy.AppException;
import io.cordova.zhqy.Constants;
import io.cordova.zhqy.Main2Activity;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.LoginBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.MobileInfoUtils;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.MyIntentService;
import io.cordova.zhqy.utils.SPUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.ToastUtils;
import okhttp3.OkHttpClient;

import static io.cordova.zhqy.utils.AesEncryptUtile.key;



/**
 * Created by Administrator on 2018/12/11 0011.
 */

public class SplashActivity extends AppCompatActivity {

    ImageView imageView;
    private String s1;
    private String s2;
    private Handler handler = new MyHandler(this);
    private static String localVersionName;
    @BindView(R.id.webView)
    WebView webView;
    public static final int PASSWORD_LOGIN_FLAG = 0x0004;

    private ComponentName defaultComponent;
    private ComponentName testComponent;
    private PackageManager packageManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        if((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0){
            finish();
            return;
        }
        imageView =  (ImageView) findViewById(R.id.iv_bg);

        getLocalVersionName(getApplicationContext());

        String infoType = (String) SPUtils.get(MyApp.getInstance(), "InfoType", "");
        Log.e("infoType",infoType+"--------");
        if(infoType.equals("1")){
            getMsg();
            SPUtils.put(MyApp.getInstance(),"InfoType","");
        }else {
            handleOpenClick();
        }

            initView();


        //initChangeIcon();


        //getNowTime();

    }
    public void getNowTime()  {
        SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
        Date startTime = null;
        try {
            startTime = ft.parse("2020-08-12 03:26:54");
            Date endTime = ft.parse("2020-08-12 10:40:54");
            Date nowTime = new Date();
            boolean effectiveDate = isEffectiveDate(nowTime, startTime, endTime);
            if (effectiveDate) {
                switchIcon(1);
            }else {
                switchIcon(2);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }

    /**
     *
     * @param nowTime   ????????????
     * @param startTime	????????????
     * @param endTime   ????????????
     * @return
     * @author sunran   ????????????????????????????????????
     */
    public static boolean isEffectiveDate(Date nowTime, Date startTime, Date endTime) {
        if (nowTime.getTime() == startTime.getTime()
                || nowTime.getTime() == endTime.getTime()) {
            return true;
        }

        Calendar date = Calendar.getInstance();
        date.setTime(nowTime);

        Calendar begin = Calendar.getInstance();
        begin.setTime(startTime);

        Calendar end = Calendar.getInstance();
        end.setTime(endTime);

        if (date.after(begin) && date.before(end)) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * @param useCode 1?????????????????? 2 ?????????????????? 3??????????????????
     */
    private void switchIcon(int useCode) {

        try {
            //??????manifest???activity-alias ???name????????????
            String icon_tag = getPackageName()+".icon_tag";
            String icon_tag_1212 = getPackageName()+".icon_tag_1212";

            if (useCode != 3) {

                PackageManager pm = getPackageManager();

                ComponentName normalComponentName = new ComponentName(
                        getBaseContext(),
                        icon_tag);
                //???????????????????????????????????????????????????????????????activity-alias??????android:enable??????
                int normalNewState = useCode == 2 ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                //??????????????????????????????????????????
                if (pm.getComponentEnabledSetting(normalComponentName) != normalNewState) {
                    //PackageManager.DONT_KILL_APP??????????????????????????????????????????APP??????
                    pm.setComponentEnabledSetting(
                            normalComponentName,
                            normalNewState,
                            PackageManager.DONT_KILL_APP);
                }

                ComponentName actComponentName = new ComponentName(
                        getBaseContext(),
                        icon_tag_1212);
                //?????????????????????
                int actNewState = useCode == 1 ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                        : PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
                //??????????????????????????????????????????
                if (pm.getComponentEnabledSetting(actComponentName) != actNewState) {
                    pm.setComponentEnabledSetting(
                            actComponentName,
                            actNewState,
                            PackageManager.DONT_KILL_APP);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initChangeIcon() {
        //????????????activity?????????????????????
        ComponentName componentName = getComponentName();

        //?????????????????????MainActivity??????
        defaultComponent = new ComponentName(getBaseContext(), getClass().getName());  //?????????????????????
        //????????????????????????test??????
        testComponent = new ComponentName(getBaseContext(), getPackageName() + ".test_activity");

        packageManager = getPackageManager();

        //changeDefaultIcon();
        changeIcon();
    }

    private void changeIcon() {
        enableComponent(testComponent);
        disableComponent(defaultComponent);
    }

    private void changeDefaultIcon() {
        enableComponent(defaultComponent);
        disableComponent(testComponent);
    }

    /**
     * ????????????
     *
     * @param componentName
     */
    private void enableComponent(ComponentName componentName) {
        int state = packageManager.getComponentEnabledSetting(componentName);
        if (state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED) {
            //????????????
            return;
        }
        packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }


    /**
     * ????????????
     *
     * @param componentName
     */
    private void disableComponent(ComponentName componentName) {
        int state = packageManager.getComponentEnabledSetting(componentName);
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            //????????????
            return;
        }
        packageManager.setComponentEnabledSetting(componentName,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    protected void initView() {
        initThirdConfig();
        handler.sendEmptyMessageDelayed(0, 2000);

    }

    private void initThirdConfig() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                JPushInterface.setDebugMode(true);
                JPushInterface.init(SplashActivity.this);
                //MyIntentService.start(SplashActivity.this);
                initOkGo();

                SPUtil.init(SplashActivity.this, Constants.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
                String localVersionName = getLocalVersionName(SplashActivity.this);
                String imei = MobileInfoUtils.getIMEI(SplashActivity.this);

                SPUtils.put(SplashActivity.this,"versionName",localVersionName);
                SPUtils.put(SplashActivity.this,"imei",imei);
                AppException appException = AppException.getInstance();
                appException.init(SplashActivity.this);

                UMShareAPI.get(SplashActivity.this);

                UMConfigure.init(SplashActivity.this,Constants.umeng_init_data,Constants.umeng_init_name,UMConfigure.DEVICE_TYPE_PHONE,"");
                UMConfigure.setLogEnabled(true);
                UMConfigure.isDebugLog();
                MobclickAgent.setScenarioType(SplashActivity.this, MobclickAgent.EScenarioType.E_UM_NORMAL);
                UMShareConfig config = new UMShareConfig();
                config.isNeedAuthOnGetUserInfo(true);
                UMShareAPI.get(SplashActivity.this).setShareConfig(config);
                PlatformConfig.setWeixin(Constants.umeng_init_data_weixin, Constants.umeng_init_data_weixin_value);
                PlatformConfig.setSinaWeibo(Constants.umeng_init_data_weibo, Constants.umeng_init_data_weibo_value, Constants.umeng_init_data_weibo_address);
                PlatformConfig.setQQZone(Constants.umeng_init_data_qq, Constants.umeng_init_data_qq_value);

                //????????????????????????
                JVerificationInterface.setDebugMode(true);
                JVerificationInterface.init(SplashActivity.this);
            }
        }.start();

    }


    /**
     * ?????????ok
     */
    private void initOkGo() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("feng");
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        loggingInterceptor.setColorLevel(Level.INFO);
        builder.addInterceptor(loggingInterceptor);
        builder.cookieJar(new CookieJarImpl(new SPCookieStore(this)));
        builder.readTimeout(30000, TimeUnit.MILLISECONDS);
        builder.writeTimeout(30000, TimeUnit.MILLISECONDS);
        builder.connectTimeout(3000, TimeUnit.MILLISECONDS);
        builder.cookieJar(new CookieJarImpl(new DBCookieStore(this)));

//
//        try {
//            HttpsUtils.SSLParams sslParams3 = HttpsUtils.getSslSocketFactory(getAssets().open("a.cer"));
//            builder.sslSocketFactory(sslParams3.sSLSocketFactory, sslParams3.trustManager);
//            builder.hostnameVerifier(new SafeHostnameVerifier());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        OkGo.getInstance().init((Application) MyApp.getInstance())
                .setOkHttpClient(builder.build())
                .setCacheMode(CacheMode.FIRST_CACHE_THEN_REQUEST)
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)
                .setRetryCount(3);

    }

    /**
     * ???????????????????????????????????????????????????????????????2?????????????????????,????????????????????????????????????????????????????????????????????????
     *
     */
    private class MyHandler extends Handler {

        private WeakReference<Context> reference;

        public MyHandler(Context context) {
            reference = new WeakReference<>(context);
        }

        @Override
        public void handleMessage(Message msg) {
            if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""))){
                Intent intent = new Intent(getApplicationContext(),Main2Activity.class);
                intent.putExtra("splash","splash");
                startActivity(intent);
                finish();
            }else {

                /*if (SPUtil.getInstance().getBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN)) {
                   Intent intent = new Intent(SplashActivity.this,LoginActivity3.class);
                   intent.putExtra("splash","splash");
                   startActivity(intent);
                   finish();
                }else {
                    netWorkLogin();
                }
*/
                netWorkLogin();
            }
            //netWorkLogin();

        }
    }

    LoginBean loginBean;
    String tgt;
    private void netWorkLogin() {
        s1 = (String) SPUtils.get(MyApp.getInstance(),"username","");
        s2 =  (String) SPUtils.get(MyApp.getInstance(),"password","");

        try {
            String imei = AesEncryptUtile.encrypt((String) SPUtils.get(this, "imei", ""), key);
            OkGo.<String>get(UrlRes.HOME2_URL +UrlRes.loginUrl)
                    .params("openid",AesEncryptUtile.openid)
                    .params("username",s1)
                    .params("password",s2)
                    .params("type","10")
                    .params("equipmentId",imei)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            loginBean = JSON.parseObject(response.body(),LoginBean.class);
                            if (loginBean.isSuccess() ) {
                                try {

                                    tgt = AesEncryptUtile.decrypt(loginBean.getAttributes().getTgt(),key);
                                    Log.e("tgt",tgt);
                                    String userName = AesEncryptUtile.decrypt(loginBean.getAttributes().getUsername(),key) ;
                                    String userId  = AesEncryptUtile.encrypt(userName+ "_"+ Calendar.getInstance().getTimeInMillis(),key);

                                    SPUtils.put(MyApp.getInstance(),"userId",userId);
//                                SPUtils.put(MyApp.getInstance(),"tgt",tgt);
                                    SPUtils.put(getApplicationContext(),"TGC",tgt);
                                    SPUtils.put(getApplicationContext(),"username",s1);
                                    SPUtils.put(getApplicationContext(),"password",s2);

                                    String msspid = loginBean.getAttributes().getMssPid();
                                    SPUtils.put(getApplicationContext(),"msspID",msspid);


                               /* webView.setWebViewClient(mWebViewClient);
                                webView.loadUrl("http://iapp.zzuli.edu.cn/portal/login/appLogin");*/
                                    Intent intent = new Intent(MyApp.getInstance(),Main2Activity.class);
                                    intent.putExtra("userId",userName);
                                    intent.putExtra("splash","splash");
                                    startActivity(intent);
                                    finish();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }else {
                                ToastUtils.showToast(MyApp.getInstance(),loginBean.getMsg());

                                SPUtils.put(MyApp.getInstance(),"userId","");
//                                SPUtils.put(MyApp.getInstance(),"tgt",tgt);
                                SPUtils.put(getApplicationContext(),"TGC","");
                                SPUtils.put(getApplicationContext(),"username","");
                                SPUtils.put(getApplicationContext(),"password","");
                                Intent intent = new Intent(MyApp.getInstance(),Main2Activity.class);
                                intent.putExtra("splash","splash");
                                startActivity(intent);
                                finish();

                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                           /* SPUtils.put(MyApp.getInstance(),"userId","");
//                                SPUtils.put(MyApp.getInstance(),"tgt",tgt);
                            SPUtils.put(getApplicationContext(),"TGC","");
                            SPUtils.put(getApplicationContext(),"username","");
                            SPUtils.put(getApplicationContext(),"password","");*/
                            Intent intent = new Intent(MyApp.getInstance(),Main2Activity.class);
                            intent.putExtra("splash","splash");
                            startActivity(intent);
                            finish();

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    /**?????? OPPO  Fac*/
    private static final String TAG = "HuaWei";
    /**??????Id**/
    private static final String KEY_MSGID = "msg_id";
    private static final String KEY_TYPE = "msg_type";
    /**????????????????????????**/
    private static final String KEY_WHICH_PUSH_SDK = "rom_type";
    /**????????????**/
    private static final String KEY_TITLE = "n_title";
    /**????????????**/
    private static final String KEY_CONTENT = "n_content";
    /**??????????????????**/
    private static final String KEY_EXTRAS = "n_extras";

    /**
     * ??????????????????????????????????????????Activity????????????
     * Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK
     * ???????????????????????????onCreat??????????????????????????????
     */
    private void handleOpenClick() {
        Log.e(TAG, "???????????????????????????");
        String data = null;
        //???????????????????????????jpush??????
        if (getIntent().getData() != null) {
            data = getIntent().getData().toString();
        }

        //??????fcm???oppo???????????????jpush??????
        if (TextUtils.isEmpty(data) && getIntent().getExtras() != null) {
            data = getIntent().getExtras().getString("JMessageExtra");
        }

        Log.e(TAG, " -?????????- " + String.valueOf(data));
        if (TextUtils.isEmpty(data)) return;
        try {
            JSONObject jsonObject = new JSONObject(data);
            String msgId = jsonObject.optString(KEY_MSGID);
            String msgType = jsonObject.optString(KEY_TYPE);
            byte whichPushSDK = (byte) jsonObject.optInt(KEY_WHICH_PUSH_SDK);
            String title = jsonObject.optString(KEY_TITLE);
            String content = jsonObject.optString(KEY_CONTENT);
            String extras = jsonObject.optString(KEY_EXTRAS);

            Log.e("extras-splash",extras);
            try {
                JSONObject extraJson = new JSONObject(extras);
                if (extraJson.length() > 0) {
                    Log.e("extras", extras);
                    Log.e("extraJson", extraJson.getString("messageId"));
                    msgId =  extraJson.getString("messageId");
                    msgType =  extraJson.getString("messageType");
                    Log.e("msgId", msgId);
                    SPUtils.put(MyApp.getInstance(),"msgId",msgId);
                    SPUtils.put(MyApp.getInstance(),"msgType",msgType);
                }
            } catch (JSONException ignored) {
                Log.e("JPush",ignored.getMessage());
            }
            //??????????????????
            JPushInterface.reportNotificationOpened(this, msgId, whichPushSDK);
        } catch (JSONException e) {
            Log.w(TAG, "parse notification error");
        }


    }

    /**
     * ??????????????????
     * msgType ????????????(-1:?????????0:????????????,1:??????,2:??????,3:??????,4:??????,5:????????????6.??????)
     *
     * */
    private void getMsg() {
        Intent intent = getIntent();
        if (null != intent) {
            Bundle bundle = getIntent().getExtras();
            String title = null;
            String content = null;
            String extrasBean = null;
            String extrasBean2 = null;
            String msgId = null;
            String msgType = null;
            if(bundle!=null){

                Log.e("bundle",bundle+"");
               // ToastUtils.showToast(SplashActivity.this,bundle+"");
                title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                content = bundle.getString(JPushInterface.EXTRA_ALERT);
                extrasBean = bundle.getString(JPushInterface.EXTRA_EXTRA);
                extrasBean2 = bundle.getString("JMessageExtra");
                if(!StringUtils.isEmpty(extrasBean)){
                    try {
                        JSONObject extraJson = new JSONObject(extrasBean);
                        if (extraJson.length() > 0) {
                            Log.e("extras", extrasBean);
                            Log.e("extraJson", extraJson.getString("messageId"));
                            msgId =  extraJson.getString("messageId");
                            msgType =  extraJson.getString("messageType");
                            Log.e("msgId", msgId);
                            SPUtils.put(MyApp.getInstance(),"msgId",msgId);
                            SPUtils.put(MyApp.getInstance(),"msgType",msgType);

                        }
                    } catch (JSONException ignored) {
                        Log.e("JPush",ignored.getMessage());
                    }
                }

            }
            Log.e("JPush","Title : " + title + "  " + "Content : " + content + "   msgId  : " + msgId + "   msgType  : " + msgType);


        }
    }


    /**
     * ?????????????????????????????????
     */

    public static String getLocalVersionName(Context ctx) {
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersionName = packageInfo.versionName;
            Log.d("TAG", "????????????????????? = " + localVersionName);
              SPUtils.put(ctx.getApplicationContext(),"VersionName", localVersionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersionName;
    }



    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this); // ????????????
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this); // ????????????
    }


}
