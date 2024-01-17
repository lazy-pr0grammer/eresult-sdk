package com.lazy.eresultsdkexample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.eresult.sdk.EResult
import com.eresult.sdk.data.type.ResultType

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val result = EResult.Builder()
            .setResultType(ResultType.BOARD)
            .build()

        result.query()
    }
}