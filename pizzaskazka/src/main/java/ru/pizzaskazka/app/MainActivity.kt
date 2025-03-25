package ru.pizzaskazka.app
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.core.content.edit


class MainActivity : AppCompatActivity() {
    var paralyze_bottomNav = true
    var isLukoshko = false
    var isMain = false
    lateinit var main: WebView
    lateinit var about: WebView
    lateinit var profile: WebView
    lateinit var lukoshko: WebView
    var CHANNEL_ID = "order_channel";
    var NOTIFICATION_ID = 0;
    var lukoshko_state = false
    lateinit var bottomNav: BottomNavigationView
    lateinit var sharedPref: SharedPreferences
    lateinit var webViewMap: MutableMap<WebView, Boolean>
    lateinit var webViewMap2: MutableMap<WebView, Int>
    var cart_num = 0
    var cookie_first = true

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(null)

        setContentView(R.layout.activity_main)


        this.main = findViewById(R.id.main)
        this.about = findViewById(R.id.about)
        this.profile = findViewById(R.id.profile)
        this.lukoshko = findViewById(R.id.lukoshko)

        main.webViewClient = MyWebViewClient();
        profile.webViewClient = MyWebViewClient();
        about.webViewClient = MyWebViewClient();
        lukoshko.webViewClient = MyWebViewClient();



        var bottomNav = findViewById<BottomNavigationView>(R.id.toolbar)
        this.bottomNav = bottomNav



        sharedPref = this.getPreferences(Context.MODE_PRIVATE)
        clearAllCookies(this)
        if(this.sharedPref.getInt("city", 0) == 0){
            bottomNav.visibility = View.GONE
        }
        else{
            this.paralyze_bottomNav = false
        }
        cart_num = sharedPref.getInt("cart_num", 0)
        if (cart_num!=0){
            var web_class = WebAppInterface(this)
            web_class.onCartUpdate(cart_num.toString())
        }

