package com.example.appadministrator

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ShowResult : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_result)
        val message = intent.getStringExtra("message")
        val transitionBtnMessage = intent.getStringExtra("transitionBtnMessage")
        val isBeforeLogin = intent.getBooleanExtra("isBeforeLogin", true)

        val txtMessage: TextView = findViewById(R.id.message)
        val transitionBtn: Button = findViewById(R.id.transitionButton)

        txtMessage.text = message
        transitionBtn.text = transitionBtnMessage

        transitionBtn.setOnClickListener {
            intent = if(isBeforeLogin){
                Intent(this@ShowResult, AdminLogin::class.java)
            }else{
                Intent(this@ShowResult, Administrator::class.java)
            }

            startActivity(intent)
        }
    }
}