package io.kuenzler.android.stringsmash;

import android.graphics.Paint;
import android.widget.TextView;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import static de.robv.android.xposed.XposedHelpers.findAndHookMethod;

public class Xposed implements IXposedHookZygoteInit, IXposedHookLoadPackage {

    @Override
    public void initZygote(StartupParam hodorHodorHodorHodor) throws Throwable {
        XC_MethodHook methodHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                CharSequence isNull = (CharSequence) param.args[0];
                XSharedPreferences pref = new XSharedPreferences("io.kuenzler.android.stringsmash", "user_settings");
                Boolean doIt = pref.getBoolean("enabled", false);
                if (isNull != null && doIt) {
                    String text = pref.getString("text", "");
                    String finalStrng = isNull.toString().replaceAll("\\w+", text);
                    param.args[0] = finalStrng;
                }
            }
        };

        findAndHookMethod(TextView.class, "setText", CharSequence.class, TextView.BufferType.class, boolean.class, int.class, methodHook);
        findAndHookMethod(TextView.class, "setHint", CharSequence.class, methodHook);
        findAndHookMethod("android.view.GLES20Canvas", null, "drawText", String.class, float.class, float.class, Paint.class, methodHook);
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        if (lpparam.packageName.equals("io.kuenzler.android.stringsmash")) {

            findAndHookMethod("io.kuenzler.android.stringsmash.MainActivity", lpparam.classLoader, "isModuleActive", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    param.setResult(true);
                    XposedBridge.log("Xposed Module in \"StringSmash\" is enabled");
                }
            });
        }
    }
}