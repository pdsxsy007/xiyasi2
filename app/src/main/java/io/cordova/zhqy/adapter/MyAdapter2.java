package io.cordova.zhqy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.activity.InfoDetailsActivity;
import io.cordova.zhqy.bean.OAMsgListBean2;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;


/**
 * Created by Administrator on 2019/4/17 0017.
 */

public class MyAdapter2 extends CommonAdapter<OAMsgListBean2.ObjBean> {
    Context mContext;
    public MyAdapter2(Context context, int layoutId, List<OAMsgListBean2.ObjBean> datas) {
        super(context, layoutId, datas);
        mContext = context;
    }

    @Override
    protected void convert(ViewHolder holder, final OAMsgListBean2.ObjBean s, int position) {

        holder.setText(R.id.tv_name,s.getMessageAppName());
        holder.setTextColor(R.id.tv_name, Color.parseColor("#000000"));
        holder.setText(R.id.tv_present,s.getMessageTitle());
        holder.setTextColor(R.id.tv_present,Color.parseColor("#000000"));

        ImageView iv = holder.getConvertView().findViewById(R.id.oa_img);
        switch (position%6) {
            case 0:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon2)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
            case 1:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon1)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
            case 2:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon2)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
            case 3:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon4)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
            case 4:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon3)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
            case 5:
                Glide.with(mContext)
                        .load(R.mipmap.message_icon5)
                        //.transform(new CircleCrop(mContext))
                        .into(iv);
                break;
        }


        holder.setOnClickListener(R.id.ll_msg, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OkGo.<String>post(UrlRes.HOME_URL + UrlRes.searchMessageById)
                        .tag(this)
                        .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                        .params("messageDetailId", s.getMessageId())
                        .execute(new StringCallback() {
                            @Override
                            public void onSuccess(Response<String> response) {
                                Log.e("SysMsg",response.body());
                                Intent intent = new Intent(MyApp.getInstance(), InfoDetailsActivity.class);

                                intent.putExtra("title2",s.getMessageTitle().toString());
                                intent.putExtra("time",s.getMessageSendTime()+"");
                                intent.putExtra("msgsender",s.getMessageSender()+"");
                                if ("".equals(s.getMessageUrl())){
                                    intent.putExtra("appUrl2",s.getMessageContent().toString());

                                }else if(null == s.getMessageUrl() ){
                                    intent.putExtra("appUrl2",s.getMessageContent().toString());
                                    //intent.putExtra("appUrl2","????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????<a href=\"http://xytz.zzuli.edu.cn:8080/result/inquiry\">????????????</a>???????????????????????????????????????????????????<a href=\"http://info.zzuli.edu.cn/_t598/2019/0124/c13520a193789/page.htm\">  ??????</a>?????????????????????????????????????????????????????????");
                                }else {
                                    intent.putExtra("appUrl",s.getMessageUrl().toString());
                                }


                                mContext.startActivity(intent);

                                Intent intent2 = new Intent();
                                intent2.setAction("refreshMsg");
                                intent2.putExtra("state",s.getPortalMessageDetailList().get(0).getMessageDetailState()+"");
                                mContext.sendBroadcast(intent2);

                            }
                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);

                            }
                        });

            }
        });
//        if(s.getPortalMessageDetailList().size() != 0){
//            Intent intent2 = new Intent();
//            intent2.setAction("refreshMsg2");
//            intent2.putExtra("state",s.getPortalMessageDetailList().get(0).getMessageDetailState()+"");
//            mContext.sendBroadcast(intent2);
//        }

    }

}
