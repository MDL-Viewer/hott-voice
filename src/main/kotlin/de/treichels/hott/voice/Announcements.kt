package de.treichels.hott.voice

import de.treichels.hott.messages.Messages
import de.treichels.hott.model.enums.TransmitterType
import java.util.*
import java.util.logging.Logger
import java.util.prefs.BackingStoreException
import java.util.prefs.Preferences

/**
 * A utility class to translate announcement number into names.
 *
 * The class also allows to load custom system and user void data files (.vdf) to read names from.
 *
 * @author oliver.treichel.gmx.de
 */
object Announcements {
    private val MC28_DEFAULT_ANNOUNCEMENTS = arrayOf("01.BEEP_START", "02.BEEP_SETUP", "03_BEEP_TIMER_GO", "04.BEEP_CLKBEEP3", "05.BEEP_CLKBEEP2", "06.BEEP_CLKBEEP1", "07.BEEP_CLKHIGH", "08.BEEP_CLKSEC", "09.BEEP_TRMCEN", "10.BEEP_TRMMOVE", "11.BEEP_JOG_VALUE", "12.BEEP_JOG_VALUE", "13.EEP_JOG_INCMOV", "14.BEEP_JOG_DECMO", "15.BEEP_JOG_UP", "16.BEEP_JOG_DN", "17.BEEP_WARNING", "18.BEEP_POWERDOWN", "19.BEEP_JOG_KEY", "20_wMinGeschwindi", "21_wSinkrate3s", "22_wSinkrate1s", "23_wMaxEntfernung", "24_wSchwacher_Tel", "25_wMinTempSensor", "26_wMinTempSensor", "27_wMaxTempSensor", "28_wMaxTempSensor", "29_wUeberspgSenso", "30_wUeberspgSenso", "31_wMaxGeschwindi", "32_wSteigrate3s", "33_wSteigrate1s", "34_wMinHoehe", "35_wMinEingangssp", "36_wMinZellenSpan", "37_wUnterspannung", "38_wUnterspannung", "39_wMinimalDrehza", "40_wKraftstoffres", "41_wKapazitaet", "42_wMaxStrom", "43_wMaxEingSpg", "44_wMaximalDrehza", "45_wMaxHoehe", "46_wAbruptesSinke", "47_wSchnellesSink", "48_wNormalesSinke", "49_wLangsamesSink", "50_wSehrLangSinke", "51_wSehrLangSteig", "52_wLangsamesStei", "53_wNormalesSteig", "54_wSchnellesStei", "55_wAbruptesSteig", "56_w20m", "57_w40m", "58_w60m", "59_w80m", "60_w100m", "61_w200m", "62_w400m", "63_w600m", "64_w800m", "65_w1000m", "66_wMaxServoTempe", "67_wMaxServoPosDi", "68_wEmpfnger", "69_wPriorBeep", "70_eSenderSpg", "71_eModellzeit", "72_eAkkuzeit", "73_eStoppuhr", "74_eBetriebszeit", "75_eUhr1", "76_eUhr2", "77_eUhr3", "78_eRundenzeit", "79_eCountdown", "80_eUpTimer", "81_eUhrzeit", "82_eServoTemperat", "83_eServoPosDiff", "84_eServoStrom", "85_eEmpfSpg", "86_eEmpfTemp", "87_eEmpfangsstaer", "88_eniedrEmpfSpg", "89_eUntereZelle1", "90_eUntereZelle2", "91_eUntereZelle3", "92_eUntereZelle4", "93_eUntereZelle5", "94_eUntereZelle6", "95_eUntereZelle7", "96_eObereZelle1", "97_eObereZelle2", "98_eObereZelle3", "99_eObereZelle4", "100_eObereZelle5", "101_eObereZelle6", "102_eObereZelle7", "103_eSensor1Spann", "104_eSensor2Spann", "105_eSensor1Temp", "106_eSensor2Temp", "107_eKraftstoff", "108_Hoehe_leer", "109_eMaxHoehe", "110_eMinHoehe", "111_eDrehzahl", "112_eSpannung", "113_eStrom", "114_eGeschwindigk", "115_eEntfernung", "116_eRichtung", "117_eHoehendiffer", "118_n0", "119_n1", "120_n2", "121_n3", "122_n4", "123_n5", "124_n6", "125_n7", "126_n8", "127_n9", "128_n10", "129_n11", "130_n12", "131_n13", "132_n14", "133_n15", "134_n16", "135_n17", "136_n18", "137_n19", "138_n20", "139_n21", "140_n22", "141_n23", "142_n24", "143_n25", "144_n26", "145_n27", "146_n28", "147_n29", "148_n30", "149_n31", "150_n32", "151_n33", "152_n34", "153_n35", "154_n36", "155_n37", "156_n38", "157_n39", "158_n40", "159_n41", "160_n42", "161_n43", "162_n44", "163_n45", "164_n46", "165_n47", "166_n48", "167_n49", "168_n50", "169_n51", "170_n52", "171_n53", "172_n54", "173_n55", "174_n56", "175_n57", "176_n58", "177_n59", "178_n60", "179_n61", "180_n62", "181_n63", "182_n64", "183_n65", "184_n66", "185_n67", "186_n68", "187_n69", "188_n70", "189_n71", "190_n72", "191_n73", "192_n74", "193_n75", "194_n76", "195_n77", "196_n78", "197_n79", "198_n80", "199_n81", "200_n82", "201_n83", "202_n84", "203_n85", "204_n86", "205_n87", "206_n88", "207_n89", "208_n90", "209_n91", "210_n92", "211_n93", "212_n94", "213_n95", "214_n96", "215_n97", "216_n98", "217_n99", "218_n100", "219_n1000", "220_uKomma", "221_uVolt", "222_uAmpere", "223_uDecibel", "224_uMillisekunde", "225_uGrad", "226_uProzent", "227_uUmin", "228_uMeter", "229_uMinus", "230_uPlus", "231_uKilometer", "232_uMeterproSek", "233_uMeterpro3Sek", "234_uMeterpro10Se", "235_uKilometerpro", "236_uUhr", "237_uMilliAh", "238_uMilliliter", "239_eMinuten_team", "240_eSekunden_tea", "241_uStunde", "242_eMotorlaufzei", "243_eLogzeit_empt", "244_eRahmenzeit", "245_eUhr1", "246_eUhr2", "247_eUhr3", "248_Satellitenzah", "249_Kompassrichtu", "250_Heimrichtung_", "251_Feet_22k", "252_Meilen_pro_St", "253_10000_22k", "254_Nickrichtung_", "255_Roll_Richtung", "256_Flugrichtung_", "257_Reglertempera", "258_maximale_Regl", "259_Motortemperat", "260_maximale_Moto", "261_Signalfehler_", "262_Fehler_Spannu", "263_Fehler_Motors", "264_Fehler_Regler", "265_Fehler_Motor_", "266_Fehler_Motort", "267_Strom_zu_hoch", "268_maximale Empf", "269_minimale_Empf", "270_Komm_nach_Hau", "271_Modus_Normal_", "272_GPS_Modus_22k", "273_Heimrichtungs", "274_Flugrichtungs", "275_Normal_22k", "276_Aufnahme_star", "277_Aufnahme_stop", "278_Sprache_Schie", "279_Landegestell_", "280_Landegestell_", "281_Phase_2_22k", "282_Phase_3_22k", "283_Phase_4_22k", "284_Autorotation_", "285_Kamerasteueru", "286_Flugsteuerung", "287_Drehratenmodu", "288_Lagemodus_22k", "289_Lagemodus_mit", "290_Autopilotmodu", "291_Automatische_", "292_Sprache_Panor", "293_schwacher_Tel", "294_Schwebegas_22", "295_Phase_1_22k", "296_Phase_5_22k", "297_Phase_6_22k", "298_Phase_7_22k", "299_Gier_Richtung", "300_Landegestell_", "301_links_22k", "302_rechts_22k", "303_Dualrate_1_22", "304_Dualrate_2_22", "305_Dualrate_3_22", "306_Expo_1_22k", "307_Expo_2_22k", "308_Expo_3_22k", "309_Boot_22k", "310_Fahrzeug_22k", "311_Flugzeug_22k", "312_Helikopter_22", "313_Schiff_22k", "314_Kopter_22k", "315_runter_22k", "316_Lehrer_22k", "317_Schueler_22k", "318_Alpha_22k", "319_Fail_safe_22k", "320_Fehler_Kalibr", "321_Daten_nicht_e", "322_Datenbusfehle", "323_Navigationsfe", "324_Fehler_22k", "325_Kompassfehler", "326_Sensorfehler_", "327_GPS_Fehler_22", "328_Motorfehler_2", "329_Temperaturfeh", "330_Hoehe_erreich", "331_Wegpunkt_erre", "332_naechster_Weg", "333_Landung_22k", "334_GPS_Fix_22k", "335_Unterspannung", "336_GPS_Position_", "337_GPS_aus_22k", "338_Multicopter_2", "339_carefree_an_2", "340_carefree_aus_", "341_Kalibrieren_2", "342_maximale_Entf", "343_maximale_Hoeh", "344_sinken_22k", "345_steigen_22k", "346_halten_22k", "347_GPS_an_22k", "348_folgen_22k", "349_starten_22k", "350_Fail_safe_Sic", "351_Redundanzfehl", "352_Motorueberlas", "353_Flugphase_22k", "354_Lenkmodus_22k", "355_Programm_22k", "356_Modus_22k", "357_Start_22k", "358_Start_2_22k", "359_Thermik_22k", "360_Thermik_2_22k", "361_Strecke_22k", "362_Strecke_2_22k", "363_Landung_22k", "364_Landung_2_22k", "365_Normal_22k", "366_Akro_22k", "367_Akro_2_22k", "368_Akro_3D_22k", "369_Schnellflug_2", "370_Schnellflug_2", "371_Schleppflug_2", "372_Test_22k", "373_Test_2_22k", "374_Autorotation_", "375_Schweben_22k", "376_Schweben_2_22", "377_Lenkung_norma", "378_Allradlenkung", "379_Lenkung_vorne", "380_Lenkung_hinte", "381_Lenkung_umgek", "382_Gyro_an_22k", "383_Gyro_aus_22k", "384_ABS_Bremse_an", "385_ABS_Bremse_au", "386_Dualrate_an_2", "387_Dualrate_aus_", "388_Expo_an_22k", "389_Expo_aus_22k", "390_Fahrwerk_ein_", "391_Fahrwerk_aus_", "392_Klappen_ein_2", "393_Klappen_Start", "394_Klappen_Anflu", "395_Klappen_Landu", "396_Motor_an_22k", "397_Motor_aus_22k", "398_Stroemungsabr", "399_Landegeschwin", "400_Fische_22k", "401_Multikanal_1_", "402_Multikanal_2_", "403_Multikanal_3_", "404_Multikanal_4_", "405_Multikanal_5_", "406_Multikanal_6_", "407_Multikanal_7_", "408_Multikanal_8_", "409_Multikanal_9_", "410_Multikanal_10", "411_Multikanal_11", "412_Multikanal_12", "413_Multikanal_13", "414_Multikanal_14", "415_Multikanal_15", "416_Multikanal_16", "417_Multikanal_1_", "418_Multikanal_2_", "419_Multikanal_3_", "420_Multikanal_4_", "421_Multikanal_5_", "422_Multikanal_6_", "423_Multikanal_7_", "424_Multikanal_8_", "425_Multikanal_9_", "426_Multikanal_10", "427_Multikanal_11", "428_Multikanal_12", "429_Multikanal_13", "430_Multikanal_14", "431_Multikanal_15", "432_Multikanal_16")
    private val MC32_DEFAULT_ANNOUNCEMENTS = arrayOf("01.BEEP_START", "02.BEEP_SETUP", "03_BEEP_TIMER_GO", "04.BEEP_CLKBEEP3", "05.BEEP_CLKBEEP2", "06.BEEP_CLKBEEP1", "07.BEEP_CLKHIGH", "08.BEEP_CLKSEC", "09.BEEP_TRMCEN", "10.BEEP_TRMMOVE", "11.BEEP_JOG_VALUE", "12.BEEP_JOG_VALUE", "13.EEP_JOG_INCMOV", "14.BEEP_JOG_DECMO", "15.BEEP_JOG_UP", "16.BEEP_JOG_DN", "17.BEEP_WARNING", "18.BEEP_POWERDOWN", "19.BEEP_JOG_KEY", "20_wMinGeschwindi", "21_wSinkrate3s", "22_wSinkrate1s", "23_wMaxEntfernung", "24_wSchwacher_Tel", "25_wMinTempSensor", "26_wMinTempSensor", "27_wMaxTempSensor", "28_wMaxTempSensor", "29_wUeberspgSenso", "30_wUeberspgSenso", "31_wMaxGeschwindi", "32_wSteigrate3s", "33_wSteigrate1s", "34_wMinHoehe", "35_wMinEingangssp", "36_wMinZellenSpan", "37_wUnterspannung", "38_wUnterspannung", "39_wMinimalDrehza", "40_wKraftstoffres", "41_wKapazitaet", "42_wMaxStrom", "43_wMaxEingSpg", "44_wMaximalDrehza", "45_wMaxHoehe", "46_wAbruptesSinke", "47_wSchnellesSink", "48_wNormalesSinke", "49_wLangsamesSink", "50_wSehrLangSinke", "51_wSehrLangSteig", "52_wLangsamesStei", "53_wNormalesSteig", "54_wSchnellesStei", "55_wAbruptesSteig", "56_w20m", "57_w40m", "58_w60m", "59_w80m", "60_w100m", "61_w200m", "62_w400m", "63_w600m", "64_w800m", "65_w1000m", "66_wMaxServoTempe", "67_wMaxServoPosDi", "68_wEmpfänger", "69_wPriorBeep", "70_eSenderSpg", "71_eModellzeit", "72_eAkkuzeit", "73_eStoppuhr", "74_eBetriebszeit", "75_eUhr1", "76_eUhr2", "77_eUhr3", "78_eRundenzeit", "79_eCountdown", "80_eUpTimer", "81_eUhrzeit", "82_eServoTemperat", "83_eServoPosDiff", "84_eServoStrom", "85_eEmpfSpg", "86_eEmpfTemp", "87_eEmpfangsstaer", "88_eniedrEmpfSpg", "89_eUntereZelle1", "90_eUntereZelle2", "91_eUntereZelle3", "92_eUntereZelle4", "93_eUntereZelle5", "94_eUntereZelle6", "95_eUntereZelle7", "96_eObereZelle1", "97_eObereZelle2", "98_eObereZelle3", "99_eObereZelle4", "100_eObereZelle5", "101_eObereZelle6", "102_eObereZelle7", "103_eSensor1Spann", "104_eSensor2Spann", "105_eSensor1Temp", "106_eSensor2Temp", "107_eKraftstoff", "108_Hoehe_leer", "109_eMaxHoehe", "110_eMinHoehe", "111_eDrehzahl", "112_eSpannung", "113_eStrom", "114_eGeschwindigk", "115_eEntfernung", "116_eRichtung", "117_eHoehendiffer", "118_n0", "119_n1", "120_n2", "121_n3", "122_n4", "123_n5", "124_n6", "125_n7", "126_n8", "127_n9", "128_n10", "129_n11", "130_n12", "131_n13", "132_n14", "133_n15", "134_n16", "135_n17", "136_n18", "137_n19", "138_n20", "139_n21", "140_n22", "141_n23", "142_n24", "143_n25", "144_n26", "145_n27", "146_n28", "147_n29", "148_n30", "149_n31", "150_n32", "151_n33", "152_n34", "153_n35", "154_n36", "155_n37", "156_n38", "157_n39", "158_n40", "159_n41", "160_n42", "161_n43", "162_n44", "163_n45", "164_n46", "165_n47", "166_n48", "167_n49", "168_n50", "169_n51", "170_n52", "171_n53", "172_n54", "173_n55", "174_n56", "175_n57", "176_n58", "177_n59", "178_n60", "179_n61", "180_n62", "181_n63", "182_n64", "183_n65", "184_n66", "185_n67", "186_n68", "187_n69", "188_n70", "189_n71", "190_n72", "191_n73", "192_n74", "193_n75", "194_n76", "195_n77", "196_n78", "197_n79", "198_n80", "199_n81", "200_n82", "201_n83", "202_n84", "203_n85", "204_n86", "205_n87", "206_n88", "207_n89", "208_n90", "209_n91", "210_n92", "211_n93", "212_n94", "213_n95", "214_n96", "215_n97", "216_n98", "217_n99", "218_n100", "219_n1000", "220_uKomma", "221_uVolt", "222_uAmpere", "223_uDecibel", "224_uMillisekunde", "225_uGrad", "226_uProzent", "227_uUmin", "228_uMeter", "229_uMinus", "230_uPlus", "231_uKilometer", "232_uMeterproSek", "233_uMeterpro3Sek", "234_uMeterpro10Se", "235_uKilometerpro", "236_uUhr", "237_uMilliAh", "238_uMilliliter", "239_eMinuten_team", "240_eSekunden_tea", "241_uStunde", "242_eMotorlaufzei", "243_eLogzeit_empt", "244_eRahmenzeit", "245_eUhr1", "246_eUhr2", "247_eUhr3", "248_Satellitenzah", "249_Kompassrichtu", "250_Heimrichtung_", "251_Feet_22k", "252_Meilen_pro_St", "253_10000_22k", "254_Normal_22k", "255_halten_22k", "256_idle_up_1_22k", "257_idle_up_2_22k", "258_Akro_22k", "259_Start_22k", "260_Strecke_22k", "261_Schnellflug_2", "262_Landung_22k", "263_Schweben_22k", "264_Akro_3D_22k", "265_Thermik_22k", "266_Start_2_22k", "267_cruisen_22k", "268_Lehrer_22k", "269_Schueler_22k", "270_Flipfunktion_", "271_Autorotation_", "272_Schleppflug_2", "273_Hohe_Raten_22", "274_Mittlere_Rate", "275_Niedrige_Rate", "276_Lagemodus_22k", "277_Drehratenmodu", "278_Fahrwerk_ein_", "279_Fahrwerk_aus_", "280_Klappen_ein_2", "281_Bremse_ein_22", "282_Bremse_aus_22", "283_Motor_an_22k", "284_Motor_aus_22k")

