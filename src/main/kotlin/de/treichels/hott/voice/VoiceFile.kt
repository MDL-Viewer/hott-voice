package de.treichels.hott.voice

import de.treichels.hott.model.enums.TransmitterType
import javafx.beans.InvalidationListener
import javafx.beans.Observable
import javafx.beans.binding.BooleanBinding
import javafx.beans.property.SimpleIntegerProperty
import javafx.beans.property.SimpleObjectProperty
import javafx.collections.FXCollections
import tornadofx.*

data class VoiceInfo(val vdfType: VDFType, val flashSize: Int, val sectorCount: Int, val sectorSize: Int, val voiceVersion: Int, val infoSize: Int, val maxDataSize: Int)
data class VoiceFileInfo(val vdfType: VDFType, val size: Int, val baseAddress: Int, val index: Int, val reverseIndex: Int, val sampleRate: Int, val name: String)

open class ObservableBase : Observable {
    // listener list
    private val listeners = mutableListOf<InvalidationListener>()

    override fun addListener(listener: InvalidationListener?) {
        if (listener != null) listeners.add(listener)
    }

    override fun removeListener(listener: InvalidationListener?) {
        if (listener != null) listeners.remove(listener)
    }

    fun invalidate() {
        listeners.forEach { it.invalidated(this) }
    }
}

open class VoiceFileBase(vdfType: VDFType, transmitterType: TransmitterType, vdfVersion: Int, country: CountryCode, list: List<VoiceData>) : ObservableBase(){
    // properties
    val vdfTypeProperty = SimpleObjectProperty<VDFType>(vdfType)
    val transmitterTypeProperty = SimpleObjectProperty<TransmitterType>(transmitterType)
    val vdfVersionProperty = SimpleIntegerProperty(vdfVersion)
    val countryProperty = SimpleObjectProperty<CountryCode>(country)
    val voiceList = list.asObservable()
    val sizeProperty = voiceList.sizeProperty
    val systemVDFProperty: BooleanBinding = vdfTypeProperty.isEqualTo(VDFType.System)

    // simple values
    var vdfType: VDFType by vdfTypeProperty
    var transmitterType: TransmitterType by transmitterTypeProperty
    var vdfVersion by vdfVersionProperty
    var country: CountryCode by countryProperty
    val rawDataSize: Int
        get() = voiceList.stream().mapToInt { it.rawData.size }.sum()
    val size by sizeProperty
    val isSystemVDF by systemVDFProperty

    // copy constructor
    constructor(other: VoiceFileBase) : this(other.vdfType, other.transmitterType, other.vdfVersion, other.country, ArrayList(other.voiceList.map(VoiceData::clone)))

    init {
        // add invalidation listeners - trigger events on bindings
        this.countryProperty.addListener { _ -> invalidate() }
        this.transmitterTypeProperty.addListener { _ -> invalidate() }
        this.vdfTypeProperty.addListener { _ -> invalidate() }
        this.vdfVersionProperty.addListener { _ -> invalidate() }
        this.voiceList.addListener { _: Observable -> invalidate() }
    }

    // copy state
    fun copy(other: VoiceFileBase) {
        this.vdfType = other.vdfType
        this.transmitterType = other.transmitterType
        this.vdfVersion = other.vdfVersion
        this.country = other.country
        voiceList.clear()
        voiceList.addAll(other.voiceList.map(VoiceData::clone))
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is VoiceFileBase) return false

        if (vdfType != other.vdfType) return false
        if (transmitterType != other.transmitterType) return false
        if (vdfVersion != other.vdfVersion) return false
        if (country != other.country) return false
        if (voiceList != other.voiceList) return false

        return true
    }

    override fun hashCode(): Int {
        var result = vdfType.hashCode()
        result = 31 * result + transmitterType.hashCode()
        result = 31 * result + vdfVersion
        result = 31 * result + country.hashCode()
        result = 31 * result + voiceList.hashCode()
        return result
    }
}

/**
 * Representation of a void data file (.vdf).
 *
 * @author oliver.treichel@gmx.de
 */
class VoiceFile(vdfType: VDFType = VDFType.User, transmitterType: TransmitterType = TransmitterType.mc28, vdfVersion: Int = 3000, country: CountryCode = CountryCode.GLOBAL,
                list: MutableList<VoiceData> = mutableListOf()) : VoiceFileBase(vdfType, transmitterType, vdfVersion, country, FXCollections.observableList(list)) {

    private val savedState: VoiceFileBase = VoiceFileBase(this)
    val dirtyBinding: BooleanBinding = booleanBinding(this, this, op = { !equals(savedState) })
    val isDirty by dirtyBinding

    fun clean() {
        savedState.copy(this)
        dirtyBinding.invalidate()
    }
}
