package io.cordova.zhqy.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.DownloadListener;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.just.agentweb.AbsAgentWebSettings;
import com.just.agentweb.AgentWeb;
import com.just.agentweb.IAgentWebSettings;
import com.just.agentweb.WebListenerManager;
import com.just.agentweb.download.AgentWebDownloader;
import com.just.agentweb.download.DefaultDownloadImpl;
import com.just.agentweb.download.DownloadListenerAdapter;
import com.just.agentweb.download.DownloadingService;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import gdut.bsx.share2.Share2;
import gdut.bsx.share2.ShareContentType;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.CookieUtils;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.FileUtil;

/**
 * Created by Administrator on 2019/4/16 0016.
 */

public class InfoDetailsActivity2 extends BaseActivity {

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.webview)
    WebView webView;
    private Context context = this;
    //?????????
    private ProgressBar mProgress;
    //??????????????????
    private TextView mProgressText;
    //???????????????
    private Dialog downloadDialog;
    //????????????
    private Thread downLoadThread;
    //????????????url
    private String apkUrl = "";
    //?????????????????????
    private String savePath = "";
    //apk??????????????????
    private String apkFilePath = "";
    //????????????????????????
    private String tmpFilePath = "";
    //??????????????????
    private String apkFileSize;
    //?????????????????????
    private String tmpFileSize;
    //?????????
    private int progress;
    //????????????
    private boolean interceptFlag;
    //??????SD???
    private static final int DOWN_NOSDCARD = 0;
    //????????????
    private static final int DOWN_UPDATE = 1;
    //????????????
    private static final int DOWN_OVER = 2;


    @BindView(R.id.layout_back)
    RelativeLayout layout_back;
    String appUrl,title,tgc;
    String appUrl2;
    String time;
    String msgsender;

    @Override
    protected int getResourceId() {
        return R.layout.activity_info_details2;
    }

    @Override
    protected void initView() {
        super.initView();

        title = getIntent().getStringExtra("title2");
        appUrl = getIntent().getStringExtra("appUrl");
        appUrl2 = getIntent().getStringExtra("appUrl2");
        msgsender = getIntent().getStringExtra("msgsender");
        time = getIntent().getStringExtra("time");

        tgc = (String) SPUtils.get(getApplicationContext(), "TGC", "");
        if (StringUtils.isEmpty(title)){
            tv_title.setText("????????????");
        }else {
            tv_title.setText(title);
        }


        if(appUrl.contains("gilight://")){
            //gilight://url=weixin://123
            if(!appUrl.contains("http")){
                final String endUrl = appUrl.substring(10,appUrl.length());
                String[] split = endUrl.split("//");
                String s = split[0];
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("????????????"+s);
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String appServiceUrl2 = UrlRes.huanxingUrl+"?"+endUrl;
                        Log.e("appServiceUrl2",appServiceUrl2);
                        Uri uri = Uri.parse(appServiceUrl2);
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            // ???????????? ????????????
                            startActivity(intent);
                            finish();
                        } else {
                            //??????????????? ???????????? ????????????
                            //Toast.makeText(MainActivity.this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
                builder.create().show();
            }else {

                String endUrl = appUrl.substring(10,appUrl.length());
                String appServiceUrl2 = UrlRes.huanxingUrl+"?"+endUrl;
                Log.e("appServiceUrl2",appServiceUrl2);
                Uri uri = Uri.parse(appServiceUrl2);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                if (intent.resolveActivity(getPackageManager()) != null) {
                    // ???????????? ????????????
                    startActivity(intent);
                    finish();
                } else {
                    //??????????????? ???????????? ????????????
                    //Toast.makeText(MainActivity.this, "???????????????????????????????????????", Toast.LENGTH_SHORT).show();

                }

            }



        }

        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        webView.setDownloadListener(new MyWebViewDownLoadListener());
        webView.setWebChromeClient(mWebChromeClient);
        webView.setWebViewClient(mWebViewClient);
        webView.loadUrl(appUrl);

    }

    @Override
    protected void initData() {
        super.initData();
        layout_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!webView.canGoBack()){
                    InfoDetailsActivity2.this.finish();
                }
            }
        });
    }

    private class MyWebViewDownLoadListener implements DownloadListener {
        @Override
        public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimeType,long contentLength) {
            // ????????????????????????????????????
            apkUrl = url;
            interceptFlag = false;
            showDownloadDialog();
        }


    }
    /**
     * ?????????????????????
     */
    private void showDownloadDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("??????????????????");

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.download_progress, null);
        mProgress = view.findViewById(R.id.download_progress);
        mProgressText = view.findViewById(R.id.download_progress_text);

        builder.setView(view);
        builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                interceptFlag = true;
            }
        });

        downloadDialog = builder.create();
        downloadDialog.setCanceledOnTouchOutside(false);
        downloadDialog.show();


        downloadApk();
    }


    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageCommitVisible(WebView view, String url) {
            super.onPageCommitVisible(view, url);

        }
        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {

            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.getSettings().setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
                handler.cancel(); // ???????????????????????????
            }*/
            handler.proceed();

        }
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            CookieManager cookieManager = CookieManager.getInstance();
            if(Build.VERSION.SDK_INT>=21){
                cookieManager.setAcceptThirdPartyCookies(view, true);
            }



        }
        /**????????????*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url =  null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
            }
            if(url.contains("chaoxingshareback")){
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                startActivity(intent);
            }


            return super.shouldOverrideUrlLoading(view, request);
        }


        /**????????????*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains("http://kys.zzuli.edu.cn/cas/login")) {
                if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""))){
                    Intent intent = new Intent(getApplicationContext(),LoginActivity2.class);
                    startActivity(intent);
                    finish();

                    return true;
                }
            }


            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            CookieUtils.syncCookie("http://kys.zzuli.edu.cn","CASTGC="+tgc,getApplication());

        }
    };
    private WebChromeClient mWebChromeClient = new WebChromeClient() {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {

        }

        /*Title*/
        @Override
        public void onReceivedTitle(WebView view, String title) {
            super.onReceivedTitle(view, title);
//            if (mTitleTextView != null) {
//                mTitleTextView.setText(title);
//            }
        }

    };

    /**
     * ??????????????????
     */
    private void downloadApk(){
        downLoadThread = new Thread(mDownApkRunnable);
        downLoadThread.start();
    }

    private Runnable mDownApkRunnable = new Runnable() {
        @Override
        public void run() {

            try {
                long currTime = System.currentTimeMillis();
                String apkName = currTime +".apk";
                String tmpApk = currTime+".tmp";

                //?????????????????????SD???
                String storageState = Environment.getExternalStorageState();
                if(storageState.equals(Environment.MEDIA_MOUNTED)){
                    savePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/DownLoad/";
                    File file = new File(savePath);
                    if(!file.exists()){
                        file.mkdirs();
                    }
                    apkFilePath = savePath + apkName;
                    tmpFilePath = savePath + tmpApk;
                }

                //????????????SD????????????????????????
                if(apkFilePath == null || apkFilePath == ""){
                    mHandler.sendEmptyMessage(DOWN_NOSDCARD);
                    return;
                }

                File apkFile = new File(apkFilePath);

                //????????????????????????
                if(apkFile.exists()){
                    downloadDialog.dismiss();
                    //???????????????????????????????????????
                    return;
                }

                //????????????????????????
                File tmpFile = new File(tmpFilePath);
                FileOutputStream fos = new FileOutputStream(tmpFile);

                URL url = new URL(apkUrl);
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.connect();
                int length = conn.getContentLength();
                InputStream is = conn.getInputStream();

                //???????????????????????????2??????????????????
                DecimalFormat df = new DecimalFormat("0.00");
                //???????????????????????????????????????
                apkFileSize = df.format((float) length / 1024 / 1024) + "MB";

                int count = 0;
                byte buf[] = new byte[1024];

                do{
                    int numRead = is.read(buf);
                    count += numRead;
                    //????????????????????????????????????????????????
                    tmpFileSize = df.format((float) count / 1024 / 1024) + "MB";
                    //???????????????
                    progress =(int)(((float)count / length) * 100);
                    //????????????
                    mHandler.sendEmptyMessage(DOWN_UPDATE);
                    if(numRead <= 0){
                        //???????????? - ???????????????????????????APK??????
                        if(tmpFile.renameTo(apkFile)){
                            //????????????
                            mHandler.sendEmptyMessage(DOWN_OVER);
                        }
                        break;
                    }
                    fos.write(buf,0,numRead);
                }while(!interceptFlag);//???????????????????????????

                fos.close();
                is.close();
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch(IOException e){
                e.printStackTrace();
            }

        }
    };

    private Handler mHandler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DOWN_UPDATE:
                    mProgress.setProgress(progress);
                    mProgressText.setText(tmpFileSize);
                    break;
                case DOWN_OVER:
                    downloadDialog.dismiss();
                    //?????????????????????????????????
                    Toast.makeText(context, "????????????", Toast.LENGTH_SHORT).show();
                    installNormal(context,apkFilePath);
                    break;
                case DOWN_NOSDCARD:
                    downloadDialog.dismiss();
                    Toast.makeText(context, "??????????????????????????????SD???????????????", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };



    /**
     * ????????????
     * @param context ?????????
     * @param apkPath apk?????????????????????????????????
     */
    private static void installNormal(Context context, String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        //?????????7.0???????????????????????????uri?????????
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
            File file = (new File(apkPath));
            // ???????????????Activity???????????????Activity,?????????????????????
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //??????1:?????????, ??????2:Provider???????????? ??????????????????????????????,??????3:???????????????
            Uri apkUri = FileProvider.getUriForFile(context, "io.cordova.zhqy.fileprovider", file);
            //???????????????????????????????????????????????????Uri??????????????????
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                    "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }



    /**
     * @return IAgentWebSettings
     */
    public IAgentWebSettings getSettings()  {
        return new AbsAgentWebSettings() {
            private AgentWeb mAgentWeb;
            private WebSettings mWebSettings;
            @Override
            protected void bindAgentWebSupport(AgentWeb agentWeb) {
                this.mAgentWeb = agentWeb;
            }

            @Override
            public IAgentWebSettings toSetting(WebView webView) {

                return super.toSetting(webView);
            }

            /**
             * AgentWeb 4.0.0 ??????????????? DownloadListener ?????? ???????????????API ?????? Download ??????????????????????????????????????????
             * ????????????????????? AgentWeb Download ?????? ??? ???????????? compile 'com.just.agentweb:download:4.0.0 ???
             * ???????????????????????????????????????????????? AgentWebSetting ??? New ??? DefaultDownloadImpl?????????DownloadListenerAdapter
             * ???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????? setDownloader ????????????????????????
             * @param webView
             * @param downloadListener
             * @return WebListenerManager
             */
            @Override
            public WebListenerManager setDownloader(WebView webView, DownloadListener downloadListener) {
                return super.setDownloader(webView,
                        DefaultDownloadImpl
                                .create((Activity) webView.getContext(),
                                        webView,
                                        mDownloadListenerAdapter,
                                        mDownloadListenerAdapter,
                                        this.mAgentWeb.getPermissionInterceptor()));
            }
        };
    }
    private static final int REQUEST_SHARE_FILE_CODE = 120;
    private DownloadingService mDownloadingService;
    /**
     * ????????? AgentWeb  4.0.0
     */
    protected DownloadListenerAdapter mDownloadListenerAdapter = new DownloadListenerAdapter() {
        /**
         *
         * @param url                ????????????
         * @param userAgent          UserAgent
         * @param contentDisposition ContentDisposition
         * @param mimetype           ?????????????????????
         * @param contentLength      ????????????
         * @param extra              ???????????? ??? ?????????????????? Extra ????????????icon ??? ??????????????? ??? ?????????????????????
         * @return true ???????????????????????????????????? ??? false ?????? AgentWeb ??????
         */
        @Override
        public boolean onStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength, AgentWebDownloader.Extra extra) {

           /* extra.setOpenBreakPointDownload(true) // ????????????????????????
                    .setIcon(R.drawable.ic_file_download_black_24dp) //???????????????icon
                    .setConnectTimeOut(6000) // ??????????????????
                    .setBlockMaxTime(10 * 60 * 1000)  // ???8KB??????????????????60s ?????????60s??????????????????????????????8KB????????????????????????
                    .setDownloadTimeOut(Long.MAX_VALUE) // ??????????????????
                    .setParallelDownload(false)  // ??????????????????????????????
                    .setEnableIndicator(true)  // false ??????????????????
                    //.addHeader("Cookie", "xx") // ??????????????????
                    //  .setAutoOpen(false) // ????????????????????????
                    .setForceDownload(true); // ???????????????????????????????????????*/

            String fileName = URLUtil.guessFileName(url, contentDisposition, mimetype);
            String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    .getAbsolutePath() + File.separator + fileName;
            extra.setForceDownload(true);
            new DownloadTask().execute(url, destPath);

            return false;
        }

        /**
         *
         * ????????????????????????????????????????????????????????????
         * @param url
         * @param downloadingService  ?????????????????? DownloadingService#shutdownNow ????????????
         */
        @Override
        public void onBindService(String url, DownloadingService downloadingService) {
            super.onBindService(url, downloadingService);
            mDownloadingService = downloadingService;
            Log.i("????????????", "onBindService:" + url + "  DownloadingService:" + downloadingService);
        }

        /**
         * ??????onUnbindService??????????????????????????? DownloadingService???
         * @param url
         * @param downloadingService
         */
        @Override
        public void onUnbindService(String url, DownloadingService downloadingService) {
            super.onUnbindService(url, downloadingService);
            mDownloadingService = null;
            Log.i("??????onUnbindService??????", "onUnbindService:" + url);
        }

        /**
         *
         * @param url  ????????????
         * @param loaded  ?????????????????????
         * @param length    ??????????????????
         * @param usedTime   ?????? ?????????ms
         * ????????????????????????????????? ???????????? AsyncTask #XX ?????? AgentWeb # XX
         */
        @Override
        public void onProgress(String url, long loaded, long length, long usedTime) {
            int mProgress = (int) ((loaded) / Float.valueOf(length) * 100);
            Log.i("????????????", "onProgress:" + mProgress);
            super.onProgress(url, loaded, length, usedTime);
        }

        /**
         *
         * @param path ?????????????????????
         * @param url  ????????????
         * @param throwable    ????????????????????????????????????
         * @return true ???????????????????????????????????????????????? ???false ????????????AgentWeb ??????
         */
        @Override
        public boolean onResult(String path, String url, Throwable throwable) {
            if (null == throwable) { //????????????
                //do you work
                //Log.e("????????????",path);
                //Log.e("????????????",url);

                Uri shareFileUrl = FileUtil.getFileUri(InfoDetailsActivity2.this,null,new File(path));
                //Log.e("path2", String.valueOf(shareFileUrl));
                new Share2.Builder(InfoDetailsActivity2.this)
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(shareFileUrl)
                        .setTitle("Share File")
                        .setOnActivityResult(REQUEST_SHARE_FILE_CODE)
                        .build()
                        .shareBySystem();

            } else {//????????????
                //Log.e("path",path);

                Uri shareFileUrl = FileUtil.getFileUri(InfoDetailsActivity2.this,null,new File(path));
                //Log.e("path2", String.valueOf(shareFileUrl));
                new Share2.Builder(InfoDetailsActivity2.this)
                        .setContentType(ShareContentType.FILE)
                        .setShareFileUri(shareFileUrl)
                        .setTitle("Share File")
                        .setOnActivityResult(REQUEST_SHARE_FILE_CODE)
                        .build()
                        .shareBySystem();

            }
            return false; // true  ????????????????????????????????? , ??????????????????
        }
    };

    @Override
    protected void onPause() {
        webView.onPause();
        super.onPause();
    }
    @Override
    protected void onResume() {

        webView.onResume();
        super.onResume();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //mAgentWeb.destroy();
        webView.destroy();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(KeyEvent.KEYCODE_BACK == keyCode){
            if (!webView.canGoBack()){
                InfoDetailsActivity2.this.finish();
                return true;

            }


        }
        return super.onKeyDown(keyCode, event);
    }

    /*
     * ???????????????????????????
     */
    public static String stampToDate(String s){
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /**
     * ???????????????????????????
     * @return true ???????????????
     */
    private boolean hasApplication(String scheme) {
        PackageManager manager = getPackageManager();
        Intent action = new Intent(Intent.ACTION_VIEW);
        action.setData(Uri.parse(scheme));
        List list = manager.queryIntentActivities(action, PackageManager.GET_RESOLVED_FILTER);
        return list != null && list.size() > 0;
    }

    //?????????
    class DownloadTask extends AsyncTask<String, Void, Void> {
        // ?????????????????????URL ??? ????????????
        private String url;
        private String destPath;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Void doInBackground(String... params) {

            url = params[0];
            destPath = params[1];
            Log.e("???????????????",destPath);
            OutputStream out = null;
            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(15000);
                urlConnection.setReadTimeout(15000);
                InputStream in = urlConnection.getInputStream();
                out = new FileOutputStream(params[1]);
                byte[] buffer = new byte[10 * 1024];
                int len;
                while ((len = in.read(buffer)) != -1) {
                    out.write(buffer, 0, len);
                }
                in.close();
            } catch (IOException e) {

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {

                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

//                Intent handlerIntent = new Intent(Intent.ACTION_VIEW);
//                String mimeType = getMIMEType(url);
//                Uri uri = Uri.fromFile(new File(destPath));
//
//                handlerIntent.setDataAndType(uri, mimeType);

            startActivity(getFileIntent(new File(destPath)));
        }
    }

    public  Intent getFileIntent(File file){

//       Uri uri = Uri.parse("http://m.ql18.com.cn/hpf10/1.pdf");
        Uri uri = Uri.fromFile(file);
        String type = getMIMEType(file);
        Log.i("tag", "type="+type);
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.setAction(Intent.ACTION_SEND);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setDataAndType(uri, type);
        Log.e("????????????",uri.getPath());
        return intent;
    }
    public String getMIMEType(File f){
        String type="";
        String fName=f.getName();
        /* ??????????????? */
        String end=fName.substring(fName.lastIndexOf(".")+1,fName.length()).toLowerCase();

        /* ???????????????????????????MimeType */
        if(end.equals("pdf")){
            type = "application/pdf";//
        }
        else if(end.equals("m4a")||end.equals("mp3")||end.equals("mid")||
                end.equals("xmf")||end.equals("ogg")||end.equals("wav")){
            type = "audio/*";
        }
        else if(end.equals("3gp")||end.equals("mp4")){
            type = "video/*";
        }
        else if(end.equals("jpg")||end.equals("gif")||end.equals("png")||
                end.equals("jpeg")||end.equals("bmp")){
            type = "image/*";
        }
        else if(end.equals("apk")){
            /* android.permission.INSTALL_PACKAGES */
            type = "application/vnd.android.package-archive";
        }
        else if(end.equals("pptx")||end.equals("ppt")){
            type = "application/vnd.ms-powerpoint";
        }else if(end.equals("docx")||end.equals("doc")){
            type = "application/vnd.ms-word";
        }else if(end.equals("xlsx")||end.equals("xls")){
            type = "application/vnd.ms-excel";
        }
        else{
//        /*??????????????????????????????????????????????????????????????? */
            type="*/*";
        }

        return type;
    }

}
