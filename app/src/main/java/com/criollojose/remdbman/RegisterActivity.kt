package com.criollojose.remdbman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import com.criollojose.remdbman.databinding.ActivityRegisterBinding
import com.criollojose.remdbman.utils.Constants
import com.google.firebase.auth.FirebaseAuth

class RegisterActivity : AppCompatActivity() {
    //start variables declaration
    lateinit var editTextEmail: EditText
    lateinit var editTextPassword: EditText
    lateinit var editTextRetypePass:EditText
    lateinit var buttonSignUp: Button
    lateinit var buttonBack: Button
    lateinit var auth: FirebaseAuth
    //end variables declaration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //variable initialization start
        editTextEmail=binding.raEtUser
        editTextPassword=binding.raEtPass
        editTextRetypePass=binding.raEtRetypePass
        buttonBack=binding.raBtnBack
        buttonSignUp=binding.raBtnSignUp
        auth= FirebaseAuth.getInstance()
        //variable initialization end
        buttonSignUp.setOnClickListener {
            val mail=editTextEmail.text.toString()
            val pass=editTextPassword.text.toString()
            if(!requiredDataValidation())
                return@setOnClickListener
            SignUpNewUser(mail,pass)
        }
        buttonBack.setOnClickListener {
            val goToMainActivity=Intent(this,MainActivity::class.java)
            startActivity(goToMainActivity)
            finish()
        }
    }

    //utils functions start
    private fun requiredDataValidation():Boolean{
        val mail=editTextEmail.text.toString()
        val pass=editTextPassword.text.toString()
        val retipedPass=editTextRetypePass.text.toString()
        if(mail.isEmpty()){
            editTextEmail.setError(getString(R.string.ra_mail_required))
            editTextEmail.requestFocus()
            return false
        }
        //es una email correcto
        if(!isValidMail(mail)){
            editTextEmail.setError(getString(R.string.ra_mail_invalid))
            editTextEmail.requestFocus()
            return false
        }
        if (pass.isEmpty()) {
            editTextPassword.setError(getString(R.string.ra_password_required))
            editTextPassword.requestFocus()
            return false
        }
        if (pass.length < Constants.PASS_LENGTH) {
            editTextPassword.setError(getString(R.string.ra_password_invalid, Constants.PASS_LENGTH))
            editTextPassword.requestFocus()
            return false
        }
        if (retipedPass.isEmpty()) {
            editTextRetypePass.setError(getString(R.string.ra_retype_password_required))
            editTextRetypePass.requestFocus()
            return false
        }
        if (retipedPass.length < Constants.PASS_LENGTH) {
            editTextRetypePass.setError(getString(R.string.ra_retype_password_invalid, Constants.PASS_LENGTH))
            editTextRetypePass.requestFocus()
            return false
        }
        if(pass!=retipedPass){
            editTextRetypePass.setError(getString(R.string.ra_passwords_not_match))
            editTextRetypePass.requestFocus()
            return false
        }
        return true;
    }
    private fun isValidMail(string:String):Boolean{
        return Constants.EMAIL_ADDRESS_PATTERN.matcher(string).matches()
    }
    fun SignUpNewUser(email:String, password:String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    Toast.makeText(baseContext, R.string.ra_registred_user_succ,
                        Toast.LENGTH_SHORT).show()
                    var intent = Intent(this,MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        baseContext, task.exception!!.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

    //utils function end


}