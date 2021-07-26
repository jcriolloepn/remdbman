package com.criollojose.remdbman

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.criollojose.remdbman.adpaters.CnxItemAdapter
import com.criollojose.remdbman.beans.ConectionItem
import com.criollojose.remdbman.databinding.ActivityConectionListBinding

class ConectionList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityConectionListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.cnxListRecycler.layoutManager=LinearLayoutManager(this)
        val cnxList= mutableListOf<ConectionItem>();
        cnxList.add(ConectionItem("1","CNX_1","192.168.0.1","admin"))
        cnxList.add(ConectionItem("2","CNX_2","192.168.0.1","admin"))
        cnxList.add(ConectionItem("3","CNX_3","192.168.0.1","admin"))
        cnxList.add(ConectionItem("4","CNX_4","192.168.0.1","admin"))
        cnxList.add(ConectionItem("5","CNX_5","192.168.0.1","admin"))
        cnxList.add(ConectionItem("6","CNX_6","192.168.0.1","admin"))
        cnxList.add(ConectionItem("7","CNX_7","192.168.0.1","admin"))
        cnxList.add(ConectionItem("8","CNX_8","192.168.0.1","admin"))

        val adapter=CnxItemAdapter()
        binding.cnxListRecycler.adapter=adapter
        adapter.submitList(cnxList)

        adapter.onItemNameListener={
            //Toast.makeText(this,"Se abre el dialogo para ejecutar queries para la conexión de nombre: "+it.name, Toast.LENGTH_LONG).show()
            val goToQueryActivity=Intent(this,QueryActivity::class.java)
            startActivity(goToQueryActivity)
        }
        adapter.onDeleteListener={
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Confirmación de Eliminación")
            dialogBuilder.setMessage("¿Esta seguro que desea eliminar la conexión: "+it.name+" ?")
            dialogBuilder.setPositiveButton("Delete", DialogInterface.OnClickListener { _, _ ->
                Toast.makeText(this,"Conexión eliminada",Toast.LENGTH_LONG).show()
            })
            dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                //pass
            })
            dialogBuilder.create().show()
        }
        adapter.onUpdateListener={
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setTitle("Confirmación de Actualizacion")
            dialogBuilder.setMessage("¿Esta seguro que desea actualizar la conexión: "+it.name+" ?")
            dialogBuilder.setPositiveButton("Actualizar", DialogInterface.OnClickListener { _, _ ->
                Toast.makeText(this,"Se abre el diálogo para actualizar la conexion",Toast.LENGTH_LONG).show()
            })
            dialogBuilder.setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                //pass
            })
            dialogBuilder.create().show()
        }

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
            R.id.usetoken->{
                val goToSetLock= Intent(this,SetLock::class.java)
                startActivity(goToSetLock)
                true
            }
            R.id.logout->{
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}