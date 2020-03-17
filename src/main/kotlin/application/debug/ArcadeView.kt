package application.debug

import javafx.application.Platform
import models.Player
import session.Session

interface ArcadeView {

    fun applyData(p: Player) = Platform.runLater {}
    fun applyData(s: Session) = Platform.runLater {}
    fun applyData(s: Session, index:Int = -1) = Platform.runLater {}
    fun updateAnimation(s: Session) = Platform.runLater {}

}