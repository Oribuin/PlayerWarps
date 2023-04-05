package xyz.oribuin.playerwarps.util

import java.io.*
import java.util.*
import kotlin.reflect.KClass

object ListSerializer : Serializable {
    /**
     * Serialize a list to a string.
     *
     * @param list The list to serialize
     * @param <T>  The type of the list
     * @return The serialized list
    </T> */
    fun <T> serialize(list: List<T>): String? {
        return try {
            val baos = ByteArrayOutputStream()
            val oos = ObjectOutputStream(baos)

            oos.writeObject(list)
            oos.close()

            Base64.getEncoder().encodeToString(baos.toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Deserialize a list from a string.
     *
     * @param tClass         The class of the list
     * @param serializedList The serialized list
     * @param <T>            The type of the list
     * @return The deserialized list
    </T> */
    @Suppress("UNCHECKED_CAST")
    fun <T : Any> deserialize(tClass: KClass<T>, serializedList: String): List<T> {
        return try {
            val data = Base64.getDecoder().decode(serializedList)
            val inputStream = ByteArrayInputStream(data)
            val stream = ObjectInputStream(inputStream)
            val obj = stream.readObject()
            stream.close()
            if (obj is List<*> && obj.isNotEmpty() && obj[0]?.javaClass == tClass.java) {
                    return obj as List<T>
            }

            ArrayList()
        } catch (e: Exception) {
            e.printStackTrace()
            ArrayList()
        }
    }
}