    /** Preferences node to store names  */
    private val PREFS = Preferences.userNodeForPackage(Announcements::class.java)
    private val logger: Logger = Logger.getLogger(javaClass.name)

    private const val USER_VOICE_FILE = "Announcements.USER_VOICE_FILE"
    private const val SYSTEM_VOICE_FILE = "Announcements.SYSTEM_VOICE_FILE"
    private const val VOICE_COUNT = "%s_%sVoiceCount"
    private const val VOICE_FILE = "%s_%sVoiceFile_%d"

    fun getAnnouncement(transmitterType: TransmitterType, index: Int): String {
        val sysCount = getSystemPrefSize(transmitterType)
        return if (index >= sysCount) getUserAnnouncement(transmitterType, index - sysCount) else getSystemAnnouncement(transmitterType, index)
    }

    private fun getAnnouncement(vdfType: VDFType, transmitterType: TransmitterType, index: Int): String {
        return PREFS.get(getFileKey(vdfType, transmitterType, index), getDefaultFileName(vdfType, index))
    }

    /**
     * Preferences key for the number of announcements.
     *
     * @param vdfType
     * Type of announcements
     * @param transmitterType
     * Type of transmitter
     * @return The name of the key in the preferences node
     */
    private fun getCountKey(vdfType: VDFType, transmitterType: TransmitterType): String {
        return String.format(VOICE_COUNT, transmitterType.name, vdfType.name)
    }

