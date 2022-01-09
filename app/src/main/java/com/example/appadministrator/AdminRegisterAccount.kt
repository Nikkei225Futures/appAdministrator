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
import org.json.JSONObject
import java.net.URI

class AdminRegisterAccount : AppCompatActivity() {
    companion object{
        const val registerReqId: Int = 2
    }

    private val uri = WsClient.serverRemote
    private var client = RegisterWsClient(this, uri)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_register_account)
    }

    override fun onResume() {
        super.onResume()
        client.connect()

        //edit text, name, password, and admin_password
        val eTxtUserName: EditText = findViewById(R.id.textBoxUserName)
        val eTxtPassword: EditText = findViewById(R.id.textBoxPassword)
        val eTxtAdminPassword: EditText = findViewById(R.id.textBoxAdminPassword)
        val errorDisplay: TextView = findViewById(R.id.errorDisplay)

        //when register button pushed
        val buttonRegister: Button = findViewById(R.id.buttonRegister)
        buttonRegister.setOnClickListener {
            val registerRequest = JSONObject()
            val registerParams = JSONObject()
            val userName: String = eTxtUserName.text.toString()
            val password: String = eTxtPassword.text.toString()
            val adminPassword: String = eTxtAdminPassword.text.toString()

            //if some field empty,
            if(userName.isEmpty() || password.isEmpty() || adminPassword.isEmpty()){
                when {
                    userName.isEmpty() -> {
                        errorDisplay.text = "ユーザネームが入力されていません"
                    }
                    password.isEmpty() -> {
                        errorDisplay.text = "パスワードが入力されていません"
                    }
                    adminPassword.isEmpty() -> {
                        errorDisplay.text = "admin_passwordが入力されていません"
                    }
                }
                errorDisplay.visibility = View.VISIBLE
                return@setOnClickListener
            }

            registerParams.put("admin_name", userName)
            registerParams.put("password", password)
            registerParams.put("admin_password", adminPassword)

            registerRequest.put("jsonrpc", "2.0")
            registerRequest.put("id", registerReqId)
            registerRequest.put("method", "register/admin")

            registerRequest.put("params", registerParams)

            Log.i(javaClass.simpleName, "send register req")
            Log.i(javaClass.simpleName, registerRequest.toString())
            client.send(registerRequest.toString())

        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onRestart() {
        super.onRestart()
        client = RegisterWsClient(this, uri)
    }
}

class RegisterWsClient(private val activity: Activity, uri: URI) : WsClient(uri){
    private val errorDisplay : TextView by lazy {
        activity.findViewById(R.id.errorDisplay)
    }

    override fun onMessage(message: String?) {
        super.onMessage(message)
        Log.i(javaClass.simpleName, "msg arrived")
        Log.i(javaClass.simpleName, "$message")

        val wholeMsg = JSONObject("$message")
        val resId: Int = wholeMsg.getInt("id")
        val result: JSONObject = wholeMsg.getJSONObject("result")
        val status: String = result.getString("status")

        //if message is about register/admin
        if(resId == AdminRegisterAccount.registerReqId){
            //if success, transition to ShowResult page
            if(status == "success"){
                val intent = Intent(activity, ShowResult::class.java)
                val message = "アカウント登録が完了しました"
                val transitionBtnMessage = "ログインページへ"
                val isBeforeLogin = true

                intent.putExtra("message", message)
                intent.putExtra("transitionBtnMessage", transitionBtnMessage)
                intent.putExtra("isBeforeLogin", isBeforeLogin)
                activity.runOnUiThread{
                    activity.startActivity(intent)
                }

            //when error occurred with registration
            }else if(status == "error"){
                val reason: String = result.getString("reason")
                activity.runOnUiThread{
                    if(reason == "You don't have permission to create admin account. admin_password is wrong."){
                        errorDisplay.text = "admin_passwordが間違っています"
                    }else{
                        errorDisplay.text = reason
                    }
                    errorDisplay.visibility = View.VISIBLE
                    Log.i(javaClass.simpleName, "registration failed with reason $reason")
                }
            }
        }

    }
}