package com.criollojose.remdbman

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import com.criollojose.remdbman.databinding.ActivityQueryBinding

class QueryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityQueryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val query=binding.query
        val imageQuery=binding.imageQuery

        binding.deleteQuery.setOnClickListener {
            query.setText("",TextView.BufferType.EDITABLE)
            imageQuery.visibility=View.INVISIBLE
        }
       binding.executeQuery.setOnClickListener {
           imageQuery.visibility=View.VISIBLE
       }
    }
}