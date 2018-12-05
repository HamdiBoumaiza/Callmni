package hb.com.callmni.fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import hb.com.callmni.LATEST_MESSAGES
import hb.com.callmni.R
import hb.com.callmni.USER_KEY
import hb.com.callmni.activities.ChatActivity
import hb.com.callmni.adapters.MessagesAdapter
import hb.com.callmni.callbacks.IAdapterOnClickItemListener
import hb.com.callmni.models.ChatModel
import hb.com.callmni.models.UserModel
import kotlinx.android.synthetic.main.fragment_messages.*
import timber.log.Timber
import java.util.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.set

class MessagesFragment : Fragment(), IAdapterOnClickItemListener {
    override fun onClickitem(user: UserModel) {
        val intent = Intent(activity, ChatActivity::class.java)
        intent.putExtra(USER_KEY, user)
        startActivity(intent)
    }

    var mContext: Context? = null
    val latestMessagesMap = HashMap<String, ChatModel>()
    var messagesAdapter: MessagesAdapter? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_messages, container, false)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
        mContext = context
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        listenForLatestMessages()
    }


    private fun setRecyclerViewMessages() {

        if (latestMessagesMap.size != 0) {
           if (no_messages_textView != null) no_messages_textView.visibility = View.GONE
        }

        val listChats = ArrayList<ChatModel>()
        latestMessagesMap.values.forEach {
            listChats.add(it)
        }
        messagesAdapter = MessagesAdapter(this, listChats, mContext!!)
        if (recyclerMessages != null) recyclerMessages.adapter = messagesAdapter
    }

    private fun listenForLatestMessages() {
        val fromId = FirebaseAuth.getInstance().uid

        val ref = FirebaseDatabase.getInstance().getReference("$LATEST_MESSAGES$fromId")
        ref.addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatModel::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                setRecyclerViewMessages()
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatModel::class.java) ?: return
                latestMessagesMap[p0.key!!] = chatMessage
                setRecyclerViewMessages()
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

            override fun onCancelled(p0: DatabaseError) {
                Timber.e("canceled")
            }
        })

    }


}
