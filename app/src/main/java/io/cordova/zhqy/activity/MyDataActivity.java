package io.cordova.zhqy.activity;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.StringSignature;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.OnClick;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.widget.XCRoundImageView;
import io.reactivex.functions.Consumer;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.CurrencyBean;
import io.cordova.zhqy.bean.GetServiceImgBean;
import io.cordova.zhqy.bean.UserMsgBean;

import io.cordova.zhqy.utils.BaseActivity2;
import io.cordova.zhqy.utils.CircleCrop;

import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ViewUtils;


/**
 * Created by Administrator on 2018/11/21 0021.
 */

public class MyDataActivity extends BaseActivity2 {

    @BindView(R.id.iv_user_head)
    XCRoundImageView ivUserHead;
    @BindView(R.id.tv_user_name)
    TextView tvUserName;
    //    @BindView(R.id.tv_company)
//    TextView tvCompany;
    @BindView(R.id.tv_student_number)
    TextView tvStudentNumber;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_grade)
    TextView tvGender;
    @BindView(R.id.tv_nation)
    TextView tvNation;
    @BindView(R.id.tv_department)
    TextView tvDepartment;
    @BindView(R.id.tv_major)
    TextView tvMajor;
    @BindView(R.id.tv_sex)
    TextView tvSex;
    @BindView(R.id.tv_class)
    TextView tvClass;
    @BindView(R.id.tv_birthday)
    TextView tvBirthday;
    @BindView(R.id.tv_native_place)
    TextView tvNativePlace;
    @BindView(R.id.tv_mobile)
    TextView tvMobile;

//    @BindView(R.id.rv_user_data)
//    RecyclerView rvUserData;

    @BindView(R.id.tv_type)
    TextView tv_type;


    @BindView(R.id.tv_app_setting)
            ImageView tv_app_setting;

    String mMobile;
    boolean allowedScan = false;
    @Override
    protected int getResourceId() {
        return R.layout.activity_my_data;
    }

    @OnClick({R.id.iv_user_head,R.id.ll_my_mobile})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_user_head:

                //????????????????????????
                //setPermission();
               /* if (allowedScan){
                  selectImg();
                }else {
                    Toast.makeText(this,"????????????????????????",Toast.LENGTH_SHORT).show();
                    setPermission();
                }*/
                break;
            case R.id.ll_my_mobile:
                //???????????????????????????
