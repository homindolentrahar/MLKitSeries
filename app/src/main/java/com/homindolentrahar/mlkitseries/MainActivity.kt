package com.homindolentrahar.mlkitseries

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.homindolentrahar.mlkitseries.fragment.TextRecognitionFragment

class MainActivity : AppCompatActivity() {
    private val PERMISSION_RC = 212
    private val permissions =
        arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        Checking Permission
        if (!isPermissionGranted()) {
            requestPermission()
        }
//        Toolbar title
        title = "ML Kit Series"
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.text_recognition -> {
                changeContent(TextRecognitionFragment())
            }
            R.id.face_detection -> {
            }
            R.id.barcode_scanning -> {
            }
            R.id.landmark_recognition -> {
            }
            R.id.language_identification -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun changeContent(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, permissions, PERMISSION_RC)
    }

    private fun isPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }
}
