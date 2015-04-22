package tinker.goldilocks.model

/**
 * A temp probe with pin and target temp. This might just be a temp probe (e.g. for mash tun) or just a pin
 * (e.g. for turning on a pump).
 */
class Control {

    String id
    String name

    String tempProbe

    /** Matches names of constants in {@link com.pi4j.io.gpio.RaspiPin} */
    String pin

    String color

    Double targetTemp
    String pinState       // off, auto, on

    // temp control tuning parameters for when pinState == "auto"
    Double gainPerMin
    Integer lagTimeSecs
    Boolean autoTune

    // current values
    Double temp
    Boolean pinOn
    List<String> errors

    boolean empty() { !name && !tempProbe && !pin }
}