        WebViewHelper(main, this).setupWebView("https://pizzaskazka-test.rhvps1.rin.am/?app=android")
        main.loadUrl("https://pizzaskazka-test.rhvps1.rin.am/?app=android")
//        WebViewHelper(about, this).setupWebView("https://pizzaskazka-test.rhvps1.rin.am/about/?app=android")
//        WebViewHelper(profile, this).setupWebView("https://pizzaskazka-test.rhvps1.rin.am/cabinet/?app=android")
//        WebViewHelper(lukoshko, this).setupWebView("https://pizzaskazka-test.rhvps1.rin.am/shop/cart/?app=android")
        bottomNav.setOnItemReselectedListener {
            true
        }
        bottomNav.setOnItemSelectedListener {
            if(!cookie_first){
                loadUrlProgrammatically(main, "https://pizzaskazka-test.rhvps1.rin.am/?app=android")
                syncCookiesFromSource(main, listOf(about, profile, lukoshko))
//                loadUrlProgrammatically(about, "https://pizzaskazka-test.rhvps1.rin.am/about/?app=android")
//                loadUrlProgrammatically(profile, "https://pizzaskazka-test.rhvps1.rin.am/cabinet/?app=android")
//                loadUrlProgrammatically(lukoshko, "https://pizzaskazka-test.rhvps1.rin.am/shop/cart/?app=android")
                WebViewHelper(about, this).setupWebView("https://pizzaskazka-test.rhvps1.rin.am/about/?app=android")
                WebViewHelper(profile, this).setupWebView("https://pizzaskazka-test.rhvps1.rin.am/cabinet/?app=android")
                WebViewHelper(lukoshko, this).setupWebView("https://pizzaskazka-test.rhvps1.rin.am/shop/cart/?app=android")
                cookie_first = true

            }

            when(it.itemId){
                else -> if (paralyze_bottomNav) {
                    false // Do nothing if navigation is paralyzed
                } else {
                    when (it.itemId) {
                        R.id.menu_tab -> {
                            lukoshko_state = true
                            setCurrentFragment(main)
//                            loadUrlProgrammatically(
//                                about,
//                                "https://pizzaskazka-test.rhvps1.rin.am/about/?app=android"
//                            )
//                            loadUrlProgrammatically(
//                                profile,
//                                "https://pizzaskazka-test.rhvps1.rin.am/cabinet/?app=android"
//                            )
//                            loadUrlProgrammatically(
//                                lukoshko,
//                                "https://pizzaskazka-test.rhvps1.rin.am/shop/cart/?app=android"
//                            )


                        }

                        R.id.skazka_tab -> {
                            lukoshko_state = false
                            setCurrentFragment(about)
//                            loadUrlProgrammatically(
//                                profile,
//                                "https://pizzaskazka-test.rhvps1.rin.am/cabinet/?app=android"
//                            )
//                            loadUrlProgrammatically(
//                                lukoshko,
//                                "https://pizzaskazka-test.rhvps1.rin.am/shop/cart/?app=android"
//                            )
                        }

                        R.id.lk_tab -> {
                            lukoshko_state = false
                            setCurrentFragment(profile)
//                            loadUrlProgrammatically(
//                                lukoshko,
//                                "https://pizzaskazka-test.rhvps1.rin.am/shop/cart/?app=android"
//                            )
                        }

                        R.id.lukoshko_tab -> {
                            lukoshko_state = true
                            setCurrentFragment(lukoshko)
//                            loadUrlProgrammatically(
//                                main,
//                                "https://pizzaskazka-test.rhvps1.rin.am/?app=android"
//                            )
//                            loadUrlProgrammatically(
//                                profile,
//                                "https://pizzaskazka-test.rhvps1.rin.am/cabinet/?app=android"
//                            )


//                            createNotificationChannel()
//                            showNotification("Уведомление", "Вы в лукошке")


                        }
                    }
                }

            }
            true
        }


    }
    private fun initialize_webViewMap(){
        init_main()
        webViewMap = mutableMapOf(this.main to false, this.about to false, this.profile to false, this.lukoshko to false)
//        webViewMap2 = mutableMapOf(menu to this.main, ab to this.about, this.profile to this.profile, this.lukoshko to this.lukoshko)
    }

    private fun init_main(){
        this.main = findViewById(R.id.main)
        this.about = findViewById(R.id.about)
        this.profile = findViewById(R.id.profile)
        this.lukoshko = findViewById(R.id.lukoshko)
    }

    private fun createNotificationChannel() {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Заказы",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Уведомления о заказах"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500)
            }

            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Log.d("Notification", "Channel created: $CHANNEL_ID")
        }
    private fun showNotification(title: String, message: String) {
        // Создаем Intent для открытия Order Activity
        val intent = Intent(this, Order::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        // Создаем PendingIntent
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )

        // Создаем уведомление
        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true) // Уведомление исчезнет после нажатия

        // Отправляем уведомление
        with(NotificationManagerCompat.from(this)) {
            try {
                notify(NOTIFICATION_ID, builder.build())
            } catch (e: SecurityException) {
                Log.e("MainActivity", "Нет разрешения на отправку уведомлений", e)
            }
        }


    }


    fun setCurrentFragment(view: WebView){
        main.visibility = View.GONE
        about.visibility = View.GONE
        profile.visibility = View.GONE

        lukoshko.visibility = View.GONE

        view.visibility = View.VISIBLE

    }

    private fun clearAllCookies(activity: MainActivity) {
        activity.sharedPref.edit() { putInt("cart_num", 0) }
        activity.sharedPref.edit() { putInt("city", 0) }

        val cookieManager = CookieManager.getInstance()

        cookieManager.removeAllCookies { success ->
            if (success) {
                Log.d("CookieManager", "All cookies removed successfully")
            } else {
                Log.e("CookieManager", "Failed to remove cookies")
            }
        }
        cookieManager.flush()

        // Очищаем кэш и данные WebView
        listOf(main, about, profile, lukoshko).forEach { webView ->
            webView.clearCache(true)
            webView.clearFormData()
            webView.clearHistory()
            webView.clearSslPreferences()
        }
    }


    inner class Order(){
        fun viewOrderStatus(){

        }
    }

    fun syncCookiesFromSource(sourceWebView: WebView, targetWebViews: List<WebView>) {
        val cookieManager = CookieManager.getInstance()
        val url = sourceWebView.url
        Log.d("CookieDebug", "Syncing cookies from URL: $url")
        val cookies = cookieManager.getCookie(url)
        Log.d("CookieDebug", "Cookies from source WebView: $cookies")

        if (!cookies.isNullOrEmpty()) {
            // Устанавливаем cookies в другие WebView
            targetWebViews.forEach { targetWebView ->
                cookies.split(";").forEach { cookie ->
                    val trimmedCookie = cookie.trim()
                    if (trimmedCookie.isNotEmpty()) {
                        Log.d("CookieDebug", "Setting cookie in target WebView: $trimmedCookie")
                        cookieManager.setCookie(url, trimmedCookie)
                    }
                }
            }
            cookieManager.flush() // Применяем изменения
            Log.d("CookieDebug", "Cookies flushed and synced")
        } else {
            Log.d("CookieDebug", "No cookies found for URL: $url")
        }
    }
    fun loadUrlProgrammatically(webView: WebView, url: String) {
        if (!this::webViewMap.isInitialized) {
            initialize_webViewMap()
        }
        // Проверяем, совпадает ли текущий URL с новым
        if (webView.url == url) {
            Log.d("WebViewLoad", "URL $url already loaded in ${webView.id}, skipping")
            return
        }
        Log.d("Programmatic URL", url)
        webViewMap[webView] = true
        webView.loadUrl(url)

    }


    open inner class MyWebViewClient : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            val uri = request?.url
            val url = uri.toString()
            webViewMap.forEach{
                if (it.value){
                    return false
                }

            }
