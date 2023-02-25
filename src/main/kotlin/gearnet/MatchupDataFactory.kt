package gearnet

import gearnet.GearNet.MatchupData
import gearnet.GearNet.PlayerData
import gearnet.GearNetShifter.Shift
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
                    // NOTE: INVALIDATE MATCHUPS WHEN cabinetId EXCEEDS 3 TO EXCLUDE TRAINING CAB
                    // NOTE: INVALIDATED MATCHUPS WITH A WINNER SHOULD BE ARCHIVED
                    val winner = if (oldMatch.winner != -1 && oldMatch.shift != Shift.GEAR_VICTORY) oldMatch.winner
                    else if (data1.matchesWon > oldData1.matchesWon && data2.matchesSum > oldData2.matchesSum) PLAYER_1
                    else if (data1.matchesWon > oldData1.matchesWon && data2.matchesSum > oldData2.matchesSum) PLAYER_2
                    else -1

                    val shift = when {
                        oldMatch.winner > -1 -> Shift.GEAR_VICTORY
                        data1.isLoading() || data2.isLoading() -> Shift.GEAR_LOADING
                        oldMatch.shift == Shift.GEAR_MATCH && oldMatch.winner == -1 -> Shift.GEAR_MATCH
                        oldMatch.shift == Shift.GEAR_LOADING && !data1.isLoading() && !data2.isLoading() -> Shift.GEAR_MATCH
                        else -> Shift.GEAR_LOBBY
                    }

                    if (data1.isOnCabinet(clientCabinet) && data2.isOnCabinet(clientCabinet)) {
                        if (data1.isSeated(PLAYER_1)) {
                            if (muList.none { it.equals(MatchupData(data1, data2, shift, winner, matchData.timer)) }) muList.add(
                                MatchupData(data1, data2, shift, winner, matchData.timer)
                            )
                        } else {
                            if (muList.none { it.equals(MatchupData(data2, data1, shift, winner, matchData.timer)) }) muList.add(
                                MatchupData(data2, data1, shift, winner, matchData.timer)
                            )
                        }
                    } else if (data1.isSeated(PLAYER_1)) {
                        if (muList.none { it.equals(
                                MatchupData(
                                    data1,
                                    data2,
                                    shift,
                                    -1,
                                    -1
                                )
                            ) }) muList.add(MatchupData(data1, data2, shift, winner, -1))
                    } else {
                        if (muList.none { it.equals(
                                MatchupData(
                                    data2,
                                    data1,
                                    shift,
                                    -1,
                                    -1
                                )
                            ) }) muList.add(MatchupData(data2, data1, shift, winner, -1))
                    }
                }
            }
        }
        return muList
    }

}