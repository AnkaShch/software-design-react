package model

import org.bson.Document

class User(private val id: Int, private val name: String, val currency: Currency) {

    constructor(doc: Document) : this(
        doc.getInteger("id"),
        doc.getString("name"),
        Currency.valueOf(doc.getString("currency").toUpperCase())
    )

    fun toDocument(): Document {
        return Document("id", id).append("name", name).append("currency", currency.toString())
    }

    override fun toString(): String {
        return "User: {\n" +
                "\tid: " + id + ",\n" +
                "\tname: " + name + ",\n" +
                "\tcurrency: " + currency.toString() + "\n" +
                "}"
    }

    enum class Currency {
        USD, RUB
    }
}