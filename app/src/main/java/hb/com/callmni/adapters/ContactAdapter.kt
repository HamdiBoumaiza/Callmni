package hb.com.callmni.adapters

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import com.bumptech.glide.Glide
import hb.com.callmni.R
import hb.com.callmni.callbacks.IAdapterOnClickItemListener
import hb.com.callmni.models.UserModel
import kotlinx.android.synthetic.main.item_contact.view.*

class ContactAdapter(var users: ArrayList<UserModel>,
                     val context: Context,
                     var myClickListener: IAdapterOnClickItemListener) : Filterable, RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

    private val usersLocal: ArrayList<UserModel> = users

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_contact, parent, false))
    }

    override fun getItemCount(): Int {
        return users.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user = users[position]
        Glide.with(context).load(user.image).into(holder.imageUser)
        holder.nameUser.text = user.name
    }


    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    users = usersLocal
                } else {
                    val filteredList = java.util.ArrayList<UserModel>()
                    for (row in usersLocal) {
                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.name.toLowerCase().contains(charString.toLowerCase()) || row.email.contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }
                    users = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = users
                return filterResults
            }

            override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                users = filterResults.values as java.util.ArrayList<UserModel>
                notifyDataSetChanged()
            }
        }
    }


    inner class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {

        val imageUser = v.user_img
        val nameUser = v.name_textview_contact

        init {
            v.setOnClickListener {
                myClickListener.onClickitem(users[adapterPosition])
            }
        }

    }


}