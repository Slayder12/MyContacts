package com.example.mycontacts.utils

import android.annotation.SuppressLint
import android.content.Context
import android.provider.ContactsContract
import com.example.mycontacts.models.ContactModel

object ContactUtils {
    @SuppressLint("Range")
    fun fetchContacts(context: Context): MutableList<ContactModel> {
        val contactList = mutableListOf<ContactModel>()
        val phones = context.contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )
        phones?.use { cursor ->
            while (cursor.moveToNext()) {
                val name = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                )
                val phone = cursor.getString(
                    cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                )
                contactList.add(ContactModel(name, phone))
            }
        }
        return contactList
    }
}