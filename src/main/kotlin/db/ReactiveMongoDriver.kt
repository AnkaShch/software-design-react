package db

import com.mongodb.client.model.Filters.eq
import com.mongodb.rx.client.MongoClient
import com.mongodb.rx.client.MongoClients
import com.mongodb.rx.client.Success
import model.Product
import model.User
import org.bson.Document
import rx.Observable
import rx.functions.Func1
import rx.functions.Func2
import java.util.concurrent.TimeUnit

class ReactiveMongoDriver {
    private val mongoClient: MongoClient = MongoClients.create()
    fun addUser(user: User): Success {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection("users")
            .insertOne(user.toDocument()).timeout(10, TimeUnit.SECONDS).toBlocking().single()
    }

    val users: Observable<String>
        get() = mongoClient.getDatabase(DATABASE_NAME).getCollection("users").find().toObservable()
            .map { document -> User(document).toString() }.reduce { user1, user2 -> user1.toString() + "\n" + user2 }

    fun findUser(id: Int): User? {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection("users")
            .find(eq("id", id)).first().map { doc: Document? ->
                User(
                    doc!!
                )
            }.timeout(10, TimeUnit.SECONDS).toBlocking().single()
    }

    fun addProduct(product: Product): Success {
        return mongoClient.getDatabase(DATABASE_NAME).getCollection("products")
            .insertOne(product.toDocument()).timeout(10, TimeUnit.SECONDS).toBlocking().single()
    }

    fun getProducts(id: Int?): Observable<String>? {
        val currency = findUser(id!!)!!.currency
        return mongoClient.getDatabase(DATABASE_NAME).getCollection("products").find().toObservable()
            .map { document: Document? ->
                Product(document!!).toString(
                    currency
                )
            }.reduce { prod1: String, prod2: String ->
                """
                $prod1
                $prod2
                """.trimIndent()
            }
    }

    companion object {
        private const val DATABASE_NAME = "shop"
    }

}