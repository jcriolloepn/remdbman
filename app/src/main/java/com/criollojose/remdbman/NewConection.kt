package com.criollojose.remdbman

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonArrayRequest
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.criollojose.remdbman.beans.MysqlConfig
import com.criollojose.remdbman.beans.MysqlConnectionMap
import com.criollojose.remdbman.databinding.ActivityNewConectionBinding
import com.criollojose.remdbman.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.getField
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class NewConection : AppCompatActivity() {
    lateinit var editTextCnxName:EditText
    lateinit var edittextHost:EditText
    lateinit var editTextPort:EditText
    lateinit var editTextDBName:EditText
    lateinit var editTextSchemaName: EditText
    lateinit var editTextDBUser: EditText
    lateinit var editTextDBPassword:EditText
//    lateinit var editTextSSHServer:EditText
//    lateinit var editTextSSHPort: EditText
//    lateinit var editTextSSHUser:EditText
//    lateinit var editTextSSHPassword: EditText
//    lateinit var spinerVendor:Spinner
    lateinit var useSSHConfirmation:Switch
    lateinit var buttonTestConection:Button
    lateinit var buttonSaveConnection:Button
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityNewConectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*//start:spiner and switch functions
        spinerVendor=binding.vendor
        useSSHConfirmation=binding.useSshSw
        useSSHConfirmation.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.sshParameters.visibility = View.VISIBLE
                } else {
                    binding.sshParameters.visibility = View.INVISIBLE
                }
            }
        }
        ArrayAdapter.createFromResource(
                this,
                R.array.vendor_options,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinerVendor.adapter = adapter
        }
        //end:spiner and switch functions*/

        //start:variable inicialization
        editTextCnxName=binding.ncEtCnxName
        edittextHost=binding.ncEtHost
        editTextPort=binding.ncEtPort
        editTextDBName=binding.ncEtDatabaseName
        editTextSchemaName=binding.ncEtSchemaName
        editTextDBUser=binding.ncEtDbUserName
        editTextDBPassword=binding.ncEtDbPassword
        //ssh
        /*
        editTextSSHServer=binding.ncEtSshServer
        editTextSSHPort=binding.ncEtSshPort
        editTextSSHUser=binding.ncEtSshUser
        editTextSSHPassword=binding.ncEtSshPassword
        */
        buttonSaveConnection=binding.ncBtnSaveCnx
        buttonTestConection=binding.ncBtnTestCnx
        auth = FirebaseAuth.getInstance();
        //end: variable inicialization

        buttonTestConection.setOnClickListener {
            //volleyPost(MysqlConfig("localhost","root","root","db_pruebas"));//develop pourpose only
            if(!requiredDataValidation())
                return@setOnClickListener
         testConnection(MysqlConfig(edittextHost.text.toString(),
            editTextDBUser.text.toString(),
            editTextDBPassword.text.toString(),
            editTextDBName.text.toString(),
            editTextPort.text.toString()))
        }

        buttonSaveConnection.setOnClickListener {
            if(!requiredDataValidation())
                return@setOnClickListener
            val currentUser=auth.currentUser
            val cnxName=editTextCnxName.text.toString()
            var systemUserName=getString(R.string.nck_unknow_user)
            if(currentUser != null)
            {
                systemUserName=auth.currentUser!!.email.toString()
            }
            procNewConnection(systemUserName,cnxName,MysqlConfig(edittextHost.text.toString(),
                    editTextDBUser.text.toString(),
                    editTextDBPassword.text.toString(),
                    editTextDBName.text.toString(),
                    editTextPort.text.toString()))
        }
    }
    private fun procNewConnection (sistemUserName:String,cnxName:String,newConnection:MysqlConfig){
            val postUrl=Constants.URL_MYSQL_DBCLIENT+"/getConnection"
            val requestQueue =Volley.newRequestQueue(this)
            val configObject=JSONObject()
            //jsonobj.put("query","Select * from personas")
            configObject.put("host",newConnection.host)
            configObject.put("user",newConnection.user)
            configObject.put("password",newConnection.password)
            configObject.put("database",newConnection.database)
            configObject.put("port",newConnection.port)
            configObject.put("supportBigNumbers",newConnection.supportBigNumbers)
            configObject.put("bigNumberStrings",newConnection.bigNumberStrings)

            val request=JsonObjectRequest(Request.Method.POST, postUrl, configObject,
                    {
                        response ->
                        val responseObject=JSONObject(response.toString())
                        if(responseObject.optString("response")=="ok"){
                            procNewConnectionFirebase(sistemUserName,cnxName,newConnection)
                        }else{
                            val builder=AlertDialog.Builder(this)
                            builder.setTitle(getString(R.string.nck_alert_title))
                            builder.setMessage(getString(R.string.nck_fail_conection))
                            builder.show()
                        }

                    }, { error: VolleyError ->
                Log.d("API","Error $error.message")
            }
            )
            requestQueue.add(request)
    }
    private fun procNewConnectionFirebase(sistemUserName:String,cnxName:String,newConnection:MysqlConfig){
        val db=Firebase.firestore
        db.collection("usersRemDBMan")
            .document(sistemUserName)
            .get()
            .addOnSuccessListener { result->
                if(result!=null ){
                    val cnxsMap=result.toObject<MysqlConnectionMap>()
                    if (cnxsMap!=null){
                        if(cnxsMap.conections.containsKey(cnxName)){
                            editTextCnxName.setError(getString(R.string.ncK_error_cnx_already_exists))
                            editTextCnxName.requestFocus()
                        }else{
                            updtaeConnectionArray(sistemUserName,cnxName,newConnection)
                        }
                    }else{
                        Log.d("resultado1","no hay converion ${cnxsMap}")
                        startConnectionArray(sistemUserName,cnxName,newConnection)
                    }
                }else{
                    Log.d("resultado1","no hay algo que mostrar ${result}")
                }
            }
            .addOnFailureListener{
                    e -> Log.d("ERROR_VER_EXISTENCIA","no se guardo: ${e}")
            }
    }

    private fun startConnectionArray(sistemUserName:String,cnxName:String,newConnection:MysqlConfig){
        val db=Firebase.firestore
        val cnx= MysqlConnectionMap(mapOf(cnxName to newConnection))

        db.collection("usersRemDBMan")
            .document(sistemUserName)
            .set(cnx)
            .addOnSuccessListener {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle(getString(R.string.nck_alert_save_connection_title))
                dialogBuilder.setMessage(getString(R.string.nck_succesfull_cnx_saved))
                dialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    val goToConnectionListActivity=Intent(this,ConectionList::class.java)
                    startActivity(goToConnectionListActivity)
                })
                dialogBuilder.create().show()
                //Toast.makeText(applicationContext,getString(R.string.nck_succesfull_cnx_saved),Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e -> Toast.makeText(applicationContext,"no se guardo: ${e}",Toast.LENGTH_LONG).show() }

    }
    private fun updtaeConnectionArray(sistemUserName:String,cnxName:String,newConnection:MysqlConfig) {
        val db=Firebase.firestore
        val cnx= MysqlConnectionMap(mapOf(cnxName to newConnection))
        db.collection("usersRemDBMan")
            .document(sistemUserName)
            .set(cnx, SetOptions.merge())
            .addOnSuccessListener {
                val dialogBuilder = AlertDialog.Builder(this)
                dialogBuilder.setTitle(getString(R.string.nck_alert_save_connection_title))
                dialogBuilder.setMessage(getString(R.string.nck_succesfull_cnx_saved))
                dialogBuilder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    val goToConnectionListActivity=Intent(this,ConectionList::class.java)
                    startActivity(goToConnectionListActivity)
                })
                dialogBuilder.create().show()
                //Toast.makeText(applicationContext,getString(R.string.nck_succesfull_cnx_saved),Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener { e -> Toast.makeText(applicationContext,"no se guardo: ${e}",Toast.LENGTH_LONG).show() }

    }

    private fun testConnection(mysqlConfig: MysqlConfig){
        val postUrl=Constants.URL_MYSQL_DBCLIENT+"/getConnection"
        val requestQueue =Volley.newRequestQueue(this)
        val configObject=JSONObject()
        //jsonobj.put("query","Select * from personas")
        configObject.put("host",mysqlConfig.host)
        configObject.put("user",mysqlConfig.user)
        configObject.put("password",mysqlConfig.password)
        configObject.put("database",mysqlConfig.database)
        configObject.put("port",mysqlConfig.port)
        configObject.put("supportBigNumbers",mysqlConfig.supportBigNumbers)
        configObject.put("bigNumberStrings",mysqlConfig.bigNumberStrings)

        val request=JsonObjectRequest(Request.Method.POST, postUrl, configObject,
                {
                    response ->
                    val responseObject=JSONObject(response.toString())
                    val builder=AlertDialog.Builder(this)
                    builder.setTitle(getString(R.string.nck_alert_title))
                    if(responseObject.optString("response")=="ok"){
                        builder.setMessage(getString(R.string.nck_successfull_conection))
                    }else{
                        builder.setMessage(getString(R.string.nck_fail_conection))
                    }
                    builder.setPositiveButton("OK", DialogInterface.OnClickListener { _, _ ->
                    })
                    builder.show()
                }, { error: VolleyError ->
            Log.d("API","Error $error.message")
        }
        )
        requestQueue.add(request)
    }
    //validation data functions
    private fun requiredDataValidation():Boolean{
        val cnxName=editTextCnxName.text.toString()
        val host=edittextHost.text.toString()
        val port=editTextPort.text.toString()
        val dataBase=editTextDBName.text.toString()
        val dbUser=editTextDBName.text.toString()
        val dbPass=editTextDBPassword.text.toString()
        if(cnxName.isEmpty()){
            editTextCnxName.setError(getString(R.string.nck_field_is_required))
            editTextCnxName.requestFocus()
            return false
        }
        if(host.isEmpty()){
            edittextHost.setError(getString(R.string.nck_field_is_required))
            edittextHost.requestFocus()
            return false
        }
        if(port.isEmpty()){
            editTextPort.setError(getString(R.string.nck_field_is_required))
            editTextPort.requestFocus()
            return false
        }
        if(dbUser.isEmpty()){
            editTextDBName.setError(getString(R.string.nck_field_is_required))
            editTextDBName.requestFocus()
            return false
        }
        if(dbPass.isEmpty()){
            editTextDBPassword.setError(getString(R.string.nck_field_is_required))
            editTextDBPassword.requestFocus()
            return false
        }
        if(dataBase.isEmpty()){
            editTextDBName.setError(getString(R.string.nck_field_is_required))
            editTextDBName.requestFocus()
            return false
        }
        return true
    }

    //start functions with develop pourpose only
    /*private fun getDataFRomFirebase(){
       val db=Firebase.firestore
       db.collection("usersRemDBMan")
           .document("user1")
           .get()
           .addOnSuccessListener { result->

                   val user1=result.toObject<MysqlConnectionMap>()
               if (user1 != null) {
                   Log.d("resultado1",user1.conections.containsKey("cnx1").toString())
                   Log.d("resultado1",user1.conections.containsKey("cn1").toString())
                   for((key,value) in user1.conections){
                       Log.d("resultado1","key:${key}, value: $value")

                   }


               }
           }
           .addOnFailureListener { e -> Toast.makeText(applicationContext,"no se guardo: ${e}",Toast.LENGTH_LONG).show() }
   }
   private fun saveConection(){
       val db=Firebase.firestore
       val usuarioMap1: Map<String, MysqlConfig> = mapOf(
           "cnx1" to MysqlConfig("localhost","root","root","db_pruebas"),
           "cnx2" to MysqlConfig("localhost","root","root","db_pruebas"),
           "cnx3" to MysqlConfig("localhost","root","root","db_pruebas"),
       )
       val usuarioMap2: Map<String, MysqlConfig> = mapOf(
           "cnx1" to MysqlConfig("localhost","root","root","db_pruebas"),
           "cnx2" to MysqlConfig("localhost","root","root","db_pruebas"),
           "cnx3" to MysqlConfig("localhost","root","root","db_pruebas"),
       )
       val usuario1:MysqlConnectionMap= MysqlConnectionMap(usuarioMap1)
       db.collection("usersRemDBMan").document("user1").set(usuario1)
           .addOnSuccessListener { Toast.makeText(applicationContext,"se guardo",Toast.LENGTH_LONG).show()}
           .addOnFailureListener { e -> Toast.makeText(applicationContext,"no se guardo: ${e}",Toast.LENGTH_LONG).show() }

       val usuario2:MysqlConnectionMap= MysqlConnectionMap(usuarioMap2)
       db.collection("usersRemDBMan").document("user2").set(usuario1)
           .addOnSuccessListener { Toast.makeText(applicationContext,"se guardo",Toast.LENGTH_LONG).show()}
           .addOnFailureListener { e -> Toast.makeText(applicationContext,"no se guardo: ${e}",Toast.LENGTH_LONG).show() }
   } only develop pourpose only*/
}