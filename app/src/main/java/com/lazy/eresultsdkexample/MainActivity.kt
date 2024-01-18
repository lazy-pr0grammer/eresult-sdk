package com.lazy.eresultsdkexample

import android.graphics.Bitmap
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.eresult.sdk.EResult
import com.eresult.sdk.EResult.CaptchaCallback
import com.eresult.sdk.data.type.ResultType

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val i1 = findViewById<ImageView>(R.id.i1)

        val result = EResult.Builder()
            .setResultType(ResultType.BOARD)
            .build()

        result.query()

//        result.requestCaptcha(object : CaptchaCallback {
//            override fun decodedBitmap(bitmap: Bitmap?) {
//                i1.setImageBitmap(bitmap)
//            }
//
//            override fun decodingFailure(message: String?) {
//                Log.d(javaClass.simpleName, message!!)
//            }
//
//        })
    }
}