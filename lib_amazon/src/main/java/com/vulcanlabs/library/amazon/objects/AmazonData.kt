package com.vulcanlabs.library.amazon.objects

import com.google.gson.Gson

class AmazonData(
    var appId: String? = null,
    var interId: String? = null,
    var bannerId: String? = null,
    var rewardId: String? = null,
    var openId: String? = null,
    var adsType: Int = 0
) {
    companion object {
        fun readJson(json: String): AmazonData? {
            return try {
                Gson().fromJson(
                    json, AmazonData::class.java
                )
            } catch (e: Exception) {
                e.fillInStackTrace()
                null
            }
        }
    }
}