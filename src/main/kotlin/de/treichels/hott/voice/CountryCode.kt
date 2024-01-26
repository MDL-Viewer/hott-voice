package de.treichels.hott.voice

import de.treichels.hott.util.get
import java.util.*

enum class CountryCode(val code: Int) {
    GLOBAL(0), US(1), KR(2), CN(3), JP(4);

    override fun toString(): String = ResourceBundle.getBundle(javaClass.name)[name]

    companion object {
        @JvmStatic
        fun forCode(countryCode: Int): CountryCode = values().find { it.code == countryCode } ?: GLOBAL
    }
}
