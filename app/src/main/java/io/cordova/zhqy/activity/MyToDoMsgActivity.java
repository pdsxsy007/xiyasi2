package io.cordova.zhqy.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.CountBean;
import io.cordova.zhqy.bean.NoticeInfoBean;
import io.cordova.zhqy.bean.OAMsgListBean;
import io.cordova.zhqy.bean.SysMsgBean;
import io.cordova.zhqy.utils.BaseActivity2;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;

import static io.cordova.zhqy.UrlRes.findLoginTypeListUrl;

/**
 * Created by Administrator on 2018/11/22 0022.
 */

public class MyToDoMsgActivity extends BaseActivity2 {
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.tv_msg_num)
    TextView tv_msg_num;

    @BindView(R.id.db_msg_num)
    TextView db_msg_num;

    @BindView(R.id.dy_msg_num)
    TextView dy_msg_num;

    @BindView(R.id.dy_msg_present)
    TextView dy_msg_present;

    @BindView(R.id.yb_msg_num)
    TextView yb_msg_num;

    @BindView(R.id.yy_msg_num)
    TextView yy_msg_num;

    @BindView(R.id.db_msg_present)
    TextView db_msg_present;

    @BindView(R.id.yb_msg_present)
    TextView yb_msg_present;

    @BindView(R.id.yy_msg_present)
    TextView yy_msg_present;

    @BindView(R.id.my_msg_present)
    TextView my_msg_present;

    @BindView(R.id.system_msg_present)
    TextView system_msg_present;
    @BindView(R.id.dy_msg_num2)
    TextView dy_msg_num2;
    @BindView(R.id.dy_msg_present2)
    TextView dy_msg_present2;

    @Override
    protected int getResourceId() {
        return R.layout.activity_my_to_do_msg;
    }

    @Override
    protected void initView() {
        super.initView();
        tvTitle.setText("????????????");

//        netWorkYyMsg();
    }




    @OnClick({R.id.ll_system_msg, R.id.ll_db_msg, R.id.ll_dy_msg, R.id.ll_yb_msg, R.id.ll_yy_msg, R.id.ll_my_msg,R.id.ll_email})
    public void onViewClicked(View view) {
        Intent intent;
        if(sign.equals("2")){
            switch (view.getId()) {

                case R.id.ll_system_msg://????????????
                    intent = new Intent(MyApp.getInstance(), SystemMsgActivity.class);
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_db_msg://d????????????
                    intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                    intent.putExtra("type","1");
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_dy_msg://????????????
                    intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                    intent.putExtra("type","2");
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_yb_msg://????????????
                    intent = new Intent(MyApp.getInstance(), OaMsgYBActivity.class);
                    intent.putExtra("type","3");
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_yy_msg://????????????
                    intent = new Intent(MyApp.getInstance(), OaMsgYBActivity.class);
                    intent.putExtra("type","4");
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_my_msg://????????????
                    //intent = new Intent(MyApp.getInstance(), MyShenqingActivity.class);
                 /*   intent = new Intent(MyApp.getInstance(), OaMsgYBActivity.class);
                    intent.putExtra("type","5");
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);*/

                    intent = new Intent(MyApp.getInstance(), MyShenqingActivity.class);
                    intent.putExtra("type","sq");
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_email://????????????
                    String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                    if(isOpen.equals("") || isOpen.equals("1")){
                        intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                    }else {
                        intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                    }
                    intent.putExtra("appUrl","http://kys.zzuli.edu.cn/cas/login?service=http://mail.zzuli.edu.cn/coremail/cmcu_addon/sso.jsp?face=hxphone");
                    startActivity(intent);
                    break;

            }
        }else {
            switch (view.getId()) {
                case R.id.ll_system_msg://????????????
                    intent = new Intent(MyApp.getInstance(), SystemMsgActivity.class);
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_db_msg://d????????????
                    intent = new Intent(MyApp.getInstance(), OaMsgActivity2.class);
                    intent.putExtra("type","db");
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_dy_msg://????????????
                    intent = new Intent(MyApp.getInstance(), OaMsgActivity2.class);
                    intent.putExtra("type","dy");
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_yb_msg://????????????
                    intent = new Intent(MyApp.getInstance(), OaMsgActivity2.class);
                    intent.putExtra("type","yb");
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_yy_msg://????????????
                    intent = new Intent(MyApp.getInstance(), OaMsgActivity2.class);
                    intent.putExtra("type","yy");
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_my_msg://????????????
                    intent = new Intent(MyApp.getInstance(), MyShenqingActivity.class);
                    intent.putExtra("type","sq");
                    intent.putExtra("msgType","????????????");
                    startActivity(intent);
                    break;
                case R.id.ll_email://????????????
                    String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                    if(isOpen.equals("") || isOpen.equals("1")){


                        intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                    }else {
                        intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                    }
                    intent.putExtra("flag","1");
                    intent.putExtra("appUrl","http://kys.zzuli.edu.cn/cas/login?service=http://mail.zzuli.edu.cn/coremail/cmcu_addon/sso.jsp?face=hxphone");
                    startActivity(intent);
                    break;

            }
        }

    }

   /** ??????????????????*/

    private void netWorkSystemMsg() {
        String userId = (String) SPUtils.get(MyApp.getInstance(), "userId", "");
        Log.e("userId",userId);
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_countUnreadMessagesForCurrentUser)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {

                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        Log.e("??????????????????",countBean.getObj());
                        //yy_msg_num.setText(countBean.getCount()+"");
                        if(countBean.getObj().equals("0")){
                            system_msg_present.setText("??????????????????????????????");
                            tv_msg_num.setText(countBean.getObj()+"");
                        }else {
                            tv_msg_num.setText(countBean.getObj()+"");
                            system_msg_present.setText("?????????"+countBean.getObj()+"?????????????????????");
                        }

                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("s",response.toString());
                    }
                });
    }


    /**
     * ??????
     */
    private void netWorkDbMsg() {
        netWorkOAToDoMsg();
    }

    /**
     * ??????
     */
    private void netWorkDyMsg() {
        netWorkOAToDoMsg1();
    }


    /**
     * ??????
     */
    private void netWorkYbMsg() {
        netWorkOAToDoMsg2();
    }


    /**
     * ??????
     */
    private void netWorkYyMsg() {
        netWorkOAToDoMsg3();
    }

    private void netWorkSqMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.countUnreadMessagesForCurrentUserUrl)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "5")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("????????????",response.body());
                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        //yy_msg_num.setText(countBean.getCount()+"");
                        my_msg_present.setText("?????????"+countBean.getObj()+"???");
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

    }

    private void netWorkSqMsg2() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_count)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "sq")
                .params("workType", "worksq")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());
                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        //yy_msg_num.setText(countBean.getCount()+"");
                        my_msg_present.setText("?????????"+countBean.getCount()+"???");
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });

    }
    private void netWorkOAToDoMsg2() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.countUnreadMessagesForCurrentUserUrl)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "3")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("??????",response.body());
                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        yb_msg_num.setText(countBean.getObj()+"");
                        yb_msg_present.setText("????????????"+countBean.getObj()+"???");
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    private void netWorkOAToDoMsg3() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.countUnreadMessagesForCurrentUserUrl)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "4")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("??????",response.body());
                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        yy_msg_num.setText(countBean.getObj()+"");
                        yy_msg_present.setText("????????????"+countBean.getObj()+"???");
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }


    private void netWorkOAToDoMsg1() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.countUnreadMessagesForCurrentUserUrl)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "2")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("??????",response.body());
                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        dy_msg_num.setText(countBean.getObj()+"");
                        if(countBean.getObj().equals("0")){
                            dy_msg_present.setText("????????????????????????");
                        }else {
                            dy_msg_present.setText("?????????????????????"+countBean.getObj()+"???");
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
    private void netWorkOAToDoMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.countUnreadMessagesForCurrentUserUrl)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "1")//(1:??????,2:??????,3:??????,4:??????,5:??????)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("??????",response.body());
                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        db_msg_num.setText(countBean.getObj()+"");
                        if(countBean.getObj().equals("0")){
                            db_msg_present.setText("?????????????????????????????????");
                        }else {
                            db_msg_present.setText("?????????????????????"+countBean.getObj()+"???");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });



    }
    private void netWorkOAToDoMsg2_old() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_count)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "yb")
                .params("workType", "workdb")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());
                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        yb_msg_num.setText(countBean.getCount()+"");
                        yb_msg_present.setText("????????????"+countBean.getCount()+"???");
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                    }
                });
    }

    private void netWorkOAToDoMsg3_old() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_count)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "yy")
                .params("workType", "workdb")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());
                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        yy_msg_num.setText(countBean.getCount()+"");
                        yy_msg_present.setText("????????????"+countBean.getCount()+"???");
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }


    private void netWorkOAToDoMsg1_old() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_count)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "dy")
                .params("workType", "workdb")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());
                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        dy_msg_num.setText(countBean.getCount()+"");
                        if(countBean.getCount() == 0){
                            dy_msg_present.setText("????????????????????????");
                        }else {
                            dy_msg_present.setText("?????????????????????"+countBean.getCount()+"???");
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
    private void netWorkOAToDoMsg_old() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_count)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "db")
                .params("workType", "workdb")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());
                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        db_msg_num.setText(countBean.getCount()+"");
                        if(countBean.getCount() == 0){
                            db_msg_present.setText("?????????????????????????????????");
                        }else {
                            db_msg_present.setText("?????????????????????"+countBean.getCount()+"???");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });

    }
   String sign;
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
                            sign = configValue;
                            if(configValue.equals("1")){
                                netWorkOAToDoMsg2_old();
                                netWorkOAToDoMsg3_old();
                                netWorkOAToDoMsg_old();
                                netWorkOAToDoMsg1_old();
                                netWorkSqMsg2();
                            }else{
                                netWorkDbMsg();
                                netWorkDyMsg();
                                netWorkYbMsg();
                                netWorkYyMsg();
                                netWorkSqMsg2();
                            }
                        }else {
                            netWorkDbMsg();
                            netWorkDyMsg();
                            netWorkYbMsg();
                            netWorkYyMsg();
                            netWorkSqMsg2();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        netWorkDbMsg();
                        netWorkDyMsg();
                        netWorkYbMsg();
                        netWorkYyMsg();
                        netWorkSqMsg2();
                    }
                });


    }


    @Override
    protected void onResume() {
        super.onResume();
        netWorkSystemMsg();
        getBack();

        netWorkEmailMsg();
    }

    private void netWorkEmailMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_emai_count)
        //OkGo.<String>post("http://192.168.30.28:8080/portal/mobile/mailbox/emailCount")
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("????????????????????????",response.body());
                        CountBean countBean = JSON.parseObject(response.body(), CountBean.class);
                        dy_msg_num2.setText(countBean.getCount()+"");
                        if(countBean.getCount() == 0){
                            dy_msg_present2.setText("??????????????????????????????");
                        }else {
                            dy_msg_present2.setText("?????????????????????"+countBean.getCount()+"???");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
}
