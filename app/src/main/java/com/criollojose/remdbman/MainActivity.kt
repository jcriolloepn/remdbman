package com.criollojose.remdbman

import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.criollojose.remdbman.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var typeFace=Typeface.createFromAsset(assets, "fonts/SourceCodePro-Bold.ttf")

        val binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val unlockButton=binding.unlockButton
        unlockButton.setOnClickListener {
            val goToListItem=Intent(this,ConectionList::class.java)
            startActivity(goToListItem)
        }
        unlockButton.typeface =typeFace
    }
}