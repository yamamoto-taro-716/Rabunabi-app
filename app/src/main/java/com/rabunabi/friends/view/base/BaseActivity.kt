package com.rabunabi.friends.view.base

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import com.google.android.gms.ads.*
import com.rabunabi.friends.BalloonchatApplication
import com.rabunabi.friends.common.Const
import com.rabunabi.friends.common.DialogUtils
import com.rabunabi.friends.utils.SharePreferenceUtils
import java.util.*
import com.rabunabi.friends.utils.StatusBarUtil
import com.rabunabi.friends.R


abstract class BaseActivity : AppCompatActivity() {
    var adView: AdView? = null;
    var mInterstitialAd: InterstitialAd? = null;

    companion object {
        var lang: String? = "EN"

        var FORM_PROFILE = 1
        var FORM_CHAT = 2
        var FORM_HOME = 3
        var FORM_HOME_RESULT = 4
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initStatusBarColor()
        setContentView(getLayoutId());
        if (SharePreferenceUtils.getInstances().getString(Const.DEVICE_ID).isNullOrEmpty()) {
            val deviceId = Settings.Secure.getString(
                contentResolver, Settings.Secure.ANDROID_ID
            )
            SharePreferenceUtils.getInstances().saveString(Const.DEVICE_ID, deviceId)
        }
        setLanguage()
        StatusBarUtil.setColorNoTranslucent(
            this,
            resources.getColor(R.color.colorPrimary)
        )

        // admob config
        println("DIEP Admon ========= count_ads " + SharePreferenceUtils.getInstances().getStartInfo().count_ads)
        println("DIEP Admon =========  MobileAds.initialize ")
        MobileAds.initialize(this, getString(R.string.admob_app_id))
        initAdmobBanner()
        // end admob
        initView()
    }

    fun initAdmobBanner() {
        var count_ads = SharePreferenceUtils.getInstances().getStartInfo().count_ads
        adView = findViewById(R.id.ad_view)
        if (count_ads!! > 0) {
            adView?.let {
                adView!!.visibility = View.VISIBLE

             /*   val adRequest = AdRequest
                    .Builder()
                    .addTestDevice("CC9DW7W7R4H0NM3LT9OLOF7455F8800D")
                    .build()
                it.loadAd(adRequest)*/

                it.loadAd(AdRequest.Builder().build())
                ///
                adView?.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                    }

                    override fun onAdFailedToLoad(errorCode: Int) {
                        println("DIEP Admon onAdFailedToLoad " + errorCode)
                    }

                    override fun onAdOpened() {
                        // Code to be executed when the ad is displayed.
                    }

                    override fun onAdLeftApplication() {
                        // Code to be executed when the user has left the app.
                    }

                    override fun onAdClosed() {
                        //mAdView.loadAd(adRequest)
                        //Toast.makeText(applicationContext,"Banner Ad Loaded Again", Toast.LENGTH_LONG).show()
                    }
                }
            }
        } else {
            adView?.let {
                adView!!.visibility = View.INVISIBLE
            }
        }
    }

    fun initInterstitialAd(from: Int) {
        var count_ads = SharePreferenceUtils.getInstances().getStartInfo().count_ads
        if (count_ads!! == BalloonchatApplication.ADS_CONFIG) {
            if (from == FORM_PROFILE) {
                // được phép hiển thị admob. (trường hợp chờ review)
            } else {
                return
            }
        }
        if (count_ads!! > 0) {
            //Check the number of admob display
            var displayedCount =
                SharePreferenceUtils.getInstances().getInt(BalloonchatApplication.AD_DISPLAY_COUNT)
            // Create the InterstitialAd and set it up.
            mInterstitialAd = InterstitialAd(this).apply {
                adUnitId = getString(R.string.admob_interstitial_fullscreen_id)
                adListener = (object : AdListener() {
                    override fun onAdLoaded() {
                        mInterstitialAd?.let {
                            if (it.isLoaded()) {
                                if (displayedCount > count_ads) {
                                    return
                                } else {
                                    displayedCount++
                                    SharePreferenceUtils.getInstances().saveInt(
                                        BalloonchatApplication.AD_DISPLAY_COUNT, displayedCount
                                    )
                                }

                                it.show()
                            }
                        }
                    }

                    override fun onAdFailedToLoad(errorCode: Int) {
                    }

                    override fun onAdClosed() {
                    }
                })
            }
            mInterstitialAd?.let {
                if (!it?.isLoading && !it?.isLoaded) {
                    it?.loadAd(AdRequest.Builder().build())
                }
            }
        }

    }

    /*override fun onPause() {
        adView?.let {
            it.pause()
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        adView?.let {
            it.resume()
        }
    }

    override fun onDestroy() {
        adView?.let {
            it?.destroy()
        }
        super.onDestroy()
    }*/

    fun setLanguage() {
        lang = SharePreferenceUtils.getInstances().getString(
            Const.LANG_CODE
        )
        if (SharePreferenceUtils.getInstances().getString(Const.LANG_CODE) == "JP") {
            lang = "JA"
        }
        if (lang.equals("")) {
            lang = "EN"
        }
        val myLocale = Locale(
            lang, when (lang) {
                "JA" -> "JP"
                "EN" -> "US"
                else -> {
                    "VI"
                }
            }
        )
        val res = resources
        val conf = res.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            conf.setLocale(myLocale)
        } else {
            conf.locale = myLocale
        }
        res.updateConfiguration(conf, res.displayMetrics)
    }

    abstract fun getLayoutId(): Int

    abstract fun initView()


    fun showLoading() {
        DialogUtils.showLoadingDialog(this)
    }

    fun hideLoading() {
        DialogUtils.dismiss()
    }

    fun goToActivity(c: Class<*>, bundle: Bundle? = null) {
        val intent = Intent(this, c)
        bundle?.let {
            intent.putExtra(Const.KEY_EXTRA_DATA, bundle)
        }
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    fun goToActivityForResult(c: Class<*>, bundle: Bundle? = null) {
        val intent = Intent(this, c)
        bundle?.let {
            intent.putExtra(Const.KEY_EXTRA_DATA, bundle)
        }
        startActivityForResult(intent, 99)
        overridePendingTransition(0, 0)
    }

    fun extraData(getData: (Bundle) -> Unit) {
        var bundle = intent.getBundleExtra(Const.KEY_EXTRA_DATA)
        getData.invoke(bundle!!)
    }

    fun replaceFragment(viewID: Int, fragment: Fragment, tag: String? = null) {
        var transaction = supportFragmentManager.beginTransaction()
        transaction.add(viewID, fragment)
        transaction.addToBackStack(tag)
        transaction.commit()
    }

    private fun initStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR)
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
    }


}