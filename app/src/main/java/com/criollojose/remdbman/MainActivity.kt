package com.criollojose.remdbman

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.criollojose.remdbman.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val unlockButton=binding.unlockButton
        unlockButton.setOnClickListener {
            val goToListItem=Intent(this,ConectionList::class.java)
            startActivity(goToListItem)
        }
    }
}