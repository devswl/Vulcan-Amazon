package com.vulcanlabs.library.amazon.manager

import android.content.Context
import android.os.Bundle
import com.amazon.admob_adapter.APSAdMobCustomEvent
import com.amazon.device.ads.AdError
import com.amazon.device.ads.AdRegistration
import com.amazon.device.ads.DTBAdCallback
import com.amazon.device.ads.DTBAdNetwork
import com.amazon.device.ads.DTBAdNetworkInfo
import com.amazon.device.ads.DTBAdRequest
import com.amazon.device.ads.DTBAdResponse
import com.amazon.device.ads.DTBAdSize
import com.amazon.device.ads.DTBAdUtil
import com.amazon.device.ads.MRAIDPolicy
import com.applovin.mediation.MaxAdFormat
import com.google.android.gms.ads.mediation.MediationExtrasReceiver
import com.vulcanlabs.library.amazon.objects.AmazonData

class AmazonManager() {
    private var amazonData: AmazonData? = null
    fun setupConfigForAdmob(context: Context, data: AmazonData?, isTesting: Boolean) {
        amazonData = data
        amazonData?.appId?.let {
            setupAmazonForAdmob(context, it, isTesting)
        }
    }

    fun setupConfigForMax(context: Context, data: AmazonData?, isTesting: Boolean) {
        amazonData = data
        amazonData?.appId?.let {
            setupAmazonForMax(context, it, isTesting)
        }
    }

    private fun setupAmazonForAdmob(context: Context, apsId: String, isTesting: Boolean) {
        // Initialize Amazon Publisher Services (APS) SDK
        AdRegistration.getInstance(apsId.trim(), context)
        AdRegistration.enableTesting(isTesting)
        AdRegistration.enableLogging(isTesting)
        AdRegistration.setAdNetworkInfo(DTBAdNetworkInfo(DTBAdNetwork.ADMOB))
    }

    private fun setupAmazonForMax(context: Context, apsId: String, isTesting: Boolean) {
        try {
            AdRegistration.getInstance(apsId.trim(), context)
            AdRegistration.enableTesting(isTesting)
            AdRegistration.enableLogging(isTesting)
            AdRegistration.setMRAIDSupportedVersions(arrayOf("1.0", "2.0", "3.0"))
            AdRegistration.setMRAIDPolicy(MRAIDPolicy.CUSTOM)
            AdRegistration.setAdNetworkInfo(DTBAdNetworkInfo(DTBAdNetwork.MAX))
        } catch (_: Exception) {
        }
        // Initialize Amazon Publisher Services (APS) SDK
    }

    fun initAdMobInterstitial(): Pair<Class<out MediationExtrasReceiver>, Bundle>? {
        val pair = amazonData?.interId?.let {
            val bundle = DTBAdUtil.createAdMobInterstitialRequestBundle(it)
            Pair<Class<out MediationExtrasReceiver>, Bundle>(
                APSAdMobCustomEvent::class.java,
                bundle
            )

        }
        return pair
    }

    fun initAdMobReward(): Pair<Class<out MediationExtrasReceiver>, Bundle>? {
        val pair = amazonData?.rewardId?.let {
            val bundle = DTBAdUtil.createAdMobInterstitialRequestBundle(it)
            Pair<Class<out MediationExtrasReceiver>, Bundle>(
                APSAdMobCustomEvent::class.java,
                bundle
            )
        }
        return pair
    }

    fun initAdMobBanner(): Pair<Class<out MediationExtrasReceiver>, Bundle>? {
        val pair = amazonData?.bannerId?.let {
            val bundle = DTBAdUtil.createAdMobBannerRequestBundle(it, 320, 50)
            Pair<Class<out MediationExtrasReceiver>, Bundle>(
                APSAdMobCustomEvent::class.java,
                bundle
            )
        }
        return pair
    }

    fun initMaxInterstitial(callback: ((Result<Pair<Any, Any>?>) -> Unit)? = null) {
        amazonData?.interId?.let {
            val adLoader = DTBAdRequest()
            adLoader.setSizes(DTBAdSize.DTBInterstitialAdSize(it))
            adLoader.loadAd(object : DTBAdCallback {
                override fun onFailure(adError: AdError) {
                    callback?.invoke(Result.success(Pair("amazon_ad_error", adError)))
                }

                override fun onSuccess(dtbAdResponse: DTBAdResponse) {
                    callback?.invoke(Result.success(Pair("amazon_ad_response", dtbAdResponse)))
                }
            })
        }
    }

    fun initMaxReward(callback: ((Result<Pair<Any, Any>?>) -> Unit)? = null) {
        amazonData?.rewardId?.let {
            val adLoader = DTBAdRequest()
            adLoader.setSizes(DTBAdSize.DTBVideo(320, 480, it))
            adLoader.loadAd(object : DTBAdCallback {
                override fun onFailure(adError: AdError) {
                    callback?.invoke(Result.success(Pair("amazon_ad_error", adError)))
                }

                override fun onSuccess(dtbAdResponse: DTBAdResponse) {
                    callback?.invoke(Result.success(Pair("amazon_ad_response", dtbAdResponse)))
                }
            })
        }
    }

    fun initMaxBanner(callback: ((Result<Pair<Any, Any>?>) -> Unit)? = null) {
        amazonData?.bannerId?.let {
            val adFormat = MaxAdFormat.BANNER
            val rawSize = DTBAdSize(adFormat.size.width, adFormat.size.height, it)
            val size = DTBAdSize(rawSize.width, rawSize.height, it)
            val adLoader = DTBAdRequest()
            adLoader.setSizes(size)
            adLoader.loadAd(object : DTBAdCallback {
                override fun onFailure(adError: AdError) {
                    callback?.invoke(Result.success(Pair("amazon_ad_error", adError)))
                }

                override fun onSuccess(dtbAdResponse: DTBAdResponse) {
                    callback?.invoke(Result.success(Pair("amazon_ad_response", dtbAdResponse)))
                }
            })
        }
    }
}