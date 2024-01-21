package com.lazy.eresultsdkexample

import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.eresult.sdk.EResult
import com.eresult.sdk.EResult.ResultCallback
import com.eresult.sdk.data.BoardType
import com.eresult.sdk.data.ExamType
import com.eresult.sdk.data.ResultType
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val b1 = findViewById<Button>(R.id.b1)
        val e1 = findViewById<EditText>(R.id.e1)
        val i1 = findViewById<ImageView>(R.id.i1)

        val result = EResult.Builder()
            .setYear("2023")
            .setExamType(ExamType.HSC)
            .setRegistrationId("reg_id")
            .setStudentRollNumber("roll_num")
            .setBoardType(BoardType.CHITTAGONG)
            .setResultType(ResultType.INDIVIDUAL)
            .build()

        result.requestCaptcha(object : ResultCallback<Bitmap> {
            override fun onResponse(result: Bitmap?) {
                i1.setImageBitmap(result)
            }

            override fun onFailure(result: String?) {
                toast(result!!)
            }
        })

        b1.setOnClickListener {
            if (e1.text.isNotEmpty()) {
                result.requestResult(e1.text.toString(), object : ResultCallback<String> {
                    override fun onResponse(result: String?) {
                        MaterialAlertDialogBuilder(this@MainActivity)
                            .setTitle("Result")
                            .setMessage(result)
                            .create()
                            .show()
                    }

                    override fun onFailure(result: String?) {
                        toast(result!!)
                    }
                })
            }
        }
    }

    private fun toast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }
}
