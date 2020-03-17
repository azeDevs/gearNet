package memscan

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import models.Fighter
import session.Character.getCharacterInitials
import utils.Duo
import utils.getIdString
import utils.getRandomName
import utils.isInRange
import kotlin.math.abs
import kotlin.math.max
import kotlin.random.Random

class MemRandomizer : XrdApi {

    private val clientBot = FighterData(1234567890L, "Randomizer Bot", 25, 0, 0, 0, 0, 0)
    private val delay = 512L
    private var player1 = -1L
    private var player2 = -1L
    private val botMatch:MatchData = MatchData()
    private val botLobby:MutableMap<Long, FighterData> = mutableMapOf(Pair(clientBot.steamUserId, clientBot))

    private fun advanceMatch() {
        // increment match timer
        // check if one of their Healths reached zero
        // pause and reset stats
    }

    init { randomEventLoop() }
    private fun randomEventLoop() {
        GlobalScope.launch {
            when (Random.nextInt(10)) {
                // XrdLobby AI
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
            delay(delay); randomEventLoop() }
    }

    private fun botJoinLobby() {
        if (botLobby.size >= 8) return
        val botId = Random.nextLong(1000000000, 9999999999)
        val displayName = getRandomName()
        botLobby[botId] = FighterData(botId, displayName, Random.nextInt(25).toByte(), 7, 0, 0, 0, 0)
        println("B: $displayName [${getIdString(botId)}] has joined the lobby")
    }

    private fun botLeaveLobby() {
        val s = botLobby.values.toList()[Random.nextInt(max(1, botLobby.size))]
        if (isClientBot(s) || isInMatch(s)) return
        botLobby.remove(s.steamUserId)
        println("B: ${s.displayName} [${getIdString(s.steamUserId)}] has left the lobby")
    }

    private fun botChangeLocation() {
        val s = pickRandomBotFromLobby()
        if (isInMatch(s)) return
        val seat: Duo<Int> = Duo(s.cabinetLoc.toInt(), s.playerSide.toInt())
        val seatedBots = botLobby.values.filter { it.steamUserId != s.steamUserId && isInRange(it.cabinetLoc.toInt(), 0, 3) && isInRange(it.playerSide.toInt(), 0, 1) }.toList()
        if (seatedBots.isNotEmpty()) {
            val sightedBot = seatedBots[Random.nextInt(seatedBots.size)]
            println("B: ${s.displayName} [${getIdString(s.steamUserId)}] has spotted a seated bot...${sightedBot.displayName} [${getIdString(sightedBot.steamUserId)}]")

            if (botLobby.values.none { it.cabinetLoc == sightedBot.cabinetLoc && it.playerSide.toInt() == abs(sightedBot.playerSide.toInt() - 1) }) {
                seat.p1 = sightedBot.cabinetLoc.toInt()
                seat.p2 = abs(sightedBot.playerSide.toInt() - 1)
                println("B: ${s.displayName} [${getIdString(s.steamUserId)}] has moved to cab ${Fighter(
                    sightedBot
                ).getCabinetString()}, spot ${Fighter(sightedBot).getPlaySideString(0, seat.p2)}")
            } else {
                println("TODO: Bot searched for open seats, but that hasn't been implemented yet")
            }

        }
        val t = FighterData(s.steamUserId, s.displayName, s.characterId, seat.p1.toByte(), seat.p2.toByte(), s.matchesWon, s.matchesSum, s.loadingPct)
        botLobby[t.steamUserId] = t
    }



    private fun botChangeCharacter() {
        val s = botLobby.values.toList()[Random.nextInt(max(1, botLobby.size))]
        val t = FighterData(s.steamUserId, s.displayName, Random.nextInt(25).toByte(), s.cabinetLoc, s.playerSide, s.matchesWon, s.matchesSum, s.loadingPct)
        botLobby[t.steamUserId] = t
        println("B: ${s.displayName} [${getIdString(s.steamUserId)}] changed characters from ${getCharacterInitials(s.characterId)} to ${getCharacterInitials(t.characterId)}")
    }

    private fun botsLoadMatch() {
        // filter cabinets with 2 players seated
        // once they qualify, flag them as loading
//        log("botsLoadMatch -")
    }

    private fun botTakeDamage() {
        // reduce Health of one bot, increase Tension of both
        // reduce Risc, isHit = true
        // rare chance this will set burst false
//        log("botTakeDamage -")
    }

    private fun botBlockDamage() {
        // increase Tension of both bots
        // increase Risc, isHit = true
        // rare chance this will set burst true
//        log("botBlockDamage -")
    }

    private fun pickRandomBotFromLobby() = botLobby.values.toList()[Random.nextInt(max(1, botLobby.size))]
    private fun getClientBot():FighterData = botLobby[0] ?: clientBot
    private fun isClientBot(s: FighterData) = s.steamUserId == getClientSteamId()
    private fun isInMatch(s: FighterData) = s.steamUserId == player1 || s.steamUserId == player2

    override fun isConnected() = true
    override fun getClientSteamId() = getClientBot().steamUserId
    override fun getFighterData() = botLobby.values.toList()
    override fun getMatchData() = botMatch

}