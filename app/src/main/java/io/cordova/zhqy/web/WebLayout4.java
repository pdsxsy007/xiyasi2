package io.cordova.zhqy.web;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.just.agentweb.IWebLayout;


import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;

import io.cordova.zhqy.R;
import io.cordova.zhqy.bean.ServiceAppListBean;
import io.cordova.zhqy.utils.DownPicUtil;
import io.cordova.zhqy.utils.MobileInfoUtils;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static io.cordova.zhqy.activity.SplashActivity.getLocalVersionName;


/**
 * Created by cenxiaozhong on 2017/7/1.
 * source code  https://github.com/Justson/AgentWeb
 */

public class WebLayout4 implements IWebLayout {

    private Activity mActivity;
    private final RelativeLayout mTwinklingRefreshLayout;
    private WebView mWebView = null;
    String url;

    @SuppressLint("HandlerLeak")
    Handler handler =new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            if(what == 99){
                boolean isQR = (boolean) msg.obj;
                if(isQR == false){
                    rl_code.setVisibility(View.GONE);
                }else {
                    rl_code.setVisibility(View.VISIBLE);
                }
                popupWindow.showAtLocation(mWebView, Gravity.BOTTOM, 0, 0);
            }else {

                String picFile = (String) msg.obj;
                String[] split = picFile.split("/");
                String fileName = split[split.length-1];
                /*try {

                    MediaStore.Images.Media.insertImage(mActivity.getContentResolver(), picFile, fileName, null);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }*/
                // ????????????????????????
                mActivity.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + picFile)));
                //Toast.makeText(mActivity,"????????????????????????",Toast.LENGTH_LONG).show();
            }

        }
    };

    String imei = "";
    private static final int RC_CAMERA_PERM = 123;

    public WebLayout4(final Activity activity) {
        this.mActivity = activity;
        mTwinklingRefreshLayout = (RelativeLayout) LayoutInflater.from(activity).inflate(R.layout.fragment_twk_web, null);
      //  mTwinklingRefreshLayout.setPureScrollModeOn();
        mWebView = (WebView) mTwinklingRefreshLayout.findViewById(R.id.webView);
        CookieManager cookieManager = CookieManager.getInstance();
        if(Build.VERSION.SDK_INT>=21){
            cookieManager.setAcceptThirdPartyCookies(mWebView, true);
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            CookieSyncManager.getInstance().sync();
        } else {
            cookieManager.flush();
        }
        WebSettings setting = mWebView.getSettings();
       /* setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        setting.setUseWideViewPort(true);
        setting.setLoadWithOverviewMode(true);
        setting.setDomStorageEnabled(true);
        setting.setDatabaseEnabled(true);
        mWebView.setLayerType(View.LAYER_TYPE_HARDWARE,null);

        setting.setJavaScriptEnabled(true);//??????js
        setting.setBuiltInZoomControls(true); // ??????????????????
        setting.setSupportZoom(true); // ????????????
        setting.setAllowFileAccess(true);
        setting.setAppCacheEnabled(true);
        setting.setBlockNetworkImage(false);//?????????????????????
        setting.setLoadsImagesAutomatically(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
*/
        setting.setJavaScriptEnabled(true);
        setting.setJavaScriptCanOpenWindowsAutomatically(true);
        //setting.setCacheMode(WebSettings.LOAD_NO_CACHE);

        setting.setDatabaseEnabled(true);

        setting.setAllowFileAccess(true);
        setting.setAllowFileAccessFromFileURLs(false);
        setting.setSavePassword(false);
        setting.setSupportZoom(true);
        setting.setBuiltInZoomControls(true);
        setting.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        setting.setUseWideViewPort(true);



        mWebView.getSettings().setDomStorageEnabled(true);
        mWebView.getSettings().setAppCacheMaxSize(1024*1024*8);
        String appCachePath = activity.getCacheDir().getAbsolutePath();
        mWebView.getSettings().setAppCachePath(appCachePath);
        mWebView.getSettings().setAllowFileAccess(true);
        setting.setAppCacheEnabled(true);

        if (imei.isEmpty()){
            String userAgent = setting.getUserAgentString();
            imei = MobileInfoUtils.getIMEI(activity);
//        String IMSI = MobileInfoUtils.getIMSI(activity.getApplication());
            Map<String,String> map=new HashMap<String,String>();
            map.put("version",getLocalVersionName(activity));
            map.put("device",imei);
            map.put("gilight","android");
            JSONObject json =new JSONObject(map);
            Log.e("userAgent", json.toString());
            setting.setUserAgentString(userAgent  +json.toString());
            String s = userAgent + json.toString();
            Log.e("userAgent", s);
        }

        initPop();
        mWebView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (EasyPermissions.hasPermissions(activity, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                    final WebView.HitTestResult hitTestResult = mWebView.getHitTestResult();
                    if(hitTestResult.getType()== WebView.HitTestResult.IMAGE_TYPE||
                            hitTestResult.getType()== WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {
                        url = hitTestResult.getExtra();
                        isQR = false;

                        DownPicUtil.downPic(url, new DownPicUtil.DownFinishListener(){

                            @Override
                            public void getDownPath(String s) {
                                isQR = decodeImage(s);
                                /*if(isQR){

                                }*/
                                Message message = new Message();
                                message.what = 99;
                                message.obj = isQR;
                                handler.sendMessage(message);
                            }
                        });

                    }
                } else {//???????????????????????????????????????
                    // Ask for one permission
                    EasyPermissions.requestPermissions(activity, "?????????????????????", RC_CAMERA_PERM, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }

                return true;
            }
        });



    }




    @NonNull
    @Override
    public ViewGroup getLayout() {
        return mTwinklingRefreshLayout;
    }

    @Nullable
    @Override
    public WebView getWebView() {
        return mWebView;
    }


    PopupWindow popupWindow;
    RelativeLayout rl_code;
    private void initPop() {
        View inflate = LayoutInflater.from(mActivity).inflate(R.layout.dialog_bottom, null, false);
        //?????????PopupWindow???????????????
        popupWindow = new PopupWindow(inflate, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //?????????????????????????????????PopupWindow??????????????????????????????????????????????????????
        popupWindow.setOutsideTouchable(true); //??????????????????
        popupWindow.setTouchable(true); //?????????????????????

        RelativeLayout rl_cancel = inflate.findViewById(R.id.rl_cancel);
        RelativeLayout rl_save = inflate.findViewById(R.id.rl_save);

        rl_code = inflate.findViewById(R.id.rl_code);
        rl_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownPicUtil.downPic(url, new DownPicUtil.DownFinishListener(){

                    @Override
                    public void getDownPath(String s) {
                        Toast.makeText(mActivity,"????????????",Toast.LENGTH_LONG).show();
                        Message msg = Message.obtain();
                        msg.obj=s;
                        handler.sendMessage(msg);
                    }
                });
                /*String[] split = url.split("/");
                String fileName = split[split.length - 1];
                String filePath = Environment.getExternalStorageDirectory().getPath();
                File picFile = new File(filePath + File.separator + fileName);
                boolean exists = picFile.exists();
                if(exists){
                    picFile.delete();
                }
                DownPicUtil.downPic(url, new DownPicUtil.DownFinishListener(){

                    @Override
                    public void getDownPath(String s) {
                        Toast.makeText(mActivity,"????????????",Toast.LENGTH_LONG).show();
                        Message msg = Message.obtain();
                        msg.obj=s;
                        handler.sendMessage(msg);
                    }
                });*/
                popupWindow.dismiss();
            }
        });

        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                popupWindow.dismiss();
                String[] split = url.split("/");
                String fileName = split[split.length - 1];
                String filePath = Environment.getExternalStorageDirectory().getPath();
                File picFile = new File(filePath + File.separator + fileName);
                boolean exists = picFile.exists();
                if(exists){
                    picFile.delete();
                }
            }
        });

        rl_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(mActivity,CodeWebActivity.class);
                intent.putExtra("appUrl",text);
                mActivity.startActivity(intent);
                popupWindow.dismiss();
            }
        });



    }

    boolean isQR = false;
    String text;
    /**
     * ????????????????????????
     * @param sUrl ????????????
     * @return
     */
    private boolean decodeImage(String sUrl){
        Bitmap bitmap= BitmapFactory.decodeFile(sUrl); ;
        QRCodeReader reader = new QRCodeReader();
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] data = new int[width * height];
        bitmap.getPixels(data, 0, width, 0, 0, width, height);
        RGBLuminanceSource source = new RGBLuminanceSource(width, height, data);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        Result decode = null;
        try {
            decode = reader.decode(bitmap1);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        //result = DecodeImage.handleQRCodeFormBitmap(getBitmap(sUrl));
        if(decode == null){
            isQR = false;
        }else {
            isQR = true;

            text = decode.getText();
        }
        return isQR;
    }


}
