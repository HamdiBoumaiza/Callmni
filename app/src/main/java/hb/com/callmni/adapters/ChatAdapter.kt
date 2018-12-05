package hb.com.callmni.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import hb.com.callmni.R
import hb.com.callmni.models.ChatModel
import hb.com.callmni.models.UserModel
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*


class ChatAdapter(val mContext: Context,
                  val listChats: ArrayList<ChatModel>,
                  val uid: String,
                  val currentUser: UserModel,
                  val userTo: UserModel) : RecyclerView.Adapter<ChatAdapter.ViewHolder>() {

    private val CHAT_TO = 1
    private val CHAT_FROM = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatAdapter.ViewHolder {
        val view: View
        if (viewType == CHAT_FROM) {
            view = LayoutInflater.from(parent.context).inflate(R.layout.chat_from_row, parent, false)
        } else {
            view = LayoutInflater.from(parent.context).inflate(R.layout.chat_to_row, parent, false)
        }

        return ViewHolder(view)
    }

    override fun getItemViewType(position: Int): Int {
        return if (listChats[position].fromId == uid) {
            CHAT_FROM
        } else CHAT_TO

    }


    override fun getItemCount(): Int {
        return listChats.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val chat = listChats[position]

        if (listChats[position].fromId == uid) {
            Glide.with(mContext).load(currentUser.image).into(holder.imageUserFROM)
            holder.messageFROM.text = chat.message
        } else {
            Glide.with(mContext).load(userTo.image).into(holder.imageUserTO)
            holder.messageTO.text = chat.message
        }


    }

    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val messageFROM = v.message_textview_from
        val imageUserFROM = v.user_imageview_from
        val messageTO = v.message_textview_to
        val imageUserTO = v.user_imageview_to
    }


}