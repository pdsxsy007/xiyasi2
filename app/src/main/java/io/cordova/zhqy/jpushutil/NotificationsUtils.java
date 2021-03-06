package io.cordova.zhqy.jpushutil;

import android.app.AppOpsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Administrator on 2019/1/14 0014.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class NotificationsUtils {

    private static final String CHECK_OP_NO_THROW = "checkOpNoThrow";
    private static final String OP_POST_NOTIFICATION = "OP_POST_NOTIFICATION";

    public static boolean checkFloatPermission(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT)
            return true;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                Class cls = Class.forName("android.content.Context");
                Field declaredField = cls.getDeclaredField("APP_OPS_SERVICE");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(cls);
                if (!(obj instanceof String)) {
                    return false;
                }
                String str2 = (String) obj;
                obj = cls.getMethod("getSystemService", String.class).invoke(context, str2);
                cls = Class.forName("android.app.AppOpsManager");
                Field declaredField2 = cls.getDeclaredField("MODE_ALLOWED");
                declaredField2.setAccessible(true);
                Method checkOp = cls.getMethod("checkOp", Integer.TYPE, Integer.TYPE, String.class);
                int result = (Integer) checkOp.invoke(obj, 24, Binder.getCallingUid(), context.getPackageName());
                return result == declaredField2.getInt(cls);
            } catch (Exception e) {
                return false;
            }
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                AppOpsManager appOpsMgr = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
                if (appOpsMgr == null)
                    return false;
                int mode = appOpsMgr.checkOpNoThrow("android:system_alert_window", android.os.Process.myUid(), context
                        .getPackageName());
                return mode == AppOpsManager.MODE_ALLOWED || mode == AppOpsManager.MODE_IGNORED;
            } else {
                return Settings.canDrawOverlays(context);
            }
        }
    }

    //?????????app?????????????????????
    /**
     * ????????????NotificationManagerCompat ?????? areNotificationsEnabled()????????????????????????????????????NotificationManagerCompat ??? android.support.v4.app????????????API 22.1.0 ?????????????????? areNotificationsEnabled()????????? API 24.1.0??????????????????
     * areNotificationsEnabled ?????? API 19 ??????????????????????????????API 19 ???????????????true
     * */


    public static boolean isNotificationEnabled(Context context) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
            return areNotificationsEnabled;
        }
        AppOpsManager mAppOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        ApplicationInfo appInfo = context.getApplicationInfo();
        String pkg = context.getApplicationContext().getPackageName();
        int uid = appInfo.uid;

        Class appOpsClass = null;
        /* Context.APP_OPS_MANAGER */
        try {
            appOpsClass = Class.forName(AppOpsManager.class.getName());
            Method checkOpNoThrowMethod = appOpsClass.getMethod(CHECK_OP_NO_THROW, Integer.TYPE, Integer.TYPE,
                    String.class);
            Field opPostNotificationValue = appOpsClass.getDeclaredField(OP_POST_NOTIFICATION);

            int value = (Integer) opPostNotificationValue.get(Integer.class);
            return ((Integer) checkOpNoThrowMethod.invoke(mAppOps, value, uid, pkg) == AppOpsManager.MODE_ALLOWED);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    //????????????????????????
    /**
     *
     *
     * ??????????????????????????????????????????????????????????????????????????????
     Intent intent = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
     intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
     intent.putExtra(Settings.EXTRA_CHANNEL_ID, myNotificationChannel.getId());
     startActivity(intent);
     * ??????????????????????????????????????????????????????????????? APP?????????????????????????????????Action??????Settings.ACTION_APP_NOTIFICATION_SETTINGS, ??????Action??? API 26 ????????????
     * ???????????????????????????????????????????????? APP?????????????????????????????????????????????????????????????????? APP????????????????????????Action??????Settings.ACTION_APPLICATION_DETAILS_SETTINGS*/
    public static void gotoSet(Context context) {
        Intent intent = new Intent();
        if (Build.VERSION.SDK_INT >= 26) {
            // android 8.0??????
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("android.provider.extra.APP_PACKAGE", context.getPackageName());

        } else if (Build.VERSION.SDK_INT >= 21) {
            // android 5.0-7.0
            intent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            intent.putExtra("app_package", context.getPackageName());
            intent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else {
            // ??????
            intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        }
        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

//        /*?????????????????????????????????*/
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            if (!NotificationsUtils.isNotificationEnabled(getApplicationContext())) {
//                NotificationChannel channel = mManager.getNotificationChannel(CHANNEL_ID);
//                if (channel.getImportance() == NotificationManager.IMPORTANCE_HIGH) {
//                    Intent aa = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
//                    aa.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//                    aa.putExtra(Settings.EXTRA_CHANNEL_ID, channel.getId());
//                    startActivity(aa);
//                    Toast.makeText(this, "???????????????????????????????????????????????????", Toast.LENGTH_SHORT).show();
//                }
//            }
//
//        }

    }


    /**
     * ??????????????????
     * @param context
     */
    public static void requestNotify(Context context) {
        /**
         * ???????????????????????????
         * @param context
         */
        Intent localIntent = new Intent();
        ///< ??????????????????????????????????????????
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            localIntent.setAction(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
            localIntent.putExtra(Settings.EXTRA_APP_PACKAGE, context.getPackageName());
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP &&
                Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            localIntent.setAction("android.settings.APP_NOTIFICATION_SETTINGS");
            localIntent.putExtra("app_package", context.getPackageName());
            localIntent.putExtra("app_uid", context.getApplicationInfo().uid);
        } else if (Build.VERSION.SDK_INT == Build.VERSION_CODES.KITKAT) {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.addCategory(Intent.CATEGORY_DEFAULT);
            localIntent.setData(Uri.parse("package:" + context.getPackageName()));
        } else {
            ///< 4.4???????????????app????????????????????????????????????Action???????????????????????????????????????,
            localIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
                localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.setAction(Intent.ACTION_VIEW);
                localIntent.setClassName("com.android.settings", "com.android.setting.InstalledAppDetails");
                localIntent.putExtra("com.android.settings.ApplicationPkgName", context.getPackageName());
            }
        }
        localIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(localIntent);
    }

}
