package org.abimon.localJukebox.controller

import org.abimon.localJukebox.LocalJukebox
import org.abimon.localJukebox.model.IModel
import org.abimon.localJukebox.view.IView

interface IController {
    val model: IModel
        get() = LocalJukebox.model
    val views: Array<IView>
        get() = LocalJukebox.views

    var isActive: Boolean
}