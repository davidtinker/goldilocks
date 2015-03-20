package tinker.mashtemp.model

/**
 * A mash tun or HLT.
 */
class Vessel {

    String name
    String tempProbeId
    /** Matches names of constants in {@link com.pi4j.io.gpio.RaspiPin} */
    String heaterPin

    transient Double temp
    transient String tempError

    transient Boolean heaterOn
    transient String heaterError

}
