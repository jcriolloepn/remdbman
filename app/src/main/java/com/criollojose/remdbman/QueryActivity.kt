package com.criollojose.remdbman

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.criollojose.remdbman.beans.ConectionItem
import com.criollojose.remdbman.beans.MysqlConfig
import com.criollojose.remdbman.beans.MysqlConnectionMap
import com.criollojose.remdbman.databinding.ActivityQueryBinding
import com.criollojose.remdbman.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import org.json.JSONException
import org.json.JSONObject


class QueryActivity : AppCompatActivity() {
    lateinit var editTextQuery:EditText
    lateinit var textViewResult: TextView
    lateinit var buttonExecute:Button
    lateinit var buttonDelete:Button
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityQueryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        //start: initializacion
        editTextQuery=binding.qaQuery
        textViewResult=binding.qaEtResult
        buttonExecute=binding.qaBtnExecuteQuery
        buttonDelete=binding.qaBtnDeleteQuery
        auth= FirebaseAuth.getInstance()

        val extras = intent.extras ?: return
        val cnxName = extras.getString(Constants.EXTRA_KEY_MYSQL_CNX_NAME) ?:"Unknown"
        val currentUser=auth.currentUser
        var systemUserName=getString(R.string.nck_unknow_user)
        if(currentUser != null)
        {
            systemUserName=auth.currentUser!!.email.toString()
        }
        buttonExecute.setOnClickListener {
            executeQuery(systemUserName, cnxName)
        }

        //end: initializacion

        //val imageQuery=binding.imageQuery

        /*binding.deleteQuery.setOnClickListener {
            query.setText("",TextView.BufferType.EDITABLE)
            imageQuery.visibility=View.INVISIBLE
        }
       binding.executeQuery.setOnClickListener {
           imageQuery.visibility=View.VISIBLE
       }*/
    }
    //start:util functions
    private fun executeQuery(sistemUserName: String, cnxName: String){
        val db= Firebase.firestore
        db.collection("usersRemDBMan")
                .document(sistemUserName)
                .get()
                .addOnSuccessListener { result->
                    if(result!=null ) {
                        val cnxsMap = result.toObject<MysqlConnectionMap>()
                        if (cnxsMap != null && cnxsMap.conections.size > 0) {
                            val mysqlConfig:MysqlConfig=cnxsMap.conections.getValue(cnxName)
                            //start query post to database object
                            getDataFromApi(mysqlConfig)
                            //end query post to database object
                        } else {
                            Log.d("resultado1", "no hay converion ${cnxsMap}")
                        }
                    }else{
                        Log.d("resultado1", "el resultado es null ")
                    }
                }
                .addOnFailureListener{ e -> Log.d("ERROR_VER_EXISTENCIA", "no se guardo: ${e}")
                }

    }

    private fun getDataFromApi(mysqlConfig: MysqlConfig){
        val postUrl=Constants.URL_MYSQL_DBCLIENT+"/executeQuery"
        val requestQueue = Volley.newRequestQueue(this)
        val query=editTextQuery.text.toString()
        val queryObject= JSONObject()
        //jsonobj.put("query","Select * from personas")
        queryObject.put("host", mysqlConfig.host)
        queryObject.put("user", mysqlConfig.user)
        queryObject.put("password", mysqlConfig.password)
        queryObject.put("database", mysqlConfig.database)
        queryObject.put("port", mysqlConfig.port)
        queryObject.put("supportBigNumbers", mysqlConfig.supportBigNumbers)
        queryObject.put("bigNumberStrings", mysqlConfig.bigNumberStrings)
        queryObject.put("query", query)

        val request= JsonObjectRequest(Request.Method.POST, postUrl, queryObject,
                { response ->
                    if(response.optString("response")=="error"){
                        editTextQuery.setError(getString(R.string.aqk_check_query))
                        editTextQuery.requestFocus()
                    }else{
                        if(response.optJSONArray("response").length()>0){
                            val modelObjectInResponse: JSONObject = JSONObject(response.optJSONArray("response")[0].toString())
                            var resultToString="";
                            var listKeys=mutableListOf<String>();
                            val iter: Iterator<String> = modelObjectInResponse.keys()
                            while (iter.hasNext()) {
                                val key = iter.next()
                                resultToString+=key+"  |  "
                                listKeys.add(key)
                            }
                            resultToString+="\n"
                            var responseObjectsArray=response.optJSONArray("response")
                            for (i in 0 until responseObjectsArray.length()){
                                var objectJsonToRowString="";
                                for(j in listKeys.indices){
                                    objectJsonToRowString+=JSONObject(responseObjectsArray[i].toString()).getString(listKeys[j]).toString()+" |  "
                                    Log.d("resul",JSONObject(responseObjectsArray[i].toString()).getString(listKeys[j]))
                                }
                                resultToString+=objectJsonToRowString+"\n"
                            }
                            Log.d("resul","current object"+resultToString)
                            textViewResult.text=resultToString

                        }else{
                            textViewResult.text=getString(R.string.aqk_no_results)
                        }
                    }

                }, { error: VolleyError ->
            Log.d("resultado1", "Error $error.message")
        }
        )
        requestQueue.add(request)
    }
    //end: util functions
}