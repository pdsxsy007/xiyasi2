package io.cordova.zhqy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.net.URLEncoder;
import java.util.Calendar;

import butterknife.BindView;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.MemberBean;
import io.cordova.zhqy.bean.VerCodeBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.CountDownTimerUtils;
import io.cordova.zhqy.utils.FinishActivity;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MyEditText;
import io.cordova.zhqy.utils.ToastUtils;

import static io.cordova.zhqy.UrlRes.getUserInfoByMemberIdUrl;
import static io.cordova.zhqy.UrlRes.sendVerificationUrl;
import static io.cordova.zhqy.UrlRes.verificationUrl;
import static io.cordova.zhqy.utils.AesEncryptUtile.key;

/**
 * Created by Administrator on 2019/5/16 0016.
 */

public class FindPwdActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.rl_next)
    RelativeLayout rl_next;

    @BindView(R.id.tv_app_setting)
    ImageView tv_app_setting;

    @BindView(R.id.ll_01)
    LinearLayout ll_01;

    @BindView(R.id.ll_02)
    LinearLayout ll_02;

    @BindView(R.id.iv_01)
    ImageView iv_01;

    @BindView(R.id.iv_02)
    ImageView iv_02;

    @BindView(R.id.rl_code)
    RelativeLayout rl_code;

    @BindView(R.id.tv_type)
    TextView tv_type;

    @BindView(R.id.et_type)
    TextView et_type;

    @BindView(R.id.et_username)
    MyEditText et_username;

    @BindView(R.id.tv_getcode)
    TextView tv_getcode;


    @BindView(R.id.et_code)
    EditText et_code;

    @BindView(R.id.btn)
    Button btn;

    private int type = 3;


    @Override
    protected int getResourceId() {
        return R.layout.activity_find_pwd1;
    }

    @Override
    protected void initView() {
        super.initView();
        tv_app_setting.setOnClickListener(this);
        rl_next.setOnClickListener(this);
        ll_01.setOnClickListener(this);
        ll_02.setOnClickListener(this);
        rl_code.setOnClickListener(this);
        btn.setOnClickListener(this);
        rl_next.setOnClickListener(this);
        tv_getcode.setOnClickListener(this);
        et_username.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    // ???????????????????????????????????????
                } else {
                    // ???????????????????????????????????????
                    String content = et_username.getText().toString().trim();
                    if(content.equals("")){
                        ToastUtils.showToast(FindPwdActivity.this,"???????????????");
                    }else {
                        Log.e("2","2");
                        getMemberInfo(content);
                    }
                }
            }
        });

        et_username.setOnFinishComposingListener(new MyEditText.OnFinishComposingListener() {
            @Override
            public void finishComposing() {
                Log.e("1","1");
                String content = et_username.getText().toString().trim();
                getMemberInfo(content);
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_next:

                checkCode();


                break;
            case R.id.tv_getcode://?????????????????????

                getCode();
                break;
            case R.id.tv_app_setting:
                finish();
                break;
            case R.id.ll_01:
                String content = et_username.getText().toString().trim();
                getMemberInfo(content);
                iv_01.setImageResource(R.mipmap.cb02);
                iv_01.setColorFilter(Color.parseColor("#1d4481"));
                iv_02.setImageResource(R.mipmap.cb01);
                iv_02.setColorFilter(Color.parseColor("#c2c2c2"));
                tv_type.setText("????????????");

                type = 3;

                break;
            case R.id.ll_02:
                String content1 = et_username.getText().toString().trim();
                getMemberInfo(content1);
                iv_01.setImageResource(R.mipmap.cb01);
                iv_01.setColorFilter(Color.parseColor("#c2c2c2"));
                iv_02.setImageResource(R.mipmap.cb02);
                iv_02.setColorFilter(Color.parseColor("#1d4481"));
                tv_type.setText("?????????");

                type = 2;
                break;
            case R.id.btn:
                String name = et_username.getText().toString().trim();
                if(name.equals("")){
                    ToastUtils.showToast(FindPwdActivity.this,"???????????????!");
                    return;
                }

                if(type == 3){//?????????????????????
                    if(checkEmailType.equals("")){
                        ToastUtils.showToast(FindPwdActivity.this,"???????????????????????????????????????????????????!");
                        return;
                    }
                }


                if(type == 2){//?????????????????????
                    if(checkPhoneType.equals("")){
                        ToastUtils.showToast(FindPwdActivity.this,"??????????????????????????????????????????????????????!");
                        return;
                    }
                }



                getCode();


                break;

        }
    }

    private void checkCode() {

        /* dlm ????????????
        type ???????????????(0:????????????,1:????????????,2:??????????????????,3:??????????????????,4:???????????????,5:????????????,6:????????????)
        contact ????????????(??????????????????)
                openId*/
        String content = et_username.getText().toString().trim();
        String code = et_code.getText().toString().trim();
        if(content.equals("")){
            ToastUtils.showToast(FindPwdActivity.this,"???????????????!");
            return;
        }

        if(code.equals("")){
            ToastUtils.showToast(FindPwdActivity.this,"??????????????????!");
            return;
        }

        try {
            final String username = AesEncryptUtile.encrypt(content + "_" + Calendar.getInstance().getTimeInMillis(), key);
            final String type0 = URLEncoder.encode(AesEncryptUtile.encrypt(type+"", key), "UTF-8");
            String contact = URLEncoder.encode(AesEncryptUtile.encrypt(et_type.getText().toString().trim(), key), "UTF-8");
            final String vcode = URLEncoder.encode(AesEncryptUtile.encrypt(code, key), "UTF-8");
            OkGo.<String>get(UrlRes.HOME2_URL +verificationUrl)
                    .params("openid",AesEncryptUtile.openid)
                    .params("memberId",username)
                    .params("type",type0)
                    .params("verificationCode",vcode)
                    .params("contact",contact)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("???????????????",response.body());

                            VerCodeBean verCodeBean = JsonUtil.parseJson(response.body(),VerCodeBean.class);
                            boolean success = verCodeBean.getSuccess();
                            if(success){
                                Intent intent = new Intent(FindPwdActivity.this,FindPwdNextActivity.class);
                                intent.putExtra("member",username);
                                intent.putExtra("verificationCode",vcode);
                                intent.putExtra("type",type+"");
                                startActivity(intent);
                                FinishActivity.addActivity(FindPwdActivity.this);
                            }else {
                                ToastUtils.showToast(FindPwdActivity.this,verCodeBean.getMsg());
                            }

                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);

                        }
                    });

        } catch (Exception e) {

        }

    }

    private void getCode() {
       /* dlm ????????????
        type ???????????????(0:????????????,1:????????????,2:??????????????????,3:??????????????????,4:???????????????,5:????????????,6:????????????)
        contact ????????????(??????????????????)
                openId*/
        String content = et_username.getText().toString().trim();
        try {
            String username = AesEncryptUtile.encrypt(content + "_" + Calendar.getInstance().getTimeInMillis(), key);
            String type0 = URLEncoder.encode(AesEncryptUtile.encrypt(type+"", key), "UTF-8");
            String contact = URLEncoder.encode(AesEncryptUtile.encrypt(et_type.getText().toString().trim(), key), "UTF-8");
            OkGo.<String>get(UrlRes.HOME2_URL +sendVerificationUrl)
                    .params("openId",AesEncryptUtile.openid)
                    .params("dlm",username)
                    .params("type",type0)
                    .params("contact",contact)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("?????????",response.body());

                            VerCodeBean verCodeBean = JsonUtil.parseJson(response.body(),VerCodeBean.class);
                            boolean success = verCodeBean.getSuccess();
                            if(success){
                                int frequency = verCodeBean.getAttributes().getFrequency();
                                CountDownTimerUtils mCountDownTimerUtils = new CountDownTimerUtils(btn, frequency*60*1000, 1000);
                                mCountDownTimerUtils.start();
                            }else {
                                ToastUtils.showToast(FindPwdActivity.this,verCodeBean.getMsg());
                            }

                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);

                        }
                    });

        } catch (Exception e) {

        }

    }

    private String checkEmailType = "";
    private String checkPhoneType = "";
    private void getMemberInfo(String content) {
        try {
            String username = AesEncryptUtile.encrypt(content + "_" + Calendar.getInstance().getTimeInMillis(), key);
            OkGo.<String>get(UrlRes.HOME2_URL +getUserInfoByMemberIdUrl)
                    .params("openId",AesEncryptUtile.openid)
                    .params("memberUsername",username)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("result1",response.body());


                            MemberBean memberBean = JsonUtil.parseJson(response.body(),MemberBean.class);
                            if(memberBean != null){
                                if(memberBean.getObj() != null){
                                    String memberMailbox = memberBean.getObj().getMemberMailbox();
                                    String memberPhone = memberBean.getObj().getMemberPhone();
                                    checkEmailType = memberMailbox;
                                    checkPhoneType = memberPhone;
                                    if(type ==3 ){
                                        if(memberMailbox.equals("")){
                                            et_type.setText("?????????????????????");
                                        }else {
                                            et_type.setText(memberMailbox);
                                        }

                                    }else {
                                        if(memberPhone.equals("")){
                                            et_type.setText("?????????????????????");
                                        }else {
                                            et_type.setText(memberPhone);
                                        }

                                    }

                                }
                            }



                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
