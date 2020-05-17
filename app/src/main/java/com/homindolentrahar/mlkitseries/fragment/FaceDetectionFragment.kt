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
import com.afollestad.materialdialogs.customview.customView
import com.afollestad.materialdialogs.customview.getCustomView
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import com.google.firebase.ml.vision.face.FirebaseVisionFace
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions
import com.homindolentrahar.mlkitseries.R
import com.homindolentrahar.mlkitseries.util.Constants
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraUtils
import com.otaliastudios.cameraview.Facing
import kotlinx.android.synthetic.main.face_detection_result_layout.view.*
import kotlinx.android.synthetic.main.fragment_face_detection.*

/**
 * A simple [Fragment] subclass.
 */
class FaceDetectionFragment : Fragment() {

    private var cameraFacing = Facing.FRONT
    private val options = FirebaseVisionFaceDetectorOptions.Builder()
        .enableTracking()
        .setPerformanceMode(FirebaseVisionFaceDetectorOptions.ACCURATE)
        .setLandmarkMode(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
        .setClassificationMode(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
        .build()
    private val faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(options)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_face_detection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCamera()
        initCameraListener()
        camera_view.facing = cameraFacing
        camera_view.setLifecycleOwner(this)
        btn_swap.setOnClickListener {
            cameraFacing = if (cameraFacing == Facing.FRONT) Facing.BACK else Facing.FRONT
            camera_view.facing = cameraFacing
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.CHOOSE_IMAGE_RC) {
            val imageUri = data?.data as Uri
            val image = FirebaseVisionImage.fromFilePath(requireContext(), imageUri)

            img_camera.setImageURI(imageUri)
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
        btn_swap.visibility = View.VISIBLE
        btn_show_result.visibility = View.GONE

        btn_capture.setOnClickListener {
            if (img_camera.visibility == View.VISIBLE) {
                camera_view.start()
                camera_view.visibility = View.VISIBLE
                img_camera.visibility = View.GONE
                btn_swap.visibility = View.VISIBLE
                btn_show_result.visibility = View.GONE
            } else {
                camera_view.capturePicture()
            }
        }
        btn_choose_image.setOnClickListener {
            chooseImage()
        }
    }

    private fun initCameraListener() {
        camera_view.addCameraListener(object : CameraListener() {
            override fun onPictureTaken(jpeg: ByteArray?) {
                camera_view.stop()
                CameraUtils.decodeBitmap(jpeg) { bitmap ->
                    img_camera.setImageBitmap(bitmap)

                    val metadata = FirebaseVisionImageMetadata.Builder()
                        .setWidth(bitmap.width)
                        .setHeight(bitmap.height)
                        .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                        .setRotation(if (cameraFacing == Facing.FRONT) FirebaseVisionImageMetadata.ROTATION_270 else FirebaseVisionImageMetadata.ROTATION_90)
                        .build()

                    val image = FirebaseVisionImage.fromBitmap(bitmap)
                    processImage(image)
                }
                super.onPictureTaken(jpeg)
            }
        })
    }

    private fun chooseImage() {
        val chooseImageIntent = Intent()
        chooseImageIntent.type = "image/*"
        chooseImageIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(chooseImageIntent, Constants.CHOOSE_IMAGE_RC)
    }

    private fun processImage(image: FirebaseVisionImage) {
        faceDetector.detectInImage(image)
            .addOnSuccessListener {
                camera_view.visibility = View.GONE
                img_camera.visibility = View.VISIBLE
                btn_swap.visibility = View.GONE
                btn_show_result.visibility = View.VISIBLE

                for (face in it) {
                    showResult(face)
                    btn_show_result.setOnClickListener { showResult(face) }
                }
            }
            .addOnFailureListener { }
    }

    private fun showResult(data: FirebaseVisionFace) {
        MaterialDialog(
            requireContext(),
            BottomSheet(LayoutMode.WRAP_CONTENT)
        ).show {
            customView(
                R.layout.face_detection_result_layout,
                scrollable = false,
                dialogWrapContent = true
            )
            getCustomView().tv_smile_prob.text = data.smilingProbability.toString()
            getCustomView().tv_left_eye_open_prob.text =
                data.leftEyeOpenProbability.toString()
            getCustomView().tv_right_eye_open_prob.text =
                data.rightEyeOpenProbability.toString()
        }
    }
}