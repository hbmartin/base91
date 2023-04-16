package de.bwaldvogel.base91

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class Base91Test {
    @Test
    fun testEncodeDecode() {
        val encodeDecodes: MutableMap<String, String> = mutableMapOf()
        encodeDecodes.put("test", "fPNKd")
        encodeDecodes.put("Never odd or even\n", "_O^gp@J`7RztjblLA#_1eHA")
        encodeDecodes.put("May a moody baby doom a yam?\n", "8D9Kc)=/2\$WzeFui#G9Km+<{VT2u9MZil}[A")
        encodeDecodes.put("", "")
        encodeDecodes.put("a", "GB")
        for (entry in encodeDecodes.entries) {
            val plainText: String = entry.key
            val encodedText: String = entry.value
            val encode: ByteArray = Base91.encode(plainText.toByteArray(CHARSET))
            val decode: ByteArray = Base91.decode(encode)
            assertEquals(String(encode, CHARSET), encodedText)
            assertEquals(String(decode, CHARSET), plainText)
        }
    }

    @Test
    @kotlin.Throws(Exception::class)
    fun testRandomEncodeDecode() {
        val random = Random(RANDOM_SEED)
        var encodedSize = 0
        var plainSize = 0
        var worstEncodingRatio = Double.MIN_VALUE
        var bestEncodingRatio = Double.MAX_VALUE
        for (i in 0..9999) {
            val bytes = ByteArray(random.nextInt(1000) + 100)
            random.nextBytes(bytes)
            val encode: ByteArray = Base91.encode(bytes)
            val decode: ByteArray = Base91.decode(encode)
            assertEquals(decode.size, bytes.size)
            for (j in bytes.indices) {
                assertEquals(decode[j], bytes[j])
            }

            assertTrue(decode.contentEquals(bytes))
            plainSize += bytes.size
            encodedSize += encode.size
            val encodingRatio = encode.size.toDouble() / bytes.size
            worstEncodingRatio = Math.max(worstEncodingRatio, encodingRatio)
            bestEncodingRatio = Math.min(bestEncodingRatio, encodingRatio)
        }
        val encodingRatio = encodedSize.toDouble() / plainSize
        println("encoding ratio: $encodingRatio")
        println("worst encoding ratio: $worstEncodingRatio")
        println("best encoding ratio: $bestEncodingRatio")
        assertTrue(encodingRatio >= BEST_CASE_RATIO)
        assertTrue(encodingRatio <= WORST_CASE_RATIO)
    }

    companion object {
        private val CHARSET: Charset = StandardCharsets.UTF_8
        private const val WORST_CASE_RATIO = 1.2308
        private const val BEST_CASE_RATIO = 1.1429
        private const val RANDOM_SEED: Long = 4711
    }
}
