package hb.com.callmni.activities

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import hb.com.callmni.IMAGES_FOLDER
import hb.com.callmni.R
import hb.com.callmni.USERS
import hb.com.callmni.models.UserModel
import hb.com.callmni.utils.isValidEmail
import kotlinx.android.synthetic.main.activity_register.*
import timber.log.Timber
import java.util.*

class RegisterActivity : AppCompatActivity(), View.OnClickListener {


    private val REQUEST_CODE = 123
    private var uriPhoto: Uri? = null
    private lateinit var progressDialog: ProgressDialog

    var email: String? = null
    var password: String? = null
    var name: String? = null

    override fun onClick(p0: View?) {
        when (p0!!.id) {
            R.id.register_button -> {
                registerUser()
                progressDialog.show()
            }
            R.id.already_have_account_textView -> {
                finish()
            }
            R.id.select_photo_imageView -> {
                goToGallery()
            }
        }
    }

    fun goToGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        setProgressDialog()
        setClickListeners()
    }

    fun setClickListeners() {
        register_button.setOnClickListener(this)
        already_have_account_textView.setOnClickListener(this)
        select_photo_imageView.setOnClickListener(this)
    }

    fun setProgressDialog() {
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage(getString(R.string.loading))
        progressDialog.setCancelable(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null) {

            uriPhoto = data.data

            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, uriPhoto)
            select_photo_imageView.setImageBitmap(bitmap)

        }
    }


    private fun validateCredentials(): Boolean {
        var valid = true

        if (name!!.isEmpty()) {
            name_editText.error = (getString(R.string.required_field))
            valid = false
        } else if (email!!.isEmpty()) {
            email_editText.error = (getString(R.string.required_field))
            valid = false
        } else if (password!!.isEmpty()) {
            password_editText.error = (getString(R.string.required_field))
            valid = false
        } else if (!isValidEmail(email!!)) {
            email_editText.error = (getString(R.string.enter_valid_email))
            valid = false
        } else if (password!!.length < 6) {
            password_editText.error = (getString(R.string.password_too_short))
            valid = false
        }


        if (!valid) {
            Toast.makeText(this, getString(R.string.verify_inputs), Toast.LENGTH_LONG).show()
        }

        return valid
    }


    private fun registerUser() {
        email = email_editText.text.toString()
        password = password_editText.text.toString()
        name = name_editText.text.toString()


        if (!validateCredentials()) {
            progressDialog.dismiss()
            return
        }

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email!!, password!!)
                .addOnCompleteListener {
                    if (!it.isSuccessful) return@addOnCompleteListener

                    // else if successful
                    Timber.d("isSuccessful :  ${it.result?.user?.uid}")
                    uploadImageUser()
                }
                .addOnFailureListener {
                    Timber.d("there was an error while creating your profile: ${it.message}")
                    Toast.makeText(this, "there was an error while creating your profile: ${it.message}",
                            Toast.LENGTH_SHORT).show()
                    progressDialog.dismiss()
                }
    }


    private fun uploadImageUser() {
        if (uriPhoto == null) return

        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("$IMAGES_FOLDER$filename")

        ref.putFile(uriPhoto!!)
                .addOnSuccessListener {
                    Timber.d("Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Timber.d("File Location: $it")

                        saveUserToRealTimeDatabase(it.toString())
                    }
                }
                .addOnFailureListener {
                    Timber.d("Failed to upload image to storage: ${it.message}")
                    progressDialog.dismiss()

                }
    }


    private fun saveUserToRealTimeDatabase(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("$USERS$uid")
        val user = UserModel(uid, name!!, email!!, profileImageUrl)

        ref.setValue(user)
                .addOnSuccessListener {
                    Timber.d("Finally we saved the user to Firebase Database")

                    progressDialog.dismiss()
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
                .addOnFailureListener {
                    Timber.d("Failed to set value to database: ${it.message}")
                    progressDialog.dismiss()

                }
    }


}





