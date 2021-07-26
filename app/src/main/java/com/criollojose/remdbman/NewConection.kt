package com.criollojose.remdbman

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Spinner
import com.criollojose.remdbman.databinding.ActivityNewConectionBinding

class NewConection : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding=ActivityNewConectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //val spinner: Spinner = findViewById(R.id.vendor)
        val spinner:Spinner=binding.vendor
        val useSSHConfirmation=binding.useSshSw
        useSSHConfirmation.setOnCheckedChangeListener { _, isChecked ->
            run {
                if (isChecked) {
                    binding.sshParameters.visibility = View.VISIBLE
                } else {
                    binding.sshParameters.visibility = View.INVISIBLE
                }
            }

        }
// Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
                this,
                R.array.vendor_options,
                android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }
    }
}