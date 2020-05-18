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
import com.homindolentrahar.mlkitseries.fragment.BarcodeScannerFragment
import com.homindolentrahar.mlkitseries.fragment.FaceDetectionFragment
import com.homindolentrahar.mlkitseries.fragment.LandmarkDetectionFragment
import com.homindolentrahar.mlkitseries.fragment.TextRecognitionFragment
import com.homindolentrahar.mlkitseries.util.Constants

class MainActivity : AppCompatActivity() {
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
                changeContent(FaceDetectionFragment())
            }
            R.id.barcode_scanning -> {
                changeContent(BarcodeScannerFragment())
            }
            R.id.landmark_recognition -> {
                changeContent(LandmarkDetectionFragment())
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
        ActivityCompat.requestPermissions(this, permissions, Constants.PERMISSION_RC)
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
