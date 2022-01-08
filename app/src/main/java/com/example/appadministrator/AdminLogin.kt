package com.example.appadministrator

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import java.net.URI
import org.json.*

class AdminLogin : AppCompatActivity() {
    companion object{
        const val loginMsgId: Int = 1
    }

    private val uri = WsClient.serverRemote
    private val client = MyLoginWsClient(this, uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_login)
    }

    override fun onResume() {
        super.onResume()
        client.connect()

        //edit text, name and password
        val eTxtUserName: EditText = findViewById(R.id.textBoxUserName)
        val eTxtPassword: EditText = findViewById(R.id.textBoxPassword)

        val buttonLogin: Button = findViewById(R.id.buttonLogin)
        //eventlisten
        buttonLogin.setOnClickListener {
            var loginRequest: JSONObject = JSONObject()
            var loginParams: JSONObject = JSONObject()
            val userName: String = eTxtUserName.text.toString()
            val password: String = eTxtPassword.text.toString()
            val role: String = "admin"

            loginParams.put("user_name", userName)
            loginParams.put("password", password)
            loginParams.put("role", role)

            loginRequest.put("jsonrpc", "2.0")
            loginRequest.put("id", loginMsgId)
            loginRequest.put("method", "login")
            loginRequest.put("params", loginParams)
            Log.i(javaClass.simpleName, "send login req")
            Log.i(javaClass.simpleName, loginRequest.toString())
            client.send(loginRequest.toString())
        }
    }
}

class MyLoginWsClient(private val activity: Activity, uri: URI) : WsClient(uri){

    private val errorDisplay : TextView by lazy{
        activity.findViewById<TextView>(R.id.errorDisplay)
    }

    override fun onMessage(message: String?) {
        super.onMessage(message)
        Log.i(javaClass.simpleName, "msg arrived")
        Log.i(javaClass.simpleName, "$message")
        val wholeMsg: JSONObject = JSONObject(message)
        val ResId: Int = wholeMsg.getInt("id")
        val result: JSONObject = wholeMsg.getJSONObject("result")
        val status: String = result.getString("status")

        //if message is about login
        if(ResId == AdminLogin.loginMsgId){
            if(status == "success"){
                val token: String = result.getString("token")
                val expire: String = result.getString("expire")
                Log.i(javaClass.simpleName, "login success")
                Log.i(javaClass.simpleName, "token: $token")
                Log.i(javaClass.simpleName, "expires in $expire")

                this.close(WsClient.NORMAL_CLOSURE)
                activity.runOnUiThread{
                    val intent: Intent = Intent(activity, Administrator::class.java)
                    activity.startActivity(intent)
                }

            }else if(status == "error"){
                val reason: String = result.getString("reason")
                errorDisplay.visibility = View.VISIBLE
                Log.i(javaClass.simpleName, "login failed with reason $reason")
            }
        }
    }
}
