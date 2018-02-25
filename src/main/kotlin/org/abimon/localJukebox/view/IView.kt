package org.abimon.localJukebox.view

import org.abimon.localJukebox.LocalJukebox
import org.abimon.localJukebox.controller.IController
import org.abimon.localJukebox.model.IModel

interface IView {
    val model: IModel
        get() = LocalJukebox.model
    val controller: IController
        get() = LocalJukebox.controller


    var isActive: Boolean
}