    /**
     * Default file name in case of missing preferences.
     *
     * @param vdfType
     * Type of announcements
     * @param index
     * The index of the announcement
     * @return The default file name
     */
    private fun getDefaultFileName(vdfType: VDFType, index: Int): String {
        return Messages.getString(if (vdfType === VDFType.System) SYSTEM_VOICE_FILE else USER_VOICE_FILE, index + 1)
    }

    /**
     * Preference key for the announcements.
     *
     * @param vdfType
     * Type of announcements
     * @param transmitterType
     * Type of transmitter
     * @param index
     * The index of the announcement
     * @return The announcement key in the preference node
     */
    private fun getFileKey(vdfType: VDFType, transmitterType: TransmitterType, index: Int): String {
        return String.format(VOICE_FILE, transmitterType.name, vdfType.name, index)
    }

    fun getIndexOf(transmitterType: TransmitterType, announcement: String): Int {
        val sysCount = getPrefSize(VDFType.System, transmitterType)

        // first try system announcements
        var index = getIndexOf(VDFType.System, transmitterType, announcement)

        if (index == -1) {
            // if no system announcement was found try user announcements
            index = getIndexOf(VDFType.User, transmitterType, announcement)

            // index of user announcements starts after system announcements
            if (index != -1) index += sysCount
        }

        return index // -1 => not found
    }

