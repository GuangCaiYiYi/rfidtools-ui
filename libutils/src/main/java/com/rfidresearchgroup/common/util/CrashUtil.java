package com.rfidresearchgroup.common.util;

import android.app.Activity;
import android.app.Application;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.rfidresearchgroup.common.activities.CrashActivity;

public class CrashUtil {
    private static final Thread.UncaughtExceptionHandler DEFAULT_UNCAUGHT = Thread.getDefaultUncaughtExceptionHandler();
    public static final List<Activity> activities = new ArrayList<>();

    /**
     * 崩溃日志开关（默认关闭，用户在设置页里开）。与 app 模块 Commons 使用同一份 SharedPreferences。
     */
    public static final String PREFS_NAME = "Main";
    public static final String KEY_CRASH_LOG = "crash_log_enabled";
    private static final long MAX_LOG_BYTES = 2L * 1024 * 1024;

    private static void dumpToFile(Application app, Throwable e) {
        try {
            boolean enabled = app.getSharedPreferences(PREFS_NAME, Activity.MODE_PRIVATE)
                    .getBoolean(KEY_CRASH_LOG, false);
            if (!enabled) {
                return;
            }
            File dir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "RFIDtools");
            if (!dir.exists() && !dir.mkdirs()) {
                dir = app.getExternalFilesDir(null);
            }
            if (dir == null) {
                return;
            }
            File log = new File(dir, "crash.log");
            if (log.exists() && log.length() > MAX_LOG_BYTES) {
                // 超过 2MB 就重来一份，避免把空间塞满
                if (!log.delete()) {
                    return;
                }
            }
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            StringBuilder sb = new StringBuilder();
            sb.append("\n==== ")
                    .append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US).format(new Date()))
                    .append(" | ").append(Build.MODEL)
                    .append(" | API ").append(Build.VERSION.SDK_INT)
                    .append(" ====\n")
                    .append(sw);
            FileOutputStream fos = new FileOutputStream(log, true);
            fos.write(sb.toString().getBytes("UTF-8"));
            fos.flush();
            fos.close();
        } catch (Throwable ignored) {
            // 写日志本身绝不能再引发崩溃
        }
    }

    /*
     * callback for activity!
     * */
    private static final
    Application.ActivityLifecycleCallbacks callbacks4Act = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
            //must add activity to list!
            activities.add(activity);
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {

        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            //must remove activity from list!
            activities.remove(activity);
        }
    };

    public static void register(final Application application) {
        //register callback!
        application.registerActivityLifecycleCallbacks(callbacks4Act);
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
                //先把堆栈落到文件（用户没开开关时不会写）
                dumpToFile(application, e);
                //跳转到奔溃信息处理界面
                Intent intent = new Intent(application, CrashActivity.class);
                intent.putExtra("crash", e);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //destroy all activity
                for (Activity act : activities) {
                    act.finish();
                }
                application.startActivity(intent);
                //调用默认的异常处理
                if (DEFAULT_UNCAUGHT != null) {
                    DEFAULT_UNCAUGHT.uncaughtException(t, e);
                }
            }
        });
    }
}
