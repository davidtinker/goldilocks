package tinker.mashtemp.model

/**
 * A mash tun or HLT.
 */
class Vessel {

    int id
    String name
    String tempProbe
    /** Matches names of constants in {@link com.pi4j.io.gpio.RaspiPin} */
    String heaterPin

    Double targetTemp
    String heater       // off, auto, on

    transient Double temp
    transient String tempError
    transient String heaterError

    void convertToF() {
        if (temp != null) temp = tof(temp)
        if (targetTemp != null) targetTemp = tof(temp)
    }

    private double tof(double c) { c * 9 / 5 + 32 }

}
