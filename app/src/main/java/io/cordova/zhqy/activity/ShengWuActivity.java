package io.cordova.zhqy.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import butterknife.BindView;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.AddFaceBean;
import io.cordova.zhqy.Constants;
import io.cordova.zhqy.fingerprint.FingerprintHelper;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity2;
import io.cordova.zhqy.utils.BitmapHelper;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.LQRPhotoSelectUtils;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.utils.fingerUtil.FingerprintUtil;
import io.cordova.zhqy.widget.finger.CommonTipDialog;
import io.cordova.zhqy.widget.finger.FingerprintVerifyDialog;
import io.reactivex.functions.Consumer;
import kr.co.namee.permissiongen.PermissionFail;
import kr.co.namee.permissiongen.PermissionSuccess;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Administrator on 2019/6/19 0019.
 */

public class ShengWuActivity extends BaseActivity2 implements View.OnClickListener, FingerprintHelper.SimpleAuthenticationCallback, PopupWindow.OnDismissListener {

    private boolean isOpen;
    private FingerprintHelper helper;
    private FingerprintVerifyDialog fingerprintVerifyDialog;
    private CommonTipDialog fingerprintVerifyErrorTipDialog;
    private CommonTipDialog closeFingerprintTipDialog;
    private int type = 0;

    @BindView(R.id.iv_fingerprint_login_switch)
    ImageView iv_fingerprint_login_switch;

    @BindView(R.id.ll_finger)
    LinearLayout ll_finger;

    @BindView(R.id.ll_shengwu)
    LinearLayout ll_shengwu;

    @BindView(R.id.tv_title)
    TextView tvTitle;

    private PopupWindow popupWindow;

    private LQRPhotoSelectUtils mLqrPhotoSelectUtils;

    String userId;

    private static final int ALBUM_REQUEST_CODE = 1;

    private static final int CAMERA_REQUEST_CODE = 2;

    private static final String ROOT_NAME = "UPLOAD_CACHE";

    @Override
    protected int getResourceId() {
        return R.layout.activity_shengwu;
    }

    @Override
    protected void initView() {
        super.initView();
        tvTitle.setText("????????????");

        userId = (String) SPUtils.get(MyApp.getInstance(), "userId", "");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            helper = FingerprintHelper.getInstance();
            helper.init(getApplicationContext());
            helper.setCallback(this);
            if (helper.checkFingerprintAvailable(this) != -1) {
                //????????????????????????
                iv_fingerprint_login_switch.setEnabled(true);
            }else {
                ToastUtils.showToast(this,"???????????????????????????");
            }
        }else {
            ll_finger.setVisibility(View.GONE);
        }
        isOpen = SPUtil.getInstance().getBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN);
        setSwitchStatus();
        mLqrPhotoSelectUtils = new LQRPhotoSelectUtils(this, new LQRPhotoSelectUtils.PhotoSelectListener() {
            @Override
            public void onFinish(File outputFile, Uri outputUri) {
                // 4???????????????????????????????????????????????????

                //ToastUtils.showToast(ShengWuActivity.this,outputFile.getAbsolutePath());


            }
        }, true);


