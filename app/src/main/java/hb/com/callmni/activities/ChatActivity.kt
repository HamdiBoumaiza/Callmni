package hb.com.callmni.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import hb.com.callmni.LATEST_MESSAGES
import hb.com.callmni.R
import hb.com.callmni.USER_KEY
import hb.com.callmni.USER_MESSAGES
import hb.com.callmni.adapters.ChatAdapter
import hb.com.callmni.models.ChatModel
import hb.com.callmni.models.UserModel
import kotlinx.android.synthetic.main.activity_chat.*
import timber.log.Timber


class ChatActivity : AppCompatActivity() {

    private lateinit var userTo: UserModel
    private var chatAdapter: ChatAdapter? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        userTo = intent.getSerializableExtra(USER_KEY) as UserModel
        supportActionBar!!.title = userTo.name

        listenForMessages()
        setEditTextTouchListener()
    }

    fun setEditTextTouchListener() {
        chat_editText.setOnTouchListener(object : OnTouchListener {
            override fun onTouch(v: View, event: MotionEvent): Boolean {
                val DRAWABLE_RIGHT = 2

                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= chat_editText.getRight() - chat_editText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width()) {
                        sendMessage()

                        return true
                    }
                }
                return false
            }
        })

        chat_editText.text.clear()


    }

    private fun sendMessage() {
        // how do we actually send a message to firebase...
        val message = chat_editText.text.toString()

        val fromId = FirebaseAuth.getInstance().uid
        val toId = userTo.id

        if (fromId == null) return

        val reference = FirebaseDatabase.getInstance().getReference("$USER_MESSAGES$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("$USER_MESSAGES$toId/$fromId").push()

        val chatMessage = ChatModel(reference.key!!, message, fromId, toId, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
                .addOnSuccessListener {
                    Timber.d("Saved messages : ${reference.key}")
                    chat_editText.text.clear()
                    recyclerChat.scrollToPosition(chatAdapter!!.itemCount - 1)
                }

        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("$LATEST_MESSAGES$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("$LATEST_MESSAGES$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }


    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = userTo.id
        val ref = FirebaseDatabase.getInstance().getReference("$USER_MESSAGES$fromId/$toId")
        val chatList = ArrayList<ChatModel>()


        ref.addChildEventListener(object : ChildEventListener {

            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatModel::class.java)

                if (chatMessage != null) {
                    Timber.e("message is  :: ${chatMessage.message}")
                    chatList.add(chatMessage)
                }

                chatAdapter = ChatAdapter(this@ChatActivity, chatList, fromId!!, MainActivity.currentUser!!, userTo)
                recyclerChat.adapter = chatAdapter
                recyclerChat.scrollToPosition(chatAdapter!!.itemCount - 1)

            }

            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot) {

            }

        })

    }


}
