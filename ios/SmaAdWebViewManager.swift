// SmaAdWebViewManafer.swift
import UIKit
import WebKit
import React


internal class Constants {
    static let sdkVersion = "2.0"
    static let smaadMessageHandler = "smaadWebSDK"
    static let customUserAgent = " SmaadWebViewSDK/" + sdkVersion
    static let navigatorUserAgent = "navigator.userAgent"
}

@objc(SmaAdWebViewManager)
class SmaAdWebViewManager: RCTViewManager {

    override func view() -> UIView! {
        return SmaAdWebView()
    }

    @objc override static func requiresMainQueueSetup() -> Bool {
        return false
    } 
}

class SmaAdWebView : WKWebView, WKNavigationDelegate, WKScriptMessageHandler {
    private let smaadWebviewScript = """
    var SmaadWebViewSDK = {
         open: function(url) {
             window.webkit.messageHandlers.\(Constants.smaadMessageHandler).postMessage(url);
         }
    };
    """
    private var _onLoadFinished: RCTDirectEventBlock?
    private var _onLoadStarted: RCTDirectEventBlock?
    private var _onRedirectReceived: RCTDirectEventBlock?
    private var _onLoadError: RCTDirectEventBlock?
    private var _onClosePressed: RCTDirectEventBlock?
    @objc var zoneId: String = ""
    @objc var userParameter: String = ""
    @objc var onLoadFinished: RCTDirectEventBlock?{
        set { _onLoadFinished = newValue }
        get { return _onLoadFinished }
    }

    @objc var onLoadStarted: RCTDirectEventBlock?{
        set { _onLoadStarted = newValue }
        get { return _onLoadStarted }
    }

    @objc var onRedirectReceived: RCTDirectEventBlock?{
        set { _onRedirectReceived = newValue }
        get { return _onRedirectReceived }
    }

    @objc var onLoadError: RCTDirectEventBlock?{
        set { _onLoadError = newValue }
        get { return _onLoadError }
    }

    @objc var onClosePressed: RCTDirectEventBlock? {
        set { _onClosePressed = newValue }
        get { return _onClosePressed }
    }

    // override func didSetProps(_ changedProps: [String]!) {
    //     initializeWebView()
    // }

    private func initializeWebView() {
        let baseUrl = "https://wall.smaad.net/wall/"
        let urlString = "\(baseUrl)\(zoneId)?u=\(userParameter)"
        guard let url = URL(string: urlString) else {
            print("Invalid URL")
            return 
         }
        let request = URLRequest(url: url)
        self.load(request)
    }

    override init(frame: CGRect = .zero, configuration: WKWebViewConfiguration = WKWebViewConfiguration()) {
        super.init(frame: frame, configuration: configuration)
        self.navigationDelegate = self
        initialSmaadWebView()
    }

    required init?(coder: NSCoder) {
        super.init(frame: .zero, configuration: WKWebViewConfiguration())
        translatesAutoresizingMaskIntoConstraints = false
        initialSmaadWebView()
    }

    private func initialSmaadWebView(){
        initializeWebView()
        addMessageHandler()
        updateUserAgent()
    }

    internal func addMessageHandler() {
        configuration.userContentController.add(self, name: Constants.smaadMessageHandler)
        configuration.userContentController.addUserScript(
            WKUserScript(source: smaadWebviewScript,
                         injectionTime: WKUserScriptInjectionTime.atDocumentStart,
                         forMainFrameOnly: false))
        // webViewClosed メッセージハンドラの追加
        configuration.userContentController.removeScriptMessageHandler(forName: "webViewClosed")
        configuration.userContentController.add(self, name: "webViewClosed")
        configuration.userContentController.removeScriptMessageHandler(forName: "launchURL")
        configuration.userContentController.add(self, name: "launchURL")
        
    }
    
    internal func updateUserAgent() {
        evaluateJavaScript(Constants.navigatorUserAgent) {(result, error) in
            if let userAgent = result as? String {
                self.customUserAgent = userAgent + Constants.customUserAgent
            }
        }
    }

    public func userContentController(_ userContentController: WKUserContentController, didReceive message: WKScriptMessage) {
        if message.name == "webViewClosed" {
            _onClosePressed?(["message": "WebView was closed"])
        }
        else if message.name == "launchURL"{
            if let urlString = message.body as? String,
                let url = URL(string: urlString) {
                if #available(iOS 10.0, *) {
                    UIApplication.shared.open(url)
                } else {
                    UIApplication.shared.openURL(url)
                }
            }
        }
    }

    func webView(_ webView: WKWebView, didFinish navigation: WKNavigation!) {
        if let url = self.url?.absoluteString {
            _onLoadFinished?(["url": url])
        }
    }

    func webView(_ webView: WKWebView, didStartProvisionalNavigation navigation: WKNavigation!) {
        _onLoadStarted?(["url": webView.url?.absoluteString ?? ""])
    }

    func webView(_ webView: WKWebView, didReceiveServerRedirectForProvisionalNavigation navigation: WKNavigation!) {
        _onRedirectReceived?(["url": webView.url?.absoluteString ?? ""])
    }

    func webView(_ webView: WKWebView, didFail navigation: WKNavigation!, withError error: Error) {
        _onLoadError?(["url": webView.url?.absoluteString ?? "", "error": error.localizedDescription])
    }
}
