package com.example.mycontacts

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycontacts.databinding.ActivityMainBinding
import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentProviderOperation
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.ContactsContract
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.provider.ContactsContract.CommonDataKinds.StructuredName
import android.provider.ContactsContract.RawContacts
import android.util.Log
import android.widget.Toast
import com.example.mycontacts.adapter.CustomAdapter
import com.example.mycontacts.models.ContactModel
import com.example.mycontacts.pages.MassageActivity
import com.example.mycontacts.pages.SearchActivity
import com.example.mycontacts.utils.ContactUtils
import com.example.mycontacts.utils.PermissionManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var customAdapter: CustomAdapter? = null
    private val contactModelsList = mutableListOf<ContactModel>()
    private lateinit var permissionManager: PermissionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        permissionManager = PermissionManager(this)

        setupRecyclerView()
        setupButtons()
        checkAndFetchContacts()
    }

    private fun setupRecyclerView() {
        binding.recyclerViewRV.layoutManager = LinearLayoutManager(this)
        customAdapter = CustomAdapter(contactModelsList)
        binding.recyclerViewRV.adapter = customAdapter

        customAdapter?.setOnItemClickListener(object : CustomAdapter.OnItemClickListener {
            override fun onCallClick(item: ContactModel, position: Int) {
                val person = (contactModelsList as ArrayList<ContactModel>)[position]
                val number = person.phone
                permissionManager.requestPermission(Manifest.permission.CALL_PHONE) { isGranted ->
                    if (isGranted) {
                        callTheNumber(number)
                    } else {
                        Toast.makeText(this@MainActivity, "Доступ к звонкам отклонен", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            override fun onMassageClick(item: ContactModel, position: Int) {
                val person = (contactModelsList as ArrayList<ContactModel>)[position]
                val phoneNumber = person.phone
                permissionManager.requestPermission(Manifest.permission.SEND_SMS) { isGranted ->
                    if (isGranted) {
                        openMassageActivity(phoneNumber)
                    } else {
                        Toast.makeText(this@MainActivity, "Доступ к сообщениям отклонен", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun callTheNumber(number: String?) {
        val intent = Intent(Intent.ACTION_CALL)
        intent.data = Uri.parse("tel:$number")
        startActivity(intent)
    }

    private fun openMassageActivity(phoneNumber: String?) {
        val intent = Intent(this, MassageActivity::class.java).apply {
            putExtra("phoneNumber", phoneNumber)
        }
        startActivity(intent)
    }

    private fun setupButtons() {
        binding.exitIB.setOnClickListener { finishAffinity() }
        binding.searchIB.setOnClickListener {
            val intent = Intent(this@MainActivity, SearchActivity::class.java)
            startActivity(intent)
        }
        binding.addBTN.setOnClickListener {
            addNewContact()
        }
    }

    private fun checkAndFetchContacts() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissionManager.requestPermission(Manifest.permission.READ_CONTACTS) { isGranted ->
                if (isGranted) fetchContacts()
            }
        } else {
            fetchContacts()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchContacts() {
        CoroutineScope(Dispatchers.Main).launch {
            val contacts = withContext(Dispatchers.IO) {
                ContactUtils.fetchContacts(this@MainActivity)
            }
            contactModelsList.clear()
            contactModelsList.addAll(contacts)
            customAdapter?.notifyDataSetChanged()
        }
    }

    private fun addNewContact() {
        val newContactName = binding.newContactNameET.text.toString()
        val newContactPhone = binding.newContactPhoneET.text.toString()
        if (!ContactModel.isValidate(this, newContactName, newContactPhone)) return
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissionManager.requestPermission(Manifest.permission.WRITE_CONTACTS) { isGranted ->
                if (isGranted) addContactToPhonebook(newContactName, newContactPhone)
            }
        } else {
            addContactToPhonebook(newContactName, newContactPhone)
        }
        binding.newContactNameET.text.clear()
        binding.newContactPhoneET.text.clear()
    }
    private fun addContactToPhonebook(name: String, phone: String) {
        CoroutineScope(Dispatchers.Main).launch {
            val result = withContext(Dispatchers.IO) {
                try {
                    val listCPO = ArrayList<ContentProviderOperation>().apply {
                        add(ContentProviderOperation.newInsert(RawContacts.CONTENT_URI)
                            .withValue(RawContacts.ACCOUNT_TYPE, null)
                            .withValue(RawContacts.ACCOUNT_NAME, null)
                            .build())
                        add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, StructuredName.CONTENT_ITEM_TYPE)
                            .withValue(StructuredName.DISPLAY_NAME, name)
                            .build())
                        add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, 0)
                            .withValue(ContactsContract.Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE)
                            .withValue(Phone.NUMBER, phone)
                            .withValue(Phone.TYPE, Phone.TYPE_MOBILE)
                            .build())
                    }
                    contentResolver.applyBatch(ContactsContract.AUTHORITY, listCPO)
                    true
                } catch (e: Exception) {
                    Log.e("Exception", e.message ?: "Error adding contact")
                    false
                }
            }
            if (result) {
                Toast.makeText(this@MainActivity, "$name добавлен", Toast.LENGTH_SHORT).show()
                fetchContacts()
            } else {
                Toast.makeText(this@MainActivity, "Ошибка", Toast.LENGTH_SHORT).show()
            }
        }
    }

}