package de.bwaldvogel.base91

import java.io.ByteArrayOutputStream
import java.nio.charset.StandardCharsets
import kotlin.test.Test
import kotlin.test.assertEquals

class Base91OutputStreamTest {
    @Test
    @kotlin.Throws(Exception::class)
    fun testWriteByteArrays() {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val base91OutputStream = Base91OutputStream(byteArrayOutputStream)
        base91OutputStream.write("abcdefg".toByteArray(StandardCharsets.UTF_8))
        base91OutputStream.write("higjklmn".toByteArray(StandardCharsets.UTF_8))
        base91OutputStream.flush()
        val encoded: ByteArray = byteArrayOutputStream.toByteArray()
        val decoded: ByteArray = Base91.decode(encoded)
        assertEquals(String(decoded, StandardCharsets.UTF_8), "abcdefghigjklmn")
    }

//    @Test
//    @kotlin.Throws(Exception::class)
//    fun testWriteSingleBytes() {
//        val byteArrayOutputStream = ByteArrayOutputStream()
//        val base91OutputStream = Base91OutputStream(byteArrayOutputStream)
//        base91OutputStream.write('a')
//        base91OutputStream.write('b')
//        base91OutputStream.write('c')
//        base91OutputStream.write('d')
//        base91OutputStream.flush()
//        val encoded: ByteArray = byteArrayOutputStream.toByteArray()
//        val decoded: ByteArray = Base91.decode(encoded)
//        assertThat(String(decoded, StandardCharsets.UTF_8)).isEqualTo("abcd")
//    }
}
