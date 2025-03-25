package ru.pizzaskazka.app

import android.app.AlertDialog
import android.webkit.JsResult
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView


class WebViewHelper(private val webView: WebView, activity:MainActivity) {
    private val activity = activity

    fun setupWebView(url: String) {
        webView.webChromeClient = WebChromeClient()
        webView.addJavascriptInterface(MainActivity().WebAppInterface(activity), "Android")
        val webSettings = webView.settings
        webSettings.cacheMode = WebSettings.LOAD_NORMAL
        webSettings.domStorageEnabled = true // Включить DOM Storage (для localStorage)
        webSettings.databaseEnabled = true
        webSettings.javaScriptEnabled = true
        webView.webChromeClient = object : WebChromeClient(){
            override fun onJsConfirm(
                view: WebView?,
                url: String?,
                message: String?,
                result: JsResult?
            ): Boolean {
                AlertDialog.Builder(view?.context)
                    .setTitle("")
                    .setMessage(message)
                    .setPositiveButton("OK") { _, _ -> result?.confirm() }
                    .setCancelable(true)
                    .setNegativeButton("НЕТ"){ _, _ -> result?.cancel() }
                    .create()
                    .show()
                return true // Указывает, что алерт обработан
            }
            }



        webSettings.layoutAlgorithm = WebSettings.LayoutAlgorithm.NORMAL // Оптимизация рендеринга
        webSettings.useWideViewPort = true // Использовать широкий viewport
        webSettings.loadWithOverviewMode = true // Загружать страницу в режиме overview


//        webView.webViewClient = object : WebViewClient() {
//            override fun shouldOverrideUrlLoading(
//                view: WebView?,
//                request: WebResourceRequest?
//            ): Boolean {
//                view?.loadUrl(request?.url.toString())
//                return true
//            }
//        }
//        webView.webViewClient = MyWebViewClient();

        activity.loadUrlProgrammatically(webView, url)
    }
}