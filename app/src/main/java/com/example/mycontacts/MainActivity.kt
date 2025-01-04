package com.example.mycontacts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycontacts.databinding.ActivityMainBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var customAdapter: CustomAdapter? = null
    private var contactModelsList: MutableList<ContactModel>? = null

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.recyclerViewRV.layoutManager = LinearLayoutManager(this)

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            permissionContact.launch(Manifest.permission.READ_CONTACTS)
            customAdapter?.notifyDataSetChanged()
        } else {
            getContact()
        }

    }
    @SuppressLint("Recycle", "Range")
    private fun getContact(){
        contactModelsList = ArrayList()
        val phones = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        while (phones!!.moveToNext()) {
            val name =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
            val phoneNumber =
                phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))

            val contactModel = ContactModel(name, phoneNumber)
            contactModelsList?.add(contactModel)
        }
        phones.close()
        customAdapter = CustomAdapter(contactModelsList!!)
        binding.recyclerViewRV.adapter = customAdapter
        binding.recyclerViewRV.setHasFixedSize(true)

        customAdapter?.setOnItemClickListener(object :
            CustomAdapter.OnItemClickListener{
            override fun onCallClick(item: ContactModel, position: Int) {
                val person = (contactModelsList as ArrayList<ContactModel>)[position]
                val number = person.phone
                if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.CALL_PHONE) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    permissionOfCall.launch(Manifest.permission.CALL_PHONE)
                } else{
                    callTheNumber(number)
                }
            }

            override fun onMassageClick(item: ContactModel, position: Int) {
                val person = (contactModelsList as ArrayList<ContactModel>)[position]
                val phoneNumber = person.phone
                if (ActivityCompat.checkSelfPermission(this@MainActivity, Manifest.permission.SEND_SMS) !=
                    PackageManager.PERMISSION_GRANTED
                ) {
                    permissionOfMassage.launch(Manifest.permission.SEND_SMS)
                } else {
                    val intent = Intent(this@MainActivity, MassageActivity::class.java)
                    intent.putExtra("phoneNumber", phoneNumber)
                    startActivity(intent)
                }
            }
        }
        )
    }

    private fun callTheNumber(number: String?) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }

    private val permissionContact = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted){
            Toast.makeText(this@MainActivity,
                "Получен доступ к контактам",
                Toast.LENGTH_SHORT)
                .show()
            getContact()
        } else {
            Toast.makeText(this@MainActivity,
                "В разрешении отказано",
                Toast.LENGTH_SHORT)
                .show()
        }
    }

    private  val permissionOfCall = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this@MainActivity,
                "Получен доступ к звонкам",
                Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this@MainActivity,
                "В разрешении отказано",
                Toast.LENGTH_SHORT)
                .show()
        }
    }

    private  val permissionOfMassage = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(this@MainActivity,
                "Получен доступ к сообщениям",
                Toast.LENGTH_SHORT)
                .show()
        } else {
            Toast.makeText(this@MainActivity,
                "В разрешении отказано",
                Toast.LENGTH_SHORT)
                .show()
        }
    }
}