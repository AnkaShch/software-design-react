import db.ReactiveMongoDriver
import server.Server

object Main {
    @JvmStatic
    fun main(args: Array<String>) {
        val server = Server(ReactiveMongoDriver())
        server.run() //localhost:8080
    }
}