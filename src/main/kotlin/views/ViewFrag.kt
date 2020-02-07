package views

import tornadofx.runLater

interface ViewFrag {

    data class Data(val tag: String, val value: String)

    fun update(data: Data) = runLater { }

}