//                Intent intent = new Intent(MyApp.getInstance(),MyDataChangesActivity.class);
//                if (!StringUtils.isEmpty(mMobile)){
//                    intent.putExtra("mMobile",mMobile);
//                }
//                startActivity(intent);
                break;
        }
    }


    @Override
    protected void initView() {
        super.initView();
        tv_app_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        netWorkUserMsg();
    }




    UserMsgBean userMsgBean;
    private void netWorkUserMsg() {
        ViewUtils.createLoadingDialog(this);
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.User_Msg)
                .cacheMode(CacheMode.NO_CACHE)//??????????????????
                .cacheKey("mydata")
                .params("userId", (String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.body());
                        userMsgBean = JSON.parseObject(response.body(), UserMsgBean.class);
                        ViewUtils.cancelLoadingDialog();
                        if (userMsgBean.isSuccess()) {
                            if(userMsgBean.getObj() != null){
                                tvUserName.setText(userMsgBean.getObj().getModules().getMemberNickname());
                                tvStudentNumber.setText(userMsgBean.getObj().getModules().getMemberUsername());
                                tvName.setText(userMsgBean.getObj().getModules().getMemberNickname());

                                if (userMsgBean.getObj().getModules().getMemberSex() == 1) {
                                    tvSex.setText("???");
                                }else {
                                    tvSex.setText("???");
                                }


                                try{
                                    tvNation.setText(userMsgBean.getObj().getModules().getMemberOtherNation());
                                    tvDepartment.setText(userMsgBean.getObj().getModules().getMemberOtherDepartment());
                                    tvMajor.setText(userMsgBean.getObj().getModules().getMemberOtherMajor());
                                    tvGender.setText(userMsgBean.getObj().getModules().getMemberOtherGrade());
                                    tvClass.setText(userMsgBean.getObj().getModules().getMemberOtherClass());
                                    tvBirthday.setText(userMsgBean.getObj().getModules().getMemberOtherBirthday());
                                    tvNativePlace.setText(userMsgBean.getObj().getModules().getMemberOtherNative());
                                    tvMobile.setText(userMsgBean.getObj().getModules().getMemberPhone());
                                }catch (Exception e){

                                }

                                mMobile = userMsgBean.getObj().getModules().getMemberPhone();
                                netGetUserHead();
                            }else {
                                ToastUtils.showToast(MyDataActivity.this,"????????????????????????!");
                                ViewUtils.cancelLoadingDialog();
                            }

                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ViewUtils.cancelLoadingDialog();
                        ToastUtils.showToast(MyApp.getInstance(), "?????????????????????????????????");
                    }
                });

    }


    private void netGetUserHead() {

        try {
            String pwd = URLEncoder.encode(userMsgBean.getObj().getModules().getMemberPwd(),"UTF-8");
            String ingUrl =  UrlRes.HOME2_URL+"/authentication/public/getHeadImg?memberId="+userMsgBean.getObj().getModules().getMemberUsername()+"&pwd="+pwd;

            Glide.with(this)
                    .load(ingUrl)
                    .asBitmap()
                    .placeholder(R.mipmap.tabbar_user_pre2)
                    .signature(new StringSignature(UUID.randomUUID().toString()))
                    .into(ivUserHead);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private String img64Head;
    private String headPath;
    private static final int UP_Header = 55;//?????????????????????
    private List<Uri> result;
    private List<String> path;
    private void selectImg() {
        Matisse.from(this)
                .choose(MimeType.ofImage())//????????????
                .countable(true)//true:?????????????????????;false:?????????????????????
                .maxSelectable(1)//??????????????????
                .capture(true)//????????????????????????????????????
                .captureStrategy(new CaptureStrategy(true, "com.niu.qianyuan.jiancai.fileprovider"))//??????1 true????????????????????????????????????false????????????????????????????????????2??? AndroidManifest???authorities????????????????????????7.0?????? ????????????
                .imageEngine(new GlideEngine())//??????????????????
                .forResult(UP_Header);//
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == UP_Header&& resultCode == RESULT_OK) {
            result = Matisse.obtainResult(data);
            path = Matisse.obtainPathResult(data);
//            Glide.with(MyApp.getInstance())
//                        .load(path.get(0))
//                        .transform(new CircleCrop(getApplicationContext()))
//                        .error(R.mipmap.tabbar_user_pre)
//                        .into(ivUserHead);
            //  textView.setText(result.toString());
    //        L.e("path", path.toString());
//            for (int i = 0; i <path.size() ; i++) {
//                //   PictureUtil.compressImage(result.get(i).toString(), path.get(i), 30);
//                Glide.with(MyApp.getInstance())
//                        .load(path.get(i))
//                        .error(R.mipmap.tabbar_user_pre)
//                        .into(ivUserHead);
//
//                headPath = path.get(i);
//             //   img64Head = StringUtils.imageToBase64(path.get(i));
//               // Log.i("base64",img64Head);
//                upImg();//"data:image/png;base64,"
//            }
            upImg();//"data:image/png;base64,"
        }
    }

    GetServiceImgBean getServiceImgBean;
    //???????????????????????????
    private void upImg() {
        OkGo.<String>post(UrlRes.HOME_URL+ UrlRes.Upload_Img)
                .tag(this)
                .isMultipart(true)
                .params( "file",new File(path.get(0)) )
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        //handleResponse(response);
                        Log.e("tag",response.body());
                        getServiceImgBean = JSON.parseObject(response.body(),GetServiceImgBean.class);
                        if (getServiceImgBean.isSuccess()){
                            gettImgUrl(getServiceImgBean.getObj());
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ToastUtils.showToast(getApplicationContext(),"???????????????????????????????????????");
                    }
                });
    }
//  Log.e("UPImg",response.body());
//    /*????????????????????????Url*/
//    gettImgUrl();
    /*??????????????????*/
    CurrencyBean currencyBean;//??????Bean
    private void gettImgUrl(final String obj) {
        OkGo.<String>post(UrlRes.HOME_URL+ UrlRes.Get_Img_uri)
                .tag(this)
                .params("imgageUrl",obj)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("gettImgUrl",response.body());
                        currencyBean = JSON.parseObject(response.body(),CurrencyBean.class);
                            if (!obj.isEmpty()){
                                Glide.with(MyApp.getInstance())
                                        .load(UrlRes.HOME3_URL+ obj)
                                        .transform(new CircleCrop(getApplicationContext()))
                                        .error(R.mipmap.tabbar_user_pre)
                                        .into(ivUserHead);
                            }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ToastUtils.showToast(getApplicationContext(),"???????????????????????????????????????");
                    }
                });
    }

    //????????????????????????url
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
                            //   Log.d(TAG, permission.name + " is granted.");
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            Log.e("????????????????????????", permission.name + " is denied. More info should be provided.");
                            // ????????????????????????????????????????????????????????????Never ask again???,??????????????????????????????????????????????????????????????????
                            //   Log.d(TAG, permission.name + " is denied. More info should be provided.");
                        } else {
                            // ?????????????????????????????????????????????????????????
                            //   Log.d(TAG, permission.name + " is denied.");
                            Log.e("????????????????????????", permission.name + permission.name + " is denied.");
                        }
                    }
                });
    }

}
