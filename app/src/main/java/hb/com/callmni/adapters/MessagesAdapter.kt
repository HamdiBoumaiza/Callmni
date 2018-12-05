package hb.com.callmni.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import hb.com.callmni.R
import hb.com.callmni.USERS
import hb.com.callmni.activities.MainActivity
import hb.com.callmni.callbacks.IAdapterOnClickItemListener
import hb.com.callmni.models.ChatModel
import hb.com.callmni.models.UserModel
import hb.com.callmni.utils.getDateDifference
import kotlinx.android.synthetic.main.item_messages.view.*
import java.util.*

class MessagesAdapter(var myClickListener: IAdapterOnClickItemListener,
                      val chatList: ArrayList<ChatModel>,
                      val context: Context) : RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_messages, parent, false))
    }

    override fun getItemCount(): Int {
        return chatList.size
    }

    val userList = ArrayList<UserModel>()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val chatitem = chatList[position]
        holder.message.text = chatitem.message
        holder.time.text = getDateDifference(context, chatitem.timestamp.toString())


        val chatPartnerId: String
        if (chatitem.fromId == FirebaseAuth.getInstance().uid) {
            chatPartnerId = chatitem.toId
        } else {
            chatPartnerId = chatitem.fromId
        }

        val ref = FirebaseDatabase.getInstance().getReference("$USERS$chatPartnerId")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                val chatPartnerUser = p0.getValue(UserModel::class.java)

                holder.name.text = chatPartnerUser?.name
                Glide.with(context).load(chatPartnerUser!!.image).into(holder.imageUser)

                userList.add(chatPartnerUser)

            }

            override fun onCancelled(p0: DatabaseError) {}
        })


    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val time = v.time_textview
        val name = v.name_textview
        val message = v.message_textview
        val imageUser = v.user_img

        init {
            v.setOnClickListener {
                myClickListener.onClickitem(userList[adapterPosition])
            }
        }
    }
}