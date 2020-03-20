package application.arcade

import javafx.application.Platform

interface ArcadeView {


    /**
     *
     */
    fun applyData() = Platform.runLater {}


    /**
     *
     */
    fun updateAnimation() = Platform.runLater {}


}