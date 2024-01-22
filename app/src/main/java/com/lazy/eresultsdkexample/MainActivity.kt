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

        // UI elements
        val b1 = findViewById<Button>(R.id.b1)
        val e1 = findViewById<EditText>(R.id.e1)
        val i1 = findViewById<ImageView>(R.id.i1)

        // Example of using the EResult library to build a result request
        val result = EResult.Builder() // Creating a new instance of the EResult.Builder
            .setYear("2023") // Setting the academic year
            .setExamType(ExamType.HSC) // Setting the type of examination (e.g., HSC)
            .setRegistrationId("reg_id") // Setting the registration ID
            .setStudentRollNumber("roll_num") // Setting the student roll number
            .setBoardType(BoardType.CHITTAGONG) // Setting the type of educational board (e.g., Chittagong)
            .setResultType(ResultType.INDIVIDUAL) // Setting the type of result (e.g., INDIVIDUAL)
            .build() // Building the EResult instance with the specified parameters

        // Example of requesting a captcha image
        result.requestCaptcha(object : ResultCallback<Bitmap> {
            override fun onResponse(result: Bitmap?) {
                // Displaying the captcha image in an ImageView
                i1.setImageBitmap(result)
            }

            override fun onFailure(result: String?) {
                // Handling failure to retrieve the captcha
                toast(result!!)
            }
        })

        // Example of handling a button click to request exam results
        b1.setOnClickListener {
            if (e1.text.isNotEmpty()) {
                // Example of requesting exam results using the entered data
                result.requestResult(e1.text.toString(), object : ResultCallback<String> {
                    override fun onResponse(result: String?) {
                        // Displaying the exam result using a MaterialAlertDialog
                        MaterialAlertDialogBuilder(this@MainActivity)
                            .setTitle("Result")
                            .setMessage(result)
                            .create()
                            .show()
                    }

                    override fun onFailure(result: String?) {
                        // Handling failure to retrieve exam results
                        toast(result!!)
                    }
                })
            }
        }
    }

    /**
     * Helper function for displaying Toast messages.
     *
     * @param string The message to display.
     */
    private fun toast(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }
}
