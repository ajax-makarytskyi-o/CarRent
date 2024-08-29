package fixtures

import kotlin.random.Random

object Utils {
    internal fun generateString(length: Int): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        return (1..length)
            .map { charPool[Random.nextInt(charPool.size)] }
            .joinToString("")
    }
}