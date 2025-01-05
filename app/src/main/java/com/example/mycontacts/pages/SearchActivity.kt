package com.example.mycontacts.pages

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mycontacts.adapter.CustomAdapter
import com.example.mycontacts.R
import com.example.mycontacts.databinding.ActivitySearchBinding
import com.example.mycontacts.models.ContactModel
import com.example.mycontacts.utils.ContactUtils

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var customAdapter: CustomAdapter? = null
    private val contactModelsList = mutableListOf<ContactModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.toolbar.title = ""
        setupRecyclerView()
        setupToolbar()
        fetchContacts()

        binding.searchBTN.setOnClickListener { filterContacts() }
        binding.exitIB.setOnClickListener { finishAffinity() }

    }

    private fun setupRecyclerView() {
        binding.recyclerViewRV.layoutManager = LinearLayoutManager(this)
        customAdapter = CustomAdapter(contactModelsList)
        binding.recyclerViewRV.adapter = customAdapter

        customAdapter?.setOnItemClickListener(object :
            CustomAdapter.OnItemClickListener {
            override fun onCallClick(item: ContactModel, position: Int) {
                val person = (contactModelsList as ArrayList<ContactModel>)[position]
                val number = person.phone
                callTheNumber(number)
            }
            override fun onMassageClick(item: ContactModel, position: Int) {
                val person = (contactModelsList as ArrayList<ContactModel>)[position]
                val phoneNumber = person.phone
                openMassageActivity(phoneNumber)
            }
        }
        )
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



    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24)
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun fetchContacts() {
        contactModelsList.clear()
        contactModelsList.addAll(ContactUtils.fetchContacts(this))
        customAdapter?.notifyDataSetChanged()
    }

    private fun filterContacts() {
        val query = binding.searchNameET.text.toString()
        val filteredContacts = if (query.isEmpty()) {
            contactModelsList
        } else {
            contactModelsList.filter { it.name?.contains(query, ignoreCase = true) == true }
        }
        customAdapter = CustomAdapter(filteredContacts.toMutableList())
        binding.recyclerViewRV.adapter = customAdapter
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