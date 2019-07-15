package memscan

import com.sun.jna.Memory
import com.sun.jna.Pointer
import org.jire.kotmem.win32.Kernel32.ReadProcessMemory
import org.jire.kotmem.win32.Win32Process
import org.jire.kotmem.win32.openProcess
import org.jire.kotmem.win32.processIDByName
import session.log
import utils.truncate
import java.nio.ByteBuffer


class MemHandler : XrdApi {

    var connected = false

    private var GG_PROC: Win32Process? = null

    private fun logConnected(flag:Boolean, logEntry:String): Boolean {
        if (flag != connected)  {
            connected = flag
            log(logEntry)
        }
        return flag
    }

    override fun isConnected(): Boolean {
        try {
            GG_PROC = openProcess(processIDByName("GuiltyGearXrd.exe"))
            GG_PROC!!.modules["GuiltyGearXrd.exe"]//!!.pointer
            return logConnected(true, "[MEMH] XrdApi connected")
        } catch (e: IllegalStateException) {
            return logConnected(false, "[MEMH] XrdApi disconnected")
        } catch (e: NullPointerException) {
            return logConnected(false, "[MEMH] XrdApi failed to locate memory address")
        }
    }

    override fun getClientSteamId(): Long = try {
        val id = getByteBufferFromAddress(longArrayOf(0x1AD82E4L), 8)!!.long
        id
    } catch (e: NullPointerException) {
        -1L
    }

    @UseExperimental(ExperimentalUnsignedTypes::class)
    private fun getByteBufferFromAddress(offsets: LongArray, numBytes: Int): ByteBuffer? {
        if (!isConnected()) return null
        val procBaseAddr: Pointer = GG_PROC!!.modules["GuiltyGearXrd.exe"]!!.pointer
        var bufferMem = Memory(4L)
        var lastPointer: Pointer = procBaseAddr
        for (i in 0..offsets.size - 2) {
            val newPointer = Pointer(Pointer.nativeValue(lastPointer) + offsets[i])
            if (ReadProcessMemory(GG_PROC!!.handle.pointer, newPointer, bufferMem, 4, 0) == 0L) {
                return null
            }
            lastPointer = Pointer(bufferMem.getInt(0L).toUInt().toLong())
        }
        val dataAddr = Pointer(Pointer.nativeValue(lastPointer) + offsets[offsets.size - 1])
        bufferMem = Memory(numBytes.toLong())
        if (ReadProcessMemory(GG_PROC!!.handle.pointer, dataAddr, bufferMem, numBytes, 0) == 0L) {
            return null
        }
        return bufferMem.getByteBuffer(0L, numBytes.toLong())
    }

    override fun getPlayerData(): List<PlayerData> {
        if (!isConnected()) return ArrayList()

        val offs = longArrayOf(0x1C25AB4L, 0x44CL)
        val pDatas = ArrayList<PlayerData>()
        for (i in 0..7) {
            val bb = getByteBufferFromAddress(offs, 0x48) ?: return ArrayList()
            val dispbytes = ByteArray(0x24)
            val steamid = bb.getLong(0)
            val totalmatch = bb.get(8).toInt()
            val wins = bb.get(0xA).toInt()
            val charid = bb.get(0x36)
            val cabid = bb.get(0x38)
            val playerside = bb.get(0x39)
            val loadpercent = bb.get(0x44).toInt()
            bb.position(0xC)
            bb.get(dispbytes, 0, 0x24)
            val dispname = truncate(String(dispbytes).trim('\u0000'), 24)
            val pd = PlayerData(steamid, dispname, charid.toInt(), cabid.toInt(), playerside.toInt(), wins, totalmatch, loadpercent)

            offs[1] += 0x48L
            pDatas.add(pd)
        }
        return pDatas
    }

    override fun getMatchData(): MatchData {
        val sortedStructOffs = longArrayOf(0x9CCL, 0x2888L, 0xA0F4L, 0x22960, 0x2AC64)
        val p1offs = longArrayOf(0x1B18C78L, 0L)
        val p2offs = longArrayOf(0x1B18C78L, 0L)
        p2offs[0] += 4L
        val p1roundoffset = longArrayOf(0x1A3BA38L)
        val p2roundoffset = longArrayOf(0x1A3BA3CL)
        val timeroffs = longArrayOf(0x177A8ACL, 0x450L, 0x4CL, 0x708L)
        try {
            p1offs[1] = sortedStructOffs[0]
            p2offs[1] = sortedStructOffs[0]
            val health = Pair(getByteBufferFromAddress(p1offs, 4)!!.int, getByteBufferFromAddress(p2offs, 4)!!.int)
            p1offs[1] = sortedStructOffs[1]
            p2offs[1] = sortedStructOffs[1]
            val strikeStun =
                Pair(getByteBufferFromAddress(p1offs, 4)!!.int == 1, getByteBufferFromAddress(p2offs, 4)!!.int == 1)
            p1offs[1] = sortedStructOffs[2]
            p2offs[1] = sortedStructOffs[2]
            val canBurst =
                Pair(getByteBufferFromAddress(p1offs, 4)!!.int == 1, getByteBufferFromAddress(p2offs, 4)!!.int == 1)
            p1offs[1] = sortedStructOffs[3]
            p2offs[1] = sortedStructOffs[3]
            val guardGauge = Pair(getByteBufferFromAddress(p1offs, 4)!!.int, getByteBufferFromAddress(p2offs, 4)!!.int)
            p1offs[1] = sortedStructOffs[4]
            p2offs[1] = sortedStructOffs[4]
            val tension = Pair(getByteBufferFromAddress(p1offs, 4)!!.int, getByteBufferFromAddress(p2offs, 4)!!.int)
            val timer = getByteBufferFromAddress(timeroffs, 4)!!.int
            val rounds =
                Pair(getByteBufferFromAddress(p1roundoffset, 4)!!.int, getByteBufferFromAddress(p2roundoffset, 4)!!.int)
            return MatchData(timer, health, rounds, tension, canBurst, strikeStun, guardGauge)
        } catch (e: NullPointerException) {
            return MatchData()
        }
    }

}