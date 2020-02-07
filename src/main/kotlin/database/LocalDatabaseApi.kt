package database

class LocalDatabaseApi : SqlApi {

    override fun isConnected(): Boolean {
        TODO("not implemented")
    }

    override fun getLegacyData(steamId: Long): LegacyData {
        TODO("not implemented")
    }

    override fun putLegacyData(legacy: LegacyData) {
        TODO("not implemented")
    }

    override fun getFightData(): List<FightData> {
        TODO("not implemented")
    }

    override fun putFightData(fight: FightData) {
        TODO("not implemented")
    }

}