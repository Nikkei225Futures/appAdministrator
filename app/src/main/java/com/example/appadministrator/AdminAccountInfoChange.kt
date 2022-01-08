package com.example.appadministrator

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class AdminAccountInfoChange : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_account_info_change)
        val buttonToHome: Button = findViewById(R.id.buttonHome)
        val buttonToSearchRestaurant: Button = findViewById(R.id.buttonSearchRestaurant)
        val buttonToSearchUser: Button = findViewById(R.id.buttonSearchUser)
        val buttonToSetting: Button = findViewById(R.id.buttonSetting)

        //bottom footer event listeners
        buttonToHome.setOnClickListener {
            val intent = Intent(this@AdminAccountInfoChange, Administrator::class.java)
            startActivity(intent)
        }

        buttonToSearchRestaurant.setOnClickListener{
            TODO("not yet implemented")
        }

        buttonToSearchUser.setOnClickListener {
            TODO("not yet implemented")
        }

        buttonToSetting.setOnClickListener {
            val intent = Intent(this@AdminAccountInfoChange, AdminAccountInfoChange::class.java)
            startActivity(intent)
        }

    }
}