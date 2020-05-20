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
import com.google.firebase.ml.vision.label.FirebaseVisionOnDeviceImageLabelerOptions
import com.homindolentrahar.mlkitseries.R
import com.homindolentrahar.mlkitseries.adapter.ImageLabelAdapter
import com.homindolentrahar.mlkitseries.model.ImageLabelingModel
import com.homindolentrahar.mlkitseries.util.Constants
import kotlinx.android.synthetic.main.fragment_image_labeling.*
import kotlinx.android.synthetic.main.image_labeling_result_layout.view.*

/**
 * A simple [Fragment] subclass.
 */
class ImageLabelingFragment : Fragment() {

    private lateinit var bottomSheet: MaterialDialog
    private val options = FirebaseVisionOnDeviceImageLabelerOptions.Builder()
        .setConfidenceThreshold(0.7F)
        .build()
    private val labelDetector = FirebaseVision.getInstance().getOnDeviceImageLabeler(options)
    private val adapter = ImageLabelAdapter()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_labeling, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Setup Bottom Sheet
        bottomSheet = MaterialDialog(requireContext(), BottomSheet(LayoutMode.WRAP_CONTENT))
        bottomSheet.customView(
            R.layout.image_labeling_result_layout,
            scrollable = false,
            dialogWrapContent = true
        )

        btn_choose_image.setOnClickListener { chooseImage() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == Constants.CHOOSE_IMAGE_RC) {
            val imgUri = data?.data as Uri
            val image = FirebaseVisionImage.fromFilePath(requireContext(), imgUri)

            img_camera.setImageURI(imgUri)
            analyzeImage(image)
        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun analyzeImage(image: FirebaseVisionImage) {
        labelDetector.processImage(image)
            .addOnSuccessListener { listLabel ->
                btn_show_result.visibility = View.VISIBLE

                val list = listLabel.map { label ->
                    ImageLabelingModel(label.entityId, label.text, label.confidence)
                }
                showResult(list)
                btn_show_result.setOnClickListener { showResult(list) }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Error !", Toast.LENGTH_SHORT).show()
                Log.d(ImageLabelingFragment::class.java.simpleName, it.toString())
            }
    }

    private fun showResult(labels: List<ImageLabelingModel>) {
        bottomSheet.show {
            getCustomView().rv_list_label.adapter = adapter
            adapter.submitList(labels)
        }
    }

    private fun chooseImage() {
        val chooseImageIntent = Intent()
        chooseImageIntent.type = "image/*"
        chooseImageIntent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(chooseImageIntent, Constants.CHOOSE_IMAGE_RC)
    }

}
