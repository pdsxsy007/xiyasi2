package io.cordova.zhqy.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.activity.FaceSearchActivity;
import io.cordova.zhqy.bean.AppListBean;
import io.cordova.zhqy.bean.BaseBean;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;
import pub.devrel.easypermissions.AfterPermissionGranted;

public class DargeFaceSearchUtils {

    private static final String showText01 = "人脸识别不成功，请确保光线明亮及保持正确的拍摄角度,重新再试。";
    private static final String showText02 = "人脸识别不成功，已验证失败超过三次，若有疑问请联系管理员。";


    /**
     * 根据字段检测是否弹出人脸框以及后续操作
     * @param appsBean
     * @param context
     */
    public static void jargeFace(AppListBean.ObjBean.ListBean appsBean, Context context){

        AppListBean.ObjBean.PortalAppAuthentication portalAppAuthentication = appsBean.getPortalAppAuthentication();
        if(portalAppAuthentication != null){
            String appAuthenticationFace = appsBean.getPortalAppAuthentication().getAppAuthenticationFace();
            if(appAuthenticationFace != null){
                if(appAuthenticationFace .equals("1")){
                    if (appsBean.getAppUrl().contains("{memberid}")){
                        String appUrl = appsBean.getAppUrl();
                        String s1= null;
                        try {
                            s1 = URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"personName",""), "UTF-8");
                            appUrl =  appUrl.replace("{memberid}", s1);
                            SPUtils.put(context, "appUrl",appUrl);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }else if(appsBean.getAppUrl().contains("{memberAesEncrypt}")){
                        String appUrl = appsBean.getAppUrl();
                        try {
                            String memberAesEncrypt = AesEncryptUtile.encrypt((String) SPUtils.get(MyApp.getInstance(),"personName",""), String.valueOf(appsBean.getAppSecret()));
                            String s2=  URLEncoder.encode(memberAesEncrypt, "UTF-8");
                            appUrl =  appUrl.replace("{memberAesEncrypt}", s2);
                            SPUtils.put(context, "appUrl",appUrl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(appsBean.getAppUrl().contains("{quicklyTicket}")){
                        String appUrl = appsBean.getAppUrl();
                        try {
                            String s3 =  URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"TGC",""), "UTF-8");
                            appUrl = appUrl.replace("{quicklyTicket}",s3);
                            SPUtils.put(context, "appUrl",appUrl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else {
                        SPUtils.put(context, "appUrl",appsBean.getAppUrl());
                    }
                    SPUtils.put(context, "appId",appsBean.getAppId()+"");
                    SPUtils.put(context, "appName",appsBean.getAppName());
                    showFaceActivity(context);

                }else {
                    if (NetState.isConnect(context)) {
                        netWorkAppClick(appsBean.getAppId());
                    }
                    Intent intent = null;
                    String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                    if(isOpen.equals("") || isOpen.equals("1")){
                        intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                    }else {
                        intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                    }
                    if (appsBean.getAppUrl().contains("{memberid}")){
                        String appUrl = appsBean.getAppUrl();
                        String s1= null;
                        try {
                            s1 = URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"personName",""), "UTF-8");
                            appUrl =  appUrl.replace("{memberid}", s1);
                            intent.putExtra("appUrl",appUrl);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                    }else if(appsBean.getAppUrl().contains("{memberAesEncrypt}")){
                        String appUrl = appsBean.getAppUrl();
                        try {
                            String memberAesEncrypt = AesEncryptUtile.encrypt((String) SPUtils.get(MyApp.getInstance(),"personName",""), String.valueOf(appsBean.getAppSecret()));
                            String s2=  URLEncoder.encode(memberAesEncrypt, "UTF-8");
                            appUrl =  appUrl.replace("{memberAesEncrypt}", s2);
                            intent.putExtra("appUrl",appUrl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }else if(appsBean.getAppUrl().contains("{quicklyTicket}")){
                        String appUrl = appsBean.getAppUrl();
                        try {
                            String s3 =  URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"TGC",""), "UTF-8");
                            appUrl = appUrl.replace("{quicklyTicket}",s3);
                            intent.putExtra("appUrl",appUrl);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        intent.putExtra("appUrl",appsBean.getAppUrl());
                    }
                    intent.putExtra("appId",appsBean.getAppId()+"");
                    intent.putExtra("appName",appsBean.getAppName()+"");
                    context.startActivity(intent);

                }
            }else {

                if (NetState.isConnect(context)) {
                    netWorkAppClick(appsBean.getAppId());
                }
                Intent intent = null;
                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                if(isOpen.equals("") || isOpen.equals("1")){
                    intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                }else {
                    intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                }
                if (appsBean.getAppUrl().contains("{memberid}")){
                    String appUrl = appsBean.getAppUrl();
                    String s1= null;
                    try {
                        s1 = URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"personName",""), "UTF-8");
                        appUrl =  appUrl.replace("{memberid}", s1);
                        intent.putExtra("appUrl",appUrl);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }else if(appsBean.getAppUrl().contains("{memberAesEncrypt}")){
                    String appUrl = appsBean.getAppUrl();
                    try {
                        String memberAesEncrypt = AesEncryptUtile.encrypt((String) SPUtils.get(MyApp.getInstance(),"personName",""), String.valueOf(appsBean.getAppSecret()));
                        String s2=  URLEncoder.encode(memberAesEncrypt, "UTF-8");
                        appUrl =  appUrl.replace("{memberAesEncrypt}", s2);
                        intent.putExtra("appUrl",appUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }else if(appsBean.getAppUrl().contains("{quicklyTicket}")){
                    String appUrl = appsBean.getAppUrl();
                    try {
                        String s3 =  URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"TGC",""), "UTF-8");
                        appUrl = appUrl.replace("{quicklyTicket}",s3);
                        intent.putExtra("appUrl",appUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    intent.putExtra("appUrl",appsBean.getAppUrl());
                }
                intent.putExtra("appId",appsBean.getAppId()+"");
                intent.putExtra("appName",appsBean.getAppName()+"");
                context.startActivity(intent);
            }

        }else {
            if (NetState.isConnect(context)) {
                netWorkAppClick(appsBean.getAppId());
            }
            Intent intent = null;
            String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
            if(isOpen.equals("") || isOpen.equals("1")){
                intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
            }else {
                intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
            }
            if (appsBean.getAppUrl().contains("{memberid}")){
                String appUrl = appsBean.getAppUrl();
                String s1= null;
                try {
                    s1 = URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"personName",""), "UTF-8");
                    appUrl =  appUrl.replace("{memberid}", s1);
                    intent.putExtra("appUrl",appUrl);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }

            }else if(appsBean.getAppUrl().contains("{memberAesEncrypt}")){
                String appUrl = appsBean.getAppUrl();
                try {
                    String memberAesEncrypt = AesEncryptUtile.encrypt((String) SPUtils.get(MyApp.getInstance(),"personName",""), String.valueOf(appsBean.getAppSecret()));
                    String s2=  URLEncoder.encode(memberAesEncrypt, "UTF-8");
                    appUrl =  appUrl.replace("{memberAesEncrypt}", s2);
                    intent.putExtra("appUrl",appUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else if(appsBean.getAppUrl().contains("{quicklyTicket}")){
                String appUrl = appsBean.getAppUrl();
                try {
                    String s3 =  URLEncoder.encode((String) SPUtils.get(MyApp.getInstance(),"TGC",""), "UTF-8");
                    appUrl = appUrl.replace("{quicklyTicket}",s3);
                    intent.putExtra("appUrl",appUrl);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else{
                intent.putExtra("appUrl",appsBean.getAppUrl());
            }
            intent.putExtra("appId",appsBean.getAppId()+"");
            intent.putExtra("appName",appsBean.getAppName()+"");
            context.startActivity(intent);
        }

    }


    private static int imageid = 0;

    private static void showFaceActivity(Context context) {
        Intent intent = new Intent(context,FaceSearchActivity.class);
        context.startActivity(intent);
        imageid = 0;
    }

    private static void showToast(String content,Context context){
        ToastUtils.showToast(context,content);
    }

    /**
     * 记录该微应用的的访问量
     * @param appId
     *
     * */
    private static void netWorkAppClick(int appId) {
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
                        Log.e("错误",response.body());
                    }
                });
    }

    private static final int RC_CAMERA_PERM = 123;

    @AfterPermissionGranted(RC_CAMERA_PERM)
    public static void cameraTask(AppListBean.ObjBean.ListBean appsBean, Context context) {
        jargeFace(appsBean,context);
    }


    /**
     * 校验人脸验证结果
     */
    public static void jargeFaceResult(final Context context){
        String s = (String)SPUtils.get(context, "bitmap", "");
        String imei = (String) SPUtils.get(context, "imei", "");
        String username = (String) SPUtils.get(context, "phone", "");

        try {
            String secret = AesEncryptUtile.encrypt(username,AesEncryptUtile.key);
            OkGo.<String>post(UrlRes.HOME2_URL+UrlRes.distinguishFaceUrl)
                    .params( "openId",AesEncryptUtile.openid)
                    .params( "memberId",secret)
                    .params( "img",s )
                    .params( "equipmentId",imei)
                    .execute(new StringCallback(){

                        @Override
                        public void onStart(Request<String, ? extends Request> request) {
                            super.onStart(request);
                        }

                        @Override
                        public void onSuccess(Response<String> response) {
                            SPUtils.put(context,"isloading2","");
                            ViewUtils.cancelLoadingDialog();
                            Log.e("应用人脸识别",response.body());
                            BaseBean baseBean = JsonUtil.parseJson(response.body(),BaseBean.class);
                            boolean success = baseBean.isSuccess();
                            if(success){
                                SPUtils.put(context, "_1","");
                                SPUtils.put(context, "_2","");
                                SPUtils.put(context, "_3","");

                                Intent intent = null;
                                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                                if(isOpen.equals("") || isOpen.equals("1")){
                                    intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                                }else {
                                    intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                                }
                                if (NetState.isConnect(context)) {
                                    String appUrl = (String) SPUtils.get(context, "appUrl", "");
                                    netWorkAppClick(Integer.parseInt((String) SPUtils.get(context, "appId", "")));
                                }
                                intent.putExtra("appUrl",(String) SPUtils.get(context, "appUrl", ""));
                                intent.putExtra("appId",(String) SPUtils.get(context, "appId", ""));
                                intent.putExtra("appName",(String) SPUtils.get(context, "appName", ""));
                                context.startActivity(intent);

                            }else {
                                String result01 = (String) SPUtils.get(context, "_1", "");
                                String result02 = (String) SPUtils.get(context, "_2", "");
                                String result03 = (String) SPUtils.get(context, "_3", "");

                                Log.e("result02",result02);
                                Log.e("result03",result03);
                                if(result01.equals("")){
                                    //showMyDilog(showText01);
                                    showToast(showText01,context);
                                    SPUtils.put(context, "_1",Calendar.getInstance().getTimeInMillis()+"");

                                }else if(result02.equals("")){
                                    showToast(showText01,context);
                                    SPUtils.put(context, "_2",Calendar.getInstance().getTimeInMillis()+"");

                                }else if(result03.equals("")){
                                    showToast(showText02,context);
                                    SPUtils.put(context, "_3",Calendar.getInstance().getTimeInMillis()+"");
                                }else {
                                    long nowTime = Calendar.getInstance().getTimeInMillis();
                                    long rs03 = Long.parseLong(result03);
                                    if((nowTime-rs03) > 5*60*1000){
                                        SPUtils.put(context, "_1","");
                                        SPUtils.put(context, "_2","");
                                        SPUtils.put(context, "_3","");
                                        SPUtils.put(context, "_1",Calendar.getInstance().getTimeInMillis()+"");
                                        showToast(showText01,context);
                                    }else if((nowTime-rs03) <= 5*60*1000){
                                        showToast(showText02,context);
                                    }
                                }

                            }

                            imageid = 0;
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            SPUtils.put(context,"isloading2","");

                            ViewUtils.cancelLoadingDialog();
                            imageid = 0;
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
