package com.homindolentrahar.mlkitseries.util

import android.app.Activity
import android.content.Intent

object Constants {
    const val PERMISSION_RC = 212
    const val CHOOSE_IMAGE_RC = 1

    fun chooseImage(activity: Activity) {
        val chooseImageIntent = Intent()
        chooseImageIntent.type = "image/*"
        chooseImageIntent.action = Intent.ACTION_GET_CONTENT
        activity.startActivityForResult(chooseImageIntent, CHOOSE_IMAGE_RC)
    }
}