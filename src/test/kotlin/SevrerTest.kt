import com.mongodb.rx.client.Success
import db.ReactiveMongoDriver
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkClass
import model.Product
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import rx.internal.util.ScalarSynchronousObservable
import server.Server

class ServerTest() {
    var mongoDriver: ReactiveMongoDriver? = null
    var server: Server? = null

    @Before
    fun before() {
        mongoDriver = mockkClass(ReactiveMongoDriver::class)
        every { mongoDriver!!.addProduct(any()) } returns Success.SUCCESS
        every { mongoDriver!!.addUser(any()) } returns Success.SUCCESS
        server = Server(mongoDriver!!)
    }

    @Test
    fun testAddUser() {
        val params: MutableMap<String?, List<String>> = HashMap()
        params["id"] = listOf("1")
        params["name"] = listOf("Test")
        params["cur"] = listOf("rub")
        val res = server!!.addUser(params)
        Assert.assertEquals(
            "New user:\n" +
                    "User: {\n" +
                    "\tid: 1,\n" +
                    "\tname: Test,\n" +
                    "\tcurrency: RUB\n" +
                    "}",
            (res as ScalarSynchronousObservable<*>).get().toString()
        )
    }

    @Test
    fun testAddUserMissingParams() {
        val params: MutableMap<String?, List<String>> = HashMap()
        params["id"] = listOf("1")
        val res = server!!.addUser(params)
        Assert.assertEquals("Please add params: name, cur", (res as ScalarSynchronousObservable<*>).get())
    }

    @Test
    fun testAddProduct() {
        val params: MutableMap<String?, List<String>> = HashMap()
        params["id"] = listOf("1")
        params["name"] = listOf("Test")
        params["rub"] = listOf("25")
        params["usd"] = listOf("12")
        val res = server!!.addProduct(params)
        Assert.assertEquals(
            ("New product:\n" +
                    "Product: {\n" +
                    "\tid: 1,\n" +
                    "\tname: Test,\n" +
                    "\tRUB: 25,\n" +
                    "\tUSD: 12\n" +
                    "}"),
            (res as ScalarSynchronousObservable<*>).get().toString()
        )
    }

    @Test
    fun testAddProductMissingParams() {
        val params: MutableMap<String?, List<String>> = HashMap()
        params["id"] = listOf("1")
        val res = server!!.addProduct(params)
        Assert.assertEquals("Please add params: name, usd, rub", (res as ScalarSynchronousObservable<*>).get())
    }
}