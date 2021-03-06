package io.cordova.zhqy.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.net.URLEncoder;

import butterknife.BindView;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.VerCodeBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.FinishActivity;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.ToastUtils;

import static io.cordova.zhqy.UrlRes.updatePasswordUrl;
import static io.cordova.zhqy.utils.AesEncryptUtile.key;

/**
 * Created by Administrator on 2019/5/16 0016.
 */

public class FindPwdNextActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.rl_next)
    RelativeLayout rl_next;

    @BindView(R.id.rl_back)
    RelativeLayout rl_back;

    @BindView(R.id.et_01)
    EditText et_01;

    @BindView(R.id.et_02)
    EditText et_02;

    @BindView(R.id.tv_app_setting)
    ImageView tv_app_setting;

    String member;
    String code;
    String typeU;
    @Override
    protected int getResourceId() {
        return R.layout.activity_find_pwd2;
    }

    @Override
    protected void initView() {
        super.initView();
        tv_app_setting.setOnClickListener(this);
        rl_next.setOnClickListener(this);
        rl_back.setOnClickListener(this);

        member = getIntent().getStringExtra("member");
        code = getIntent().getStringExtra("verificationCode");
        typeU = getIntent().getStringExtra("type");
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_next:

                String pwd01 = et_01.getText().toString().trim();
                String pwd02 = et_02.getText().toString().trim();
                if(pwd01.equals("") || pwd02.equals("")){
                    ToastUtils.showToast(this,"???????????????");
                    return;
                }

                if(!pwd01.equals(pwd02)){
                    ToastUtils.showToast(this,"??????????????????????????????");
                    return;
                }

                if(!isLetterDigit(pwd01) || !isLetterDigit(pwd02)){
                    ToastUtils.showToast(FindPwdNextActivity.this,"?????????????????????????????????!");
                    return;

                }

                chagePwd(pwd01);

                break;
            case R.id.tv_app_setting:
                finish();
                break;
            case R.id.rl_back:
                finish();
                break;
        }
    }
    String updateType;
    private void chagePwd(String pwd01) {
        try {
            String pwd = URLEncoder.encode(AesEncryptUtile.encrypt(pwd01, key), "UTF-8");
            String type = URLEncoder.encode(AesEncryptUtile.encrypt("1", key), "UTF-8");
            if(typeU.equals("3")){//??????
                updateType = URLEncoder.encode(AesEncryptUtile.encrypt("3", key), "UTF-8");
            }else if(typeU.equals("2")){//??????
                updateType = URLEncoder.encode(AesEncryptUtile.encrypt("2", key), "UTF-8");
            }
            OkGo.<String>get(UrlRes.HOME2_URL +updatePasswordUrl)
                    .params("openId",AesEncryptUtile.openid)
                    .params("memberId",member)
                    .params("memberPwd",pwd)
                    .params("code",code)
                    .params("isUpdateDr",type)
                    .params("type",updateType)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("???????????????",response.body());

                            VerCodeBean verCodeBean = JsonUtil.parseJson(response.body(),VerCodeBean.class);
                            boolean success = verCodeBean.getSuccess();
                            if(success){
                                Intent intent = new Intent(FindPwdNextActivity.this,FindPwdCompleteActivity.class);
                                startActivity(intent);
                                FinishActivity.addActivity(FindPwdNextActivity.this);
                            }else {
                                ToastUtils.showToast(FindPwdNextActivity.this,verCodeBean.getMsg());
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

    public static boolean isLetterDigit(String str) {
        boolean isDigit = false;//????????????boolean????????????????????????????????????
        boolean isLetter = false;//????????????boolean????????????????????????????????????
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i))) {   //???char?????????????????????????????????????????????????????????
                isDigit = true;
            } else if (Character.isLetter(str.charAt(i))) {  //???char?????????????????????????????????????????????????????????
                isLetter = true;
            }
        }
        String regex = "^(?![\\d]+$)(?![a-zA-Z]+$)(?![^\\da-zA-Z]+$).{8,20}$";
        //boolean isRight = isDigit && isLetter && str.matches(regex);
        boolean isRight = str.matches(regex);
        return isRight;
    }

}
