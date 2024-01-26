package de.treichels.hott.voice

import de.treichels.hott.util.get
import java.util.*

enum class VDFType {
    System, User;

    override fun toString(): String = ResourceBundle.getBundle(javaClass.name)[name]
}