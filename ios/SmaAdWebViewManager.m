// SmaAdWebViewManager.m
#import <React/RCTViewManager.h>
#import <React/RCTEventEmitter.h>

@interface RCT_EXTERN_MODULE(SmaAdWebViewManager, RCTViewManager)

RCT_EXPORT_VIEW_PROPERTY(zoneId, NSString)
RCT_EXPORT_VIEW_PROPERTY(userParameter, NSString)

RCT_EXPORT_VIEW_PROPERTY(onLoadFinished, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLoadStarted, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onRedirectReceived, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onLoadError, RCTDirectEventBlock)
RCT_EXPORT_VIEW_PROPERTY(onClosePressed, RCTDirectEventBlock)

@end