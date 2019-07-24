package models

abstract class PlayerData<T> {

    private val data: Pair<T,T>
    private val name: String
    private val id: Long

    constructor(oldData:T, newData:T, name:String, id:Long) {
        this.data = Pair(oldData, newData)
        this.name = name
        this.id = id
    }

    fun getId() = id
    fun getName() = name
    fun isValid() = id > 0
    fun oldData() = this.data.first
    fun getData() = this.data.second

}