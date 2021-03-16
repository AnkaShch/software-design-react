package server

import com.mongodb.rx.client.Success
import db.ReactiveMongoDriver
import io.netty.buffer.ByteBuf
import io.reactivex.netty.protocol.http.server.HttpServer
import io.reactivex.netty.protocol.http.server.HttpServerRequest
import io.reactivex.netty.protocol.http.server.HttpServerResponse
import model.Product
import model.User
import rx.Observable
import java.util.*
import java.util.stream.Collectors
import kotlin.collections.HashMap

class Server(var mongoDriver: ReactiveMongoDriver) {
    fun run() {
        HttpServer.newServer(8080)
            .start { request: HttpServerRequest<ByteBuf?>, response: HttpServerResponse<ByteBuf?> ->
                val method = request.decodedPath.substring(1)
                val params =
                    request.queryParameters
                if ("addUser" == method) {
                    return@start response.writeString(addUser(params))
                }
                if ("getUsers" == method) {
                    return@start response.writeString(getUsers(params))
                }
                if ("addProduct" == method) {
                    return@start response.writeString(addProduct(params))
                }
                if ("getProducts" == method) {
                    return@start response.writeString(getProducts(params))
                }
                response.writeString(Observable.just("404. Try 'addUser' 'getUsers' 'addProduct' 'getProducts'"))
            }.awaitShutdown()
    }

    fun addUser(params: Map<String?, List<String>>): Observable<String> {
        val validation = validate(params, ADD_USER_PARAMS)
        if (validation.isNotEmpty()) {
            return Observable.just(validation)
        }
        val id = params["id"]!![0].toInt()
        val name = params["name"]!![0]
        val currency = params["cur"]!![0]
        val user = User(id, name, User.Currency.valueOf(currency.toUpperCase()))
        return if (mongoDriver.addUser(user) == Success.SUCCESS) {
            Observable.just("New user:\n$user")
        } else {
            Observable.just("Error")
        }
    }

    private fun getUsers(params: Map<String?, List<String>>?): Observable<String> {
        return mongoDriver.users
    }

    fun addProduct(params: Map<String?, List<String>>): Observable<String> {
        val validation = validate(params, ADD_PRODUCT_PARAMS)
        if (validation.isNotEmpty()) {
            return Observable.just(validation)
        }
        val id = params["id"]!![0].toInt()
        val name = params["name"]!![0]
        val usd = params["usd"]!![0]
        val rub = params["rub"]!![0]
        val product = Product(id, name,
            object : HashMap<User.Currency?, String?>() {
                init {
                    put(User.Currency.USD, usd)
                    put(User.Currency.RUB, rub)
                }
            })
        return if (mongoDriver.addProduct(product) == Success.SUCCESS) {
            Observable.just("New product:\n$product")
        } else {
            Observable.just("Error")
        }
    }

    private fun getProducts(params: Map<String?, List<String>>): Observable<String>? {
        val validation = validate(params, GET_PRODUCTS_PARAMS)
        if (validation.isNotEmpty()) {
            return Observable.just(validation)
        }
        val id = params["user_id"]!![0].toInt()
        return mongoDriver.getProducts(id)
    }

    private fun validate(params: Map<String?, List<String>>, expectedParams: List<String>): String {
        val missingParams = expectedParams.stream().filter { param: String? ->
            !params.containsKey(
                param
            )
        }.collect(Collectors.toList())
        return if (missingParams.isEmpty()) {
            ""
        } else "Please add params: " + java.lang.String.join(", ", missingParams)
    }

    companion object {
        private val ADD_USER_PARAMS = listOf("id", "name", "cur")
        private val ADD_PRODUCT_PARAMS = listOf("id", "name", "usd", "rub")
        private val GET_PRODUCTS_PARAMS = listOf("user_id")
    }
}