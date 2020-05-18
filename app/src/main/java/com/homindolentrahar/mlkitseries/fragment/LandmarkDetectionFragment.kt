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
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.cloud.landmark.FirebaseVisionCloudLandmark
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.homindolentrahar.mlkitseries.R
import com.homindolentrahar.mlkitseries.util.Constants
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraUtils
import kotlinx.android.synthetic.main.action_button_container_layout.*
import kotlinx.android.synthetic.main.fragment_landmark_detection.*

/**
 * A simple [Fragment] subclass.
 */
class LandmarkDetectionFragment : Fragment() {

    private val options = FirebaseVisionCloudDetectorOptions.Builder()
        .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
        .setMaxResults(15)
        .build()
    private val landmarkDetection =
        FirebaseVision.getInstance().getVisionCloudLandmarkDetector(options)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_landmark_detection, container, false)
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
        landmarkDetection.detectInImage(image)
            .addOnSuccessListener { listLandmark ->
                camera_view.visibility = View.GONE
                img_camera.visibility = View.VISIBLE
                btn_show_result.visibility = View.VISIBLE

                for (landmark in listLandmark) {
                    showResult(landmark)
                    btn_show_result.setOnClickListener { showResult(landmark) }
                }
            }
            .addOnFailureListener { }
    }

    private fun showResult(landmark: FirebaseVisionCloudLandmark) {
        MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT)).show {
            title(text = landmark.landmark)
            message(text = landmark.confidence.toString())
        }
    }

    private fun chooseImage() {
        val chooseImageIntent = Intent()
        chooseImageIntent.type = "image/*"
        chooseImageIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(chooseImageIntent, Constants.CHOOSE_IMAGE_RC)
    }
}
