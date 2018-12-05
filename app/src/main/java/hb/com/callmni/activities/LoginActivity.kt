package hb.com.callmni.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import hb.com.callmni.R
import hb.com.callmni.utils.isValidEmail
import kotlinx.android.synthetic.main.activity_login.*
import timber.log.Timber

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.register_button -> {
                SignInUser()
            }
            R.id.register_button_login -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        register_button.setOnClickListener(this)
        register_button_login.setOnClickListener(this)
    }


    private fun SignInUser() {
        val email = email_editText.text.toString()
        val password = password_editText.text.toString()

        if (!validateCredentials()) return

        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    Timber.d("Login Successfully: ${it.result?.user?.uid}")

                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    Timber.e("Failed to log in: ${it.message}")
                }
    }


    private fun validateCredentials(): Boolean {
        var valid = true

        val email = email_editText.text.toString()
        val password = password_editText.text.toString()

        if (email.isEmpty()) {
            email_editText.error = (getString(R.string.required_field))
            valid = false
        } else if (password.isEmpty()) {
            password_editText.error = (getString(R.string.required_field))
            valid = false
        } else if (!isValidEmail(email)) {
            email_editText.error = (getString(R.string.enter_valid_email))
            valid = false
        } else if (password.length < 6) {
            password_editText.error = (getString(R.string.password_too_short))
            valid = false
        }

        if (!valid) {
            Toast.makeText(this, getString(R.string.verify_inputs), Toast.LENGTH_LONG).show()
        }

        return valid
    }

}
