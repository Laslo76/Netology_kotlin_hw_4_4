enum class TypeObjects { user, chat, message}

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
    private var currentUserId = 0

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
            TypeObjects.user -> users
            TypeObjects.chat -> chats
            TypeObjects.message -> messages
        }

    }

}

fun main() {
    val chatServices = ChatServices
    chatServices.add(User(1, "Laslo", true))
    chatServices.add(User(2, "Gustavo", false))
    chatServices.add(User(3, "Dimon", false))

    chatServices.add(ChatRoom(1, "Hi Gustavo", 1, 2))
    chatServices.add(ChatRoom(1, "Football news", 1, 2))

    println(chatServices.get(TypeObjects.chat))
}