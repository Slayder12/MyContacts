package com.example.mycontacts

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mycontacts.databinding.ActivityMassageBinding

class MassageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMassageBinding
    private var phoneNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_massage)
        binding = ActivityMassageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = ""
        setSupportActionBar(binding.toolbar)

        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }

        phoneNumber = intent.getStringExtra("phoneNumber")
        binding.personPhoneNumberTV.text = phoneNumber

        binding.sendMassageBTN.setOnClickListener{
            if (binding.inputMassageET.text.isEmpty()) {
                Toast.makeText(applicationContext,
                    "Введите сообщение",
                    Toast.LENGTH_LONG)
                    .show()
                return@setOnClickListener
            }
            val massage =  binding.inputMassageET.text.toString()
            sendMassage(phoneNumber!!, massage)
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun sendMassage(phoneNumber: String, message: String? ){
        try {
            val smsManager: SmsManager
            if (Build.VERSION.SDK_INT>=23) {
                smsManager = this.getSystemService(SmsManager::class.java)
            } else{
                smsManager = SmsManager.getDefault()
            }
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
            Toast.makeText(applicationContext,
                "Сообщение отправлено",
                Toast.LENGTH_LONG)
                .show()
        } catch (e: Exception) {
            Toast.makeText(applicationContext,
                "Подалуйста введите все данные"+e.message.toString(),
                Toast.LENGTH_LONG)
                .show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}