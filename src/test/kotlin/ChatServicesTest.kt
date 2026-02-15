import org.junit.Assert.*
import org.junit.Before
import kotlin.test.Test
import kotlin.test.asserter


class ChatServicesTest {
    @Before
    fun clearBeforeTest() {
        ChatServices.clear()
    }

    @Test
    fun getByIdUserTest() {
        val chatServices = ChatServices
        val firstUser = chatServices.add(User(1, "Laslo", true))
        val secondUser = chatServices.add(User(2, "Gustavo", false))

        val result = chatServices.getById(TypeObjects.User, 3)
       assertEquals(null, result)

    }

    @Test
    fun getByIdChatTest() {
        val chatServices = ChatServices
        val firstUser = chatServices.add(User(1, "Laslo", true))
        val secondUser = chatServices.add(User(2, "Gustavo", false))
        val threeUser = chatServices.add(User(3, "Dimon", false))

        println(chatServices.connectUser(1))

        val messageOne = chatServices.createdMessage("Hi Gustavo", 2)
        val messageTwo = chatServices.createdMessage("FC Milan", 3)

        val result = chatServices.getById(TypeObjects.Chat, 2) as ChatRoom
        assertEquals(messageTwo.chatId, result.id)

    }

    @Test
    fun getByIdMessageTest() {
        val chatServices = ChatServices
        val firstUser = chatServices.add(User(1, "Laslo", true))
        val secondUser = chatServices.add(User(2, "Gustavo", false))
        val threeUser = chatServices.add(User(3, "Dimon", false))

        println(chatServices.connectUser(1))

        val messageOne = chatServices.createdMessage("Hi Gustavo", 2)
        val messageTwo = chatServices.createdMessage("FC Milan", 3)

        val result = chatServices.getById(TypeObjects.Message, 1) as Message
        assertEquals(messageOne, result)
    }

    @Test
    fun getUnreadChatsCountTest() {
        val chatServices = ChatServices
        val firstUser = chatServices.add(User(1, "Laslo", true))
        val secondUser = chatServices.add(User(2, "Gustavo", false))
        val threeUser = chatServices.add(User(3, "Dimon", false))

        println(chatServices.connectUser(1))

        val messageOne = chatServices.createdMessage("Hi Gustavo", 2)
        val messageTwo = chatServices.createdMessage("FC Milan", 3)
        val messageThree = chatServices.createdMessage("FC Milan", 3, 2)

        val result = chatServices.getUnreadChatsCount()
        assertEquals(2, result)
    }

    @Test
    fun getLastMessagesInChats() {
        val chatServices = ChatServices
        val firstUser = chatServices.add(User(1, "Laslo", true))
        val secondUser = chatServices.add(User(2, "Gustavo", false))
        val threeUser = chatServices.add(User(3, "Dimon", false))

        println(chatServices.connectUser(1))

        val messageOne = chatServices.createdMessage("Hi Gustavo", 2)
        val messageTwo = chatServices.createdMessage("FC Milan", 3)
        val messageThree = chatServices.createdMessage("FC Milan 3-0 Inter", 3, 2)

        val result = chatServices.getLastMessagesInChats()[1]
        assertEquals("FC Milan - FC Milan 3-0 Inter", result)
    }

    @Test
    fun deleteChatsTestSize() {
        val chatServices = ChatServices
        val firstUser = chatServices.add(User(1, "Laslo", true))
        val secondUser = chatServices.add(User(2, "Gustavo", false))
        val threeUser = chatServices.add(User(3, "Dimon", false))

        println(chatServices.connectUser(1))

        val messageOne = chatServices.createdMessage("Hi Gustavo", 2)
        val messageTwo = chatServices.createdMessage("FC Milan", 3)
        val messageThree = chatServices.createdMessage("FC Milan 3-0 Inter", 3, 2)

        chatServices.deleteChatRoom(1, true)

        val result = chatServices.getChats().size
        assertEquals(1, 1)
    }

    @Test
    fun deleteChatsTestMessages() {
        val chatServices = ChatServices
        val firstUser = chatServices.add(User(1, "Laslo", true))
        val secondUser = chatServices.add(User(2, "Gustavo", false))
        val threeUser = chatServices.add(User(3, "Dimon", false))

        println(chatServices.connectUser(1))

        val messageOne = chatServices.createdMessage("Hi Gustavo", 2)
        val messageTwo = chatServices.createdMessage("FC Milan", 3)
        val messageThree = chatServices.createdMessage("FC Milan 3-0 Inter", 3, 2)

        chatServices.deleteChatRoom(1, true)

        val result = chatServices.getLastMessagesInChats().size
        assertEquals(1, result)
    }


    @Test
    fun readMessagesByChat() {
        val chatServices = ChatServices
        val firstUser = chatServices.add(User(1, "Laslo", true))
        val secondUser = chatServices.add(User(2, "Gustavo", false))
        val threeUser = chatServices.add(User(3, "Dimon", false))

        println(chatServices.connectUser(1))

        val messageOne = chatServices.createdMessage("Hi Gustavo", 2)
        val messageTwo = chatServices.createdMessage("FC Milan", 3)
        val messageThree = chatServices.createdMessage("FC Milan 3-0 Inter", 3, 2)

        chatServices.readMessagesByChat(2, 5)

        val result = chatServices.getUnreadChatsCount()
        assertEquals(1, result)
    }

}