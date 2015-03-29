package tinker.goldilocks.model
/**
 * A mash tun or HLT.
 */
class Vessel {

    int id
    String name
    String tempProbe
    /** Matches names of constants in {@link com.pi4j.io.gpio.RaspiPin} */
    String heaterPin

    String colorScheme = "orange"

    Double targetTemp
    String heater       // off, auto, on

    transient Double temp
    transient String tempError
    transient String heaterError
}
