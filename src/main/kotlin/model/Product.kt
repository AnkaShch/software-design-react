package model

import org.bson.Document
import kotlin.collections.HashMap

class Product(private val id: Int, private val name: String, private val prices: HashMap<User.Currency?, String?>) {

    constructor(doc: Document) : this(doc.getInteger("id"),
        doc.getString("name"),
        object : HashMap<User.Currency?, String?>() {
            init {
                put(User.Currency.USD, doc.getString(User.Currency.USD.toString()))
                put(User.Currency.RUB, doc.getString(User.Currency.RUB.toString()))
            }
        })

    fun toDocument(): Document {
        return Document("id", id).append("name", name)
            .append(User.Currency.USD.toString(), prices[User.Currency.USD]).append(
                User.Currency.RUB.toString(),
                prices[User.Currency.RUB]
            )
    }

    fun toString(currency: User.Currency): String {
        return """Product: {
	id: $id,
	name: $name,
	$currency: ${prices[currency]}
}"""
    }

    override fun toString(): String {
        return """Product: {
	id: $id,
	name: $name,
	${User.Currency.RUB}: ${prices[User.Currency.RUB]},
	${User.Currency.USD}: ${prices[User.Currency.USD]}
}"""
    }
}