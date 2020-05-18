package com.homindolentrahar.mlkitseries.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.homindolentrahar.mlkitseries.R
import com.homindolentrahar.mlkitseries.util.Constants
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraUtils
import kotlinx.android.synthetic.main.action_button_container_layout.*
import kotlinx.android.synthetic.main.fragment_barcode_scanner.*

/**
 * A simple [Fragment] subclass.
 */
class BarcodeScannerFragment : Fragment() {
    private val options = FirebaseVisionBarcodeDetectorOptions.Builder()
        .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE, FirebaseVisionBarcode.FORMAT_AZTEC)
        .build()
    private val barcodeScanner = FirebaseVision.getInstance().getVisionBarcodeDetector(options)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_barcode_scanner, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCamera()
        initCameraListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.CHOOSE_IMAGE_RC) {
            camera_view.stop()
            val imgUri = data?.data as Uri
            val image = FirebaseVisionImage.fromFilePath(requireContext(), imgUri)

            img_camera.setImageURI(imgUri)
            processImage(image)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStop() {
        super.onStop()
        camera_view.destroy()
    }

    private fun initCamera() {
        camera_view.start()
        camera_view.visibility = View.VISIBLE
        img_camera.visibility = View.GONE
        btn_swap.visibility = View.GONE
        btn_show_result.visibility = View.GONE

        btn_choose_image.setOnClickListener { chooseImage() }
        btn_capture.setOnClickListener {
            if (img_camera.visibility == View.VISIBLE) {
                camera_view.start()
                camera_view.visibility = View.VISIBLE
                img_camera.visibility = View.GONE
                btn_show_result.visibility = View.GONE
            } else {
                camera_view.capturePicture()
            }
        }
    }

    private fun initCameraListener() {
        camera_view.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                super.onPictureTaken(jpeg)

                camera_view.stop()
                CameraUtils.decodeBitmap(jpeg) { bitmap ->
                    img_camera.setImageBitmap(bitmap)

                    val image = FirebaseVisionImage.fromBitmap(bitmap)
                    processImage(image)
                }
            }
        })
    }

    private fun processImage(image: FirebaseVisionImage) {
        barcodeScanner.detectInImage(image)
            .addOnSuccessListener { listBarcode ->
                camera_view.visibility = View.GONE
                img_camera.visibility = View.VISIBLE
                btn_show_result.visibility = View.VISIBLE

                for (barcode in listBarcode) {
                    showResult(barcode)
                    btn_show_result.setOnClickListener { showResult(barcode) }
                }
            }
            .addOnFailureListener { }
    }

    private fun showResult(barcode: FirebaseVisionBarcode) {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(text = barcode.displayValue)
            message(text = barcode.valueType.toString())
        }
    }

    private fun chooseImage() {
        val chooseImageIntent = Intent()
        chooseImageIntent.type = "image/*"
        chooseImageIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(chooseImageIntent, Constants.CHOOSE_IMAGE_RC)
    }
}
