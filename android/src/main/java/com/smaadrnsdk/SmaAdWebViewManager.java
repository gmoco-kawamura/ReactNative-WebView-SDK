package com.smaadrnsdk;

import android.graphics.Color;
import android.view.View;

// import androidx.annotation.NonNull;

import com.facebook.react.uimanager.SimpleViewManager;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.annotations.ReactProp;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.common.MapBuilder;

import android.webkit.WebView;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.webkit.PermissionRequest;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.uimanager.events.RCTEventEmitter;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;

import java.util.Map;

public class SmaAdWebViewManager extends SimpleViewManager<SmaAdWebView> {
  public static final String REACT_CLASS = "SmaAdWebView";
  private String zoneId = null;
  private String userParameter = null;
  private SmaAdWebView webView = null;

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  public SmaAdWebView createViewInstance(ThemedReactContext context) {
    Activity activity = getActivityFromContext(context);
    webView = new SmaAdWebView(context);

    // WebViewの設定
    WebSettings settings = webView.getSettings();
    settings.setJavaScriptEnabled(true);  // JavaScriptを有効化

    // webView.setListener(activity, new SmaAdWebView.Listener(){
    SmaAdWebView.Listener listener = new SmaAdWebView.Listener() {
      @Override
      public void onLoadStart(String url) {
        sendEvent(context, "onLoadStarted", url);
      }

      @Override
      public void onPermissionRequest(PermissionRequest request) {
          // Handle permission request, may need additional implementation
      }

      @Override
      public void shouldOverrideUrlLoading(String url) {
        sendEvent(context, "onRedirectReceived", url);
      }

      @Override
      public void onLoadStop(String url) {
        sendEvent(context, "onLoadFinished", url);
      }

      @Override
      public void onReceivedError(int errorCode, String description, String failingUrl) {
        WritableMap event = Arguments.createMap();
        event.putInt("errorCode", errorCode);
        event.putString("description", description);
        event.putString("failingUrl", failingUrl);
        context.getJSModule(RCTEventEmitter.class).receiveEvent(
          webView.getId(),
          // "onReceivedError",
          "onLoadError",
          event
        );
      }

      @Override
      public void onWebViewClosed() {
        sendEvent(context, "onClosePressed", null);
      }

      @Override
      public void onUpdateVisitedHistory(WebView view, String url, boolean isReload) {
          WritableMap event = Arguments.createMap();
          event.putString("url", url);
          event.putBoolean("isReload", isReload);
          context.getJSModule(RCTEventEmitter.class).receiveEvent(
              webView.getId(),
              "onUpdateVisitedHistory",
              event
          );
      }

      @Override
      public void onConsoleMessage(String message, int lineNumber, String sourceID) {
        WritableMap event = Arguments.createMap();
        event.putString("message", message);
        event.putInt("lineNumber", lineNumber);
        event.putString("sourceID", sourceID);
        context.getJSModule(RCTEventEmitter.class).receiveEvent(
          webView.getId(),
          "onConsoleMessage",
          event
        );
      }
    };

    // JavaScriptインターフェースを追加
    webView.addJavascriptInterface(new JavaScriptBridgeInterface(activity, listener), "Android");

    webView.setListener(activity, listener);
    updateWebView();
    return webView;
  }

  @ReactProp(name = "zoneId")
  public void setZoneId(SmaAdWebView view, String zoneId) {
    this.zoneId = zoneId;
    updateWebView();
  }

  @ReactProp(name = "userParameter")
  public void setUserParameter(SmaAdWebView view, String userParameter) {
    this.userParameter = userParameter;
    updateWebView();
  }

  private void updateWebView() {
    if (this.zoneId != null && this.userParameter != null && webView != null) {
      showWebView();
    }
  }

  public void showWebView(){
    String url = String.format("https://wall.smaad.net/wall/%s?u=%s", this.zoneId, this.userParameter);
    webView.loadUrl(url);
  }

  private void sendEvent(ThemedReactContext context, String eventName, String eventData) {
    WritableMap params = Arguments.createMap();
    params.putString("url", eventData);
    context.getJSModule(RCTEventEmitter.class).receiveEvent(
        webView.getId(),
        eventName,
        params
    );
  }

  private Activity getActivityFromContext(Context context) {
    while (context instanceof ContextWrapper) {
        if (context instanceof Activity) {
            return (Activity) context;
        }
        context = ((ContextWrapper) context).getBaseContext();
    }
    return null;
  }

  @Override
  public Map getExportedCustomDirectEventTypeConstants() {
    return MapBuilder.builder()
      .put("onLoadFinished", MapBuilder.of("registrationName", "onLoadFinished"))
      .put("onLoadStarted", MapBuilder.of("registrationName", "onLoadStarted"))
      .put("onRedirectReceived", MapBuilder.of("registrationName", "onRedirectReceived"))
      .put("onLoadError", MapBuilder.of("registrationName", "onLoadError"))
      .put("onClosePressed", MapBuilder.of("registrationName", "onClosePressed"))
      .put("onUpdateVisitedHistory", MapBuilder.of("registrationName", "onUpdateVisitedHistory"))
      .put("onConsoleMessage", MapBuilder.of("registrationName", "onConsoleMessage"))
      .build();
  }

}
