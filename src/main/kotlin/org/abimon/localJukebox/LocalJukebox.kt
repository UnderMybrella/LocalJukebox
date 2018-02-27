package org.abimon.localJukebox

import org.abimon.localJukebox.controller.ConsoleController
import org.abimon.localJukebox.controller.IController
import org.abimon.localJukebox.model.IModel
import org.abimon.localJukebox.model.JukeboxDriver
import org.abimon.localJukebox.view.IView
import org.abimon.localJukebox.view.SystemAudioOutputView

object LocalJukebox {
    lateinit var model: IModel
    lateinit var views: Array<IView>
    lateinit var controller: IController

    @JvmStatic
    fun main(args: Array<String>) {
        setup(args)
    }

    fun setup(args: Array<String>) {
        model = JukeboxDriver
        views = arrayOf(SystemAudioOutputView)
        controller = ConsoleController
    }
}