    private fun getIndexOf(vdfType: VDFType, transmitterType: TransmitterType, announcement: String): Int {
        return loadPrefs(vdfType, transmitterType).indexOf(announcement)
    }

    private fun getPrefSize(vdfType: VDFType, transmitterType: TransmitterType): Int {
        return PREFS.getInt(getCountKey(vdfType, transmitterType), 0)
    }

    private fun getSystemAnnouncement(transmitterType: TransmitterType, index: Int): String {
        return getAnnouncement(VDFType.System, transmitterType, index)
    }

    private fun getSystemPrefSize(transmitterType: TransmitterType): Int {
        var sysCount = getPrefSize(VDFType.System, transmitterType)

        // no preferences? load defaults
        if (sysCount == 0)
            when (transmitterType) {
                TransmitterType.mc16, TransmitterType.mc20, TransmitterType.mc32, TransmitterType.mx12, TransmitterType.mx16, TransmitterType.mx20 -> try {
                    saveSystemPrefs(transmitterType, Arrays.asList(*MC32_DEFAULT_ANNOUNCEMENTS))
                    sysCount = MC32_DEFAULT_ANNOUNCEMENTS.size
                } catch (e: BackingStoreException) {
                    logger.throwing(javaClass.name, "getSystemPrefSize", e)
                }

                TransmitterType.mc26, TransmitterType.mc28 -> try {
                    saveSystemPrefs(transmitterType, Arrays.asList(*MC28_DEFAULT_ANNOUNCEMENTS))
                    sysCount = MC28_DEFAULT_ANNOUNCEMENTS.size
                } catch (e: BackingStoreException) {
                    logger.throwing(javaClass.name, "getSystemPrefSize", e)
                }

                TransmitterType.mz12, TransmitterType.mz18, TransmitterType.mz24 -> {
                }

                else -> {
                }
            }

        return sysCount
    }

