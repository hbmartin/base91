package de.bwaldvogel.base91

import java.io.ByteArrayOutputStream
import java.io.IOException
import java.nio.charset.StandardCharsets

/**
 * Modified version of Jochaim Henke’s original code from http://base91.sourceforge.net/
 *
 *
 * basE91 encoding/decoding routines
 *
 *
 * Copyright (c) 2000-2006 Joachim Henke All rights reserved.
 *
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer. - Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials provided
 * with the distribution. - Neither the name of Joachim Henke nor the names of
 * his contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Joachim Henke (Original version)
 * @author Benedikt Waldvogel (Modifications)
 */
object Base91 {
    @JvmField
    val ENCODING_TABLE: ByteArray =
        "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!#$%&()*+,./:;<=>?@[]^_`{|}~\""
            .toByteArray(StandardCharsets.ISO_8859_1)
    private val DECODING_TABLE: ByteArray = ByteArray(256) { -1 }
    @JvmField
    val BASE = ENCODING_TABLE.size
    private const val AVERAGE_ENCODING_RATIO = 1.2297f

    init {
        for (i in 0 until BASE) DECODING_TABLE[ENCODING_TABLE[i].toInt()] = i.toByte()
    }

    @JvmStatic
    fun encode(data: ByteArray?): ByteArray {
        val out = ByteArrayOutputStream()
        val base91OutputStream = Base91OutputStream(out)
        try {
            base91OutputStream.write(data)
            base91OutputStream.flush()
        } catch (e: IOException) {
            throw RuntimeException("Failed to encode", e)
        }
        return out.toByteArray()
    }

    @JvmStatic
    fun decode(data: ByteArray): ByteArray {
        var dbq = 0
        var dn = 0
        var dv = -1
        val estimatedSize = Math.round(data.size / AVERAGE_ENCODING_RATIO)
        val output = ByteArrayOutputStream(estimatedSize)
        for (i in data.indices) {
            assert(DECODING_TABLE[data[i].toInt()].toInt() != -1)
            if (dv == -1) {
                dv = DECODING_TABLE[data[i].toInt()].toInt()
            } else {
                dv += DECODING_TABLE[data[i].toInt()] * BASE
                dbq = dbq or (dv shl dn)
                dn += if (dv and 8191 > 88) 13 else 14
                do {
                    output.write(dbq.toByte().toInt())
                    dbq = dbq shr 8
                    dn -= 8
                } while (dn > 7)
                dv = -1
            }
        }
        if (dv != -1) {
            output.write((dbq or (dv shl dn)).toByte().toInt())
        }
        return output.toByteArray()
    }
}
