package com.homindolentrahar.mlkitseries.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.languageid.FirebaseLanguageIdentificationOptions
import com.homindolentrahar.mlkitseries.R
import kotlinx.android.synthetic.main.fragment_language_identification.*

/**
 * A simple [Fragment] subclass.
 */
class LanguageIdentification : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_language_identification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val inputText = edit_input_field.text.toString()
        btn_identify.setOnClickListener {
            processText(inputText)
        }
    }

    private fun processText(text: String) {
        val options = FirebaseLanguageIdentificationOptions.Builder()
            .setConfidenceThreshold(0.1F)
            .build()
        val langIdentifier =
            FirebaseNaturalLanguage.getInstance().getLanguageIdentification(options)

        langIdentifier.identifyLanguage(text)
            .addOnSuccessListener { result ->
                text_lang.visibility = View.VISIBLE
                tv_lang.visibility = View.VISIBLE

                tv_lang.text = result
            }
            .addOnFailureListener { }
    }
}