//        ll_shengwu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                openPopupWindow(view);
//            }
//        });

        ll_shengwu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cameraTask();
            }
        });

        registerBoradcastReceiver();
    }
    @Override
    protected void onResume() {
        super.onResume();
        String isloadingUp = (String) SPUtils.get(ShengWuActivity.this, "isloadingUp", "");
        if(!isloadingUp .equals("")){
            SPUtils.put(getApplicationContext(),"isloadingUp","");
            ViewUtils.createLoadingDialog2(ShengWuActivity.this,true,"???????????????");

        }

    }

    private static final int RC_CAMERA_PERM = 123;

    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraTask() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            // Have permission, do the thing!

            Intent intent = new Intent(ShengWuActivity.this, UpdateFaceActivity.class);

            startActivity(intent);
            ;//??????????????????
        } else {//???????????????????????????????????????
            // Ask for one permission
            EasyPermissions.requestPermissions(this, "?????????????????????",
                    RC_CAMERA_PERM, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }
    private int imageid = 0;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals("facedata")){
                String UpdateFaceActivity = intent.getStringExtra("UpdateFaceActivity");

                if(imageid == 0){
                    if(UpdateFaceActivity != null){
                        imageid = 1;
                        String bitmap  = (String) SPUtils.get(ShengWuActivity.this, "bitmap2", "");;
                        upToServer(bitmap);
                    }else {
                        imageid = 0;
                    }
                }
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("facedata");
        //????????????
        registerReceiver(broadcastReceiver, myIntentFilter);
    }
    private void setSwitchStatus() {
        iv_fingerprint_login_switch.setImageResource(isOpen ? R.mipmap.switch_open_icon : R.mipmap.switch_close_icon);
    }

    @Override
    protected void initListener() {
        super.initListener();
        iv_fingerprint_login_switch.setOnClickListener(this);
    }
    boolean allowedScan = false;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_fingerprint_login_switch:
                dealOnOff(isOpen);
                break;
            case R.id.ll_01:
               /* PermissionGen.with(ShengWuActivity.this)
                        .addRequestCode(LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
                        .permissions(Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.CAMERA
                        ).request();*/
                popupWindow.dismiss();
                if (allowedScan){
                    Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempCameraFile()));
                    startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                }else {
                    setPermission();
                }



                break;
            case R.id.ll_02:
             /*   PermissionGen.needPermission(ShengWuActivity.this,
                        LQRPhotoSelectUtils.REQ_SELECT_PHOTO,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}
                );
                */

                popupWindow.dismiss();
                if (allowedScan){
                    Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                    startActivityForResult(albumIntent, ALBUM_REQUEST_CODE);
                }else {
                    setPermission();
                }

                break;
            case R.id.tv_cancel:
                popupWindow.dismiss();
                break;
        }
    }

    private void dealOnOff(boolean isOpen) {
        if (isOpen) {
            type = 0;
            showCloseFingerprintTipDialog();
        } else {
            openFingerprintLogin();
        }
    }

    /**
     * @description ????????????????????????
     * @author HaganWu
     * @date 2019/1/29-10:20
     */
    private void openFingerprintLogin() {
        Log.e("hagan", "openFingerprintLogin");

        helper.generateKey();
        if (fingerprintVerifyDialog == null) {
            fingerprintVerifyDialog = new FingerprintVerifyDialog(this);
        }
        fingerprintVerifyDialog.setContentText("???????????????");
        fingerprintVerifyDialog.setOnCancelButtonClickListener(new FingerprintVerifyDialog.OnDialogCancelButtonClickListener() {
            @Override
            public void onCancelClick(View v) {
                helper.stopAuthenticate();
            }
        });
        fingerprintVerifyDialog.show();
        helper.setPurpose(KeyProperties.PURPOSE_ENCRYPT);
        helper.authenticate();
    }



    private void showCloseFingerprintTipDialog() {
        if (closeFingerprintTipDialog == null) {
            closeFingerprintTipDialog = new CommonTipDialog(this);
        }
        closeFingerprintTipDialog.setContentText("?????????????????????????");
        closeFingerprintTipDialog.setSingleButton(false);
        closeFingerprintTipDialog.setOnDialogButtonsClickListener(new CommonTipDialog.OnDialogButtonsClickListener() {
            @Override
            public void onCancelClick(View v) {

            }

            @Override
            public void onConfirmClick(View v) {
                closeFingerprintLogin();
            }
        });
        closeFingerprintTipDialog.show();
    }


    @Override
    public void onAuthenticationSucceeded(String value) {
        SPUtil.getInstance().putBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN, true);
        if(type == 0){
            if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
                fingerprintVerifyDialog.dismiss();
                Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
                isOpen = true;
                setSwitchStatus();
                saveLocalFingerprintInfo();
            }
        }else {
            if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
                fingerprintVerifyDialog.dismiss();
                Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
                isOpen = false;
                SPUtil.getInstance().putBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN, false);
                setSwitchStatus();
                helper.closeAuthenticate();
            }
        }

    }

    @Override
    public void onAuthenticationFail() {
        showFingerprintVerifyErrorInfo("???????????????");
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.dismiss();
        }
        showTipDialog(errorCode, errString.toString());
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        showFingerprintVerifyErrorInfo(helpString.toString());
    }

    /**
     * @description ????????????????????????
     * @author HaganWu
     * @date 2019/1/24-14:41
     */
    private void closeFingerprintLogin() {
        type = 1;
        openFingerprintLogin();
       /* isOpen = false;
        SPUtil.getInstance().putBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN, false);
        setSwitchStatus();
        helper.closeAuthenticate();*/
    }




    private void saveLocalFingerprintInfo() {
        SPUtil.getInstance().putString(Constants.SP_LOCAL_FINGERPRINT_INFO, FingerprintUtil.getFingerprintInfoString(getApplicationContext()));
    }

    private void showFingerprintVerifyErrorInfo(String info) {
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.setContentText(info);
        }
    }

    private void showTipDialog(int errorCode, CharSequence errString) {
        if (fingerprintVerifyErrorTipDialog == null) {
            fingerprintVerifyErrorTipDialog = new CommonTipDialog(this);
        }
        //fingerprintVerifyErrorTipDialog.setContentText("errorCode:" + errorCode + "," + errString);
        fingerprintVerifyErrorTipDialog.setContentText(errString+"");
        fingerprintVerifyErrorTipDialog.setSingleButton(true);
        fingerprintVerifyErrorTipDialog.setOnSingleConfirmButtonClickListener(new CommonTipDialog.OnDialogSingleConfirmButtonClickListener() {
            @Override
            public void onConfirmClick(View v) {
                helper.stopAuthenticate();
            }
        });
        fingerprintVerifyErrorTipDialog.show();
    }


    @PermissionSuccess(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
    private void takePhoto() {
        mLqrPhotoSelectUtils.takePhoto();
    }

    @PermissionSuccess(requestCode = LQRPhotoSelectUtils.REQ_SELECT_PHOTO)
    private void selectPhoto() {
        mLqrPhotoSelectUtils.selectPhoto();
    }

    @PermissionFail(requestCode = LQRPhotoSelectUtils.REQ_TAKE_PHOTO)
    private void showTip1() {
        //        Toast.makeText(getApplicationContext(), "???????????????????????????????????????", Toast.LENGTH_SHORT).show();
        showDialog();
    }

    @PermissionFail(requestCode = LQRPhotoSelectUtils.REQ_SELECT_PHOTO)
    private void showTip2() {
        //        Toast.makeText(getApplicationContext(), "???????????????????????????????????????", Toast.LENGTH_SHORT).show();
        showDialog();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        PermissionGen.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        mLqrPhotoSelectUtils.attachToActivityForResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:       // ?????????????????????
                    File file = composBitmap(mTempCameraFile);
                    //sendImage(file);
//                    upToServer(file);
                    //ToastUtils.showToast(ShengWuActivity.this,file+"");
                    break;
                case ALBUM_REQUEST_CODE:        // ??????????????????
                    Uri uri = data.getData();
                    File file2 = BitmapHelper.decodeUriAsFile(ShengWuActivity.this, uri);
                    file = composBitmap(file2);
//                    upToServer(file);
                    break;
            }
        }
     if(resultCode == 1){
            String imageData = data.getStringExtra("data");
            upToServer(imageData);
     }
    }

    public void showDialog() {
        //????????????????????????
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //??????????????????????????????
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        //????????????
        builder.setTitle("????????????");
        //????????????
        builder.setMessage("?????????-??????-i?????????-?????? ??????????????????????????????????????????????????????????????????????????????");

        //??????????????????????????????
        builder.setPositiveButton("?????????", new DialogInterface.OnClickListener() {//???????????????????????????????????????

            @Override
            public void onClick(DialogInterface dialog, int which) {
                //????????????????????????????????????????????????????????????
                Intent intent = new Intent(Settings.ACTION_SETTINGS);
//                intent.setData(Uri.parse("package:" + ShengWuActivity.this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        //??????????????????????????????
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        //???????????????????????????????????????
        AlertDialog dialog = builder.create();
        dialog.show();//???????????????
    }


    private void openPopupWindow(View v) {
        //?????????????????????
        if (popupWindow != null && popupWindow.isShowing()) {
            return;
        }
        //??????PopupWindow???View
        View view = LayoutInflater.from(this).inflate(R.layout.view_popupwindow, null);
        popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        //????????????,??????????????????????????????????????????
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //?????????????????????????????????
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(true);
        //????????????
        popupWindow.setAnimationStyle(R.style.PopupWindow);
        //????????????
        popupWindow.showAtLocation(v, Gravity.BOTTOM, 0, 0);
        //??????????????????
        popupWindow.setOnDismissListener(this);
        //??????PopupWindow???View????????????
        setOnPopupViewClick(view);
        //???????????????
        setBackgroundAlpha(0.5f);
    }

    private void setOnPopupViewClick(View view) {
        TextView tv_pick_phone, tv_pick_zone, tv_cancel;
        tv_pick_phone = (TextView) view.findViewById(R.id.ll_01);
        tv_pick_zone = (TextView) view.findViewById(R.id.ll_02);
        tv_cancel = (TextView) view.findViewById(R.id.tv_cancel);
        tv_pick_phone.setOnClickListener(this);
        tv_pick_zone.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);
    }


    @Override
    public void onDismiss() {
        setBackgroundAlpha(1);
    }

    //??????????????????????????????
    public void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = alpha;
        getWindow().setAttributes(lp);
    }

    /**
     * ??????????????????Base64??????????????????
     */
    public static String imageToBase64(String path){
        if(TextUtils.isEmpty(path)){
            return null;
        }
        InputStream is = null;
        byte[] data = null;
        String result = null;
        try{
            is = new FileInputStream(path);
            //???????????????????????????????????????
            data = new byte[is.available()];
            //????????????
            is.read(data);
            //????????????????????????????????????
            result = Base64.encodeToString(data,Base64.DEFAULT);
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            if(null !=is){
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        return result;
    }


    private File composBitmap(File file) {
        Bitmap bitmap = BitmapHelper.revisionImageSize(file);
        return BitmapHelper.saveBitmap2file(this, bitmap);
    }

    private File mTempCameraFile;
    private File getTempCameraFile() {
        if (mTempCameraFile == null)
            mTempCameraFile = getTempMediaFile();
        return mTempCameraFile;
    }

    /**
     * ???????????????file
     */
    public File getTempMediaFile() {
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            String fileName = getTempMediaFileName();
            file = new File(fileName);
        }
        return file;
    }

    public String getTempMediaFileName() {
        return getParentPath() + "image" + System.currentTimeMillis() + ".jpg";
    }

    private String getParentPath() {
        String parent = Environment.getExternalStorageDirectory()
                + File.separator + ROOT_NAME + File.separator;
        mkdirs(parent);
        return parent;
    }

    public boolean mkdirs(String path) {
        File file = new File(path);
        return !file.exists() && file.mkdirs();
    }


    public void upToServer(String sresult){
        OkGo.<String>post(UrlRes.HOME2_URL+ UrlRes.addFaceUrl)
                .params( "openId",AesEncryptUtile.openid)
                .params( "memberId",userId)
                .params( "img",sresult )
                .params( "code","" )
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {

                        Log.e("????????????",response.body());
                        AddFaceBean faceBean = JsonUtil.parseJson(response.body(),AddFaceBean.class);
                        boolean success = faceBean.getSuccess();
                        String msg = faceBean.getMsg();
                        ViewUtils.cancelLoadingDialog();
                        if(success == true){
                            ToastUtils.showToast(ShengWuActivity.this,msg);
                            imageid = 0;
                        }else {
                            ToastUtils.showToast(ShengWuActivity.this,msg);
                            imageid = 0;
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ViewUtils.cancelLoadingDialog();
                        ToastUtils.showToast(getApplicationContext(),"???????????????????????????????????????");
                        imageid = 0;
                    }
                });
    }

    /**????????????*/
    private void setPermission() {
        //????????????????????????
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .requestEach(Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                )
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // ???????????????????????????
                            Log.e("???????????????????????????", permission.name + " is granted.");
//                            Intent intent = new Intent(MyApp.getInstance(), QRScanActivity.class);
//                            startActivity(intent);
                            allowedScan = true;
                            //   Log.d(TAG, permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            Log.e("????????????????????????", permission.name + " is denied. More info should be provided.");
                            // ????????????????????????????????????????????????????????????Never ask again???,??????????????????????????????????????????????????????????????????
                            //   Log.d(TAG, permission.name + " is denied. More info should be provided.");
                            allowedScan = false;
                        } else {
                            // ?????????????????????????????????????????????????????????
                            //   Log.d(TAG, permission.name + " is denied.");
                            Log.e("????????????????????????", permission.name + permission.name + " is denied.");
                            allowedScan = true;
                        }
                    }
                });
    }


    /**
     *
     * @param requestCode
     * @param permissions ???????????????
     * @param grantResults ???????????????????????????
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            // camear ????????????

            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

                // ??????????????????
                Toast.makeText(getApplicationContext(), " user Permission" , Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(ShengWuActivity.this,UpdateFaceActivity.class);

                startActivity(intent);


            } else {

                //??????????????????
                Toast.makeText(getApplicationContext(), " no Permission" , Toast.LENGTH_SHORT).show();

            }



        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterReceiver(broadcastReceiver);
    }
}