//            if (webViewMap[view] == true){
//                return false
//            }
//            var a = true
//            while(a){
//                webViewMap.forEach{
//                a = it.value
//            }
//            }
            Log.d("URL", url)

            if(bottomNav.selectedItemId == R.id.about){
                return false
            }

            if (url.contains("https://pizzaskazka-test.rhvps1.rin.am/about") || url.contains("https://pizzaskazka-test.rhvps1.rin.am/delivery") || url.contains("https://pizzaskazka-test.rhvps1.rin.am/feedback") || url.contains("https://pizzaskazka-test.rhvps1.rin.am/soberi_magnityi") || url.contains("https://pizzaskazka-test.rhvps1.rin.am/catalog/havchik")) {
                loadUrlProgrammatically(about, url)
                bottomNav.selectedItemId = R.id.about

                return true

            }
            else if(url.contains("https://pizzaskazka-test.rhvps1.rin.am/cabinet/confirmation/?target=shop")){
                loadUrlProgrammatically(lukoshko, url)
                bottomNav.selectedItemId = R.id.lukoshko_tab
                lukoshko_state = true
                return true

            }
            else if(url.contains("https://pizzaskazka-test.rhvps1.rin.am/cabinet/confirmation")){
                if (lukoshko_state){
                    loadUrlProgrammatically(lukoshko, url)
                    bottomNav.selectedItemId = R.id.lukoshko_tab

                }
                else{
                    loadUrlProgrammatically(profile, url)
                    bottomNav.selectedItemId = R.id.lk_tab

                }
                return true

            }
            else if(url.contains("https://pizzaskazka-test.rhvps1.rin.am/cabinet")){
//                if (lukoshko_state){
//                    loadUrlProgrammatically(lukoshko, url)
//                    bottomNav.selectedItemId = R.id.lukoshko_tab
//
//                }
//                else{
//                    loadUrlProgrammatically(profile, url)
//                    bottomNav.selectedItemId = R.id.lk_tab
//
//                }
                loadUrlProgrammatically(profile, url)
                bottomNav.selectedItemId = R.id.lk_tab

                return true

            }
            else if ( url.contains("https://pizzaskazka-test.rhvps1.rin.am/payment/done/yookassa")) {
                loadUrlProgrammatically(profile, url)
                bottomNav.selectedItemId = R.id.lk_tab
                return true
            } else if(url.contains("https://pizzaskazka-test.rhvps1.rin.am/payment/prepaid/cash")) {
                bottomNav.selectedItemId = R.id.lk_tab
                return true
                //|| url.contains("https://pizzaskazka-test.rhvps1.rin.am/#")
            } else if(url.contains("https://pizzaskazka-test.rhvps1.rin.am/shop") ){
                if ((bottomNav.selectedItemId == R.id.lukoshko_tab) || (bottomNav.selectedItemId == R.id.lk_tab)){
                    loadUrlProgrammatically(lukoshko, url)

                }
                else{
                    bottomNav.selectedItemId = R.id.lukoshko_tab
                }
                return true
            }else if (url.contains("https://pizzaskazka-test.rhvps1.rin.am/payment") ) {
                loadUrlProgrammatically(lukoshko, url)
                bottomNav.selectedItemId = R.id.lukoshko_tab
                return true
            } else if (url.contains("https://pizzaskazka-test.rhvps1.rin.am/")) {
                loadUrlProgrammatically(main, url)
                bottomNav.selectedItemId = R.id.menu_tab
                return true
            }
            return true
        }
        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            if (view != null) {
                webViewMap.replace(view, false)
            }
            syncCookiesFromSource(main, listOf(about, profile, lukoshko))
            Log.d("WebViewDebug", "Page finished loading: $url")
        }


    }

    inner class WebAppInterface(private val activity: MainActivity) {
        @JavascriptInterface
        fun onCartUpdate (n:String){
            activity.runOnUiThread {
                if(activity::bottomNav.isInitialized){
                activity.bottomNav.getOrCreateBadge(R.id.lukoshko_tab).apply {
                    Log.d("CART", "Received from JS: $n")
                    activity.sharedPref.edit { putInt("cart_num", n.toInt()) }
                    activity.cart_num = n.toInt()
                    backgroundColor = ContextCompat.getColor(activity, R.color.main_red)
                    badgeTextColor = Color.WHITE
                    maxCharacterCount = 3
                    number = n.toInt()
                    if (n.toInt()!=0){
                        isVisible = true
                    }
                    else{
                        isVisible = false
                    }
                }}
                if (activity.bottomNav.selectedItemId != R.id.lukoshko_tab) {
                    activity.loadUrlProgrammatically(
                        activity.lukoshko,
                        "https://pizzaskazka-test.rhvps1.rin.am/shop/cart/?app=android"
                    )
                }
            }

        }
        @JavascriptInterface
        public fun onSiteClose(){
            activity.runOnUiThread {
                if(activity::bottomNav.isInitialized){
                    val bottomNav = activity.bottomNav
                    bottomNav.visibility = View.GONE
                }}
            }

        @JavascriptInterface
        public fun onSiteOpen(){
            activity.runOnUiThread {
                if(activity::bottomNav.isInitialized){
                    val bottomNav = activity.bottomNav
                    bottomNav.visibility = View.VISIBLE
            }}
        }

        @JavascriptInterface
        public fun onReload(fragment:String){
            Log.d("RELOAD","reload");
            activity.runOnUiThread {

                when(fragment){
                    "main"->{
                        if(activity::main.isInitialized ){
                            activity.main.loadUrl("https://pizzaskazka-test.rhvps1.rin.am/?app=android")
                        }
                    }
                    "about"->{
                        if(activity::about.isInitialized ){
                        activity.about.loadUrl("https://pizzaskazka-test.rhvps1.rin.am/about/?app=android")
                    }}
                    "profile"->{
                        if(activity::profile.isInitialized ){
                        activity.profile.loadUrl("https://pizzaskazka-test.rhvps1.rin.am/cabinet/?app=android")

                    }}
                    "lukoshko"->{
                        if(activity::lukoshko.isInitialized ){
                        activity.lukoshko.loadUrl("https://pizzaskazka-test.rhvps1.rin.am/shop/cart/?app=android")

                    }}
                }
            }
        }
        @JavascriptInterface
        public fun cityAccept(){
            activity.runOnUiThread{
                if (!activity::main.isInitialized){
                    activity.init_main()
                }
                Log.d("cityAccept", "cityAccepted")
                activity.loadUrlProgrammatically(activity.main, "https://pizzaskazka-test.rhvps1.rin.am/?app=android")
//                loadUrlProgrammatically(activity.about, "https://pizzaskazka-test.rhvps1.rin.am/about/?app=android")
//                loadUrlProgrammatically(activity.profile, "https://pizzaskazka-test.rhvps1.rin.am/cabinet/?app=android")
//                loadUrlProgrammatically(activity.lukoshko, "https://pizzaskazka-test.rhvps1.rin.am/shop/cart/?app=android")
                activity.paralyze_bottomNav = false
                activity.bottomNav.visibility = View.VISIBLE
                activity.sharedPref.edit{ putInt("city", 1) }
                activity.cookie_first = false

            }
        }
        }


}









