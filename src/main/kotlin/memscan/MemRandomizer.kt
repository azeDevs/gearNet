package memscan

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import utils.Duo
import utils.getRandomName
import utils.isInRange
import kotlin.math.max
import kotlin.random.Random

class MemRandomizer : XrdApi {

    private val clientBot = PlayerData(1234567890L, "Randomizer Bot", 25, 0, 0, 0, 0, 0)
    private val DELAY = 1024L
    private var timeSync = 0
    private var player1 = -1L
    private var player2 = -1L
    private val botMatch:MatchData = MatchData()
    private val botLobby:MutableMap<Long, PlayerData> = mutableMapOf(Pair(clientBot.steamUserId, clientBot))

    private fun advanceMatch() {
        // increment match timer
        // check if one of their Healths reached zero
        // pause and reset stats
    }

    init { randomEventLoop() }
    private fun randomEventLoop() {
        GlobalScope.launch { val seed = Random.nextInt(8)
            when (seed) {
                // Lobby AI
                0 -> { botJoinLobby() }
                1 -> { botLeaveLobby() }
                2 -> { botChangeLocation() }
                4 -> { botChangeCharacter() }
                // Match AI
                5 -> { botsLoadMatch() }
                6 -> { botTakeDamage() }
                7 -> { botBlockDamage() }
            }
            advanceMatch()
            delay(DELAY); randomEventLoop() }
    }

    private fun botJoinLobby() {
        if (botLobby.size == 8) return
        val botId = Random.nextLong(1000000000, 9999999999)
        botLobby.put(botId, PlayerData(botId, getRandomName(), Random.nextInt(25).toByte(), 7, 0, 0, 0, 0))
//        println("botJoinLobby ${botId}")
    }

    private fun botLeaveLobby() {
        val s = botLobby.values.toList().get(Random.nextInt(max(1, botLobby.size)))
        if (isClientBot(s) || isInMatch(s)) return
        botLobby.remove(s.steamUserId)
//        println("botLeaveLobby ${s.steamUserId}")
    }

    private fun botChangeLocation() {
        val s = pickRandomBotFromLobby()
        var seat: Duo<Int> = Duo(s.cabinetLoc.toInt(), s.playerSide.toInt())
        if (isInMatch(s)) return
        val seatedBots = botLobby.values.filter { isInRange(it.cabinetLoc.toInt(), 0, 3) && isInRange(it.playerSide.toInt(), 0, 1) }.toList()
        if (seatedBots.size > 0) {
            println("${s.steamUserId} has spotted a seated bot")
            seatedBots.forEach { seatBot ->
                when(seatBot.playerSide.toInt()) {
                    0 -> if (seatedBots.filter { botInP2 -> botInP2.cabinetLoc == seatBot.cabinetLoc && botInP2.playerSide.toInt() != 1 }.isEmpty()) {
                        seat = Duo(seatBot.cabinetLoc.toInt(), 1)
                        println("botChangeLocation ${s.steamUserId}")
                    }
                    1 -> if (seatedBots.filter { botInP2 -> botInP2.cabinetLoc == seatBot.cabinetLoc && botInP2.playerSide.toInt() != 0 }.isEmpty()) {
                        seat = Duo(seatBot.cabinetLoc.toInt(), 0)
                        println("botChangeLocation ${s.steamUserId}")
                    }
                }
            }
        }
        val t = PlayerData(s.steamUserId, s.displayName, s.characterId, seat.p1.toByte(), seat.p2.toByte(), s.matchesWon, s.matchesSum, s.loadingPct)
        botLobby.put(t.steamUserId, t)
    }



    private fun botChangeCharacter() {
        val s = botLobby.values.toList().get(Random.nextInt(max(1, botLobby.size)))
        val t = PlayerData(s.steamUserId, s.displayName, Random.nextInt(25).toByte(), s.cabinetLoc, s.playerSide, s.matchesWon, s.matchesSum, s.loadingPct)
        botLobby.put(t.steamUserId, t)
//        println("botChangeCharacter ${s.steamUserId}")
    }

    private fun botsLoadMatch() {
        // filter cabinets with 2 players seated
        // once they qualify, flag them as loading
//        println("botsLoadMatch -")
    }

    private fun botTakeDamage() {
        // reduce Health of one bot, increase Tension of both
        // reduce Risc, isHit = true
        // rare chance this will set burst false
//        println("botTakeDamage -")
    }

    private fun botBlockDamage() {
        // increase Tension of both bots
        // increase Risc, isHit = true
        // rare chance this will set burst true
//        println("botBlockDamage -")
    }

    private fun pickRandomBotFromLobby() = botLobby.values.toList().get(Random.nextInt(max(1, botLobby.size)))
    private fun getClientBot():PlayerData = botLobby[0] ?: clientBot
    private fun isClientBot(s: PlayerData) = s.steamUserId == getClientSteamId()
    private fun isInMatch(s: PlayerData) = s.steamUserId == player1 || s.steamUserId == player2

    override fun isConnected() = true
    override fun getClientSteamId() = getClientBot().steamUserId
    override fun getPlayerData() = botLobby.values.toList()
    override fun getMatchData() = botMatch
    override fun getLobbyData() = LobbyData()

}