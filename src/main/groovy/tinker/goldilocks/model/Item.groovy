package tinker.goldilocks.model

/**
 * A temp probe with pin and target temp. This might just be a temp probe (e.g. for mash tun) or just a pin
 * (e.g. for turning on a pump).
 */
class Item {

    int id
    String name

    String tempProbe

    /** Matches names of constants in {@link com.pi4j.io.gpio.RaspiPin} */
    String pin

    String color = "orange"

    Double targetTemp
    String pinState       // off, auto, on

    transient Double temp
    transient List<String> errors = []

    boolean empty() { !name && !tempProbe && !pin }
}
