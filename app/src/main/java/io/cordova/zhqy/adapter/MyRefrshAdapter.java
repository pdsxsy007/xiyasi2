package io.cordova.zhqy.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import io.cordova.zhqy.activity.SystemInfoDetailsActivity;
import io.cordova.zhqy.activity.SystemMsgActivity;
import io.cordova.zhqy.bean.SysMsgBean;
import io.cordova.zhqy.utils.CircleCrop;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;


/**
 * Created by Administrator on 2019/4/17 0017.
 */

public class MyRefrshAdapter extends CommonAdapter<SysMsgBean.ObjBean> {

    private Context context;
    public MyRefrshAdapter(Context context, int layoutId, List<SysMsgBean.ObjBean> datas) {
        super(context, layoutId, datas);
        this.context = context;
    }



    @Override
    protected void convert(final ViewHolder holder, final SysMsgBean.ObjBean s, int position) {
        String str= s.getMessageAppName();
        String messageTitle = s.getMessageTitle();

        TextView tv_name = holder.getConvertView().findViewById(R.id.tv_name);
        if(str != null){
            SpannableString string = new SpannableString("["+str+"]"+"    "+messageTitle);
            StyleSpan span = new StyleSpan(Typeface.BOLD);
            string.setSpan(span,0,str.length()+2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //holder.setText(R.id.tv_name,"["+str+"]"+"    "+messageTitle);
            tv_name.setText(string);
        }else {
            //holder.setText(R.id.tv_name,"[????????????]"+"    "+messageTitle);
            SpannableString string = new SpannableString("[????????????]"+"    "+messageTitle);
            StyleSpan span = new StyleSpan(Typeface.BOLD);
            string.setSpan(span,0,6, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            //holder.setText(R.id.tv_name,"["+str+"]"+"    "+messageTitle);
            tv_name.setText(string);
        }
        holder.setText(R.id.tv_name1,s.getMessageSender());
        String messageSendTime = s.getMessageSendTime()+"";
        String date = timeStamp2Date(messageSendTime, "yyyy-MM-dd HH:mm:ss");
        holder.setText(R.id.tv_present,date);

        ImageView imageView = holder.getConvertView().findViewById(R.id.oa_img);

        switch (position%6){
            case 0:
                Glide.with(context)
                        .load(R.mipmap.message_icon2)
                        //.transform(new CircleCrop(mContext))
                        .into(imageView);
                break;
            case 1:
                Glide.with(context)
                        .load(R.mipmap.message_icon1)
                        //.transform(new CircleCrop(mContext))
                        .into(imageView);
                break;
            case 2:
                Glide.with(context)
                        .load(R.mipmap.message_icon3)
                        //.transform(new CircleCrop(mContext))
                        .into(imageView);
                break;
            case 3:
                Glide.with(context)
                        .load(R.mipmap.message_icon4)
                        //.transform(new CircleCrop(mContext))
                        .into(imageView);
                break;
            case 4:
                Glide.with(context)
                        .load(R.mipmap.message_icon3)
                        //.transform(new CircleCrop(mContext))
                        .into(imageView);
                break;
            case 5:
                Glide.with(context)
                        .load(R.mipmap.message_icon1)
                        //.transform(new CircleCrop(mContext))
                        .into(imageView);
                break;

        }

        LinearLayout ll_msg = holder.getConvertView().findViewById(R.id.ll_msg);
        Log.d("s", s.getMessageDetailId()+".");
        if(s.getMessageDetailState() == 0){//??????

            holder.setTextColor(R.id.tv_name,Color.parseColor("#000000"));
            holder.setTextColor(R.id.tv_present,Color.parseColor("#000000"));
            holder.setVisible(R.id.rl_jiaobiao,true);
        }else {//??????
            holder.setTextColor(R.id.tv_name,Color.parseColor("#707070"));
            holder.setTextColor(R.id.tv_present,Color.parseColor("#707070"));
            holder.setTextColor(R.id.tv_name1,Color.parseColor("#707070"));
            holder.setVisible(R.id.rl_jiaobiao,false);
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
                                //intent.putExtra("backlogDetailId",s.getBacklogDetailId()+"");
                                //intent.putExtra("title2",s.getMessageAppName());
                                //null != s.getMessageUrl() ||
                                if ("".equals(s.getMessageUrl())){
                                    intent.putExtra("appUrl2",s.getMessageContent().toString());

                                }else if(null == s.getMessageUrl() ){
                                    intent.putExtra("appUrl2",s.getMessageContent().toString());
                                    //intent.putExtra("appUrl2","????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????<a href=\"http://xytz.zzuli.edu.cn:8080/result/inquiry\">????????????</a>???????????????????????????????????????????????????<a href=\"http://info.zzuli.edu.cn/_t598/2019/0124/c13520a193789/page.htm\">  ??????</a>?????????????????????????????????????????????????????????");
                                }else {
                                    intent.putExtra("appUrl",s.getMessageUrl().toString());
                                }

                               /* if (!"".equals(s.getMessageUrl())){
                                    intent.putExtra("appUrl",s.getMessageUrl().toString());
                               }else {
                                    String s1 = s.getMessageContent().toString();
                                    intent.putExtra("appUrl2",s.getMessageContent().toString());
                                   //intent.putExtra("appUrl2","????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????<a href=\"http://xytz.zzuli.edu.cn:8080/result/inquiry\">????????????</a>???????????????????????????????????????????????????<a href=\"http://info.zzuli.edu.cn/_t598/2019/0124/c13520a193789/page.htm\">  ??????</a>?????????????????????????????????????????????????????????");
                               }*/


                                context.startActivity(intent);

                                Intent intent2 = new Intent();
                                intent2.setAction("refreshMsg");
                                intent2.putExtra("state",s.getMessageDetailState()+"");
                                context.sendBroadcast(intent2);
                               /* holder.setTextColor(R.id.tv_name,Color.parseColor("#707070"));
                                holder.setTextColor(R.id.tv_present,Color.parseColor("#707070"));
                                holder.setVisible(R.id.rl_jiaobiao,false);*/
                            }
                            @Override
                            public void onError(Response<String> response) {
                                super.onError(response);

                            }
                        });

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
