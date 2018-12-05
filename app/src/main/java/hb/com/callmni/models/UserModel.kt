package hb.com.callmni.models

import java.io.Serializable


data class UserModel(val id: String, val name: String, val email: String, val image: String) : Serializable {
    constructor() : this("", "", "", "")
}