package application.views.fighters

import session.Fighter
import tornadofx.Controller

class DebugFighterController(val fighter: Fighter) : Controller() {
//    val bountyProp = observable(fighter, Fighter::getBounty, Fighter::setBounty)
//    val deltaProp = observable(fighter, Fighter::getDelta, Fighter::setDelta)

    fun save(fighter: Fighter) = println("FIGHTER ${fighter.getName()} SAVED")
}