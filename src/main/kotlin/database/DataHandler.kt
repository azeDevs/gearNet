package database

import models.Fighter
import models.Watcher

class DataHandler {

    val fighters: HashMap<Long, Fighter> = HashMap()
    val watchers: HashMap<Long, Watcher> = HashMap()

}