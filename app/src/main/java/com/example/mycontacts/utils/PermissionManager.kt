package com.example.mycontacts.utils

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import android.Manifest

class PermissionManager(private val activity: AppCompatActivity) {

    private val permissionLaunchers = mutableMapOf<String, ActivityResultLauncher<String>>()

    init {

        initializeLaunchers()
    }

    private fun initializeLaunchers() {
        val permissions = listOf(
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.SEND_SMS
        )

        permissions.forEach { permission ->
            permissionLaunchers[permission] = activity.registerForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { isGranted ->
                permissionCallbacks[permission]?.invoke(isGranted)
            }
        }
    }

    private val permissionCallbacks = mutableMapOf<String, (Boolean) -> Unit>()

    fun requestPermission(permission: String, callback: (Boolean) -> Unit) {
        permissionCallbacks[permission] = callback
        permissionLaunchers[permission]?.launch(permission)
    }
}