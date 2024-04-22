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

public class SmaAdWebViewManager extends SimpleViewManager<SmaAdWebView> {
  public static final String REACT_CLASS = "SmaAdWebView";
  private String source;
  private String zoneId = "";
  private String userParameter = "";

  @Override
  public String getName() {
    return REACT_CLASS;
  }

  @Override
  public SmaAdWebView createViewInstance(ThemedReactContext context) {
    return new SmaAdWebView(context);
  }

  @ReactProp(name = "zoneId")
  public void setZoneId(SmaAdWebView view, String zoneId) {
    this.zoneId = zoneId;
    ShowWebView(view);
  }

  @ReactProp(name = "userParameter")
  public void setUserParameter(SmaAdWebView view, String userParameter) {
    this.userParameter = userParameter;
    ShowWebView(view);
  }

  public void ShowWebView(SmaAdWebView view){
    String url = String.format("https://wall.smaad.net/wall/%s?u=%s", this.zoneId, this.userParameter);
    view.loadUrl(url);
  }
}

// public class SmaAdWebViewManager extends SimpleViewManager<View> {
//   public static final String REACT_CLASS = "SmaAdWebView";

//   @Override
//   @NonNull
//   public String getName() {
//     return REACT_CLASS;
//   }

//   @Override
//   @NonNull
//   public View createViewInstance(ThemedReactContext reactContext) {
//     return new View(reactContext);
//   }

//   @ReactProp(name = "color")
//   public void setColor(View view, String color) {
//     view.setBackgroundColor(Color.parseColor(color));
//   }
// }
