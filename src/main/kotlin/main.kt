enum class TypeObjects { User, Chat, Message}

data class User(
    val id: Int,
    val nickName: String,
    val root: Boolean
)

data class ChatRoom(
    val id: Int,
    val name: String,
    val owner: Int,
    val guest: Int
)

data class Message(
    val id: Int,
    val chatId: Int,
    val userId: Int,
    val text: String,
    val read: Boolean
)

object ChatServices{
    private var users = emptyList<User>()
    private var chats = emptyList<ChatRoom>()
    private var messages = emptyList<Message>()
    private var currentUser: User? = null

    fun add(parameter: Any): Any {
        when (parameter) {
            is User -> users += parameter
            is ChatRoom -> chats += parameter
            is Message -> messages += parameter
            else -> throw IllegalArgumentException("Неверный тип объекта")
        }
        return parameter
    }

    fun get(type: TypeObjects): List<Any> {
        return when (type) {
            TypeObjects.User -> users
            TypeObjects.Chat -> chats
            TypeObjects.Message -> messages
        }

    }

    fun getById(type: TypeObjects, id: Int): Any? {
        return when (type) {
            TypeObjects.User -> users.find { it.id==id }
            TypeObjects.Chat -> chats.find { it.id==id }
            TypeObjects.Message -> messages.find { it.id==id }
        }
    }

    fun connectUser(userId: Int): String {
        val connectetUser = users.find { it.id == userId }
        if (connectetUser != null) {
            currentUser  = connectetUser
        }
        return "Активный пользователь ${showCurrentUser()}"
    }

    fun showCurrentUser(): String {
        return currentUser?.nickName ?: "None"
    }


    fun сreateChatRoom(hatName: String, guestId: Int): ChatRoom {
        if (currentUser == null) throw IllegalArgumentException("Авторизуйтесь")

        val newId = chats.maxByOrNull { it.id }?.let { it.id + 1 } ?: 1

        val newChatRoom = ChatRoom(newId, hatName, currentUser!!.id, guestId)
        chats = chats.plus(newChatRoom)
        return newChatRoom
    }

    fun showChatRoom(): List<ChatRoom> {
        if (currentUser == null) throw IllegalArgumentException("Авторизуйтесь")

        return chats.filter { it.owner == currentUser!!.id || it.guest==currentUser!!.id } as List<ChatRoom>
    }

    fun renameChatRoom(chatId: Int, newName: String): ChatRoom {
        if (currentUser == null) throw IllegalArgumentException("Авторизуйтесь")

        val chat = getById(TypeObjects.Chat, chatId) ?: throw IllegalArgumentException("Не найден чат с id $chatId")
        val targetChat = chat as ChatRoom

        val currentChat = if (currentUser!!.root || currentUser!!.id == targetChat.owner) {
            targetChat} else throw IllegalArgumentException("Не найден чат с id $chatId")

        chats -= currentChat
        val newChat = currentChat.copy(name = newName)
        chats += newChat

        return newChat
    }


}

fun main() {
    val chatServices = ChatServices
    chatServices.add(User(1, "Laslo", true))
    chatServices.add(User(2, "Gustavo", false))
    chatServices.add(User(3, "Dimon", false))

    try {
        chatServices.сreateChatRoom("Hi Gustavo", 2)
    } catch (e: IllegalArgumentException) {
        println(e.message)// Code for handling the exception
    }
    println(chatServices.connectUser(1))

    chatServices.сreateChatRoom("Football news", 3)
    println(chatServices.showChatRoom())
    try {
        val chat = chatServices.renameChatRoom(1, "FC Milan")}
    catch (e: IllegalArgumentException) {
        println(e.message)
    }
    println(chatServices.showChatRoom())

}