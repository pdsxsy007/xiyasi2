package io.cordova.zhqy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.activity.InfoDetailsActivity;
import io.cordova.zhqy.activity.OaMsgActivity;
import io.cordova.zhqy.bean.MessageBean;
import io.cordova.zhqy.bean.OAMsgListBean;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;

/**
 * Created by Administrator on 2019/4/17 0017.
 */

public class MessageAdapter extends CommonAdapter<MessageBean.Obj> {
    Context mContext;
    public MessageAdapter(Context context, int layoutId, List<MessageBean.Obj> datas) {
        super(context, layoutId, datas);
        mContext = context;
    }

    @Override
    protected void convert(final ViewHolder holder, final MessageBean.Obj s, final int position) {

        //holder.setText(R.id.tv_name,s.getMessageTitle());
        holder.setText(R.id.tv_name1,s.getMemberNickname());
        holder.setTextColor(R.id.tv_name, Color.parseColor("#000000"));
        String messageContent = s.getMessageTitle();
        String messageSendTime = s.getMessageSendTime();
        String date = timeStamp2Date(messageSendTime, "yyyy-MM-dd HH:mm:ss");

        holder.setText(R.id.tv_name,date);
        if(null != messageContent){
            holder.setText(R.id.tv_present,messageContent);
        }else {
            holder.setText(R.id.tv_present,"");

        }
        if(s.getBacklogDetailState() == 0){//??????
            holder.setTextColor(R.id.tv_name,Color.parseColor("#8f8f94"));
            holder.setTextColor(R.id.tv_present,Color.parseColor("#000000"));
            holder.setTextColor(R.id.tv_name1,Color.parseColor("#8f8f94"));
            holder.setVisible(R.id.rl_jiaobiao,true);
        }else {//??????
            holder.setTextColor(R.id.tv_name,Color.parseColor("#707070"));
            holder.setTextColor(R.id.tv_present,Color.parseColor("#707070"));
            holder.setVisible(R.id.rl_jiaobiao,false);
        }


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
            public void onClick(View v) {
               /* Intent intent = null;
                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                if(isOpen.equals("") || isOpen.equals("1")){
                    intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                }else {
                    intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                }
                intent.putExtra("appUrl",s.getMessageUrl());
                intent.putExtra("oaMsg","oaMsg");
                String messageAppName = s.getMessageAppName();
                if(null != messageAppName){
                    intent.putExtra("appName",messageAppName);
                }

                mContext.startActivity(intent);*/

                Intent intent = new Intent(MyApp.getInstance(), InfoDetailsActivity.class);

                intent.putExtra("title2",s.getMessageTitle());
                intent.putExtra("time",s.getMessageSendTime()+"");
                intent.putExtra("msgsender",s.getSenderName()+"");
                String backlogDetailId = s.getBacklogDetailId();
                intent.putExtra("backlogDetailId",s.getBacklogDetailId()+"");

                if ("".equals(s.getMessageMobileUrl())){
                    intent.putExtra("appUrl2",s.getMessageContent());

                }else if(null == s.getMessageMobileUrl() ){
                    intent.putExtra("appUrl2",s.getMessageContent());
                }else {
                    intent.putExtra("appUrl",s.getMessageMobileUrl());
                }


                mContext.startActivity(intent);

                Intent intent2 = new Intent();
                intent2.setAction("refreshOaMessage");
                intent2.putExtra("state",s.getBacklogDetailState()+"");
                intent2.putExtra("position",position);
                mContext.sendBroadcast(intent2);
                holder.setTextColor(R.id.tv_name,Color.parseColor("#707070"));
                holder.setTextColor(R.id.tv_present,Color.parseColor("#707070"));
                holder.setVisible(R.id.rl_jiaobiao,false);

            }
        });
    }

    public static String timeStamp2Date(String seconds,String format) {
        if(seconds == null || seconds.isEmpty() || seconds.equals("null")){
            return "";
        }
        if(format == null || format.isEmpty()){
            format = "yyyy-MM-dd HH:mm:ss";
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(new Date(Long.valueOf(seconds)));
    }

}
