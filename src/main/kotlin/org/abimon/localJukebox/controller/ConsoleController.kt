package org.abimon.localJukebox.controller

object ConsoleController: IController {
    override var isActive: Boolean
        get() = true
        set(value) {}

    val SPACES_REGEX = "\\s+".toRegex()

    init {
        loop@ while (isActive) {
            print("> ")
            val line = readLine()?.split(SPACES_REGEX) ?: continue
            if(line.isEmpty())
                continue

            when(line[0].toLowerCase()) {
                "quit" -> break@loop
            }
        }
    }
}