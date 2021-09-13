package com.criollojose.remdbman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.criollojose.remdbman.databinding.ActivityMainBinding
import com.criollojose.remdbman.utils.Constants
import com.criollojose.remdbman.utils.Constants.EXTRA_LOGIN
import com.criollojose.remdbman.utils.Constants.LOGIN_KEY
import com.criollojose.remdbman.utils.Constants.PASSWORD_KEY
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    //start variables declaration
    lateinit var editTextEmail:EditText
    lateinit var editTextPassword:EditText
    lateinit var buttonLogin:Button
    lateinit var buttonNewUser:Button
    lateinit var checkBoxRememberMe:CheckBox
    lateinit var auth:FirebaseAuth
    //end variables declaration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //variable initialization
        editTextEmail=binding.maEtUser
        editTextPassword=binding.maEtPass
        buttonLogin=binding.maBtnLogin
        buttonNewUser=binding.maBtnNewUser
        checkBoxRememberMe=binding.maCbRememberMe
        auth= FirebaseAuth.getInstance()
        val masterKeyAlias: String = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        val sharedPref = EncryptedSharedPreferences.create(
            "secret_shared_prefs",//filename
            masterKeyAlias,
            this,//context
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
        editTextEmail.setText(sharedPref.getString(Constants.LOGIN_KEY, ""))
        editTextPassword.setText(sharedPref.getString(Constants.PASSWORD_KEY, ""))
        if (sharedPref.getString(Constants.LOGIN_KEY, "")!=null && sharedPref.getString(Constants.LOGIN_KEY, "")!=""
            && sharedPref.getString(Constants.PASSWORD_KEY, "")!=null && sharedPref.getString(Constants.PASSWORD_KEY, "")!=""){
            checkBoxRememberMe.isChecked=true
        }
        //end variables initialization
        //buttons handlers
        buttonLogin.setOnClickListener {
            if(checkBoxRememberMe.isChecked){
                val editor = sharedPref.edit()
                editor.putString(LOGIN_KEY, editTextEmail.text.toString())
                editor.putString(PASSWORD_KEY, editTextPassword.text.toString())
                editor.commit()
            }
            else{
                val editor = sharedPref.edit()
                editor.putString(LOGIN_KEY, "")
                editor.putString(PASSWORD_KEY, "")
                editor.commit()
            }
            val mail = editTextEmail.text.toString()
            val pass = editTextPassword.text.toString()
            if(!requiredDataValidation())
                return@setOnClickListener
            AutenticarUsuario(mail,pass)
        }

        buttonNewUser.setOnClickListener {
            var intent=Intent(this,RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
    //function override
    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if(currentUser != null)
        {
            var intent = Intent(this,ConectionList::class.java)
            intent.putExtra(EXTRA_LOGIN,auth.currentUser!!.email)
            startActivity(intent)
        }

    }
    //utils function
    private fun requiredDataValidation():Boolean{
        val mail=editTextEmail.text.toString()
        val pass=editTextPassword.text.toString()
        if(mail.isEmpty()){
            editTextEmail.setError(getString(R.string.ma_mail_required))
            editTextEmail.requestFocus()
            return false
        }
        if(!isValidMail(mail)){
            editTextPassword.setError(getString(R.string.ma_mail_invalid))
            editTextPassword.requestFocus()
            return false
        }
        if (pass.isEmpty()) {
            editTextPassword.setError(getString(R.string.ma_password_required))
            editTextPassword.requestFocus()
            return false
        }
        if (pass.length < Constants.PASS_LENGTH) {
            editTextPassword.setError(getString(R.string.ma_password_invalid,Constants.PASS_LENGTH))
            editTextPassword.requestFocus()
            return false
        }
        return true;
    }
    private fun isValidMail(string:String):Boolean{
        return Constants.EMAIL_ADDRESS_PATTERN.matcher(string).matches()
    }
    //autenticacion con firebase
    fun AutenticarUsuario(email:String, password:String){
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    var intent = Intent(this,ConectionList::class.java)
                    intent.putExtra(LOGIN_KEY,auth.currentUser!!.email)
                    intent.putExtra(EXTRA_LOGIN, auth.currentUser!!.email)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(baseContext, task.exception!!.message,
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}