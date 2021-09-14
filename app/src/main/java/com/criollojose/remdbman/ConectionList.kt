package com.criollojose.remdbman

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.criollojose.remdbman.adpaters.CnxItemAdapter
import com.criollojose.remdbman.beans.ConectionItem
import com.criollojose.remdbman.beans.MysqlConfig
import com.criollojose.remdbman.beans.MysqlConnectionMap
import com.criollojose.remdbman.databinding.ActivityConectionListBinding
import com.criollojose.remdbman.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase

class ConectionList : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var conectionRecylerView:RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityConectionListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        conectionRecylerView=binding.cnxListRecycler;
        auth = FirebaseAuth.getInstance();

        val currentUser=auth.currentUser
        var systemUserName=getString(R.string.nck_unknow_user)
        if(currentUser != null)
        {
            systemUserName=auth.currentUser!!.email.toString()
        }
        populateRecyclerView(systemUserName)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_conection_list,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId){
            R.id.new_conection->{
                val goToNewConection= Intent(this,NewConection::class.java)
                startActivity(goToNewConection)
                true
            }
            R.id.logout->{
                FirebaseAuth.getInstance().signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
   
    //funcion para poblar las conexiones existentes dentro de firebase para cada conexion
    private fun populateRecyclerView(systemUserName:String){
        val db=Firebase.firestore
        db.collection("usersRemDBMan")
                .document(systemUserName)
                .get()
                .addOnSuccessListener { result->
                    if(result!=null ){
                        val cnxsMap=result.toObject<MysqlConnectionMap>()
                        conectionRecylerView.layoutManager=LinearLayoutManager(this)
                        val cnxList= mutableListOf<ConectionItem>();
                        if (cnxsMap!=null && cnxsMap.conections.size>0){
                            for((key,value) in cnxsMap.conections){
                                cnxList.add(mysqlConfigToCnxItem(key,value))
                            }
                        }else{
                            Log.d("resultado1","no hay converion ${cnxsMap}")
                        }
                        val adapter=CnxItemAdapter()
                        conectionRecylerView.adapter=adapter
                        adapter.submitList(cnxList)
                        adapter.onItemNameListener={
                            //Toast.makeText(this,"Se abre el dialogo para ejecutar queries para la conexión de nombre: "+it.name, Toast.LENGTH_LONG).show()
                            val goToQueryActivity=Intent(this,QueryActivity::class.java)
                            goToQueryActivity.putExtra(Constants.EXTRA_KEY_MYSQL_CNX_NAME,"${it.name}")
                            startActivity(goToQueryActivity)
                        }
                        adapter.onDeleteListener={ cnxItem->
                            deleteItemList(systemUserName,cnxItem)
                        }
                        adapter.onUpdateListener={cnxItem->
                            updateItemList(cnxItem)
                        }

                    }else{
                        Log.d("resultado1","no hay algo que mostrar ${result}")
                    }
                }
                .addOnFailureListener{
                    e -> Log.d("ERROR_VER_EXISTENCIA","no se guardo: ${e}")
                }
    }
    private fun mysqlConfigToCnxItem(cnxName:String,mysqlConfig: MysqlConfig):ConectionItem{
        return ConectionItem(cnxName,cnxName,mysqlConfig.host,mysqlConfig.user)
    }


    //funcion de editar una conexion existente
    private fun deleteItemList(systemUserName:String,cnxItem:ConectionItem){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle(getString(R.string.clk_delete_alert_title))
        dialogBuilder.setMessage(getString(R.string.clk_delete_confirmation_msg)+cnxItem.name+" ?")
        dialogBuilder.setPositiveButton(getString(R.string.clk_alert_button_text), DialogInterface.OnClickListener { _, _ ->
            //Toast.makeText(this,"Conexión eliminada",Toast.LENGTH_LONG).show()
            val db=Firebase.firestore
            db.collection("usersRemDBMan")
                    .document(systemUserName)
                    .update(mapOf("conections.${cnxItem.name}" to FieldValue.delete()))
                    .addOnSuccessListener{
                        Log.d("resultado1","se elimino")
                        finish();
                        startActivity(getIntent());
                    }
                    .addOnFailureListener{
                        e -> Log.d("ERROR_VER_EXISTENCIA","no se guardo: ${e}")
                    }
        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            //pass
        })
        dialogBuilder.create().show()
    }

    private fun updateItemList(cnxItem:ConectionItem){
        val dialogBuilder = AlertDialog.Builder(this)
        dialogBuilder.setTitle("Confirmación de Actualizacion")
        dialogBuilder.setMessage("¿Esta seguro que desea actualizar la conexión: "+cnxItem.name+" ?")
        dialogBuilder.setPositiveButton("Actualizar", DialogInterface.OnClickListener { _, _ ->
            Toast.makeText(this,"Se abre el diálogo para actualizar la conexion",Toast.LENGTH_LONG).show()
        })
        dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
            //pass
        })
        dialogBuilder.create().show()
    }


}