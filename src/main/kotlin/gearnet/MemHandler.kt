package gearnet

import com.sun.jna.Memory
import com.sun.jna.Pointer
import org.jire.kotmem.win32.Kernel32.ReadProcessMemory
import org.jire.kotmem.win32.Win32Process
import org.jire.kotmem.win32.openProcess
import org.jire.kotmem.win32.processIDByName
import utils.truncate
import java.nio.ByteBuffer


class MemHandler : XrdApi {

    private var xrdProcess: Win32Process? = null

    override fun isConnected(): Boolean {
        return if (xrdProcess == null) try {
            xrdProcess = openProcess(processIDByName("GuiltyGearXrd.exe"))
            xrdProcess!!.modules["GuiltyGearXrd.exe"]!!.pointer
            true
        } catch (e: IllegalStateException) {
            false
        } catch (e: NullPointerException) {
            false
        } else true
    }

    override fun getClientSteamId(): Long = try {
        val id = getByteBuffFromAddr(longArrayOf(0x1AD82E4L), 8)!!.long
        id
    } catch (e: NullPointerException) {
        -1L
    }

    @OptIn(ExperimentalUnsignedTypes::class)
    private fun getByteBuffFromAddr(offsets: LongArray, numBytes: Int): ByteBuffer? {
        if (!isConnected()) return null
        val procBaseAddr: Pointer = xrdProcess!!.modules["GuiltyGearXrd.exe"]!!.pointer
        var bufferMem = Memory(4L)
        var lastPointer: Pointer = procBaseAddr
        for (i in 0..offsets.size - 2) {
            val newPointer = Pointer(Pointer.nativeValue(lastPointer) + offsets[i])
            if (ReadProcessMemory(xrdProcess!!.handle.pointer, newPointer, bufferMem, 4, 0) == 0L) return null
            lastPointer = Pointer(bufferMem.getInt(0L).toUInt().toLong())
        }
        val dataAddr = Pointer(Pointer.nativeValue(lastPointer) + offsets[offsets.size - 1])
        bufferMem = Memory(numBytes.toLong())
        if (ReadProcessMemory(xrdProcess!!.handle.pointer, dataAddr, bufferMem, numBytes, 0) == 0L) return null
        return bufferMem.getByteBuffer(0L, numBytes.toLong())
    }

    override fun getFighterData(): List<FighterData> {
        if (!isConnected()) return ArrayList()
        val offs = longArrayOf(0x1C25AB4L, 0x44CL)
        val fighterDatas = ArrayList<FighterData>()
        for (i in 0..7) {
            val bb = getByteBuffFromAddr(offs, 0x48) ?: return ArrayList()
            val steamId = bb.getLong(0)
            val dispNameBytes = ByteArray(0x24)
            val characterId = bb.get(0x36)
            val matchesWon = bb.get(0xA).toInt()
            val matchesSum = bb.get(8).toInt()
            val loadingPct = bb.get(0x44).toInt()
            val cabinetId = bb.get(0x38)
            val seatingId = bb.get(0x39)
            bb.position(0xC)
            bb.get(dispNameBytes, 0, 0x24)
            val displayName = truncate(String(dispNameBytes).trim('\u0000'), 24)
            val fighterData = FighterData(
                steamId,
                displayName,
                characterId,
                cabinetId,
                seatingId,
                matchesWon,
                matchesSum,
                loadingPct
            )
            offs[1] += 0x48L
            fighterDatas.add(fighterData)
        }
        return fighterDatas
    }


    override fun getMatchData(): MatchData {
        val sortedStructOffs = longArrayOf(0x9CCL, 0x2888L, 0xA0F4L, 0x22960, 0x2AC64, 0x7AF4, 0x7AF8)
        val p1offs = longArrayOf(0x1B18C78L, 0L)
        val p2offs = longArrayOf(0x1B18C78L, 0L)
        p2offs[0] += 4L
        val p1roundoffset = longArrayOf(0x1A3BA38L)
        val p2roundoffset = longArrayOf(0x1A3BA3CL)
        val timeroffs = longArrayOf(0x177A8ACL, 0x450L, 0x4CL, 0x708L)
        try {
            p1offs[1] = sortedStructOffs[0]
            p2offs[1] = sortedStructOffs[0]
            val health = Pair(getByteBuffFromAddr(p1offs, 4)!!.int, getByteBuffFromAddr(p2offs, 4)!!.int)
            p1offs[1] = sortedStructOffs[1]
            p2offs[1] = sortedStructOffs[1]
            val strikeStun =
                Pair(getByteBuffFromAddr(p1offs, 4)!!.int == 1, getByteBuffFromAddr(p2offs, 4)!!.int == 1)
            p1offs[1] = sortedStructOffs[2]
            p2offs[1] = sortedStructOffs[2]
            val canBurst =
                Pair(getByteBuffFromAddr(p1offs, 4)!!.int == 1, getByteBuffFromAddr(p2offs, 4)!!.int == 1)
            p1offs[1] = sortedStructOffs[3]
            p2offs[1] = sortedStructOffs[3]
            val guardGauge = Pair(getByteBuffFromAddr(p1offs, 4)!!.int, getByteBuffFromAddr(p2offs, 4)!!.int)
            p1offs[1] = sortedStructOffs[4]
            p2offs[1] = sortedStructOffs[4]
            val tension = Pair(getByteBuffFromAddr(p1offs, 4)!!.int, getByteBuffFromAddr(p2offs, 4)!!.int)
            p1offs[1] = sortedStructOffs[5]
            p2offs[1] = sortedStructOffs[5]
            val stunProgress = Pair(getByteBuffFromAddr(p1offs, 4)!!.int, getByteBuffFromAddr(p2offs, 4)!!.int)
            p1offs[1] = sortedStructOffs[6]
            p2offs[1] = sortedStructOffs[6]
            val maxStun = Pair(getByteBuffFromAddr(p1offs, 4)!!.int * 100, getByteBuffFromAddr(p2offs, 4)!!.int * 100)
            val timer = getByteBuffFromAddr(timeroffs, 4)!!.int
            val rounds =
                Pair(getByteBuffFromAddr(p1roundoffset, 4)!!.int, getByteBuffFromAddr(p2roundoffset, 4)!!.int)
            return MatchData(timer, health, rounds, tension, stunProgress, maxStun, canBurst, strikeStun, guardGauge)
        } catch (e: NullPointerException) {
            return MatchData()
        }
    }

}