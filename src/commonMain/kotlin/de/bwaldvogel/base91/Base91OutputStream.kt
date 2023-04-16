package de.bwaldvogel.base91

import de.bwaldvogel.base91.Base91.BASE
import de.bwaldvogel.base91.Base91.ENCODING_TABLE
import java.io.FilterOutputStream
import java.io.IOException
import java.io.OutputStream

class Base91OutputStream(out: OutputStream?) : FilterOutputStream(out) {
    private var ebq = 0
    private var en = 0

    @Throws(IOException::class)
    override fun write(b: Int) {
        ebq = ebq or (b and 255 shl en)
        en += 8
        if (en > 13) {
            var ev = ebq and 8191
            if (ev > 88) {
                ebq = ebq shr 13
                en -= 13
            } else {
                ev = ebq and 16383
                ebq = ebq shr 14
                en -= 14
            }
            out.write(ENCODING_TABLE.get(ev % BASE).toInt())
            out.write(ENCODING_TABLE.get(ev / BASE).toInt())
        }
    }

    @Throws(IOException::class)
    override fun write(data: ByteArray, offset: Int, length: Int) {
        for (i in offset until length) {
            write(data[i].toInt())
        }
    }

    @Throws(IOException::class)
    override fun flush() {
        if (en > 0) {
            out.write(ENCODING_TABLE.get(ebq % BASE).toInt())
            if (en > 7 || ebq > 90) {
                out.write(ENCODING_TABLE.get(ebq / BASE).toInt())
            }
        }
        super.flush()
    }
}
