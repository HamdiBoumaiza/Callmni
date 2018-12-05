package hb.com.callmni.fragments

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import hb.com.callmni.R
import hb.com.callmni.USER_KEY
import hb.com.callmni.activities.ChatActivity
import hb.com.callmni.adapters.ContactAdapter
import hb.com.callmni.callbacks.IAdapterOnClickItemListener
import hb.com.callmni.models.UserModel
import kotlinx.android.synthetic.main.fragment_contact.*

class ContactFragment : Fragment() {


    private lateinit var contactAdapter: ContactAdapter
    private var listUsers = ArrayList<UserModel>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contact, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        fetchUsers()
    }

    private val myClickListener = object : IAdapterOnClickItemListener {
        override fun onClickitem(user: UserModel) {
            val intent = Intent(activity, ChatActivity::class.java)
            intent.putExtra(USER_KEY, user)
            startActivity(intent)
        }
    }


    private fun fetchUsers() {
        val ref = FirebaseDatabase.getInstance().getReference("/users")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                p0.children.forEach {
                    Log.d("user :: ", it.toString())
                    val user = it.getValue(UserModel::class.java)
                    progress_bar.visibility = View.GONE
                    if (user != null) {
                        if (user.id != FirebaseAuth.getInstance().uid) {
                            listUsers.add(user)
                        }
                    }
                }

                if (listUsers.size != 0) {
                    contactAdapter = ContactAdapter(listUsers, activity!!, myClickListener)
                    recyclerContact.adapter = contactAdapter
                }
            }

            override fun onCancelled(p0: DatabaseError) {}
        })
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }


    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater!!.inflate(R.menu.menu_search, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = activity!!.getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu!!.findItem(R.id.action_search).actionView as SearchView
        searchView.setSearchableInfo(searchManager.getSearchableInfo(activity!!.componentName))
        searchView.maxWidth = (Integer.MAX_VALUE)

        // listening to search query text change
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                contactAdapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                contactAdapter.filter.filter(query)
                return false
            }
        })
        super.onCreateOptionsMenu(menu, inflater)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_search -> {
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }


}
