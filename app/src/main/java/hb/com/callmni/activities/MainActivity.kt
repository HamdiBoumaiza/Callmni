package hb.com.callmni.activities

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import hb.com.callmni.R
import hb.com.callmni.USERS
import hb.com.callmni.fragments.ContactFragment
import hb.com.callmni.fragments.MessagesFragment
import hb.com.callmni.fragments.SettingsFragment
import hb.com.callmni.models.UserModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        var currentUser: UserModel? = null
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fetchCurrentUser()

        setBottomNavigation()
    }

    fun setBottomNavigation() {
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)
        navigation.selectedItemId = (R.id.action_chat);
        toolbar.title = getString(R.string.messages)

    }

    private val mOnNavigationItemSelectedListener = BottomNavigationView.OnNavigationItemSelectedListener { item ->
        when (item.itemId) {
            R.id.action_settings -> {
                toolbar.title = getString(R.string.settings)
                loadFragment(SettingsFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_contact -> {
                toolbar.title = getString(R.string.contact)
                loadFragment(ContactFragment())
                return@OnNavigationItemSelectedListener true
            }
            R.id.action_chat -> {
                toolbar.title = getString(R.string.messages)
                loadFragment(MessagesFragment())
                return@OnNavigationItemSelectedListener true
            }
        }
        false
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main_top, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_logout -> {
                logOut()
            }
        }

        return super.onOptionsItemSelected(item)
    }


    private fun logOut() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setTitle(getString(R.string.app_name))
        builder.setMessage(getString(R.string.logout_message))
        builder.setPositiveButton(getString(R.string.yes)) { dialog, which ->
            FirebaseAuth.getInstance().signOut()
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
        }
        builder.setNegativeButton(getString(R.string.no)) { dialog, which ->
            dialog.cancel()
        }
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }


    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_container, fragment).commit()
    }


    private fun fetchCurrentUser() {
        val uid = FirebaseAuth.getInstance().uid
        val ref = FirebaseDatabase.getInstance().getReference("$USERS$uid")
        ref.addListenerForSingleValueEvent(object : ValueEventListener {

            override fun onDataChange(p0: DataSnapshot) {
                currentUser = p0.getValue(UserModel::class.java)
            }

            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }


}
