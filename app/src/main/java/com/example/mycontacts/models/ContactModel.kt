package com.example.mycontacts.models

import android.content.Context
import android.widget.Toast

data class ContactModel(val name: String?, val phone: String?) {

    companion object {

        fun isValidate(context: Context, name: String, number: String): Boolean {
            if (name.isEmpty() && number.isEmpty()) {
                Toast.makeText(context, "Введите данные", Toast.LENGTH_SHORT).show()
                return false
            }
            if (name.isEmpty()) {
                Toast.makeText(context, "Введите имя", Toast.LENGTH_SHORT).show()
                return false
            }
            if (name.length !in 2..32) {
                Toast.makeText(context, "Введите корректное имя", Toast.LENGTH_SHORT).show()
                return false
            }

            if (number.isEmpty()) {
                Toast.makeText(
                    context,
                    "Введите номер телефона", Toast.LENGTH_SHORT
                ).show()
                return false
            }
            if (number.length !in 10..15) {
                Toast.makeText(
                    context,
                    "Введите корректный номер", Toast.LENGTH_SHORT
                ).show()
                return false
            }
            return true
        }

    }
}


