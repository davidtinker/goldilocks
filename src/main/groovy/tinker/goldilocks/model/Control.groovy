package tinker.goldilocks.model

/**
 * A temp probe with pin and target temp. This might just be a temp probe (e.g. for mash tun) or just a pin
 * (e.g. for turning on a pump).
 */
class Control {

    int id
    String name

    String tempProbe

    /** Matches names of constants in {@link com.pi4j.io.gpio.RaspiPin} */
    String pin

    String color

    Double targetTemp
    String pinState       // off, auto, on

    // PID control parameters for when pinState == "auto"
    double kc   // Controller gain
    double ti   // Time-constant for I action
    double td   // Time-constant for D action

    // current values
    Double temp
    Boolean pinOn
    List<String> errors

    boolean empty() { !name && !tempProbe && !pin }
}
