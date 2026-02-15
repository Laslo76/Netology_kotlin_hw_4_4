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
    val guest: Int,
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


    fun clear() {
        users = emptyList<User>()
        chats = emptyList<ChatRoom>()
        messages = emptyList<Message>()
        currentUser = null
    }

    fun add(parameter: Any): Any {
        when (parameter) {
            is User -> users += parameter
            is ChatRoom -> chats += parameter
            is Message -> messages += parameter
            else -> throw IllegalArgumentException("Неверный тип объекта")
        }
        return parameter
    }


    fun getById(type: TypeObjects, id: Int): Any? {
        return when (type) {
            TypeObjects.User -> users.find { it.id==id }
            TypeObjects.Chat -> chats.find { it.id==id }
            TypeObjects.Message -> messages.find { it.id==id }
        }
    }

    fun connectUser(userId: Int): String {
        val connectUser = users.find { it.id == userId }
        if (connectUser != null) {
            currentUser  = connectUser
        }
        return "Активный пользователь ${showCurrentUser()}"
    }


    fun showCurrentUser(): String {
        return currentUser?.nickName ?: "None"
    }


    fun getChats(): List<ChatRoom> {
        if (currentUser == null) throw IllegalArgumentException("Авторизуйтесь")

        return chats.filter { it.owner == currentUser!!.id || it.guest==currentUser!!.id }
    }


    fun getUnreadChatsCount(): Int {
        if (currentUser == null) throw IllegalArgumentException("Авторизуйтесь")

        val listUnreadMessages = messages.filter{ it.read.not() }.map{it.chatId}.toSet()
        return listUnreadMessages.size
    }


    fun getLastMessagesInChats(): List<String> {
        if (currentUser == null) throw IllegalArgumentException("Авторизуйтесь")

        var digest = listOf<String>()
        chats.forEach {
            val idChat = it.id
            val name = it.name
            digest += messages.filter { it.chatId == idChat }
                              .maxByOrNull { it.id }?.let { "$name - ${it.text}" } ?: "$name - Сообщений нет"
        }

        return digest
    }


    private fun createChatRoom(chatName: String, guestId: Int, lastMessage:Int = 0): ChatRoom {
        if (currentUser == null) throw IllegalArgumentException("Авторизуйтесь")

        val newId = chats.maxByOrNull { it.id }?.let { it.id + 1 } ?: 1

        val newChatRoom = ChatRoom(newId, chatName, currentUser!!.id, guestId)
        chats = chats.plus(newChatRoom)
        return newChatRoom
    }


    fun renameChatRoom(chatId: Int, newName: String): ChatRoom {
        val oldChatRoom = deleteChatRoom(chatId)
        val newChat = oldChatRoom.copy(name = newName)
        chats += newChat

        return newChat
    }


    fun deleteChatRoom(chatId: Int, delete: Boolean = false): ChatRoom {
        if (currentUser == null) throw IllegalArgumentException("Авторизуйтесь")

        val chat = getById(TypeObjects.Chat, chatId) ?: throw IllegalArgumentException("Не найден чат с id $chatId")
        val targetChat = chat as ChatRoom

        val currentChat = if (currentUser!!.root || currentUser!!.id == targetChat.owner) {
            targetChat} else throw IllegalArgumentException("Не найден чат с id $chatId")
        chats -= currentChat

        if (delete) { deleteMessagesByChat(currentChat.id)}

        return currentChat
    }


    fun createdMessage(text: String, userId: Int, chatId: Int = 0): Message {
        if (currentUser == null) throw IllegalArgumentException("Авторизуйтесь")

        val newId = messages.maxByOrNull { it.id }?.let { it.id + 1 } ?: 1

        val chat = if (chatId == 0) {
            createChatRoom(text, userId)
        } else {
            getById(TypeObjects.Chat, chatId) as ChatRoom
        }

        val newMessage = Message(newId,chat.id, userId = currentUser!!.id, text = text, read = false)
        messages += newMessage
        return newMessage
    }


    fun deleteMessage(id: Int): Message {
        if (currentUser == null) throw IllegalArgumentException("Авторизуйтесь")

        val message = getById(TypeObjects.Message, id) ?: throw IllegalArgumentException("Не найдено сообщение с id $id")
        val targetMessage = message as Message

        val currentMessage = if (currentUser!!.root || currentUser!!.id == targetMessage.userId) {
            targetMessage
        } else throw IllegalArgumentException("Не найден чат с id $id")
        messages -= currentMessage

        return currentMessage
    }


    fun editMessage(id: Int, text: String): Message {
        if (currentUser == null) throw IllegalArgumentException("Авторизуйтесь")

        val targetMessage = deleteMessage(id)
        val newMessage = targetMessage.copy(text = text, read = false)

        messages -= targetMessage
        messages += newMessage
        return newMessage
    }


    fun deleteMessagesByChat(chatId: Int){
        val listMessages = messages.filter { it.chatId == chatId }
        messages -= listMessages
    }

    fun readMessagesByChat(chatId: Int, count: Int): List<Message>{
        messages.filter { it.chatId == chatId }
            .sortedBy { it.id }
            .takeLast(count)
            .filter { it.read.not() }.forEach {
                messages -= it
                messages += it.copy(read=true)}

        return messages.filter { it.chatId == chatId }.sortedBy { it.id }.takeLast(count)
    }

    fun showMessages(){
        messages.forEach { println(it) }
    }
}


fun main() {
    val chatServices = ChatServices
    chatServices.add(User(1, "Laslo", true))
    chatServices.add(User(2, "Gustavo", false))
    chatServices.add(User(3, "Dimon", false))

    println(chatServices.connectUser(1))
    chatServices.createdMessage("Hi Gustavo", 2)
    chatServices.createdMessage("FC Milan", 3)

    println(chatServices.getChats())

    println(chatServices.getUnreadChatsCount())

    chatServices.deleteMessage(1)
    //chatServices.deleteMessagesByChat(1)
    chatServices.createdMessage("FC Milan 3-0 Inter", 3, 2)
    println(chatServices.getLastMessagesInChats())
    chatServices.showMessages()

    println(chatServices.readMessagesByChat(2, 5))
    chatServices.showMessages()
}