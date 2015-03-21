package tinker.mashtemp.model

/**
 * A mash tun or HLT.
 */
class Vessel {

    String id
    String name
    String tempProbeId
    /** Matches names of constants in {@link com.pi4j.io.gpio.RaspiPin} */
    String heaterPin

    Double targetTemp
    String heater       // off, auto, on

    transient Double temp
    transient String tempError
    transient String heaterError

}
