package memscan

import memscan.GearNet.MatchupData
import memscan.GearNet.PlayerData
import models.Player.Companion.PLAYER_1
import models.Player.Companion.PLAYER_2

class MatchupDataFactory {

    /**
     *
     */
    fun getMatchupData(dataList: MutableList<PlayerData>, matchData: MatchData, frameData: GearNetFrameData, clientCabinet: Int): List<MatchupData> {
        val muList: MutableList<MatchupData> = mutableListOf()
        dataList.forEach { data1 ->
            dataList.forEach { data2 ->

                if (data1.steamId == data2.opponentId && data1.opponentId == data2.steamId) {

                    // TODO: CHECK matchesSum TO DECLARE A WINNER
                    val oldData1 = frameData.lastFrame().playerData.firstOrNull { it.steamId == data1.steamId } ?: PlayerData()
                    val oldData2 = frameData.lastFrame().playerData.firstOrNull { it.steamId == data2.steamId } ?: PlayerData()
                    val oldMatch = frameData.lastFrame().matchupData.firstOrNull { (it.player1.steamId == oldData1.steamId && it.player2.steamId == oldData2.steamId) || (it.player1.steamId == oldData2.steamId && it.player2.steamId == oldData1.steamId) } ?: MatchupData()
                    // FIXME: THIS IS CHAOS, PLS FIX
                    val winner = if (oldMatch.winner != -1) oldMatch.winner
                    else if (data1.matchesWon > oldData1.matchesWon && data2.matchesSum > oldData2.matchesSum) PLAYER_1
                    else if (data1.matchesWon > oldData1.matchesWon && data2.matchesSum > oldData2.matchesSum) PLAYER_2
                    else -1

                    if (data1.isOnCabinet(clientCabinet) && data2.isOnCabinet(clientCabinet)) {
                        if (data1.isSeatedAt(PLAYER_1)) {
                            if (muList.none { it.equals(MatchupData(data1, data2, winner, matchData.timer)) }) muList.add(
                                MatchupData(data1, data2, winner, matchData.timer)
                            )
                        } else {
                            if (muList.none { it.equals(MatchupData(data2, data1, winner, matchData.timer)) }) muList.add(
                                MatchupData(data2, data1, winner, matchData.timer)
                            )
                        }
                    } else if (data1.isSeatedAt(PLAYER_1)) {
                        if (muList.none { it.equals(
                                MatchupData(
                                    data1,
                                    data2
                                )
                            ) }) muList.add(MatchupData(data1, data2, winner))
                    } else {
                        if (muList.none { it.equals(
                                MatchupData(
                                    data2,
                                    data1
                                )
                            ) }) muList.add(MatchupData(data2, data1, winner))
                    }
                }
            }
        }
        return muList
    }

}