    private fun getUserAnnouncement(transmitterType: TransmitterType, index: Int): String {
        return getAnnouncement(VDFType.User, transmitterType, index)
    }

    fun getUserPrefSize(transmitterType: TransmitterType): Int {
        return getPrefSize(VDFType.User, transmitterType)
    }

    /**
     * Load both, system announcements and user announcements from preferences and put them into one list.
     *
     * @param transmitterType
     * Type of transmitter
     * @return The announcements
     */
    private fun loadPrefs(transmitterType: TransmitterType): List<String> {
        val announcements = loadSystemPrefs(transmitterType)
        announcements.addAll(loadUserPrefs(transmitterType))
        return announcements
    }

    /**
     * Load announcements of the given type from preferences.
     *
     * @param vdfType
     * Type of announcements
     * @param transmitterType
     * Type of transmitter
     * @return The announcements
     */
    private fun loadPrefs(vdfType: VDFType, transmitterType: TransmitterType): MutableList<String> {
        val announcements = mutableListOf<String>()
        val count = getPrefSize(vdfType, transmitterType)

        (0 until count).mapTo(announcements) { getAnnouncement(vdfType, transmitterType, it) }

        return announcements
    }

    /**
     * Load system announcements from preferences.
     *
     * @param transmitterType
     * Type of transmitter
     * @return The announcements
     */
    private fun loadSystemPrefs(transmitterType: TransmitterType): MutableList<String> {
        return loadPrefs(VDFType.System, transmitterType)
    }

