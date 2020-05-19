package com.homindolentrahar.mlkitseries.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.smartreply.FirebaseTextMessage
import com.google.firebase.ml.naturallanguage.smartreply.SmartReplySuggestionResult
import com.homindolentrahar.mlkitseries.R
import com.homindolentrahar.mlkitseries.adapter.SmartReplyAdapter
import com.homindolentrahar.mlkitseries.model.SmartReplyModel
import kotlinx.android.synthetic.main.fragment_smart_reply.*

/**
 * A simple [Fragment] subclass.
 */
class SmartReplyFragment : Fragment() {

    private val smartReplyConversation = ArrayList<SmartReplyModel>()
    private val firebaseSmartReplyConversation = ArrayList<FirebaseTextMessage>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_smart_reply, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        Attach adapter
        rv_list_chat.adapter = SmartReplyAdapter(requireContext(), smartReplyConversation)

        edit_remote_reply_layout.setEndIconOnClickListener {
            sendReply(edit_remote_reply_field)
        }
        btn_reply_one.setOnClickListener { smartReply(btn_reply_one.text.toString()) }
        btn_reply_two.setOnClickListener { smartReply(btn_reply_two.text.toString()) }
        btn_reply_three.setOnClickListener { smartReply(btn_reply_three.text.toString()) }
    }

    private fun sendReply(input: TextInputEditText) {
        val replyMessage = input.text.toString()
        smartReplyConversation.add(SmartReplyModel(false, replyMessage))
        rv_list_chat.adapter?.notifyItemInserted(smartReplyConversation.size - 1)
        rv_list_chat.scrollToPosition(smartReplyConversation.size - 1)
        input.text?.clear()

        firebaseSmartReplyConversation.add(
            FirebaseTextMessage.createForRemoteUser(
                replyMessage,
                System.currentTimeMillis(),
                "Another_user"
            )
        )

        FirebaseNaturalLanguage.getInstance().smartReply.suggestReplies(
            firebaseSmartReplyConversation
        )
            .addOnSuccessListener { result ->
                if (result.status == SmartReplySuggestionResult.STATUS_SUCCESS && result.suggestions.size == 3) {
                    btn_reply_container.visibility = View.VISIBLE
                    btn_reply_one.text = result.suggestions[0].text
                    btn_reply_two.text = result.suggestions[1].text
                    btn_reply_three.text = result.suggestions[2].text
                }
            }
    }

    private fun smartReply(message: String) {
        smartReplyConversation.add(SmartReplyModel(true, message))
        rv_list_chat.adapter?.notifyItemInserted(smartReplyConversation.size - 1)
        rv_list_chat.scrollToPosition(smartReplyConversation.size - 1)

        firebaseSmartReplyConversation.add(
            FirebaseTextMessage.createForLocalUser(
                message,
                System.currentTimeMillis()
            )
        )

        FirebaseNaturalLanguage.getInstance().smartReply.suggestReplies(
            firebaseSmartReplyConversation
        )
            .addOnSuccessListener { result ->
                if (result.status == SmartReplySuggestionResult.STATUS_SUCCESS && result.suggestions.size == 3) {
                    btn_reply_container.visibility = View.VISIBLE
                    btn_reply_one.text = result.suggestions[0].text
                    btn_reply_two.text = result.suggestions[1].text
                    btn_reply_three.text = result.suggestions[2].text
                }
            }
    }
}
