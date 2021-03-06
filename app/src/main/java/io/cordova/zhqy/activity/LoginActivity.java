package io.cordova.zhqy.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.net.URLEncoder;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.OnClick;
import io.cordova.zhqy.Main2Activity;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.GetUserIdBean;
import io.cordova.zhqy.bean.LoginBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.CookieUtils;
import io.cordova.zhqy.utils.FinishActivity;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.SystemBarTintUtils;
import io.cordova.zhqy.utils.ToastUtils;

import static io.cordova.zhqy.utils.AesEncryptUtile.key;

/**
 * Created by Administrator on 2018/11/15 0015.
 */

public class LoginActivity extends BaseActivity {
    @BindView(R.id.et_phoneNum)
    EditText etPhoneNum;
    @BindView(R.id.et_password)
    EditText etPassword;
    String userName, passWord;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.btn_login_3)
    Button btnLogin3;

    @BindView(R.id.webView)
    WebView webView;
    private Object W;
    private String s1;
    private String s2;

    @Override
    protected int getResourceId() {
        return R.layout.login_activity;
    }

    @Override
    protected void initSystemBar() {
        super.initSystemBar();
        SystemBarTintUtils.initSystemBarColor(this, R.color.colorPrimary);
    }

    @Override
    protected void initView() {
        super.initView();

    }
    GetUserIdBean getUserIdBean;

    @OnClick({R.id.l_1, R.id.btn_login,R.id.btn_login_3,R.id.iv_close})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.l_1:
                break;
            case R.id.btn_login:

                if (StringUtils.getEditTextData(etPhoneNum).isEmpty() && StringUtils.getEditTextData(etPassword).isEmpty()){
                    ToastUtils.showToast(this,"???????????????????????????");
                    return;
                }
                if(StringUtils.getEditTextData(etPhoneNum).isEmpty()){
                    ToastUtils.showToast(this,"??????????????????");
                    return;
                }

                if(StringUtils.getEditTextData(etPassword).isEmpty()){
                    ToastUtils.showToast(this,"???????????????");
                    return;
                }

                netWorkLogin();

                break;
            case R.id.btn_login_3:
             //   netExit();
                break;
            case R.id.iv_close:
                this.finish();
                break;
        }
    }
    LoginBean loginBean;
    String tgt;
    private void netWorkLogin() {
        try {
//            URLEncoder.encode( ,"UTF-8")
            s1 = URLEncoder.encode(AesEncryptUtile.encrypt(StringUtils.getEditTextData(etPhoneNum),key),"UTF-8");
            s2 =  URLEncoder.encode(AesEncryptUtile.encrypt(StringUtils.getEditTextData(etPassword),key),"UTF-8");

            Log.e("login","s1 = "+ s1 + "  ,s2  = " + s2);

        } catch (Exception e) {
            e.printStackTrace();
        }


        OkGo.<String>get(UrlRes.HOME2_URL +UrlRes.loginUrl)
                .tag(this)
                .params("openid",AesEncryptUtile.openid)
                .params("username",s1)
                .params("password",s2)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("result1",response.body());

                        loginBean = JSON.parseObject(response.body(),LoginBean.class);
                        if (loginBean.isSuccess() ) {

                            try {
                                CookieManager cookieManager =  CookieManager.getInstance();
                                cookieManager.removeAllCookie();
                                tgt = AesEncryptUtile.decrypt(loginBean.getAttributes().getTgt(),key);
                                String userName = AesEncryptUtile.decrypt(loginBean.getAttributes().getUsername(),key) ;


                                String userId  = AesEncryptUtile.encrypt(userName+ "_"+ Calendar.getInstance().getTimeInMillis(),key);
                                SPUtils.put(MyApp.getInstance(),"userId",userId);
                                SPUtils.put(MyApp.getInstance(),"personName",userName);
//                                SPUtils.put(MyApp.getInstance(),"tgt",tgt);
                                SPUtils.put(getApplicationContext(),"TGC",tgt);
                                SPUtils.put(getApplicationContext(),"username",s1);
                                SPUtils.put(getApplicationContext(),"password",s2);
                                Intent intent = new Intent(MyApp.getInstance(),Main2Activity.class);
                                intent.putExtra("userId",userName);
                                startActivity(intent);
                                FinishActivity.addActivity(LoginActivity.this);
                                webView.setWebViewClient(mWebViewClient);
                                webView.loadUrl("http://iapp.zzuli.edu.cn/portal/login/appLogin");
                                Log.e("login","tgt = "+ tgt + "  ,userName  = " + userName);

                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            ToastUtils.showToast(MyApp.getInstance(),loginBean.getMsg());
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i("userAgent4",  view.getSettings().getUserAgentString());


        }

        //        /**????????????*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url =  null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
            }


            if (url.contains("http://kys.zzuli.edu.cn/cas/login")) {
                if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""))){
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();

                    return true;
                }
            }
//            else if (url.contains("myoa.zzuli.edu.cn")) {
//                if (!url.contains("fromnewcas=Y")){
//                    url = url + "&fromnewcas=Y";
//
//                    view.loadUrl(url);
//                    Log.i("url", "== " + url);
//                    return true;
//                }
//                return false;
//            }
//            Log.i("url2", "== " + url);
            return super.shouldOverrideUrlLoading(view, request);
        }

        /**????????????*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("http://kys.zzuli.edu.cn/cas/login")) {
                if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""))){
                    Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                    startActivity(intent);
                    finish();

                    return true;
                }
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {

            CookieUtils.syncCookie("http://kys.zzuli.edu.cn","CASTGC="+tgt,getApplication());


        }

    };
}
