package com.homindolentrahar.mlkitseries.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.afollestad.materialdialogs.LayoutMode
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.bottomsheets.BottomSheet
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.text.FirebaseVisionText
import com.homindolentrahar.mlkitseries.R
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraUtils
import kotlinx.android.synthetic.main.fragment_text_recognition.*
import kotlinx.android.synthetic.main.text_recognition_result_layout.view.*

/**
 * A simple [Fragment] subclass.
 */
class TextRecognitionFragment : Fragment() {

    private val CHOOSE_IMAGE_RC = 1
    private val textRecognizer = FirebaseVision.getInstance().onDeviceTextRecognizer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_text_recognition, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initCamera()
        initCameraListener()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == CHOOSE_IMAGE_RC) {
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
        btn_show_result.visibility = View.GONE

        btn_choose_image.setOnClickListener { chooseImage() }
        btn_capture.setOnClickListener {
            if (img_camera.visibility == View.VISIBLE) {
                camera_view.visibility = View.VISIBLE
                img_camera.visibility = View.GONE
                btn_show_result.visibility = View.GONE
                camera_view.start()
            } else {
                camera_view.capturePicture()
            }
        }
    }

    private fun initCameraListener() {
        camera_view.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                camera_view.stop()
                CameraUtils.decodeBitmap(jpeg) { bitmap ->
                    img_camera.setImageBitmap(bitmap)

                    val image = FirebaseVisionImage.fromBitmap(bitmap)
                    processImage(image)
                }

                super.onPictureTaken(jpeg)
            }
        })
    }

    private fun processImage(image: FirebaseVisionImage) {
        textRecognizer.processImage(image)
            .addOnSuccessListener { text ->
                camera_view.visibility = View.GONE
                img_camera.visibility = View.VISIBLE
                btn_show_result.visibility = View.VISIBLE

                showResult(text)
                btn_show_result.setOnClickListener { showResult(text) }
            }
            .addOnFailureListener {
                showErrorToast("Error Displaying Result !")
                Log.d(TextRecognitionFragment::class.java.simpleName, it.toString())
            }
    }

    private fun showResult(text: FirebaseVisionText) {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            customView(
                R.layout.text_recognition_result_layout,
                scrollable = false,
                dialogWrapContent = true
            )
            getCustomView().tv_result.text = text.text
        }
    }

    private fun showErrorToast(text: String) {
        Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
    }

    private fun chooseImage() {
        val chooseImageIntent = Intent()
        chooseImageIntent.type = "image/*"
        chooseImageIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(chooseImageIntent, CHOOSE_IMAGE_RC)
    }
}
