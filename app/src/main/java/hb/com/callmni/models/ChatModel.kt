package hb.com.callmni.models

data class ChatModel(val id: String, val message: String, val fromId: String, val toId: String, val timestamp: Long) {
    constructor() : this("", "", "", "", -1)
}