    /**
     * Load user announcements from preferences.
     *
     * @param transmitterType
     * Type of transmitter
     * @return The announcements
     */
    private fun loadUserPrefs(transmitterType: TransmitterType): List<String> {
        return loadPrefs(VDFType.User, transmitterType)
    }

    /**
     * Save system announcements and user announcements into preferences.
     *
     * @param transmitterType
     * Type of transmitter
     * @param systemAnnouncements
     * The system announcements
     * @param userAnnouncements
     * The user announcements
     * @throws BackingStoreException
     */
    @Throws(BackingStoreException::class)
    private fun savePrefs(transmitterType: TransmitterType, systemAnnouncements: List<String>, userAnnouncements: List<String>) {
        saveSystemPrefs(transmitterType, systemAnnouncements)
        saveUserPrefs(transmitterType, userAnnouncements)
    }

    /**
     * Save announcements of the given type into prefenrences
     *
     * @param vdfType
     * The announcement type
     * @param transmitterType
     * Type of transmitter
     * @param announcements
     * The announcements
     * @throws BackingStoreException
     */
    @Throws(BackingStoreException::class)
    private fun savePrefs(vdfType: VDFType, transmitterType: TransmitterType, announcements: List<String>) {
        val count = announcements.size

        PREFS.putInt(getCountKey(vdfType, transmitterType), count)
        for (i in 0 until count)
            PREFS.put(getFileKey(vdfType, transmitterType, i), announcements[i])

        // remove extra entries
        var i = count
        while (true) {
            val key = getFileKey(vdfType, transmitterType, i)
            if (PREFS.get(key, null) == null) break

            PREFS.remove(key)
            i++
        }

        PREFS.flush()
    }

    /**
     * Save system announcements into preferences.
     *
     * @param transmitterType
     * Type of transmitter
     * @param announcements
     * The system announcements
     * @throws BackingStoreException
     */
    @Throws(BackingStoreException::class)
    fun saveSystemPrefs(transmitterType: TransmitterType, announcements: List<String>) {
        savePrefs(VDFType.System, transmitterType, announcements)
    }

    /**
     * Save user announcements into preferences.
     *
     * @param transmitterType
     * Type of transmitter
     * @param announcements
     * The user announcements
     * @throws BackingStoreException
     */
    @Throws(BackingStoreException::class)
    fun saveUserPrefs(transmitterType: TransmitterType, announcements: List<String>) {
        savePrefs(VDFType.User, transmitterType, announcements)
    }
}
