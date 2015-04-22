package tinker.goldilocks

/**
 * Temperature control algorithm. Attempts to hit a set point temperature by turning a tank heater on and off in
 * response to the measured temperature in the tank. Can auto tune its parameters.
 */
class TempController {

    /** How fast does the temp in the tank rise in degrees C / min when the heater is on? */
    double gainPerMin

    /** How long in seconds does it take for the temp to start rising after the heater is turned on? */
    int lagTimeSecs

    /** Adjust {@link #gainPerMin} and {@link #lagTimeSecs} automatically. */
    boolean autoTune

    double pendingTempIncrease      // temp gain we still expect to see that hasn't happened yet due to lag time
    double prevTemp                 // measured temp at previous tick
    int heaterOnSecs                // how long has the heater been on for?
    double heaterOnStartTemp        // measured temp when the heater was turned on

    /**
     * Should the heater be on or off? This method should be called once per second.
     *
     * @param measuredTemp of the tank
     * @param heaterOn current state of the heater
     * @param targetTemp temp we are trying to hit
     */
    boolean tick(double measuredTemp, boolean heaterOn, double targetTemp) {
        if (prevTemp == 0.0) prevTemp = measuredTemp

        if (heaterOn) {
            if (++heaterOnSecs == 1) heaterOnStartTemp = measuredTemp
        } else {
            heaterOnSecs = 0
        }

        if (autoTune) {
            // todo re-calc lag time when temp has started rising
            // todo re-calc gain (and hence pendingTempIncrease) if lag is over and temp has risen by 10 degrees
        }

        if (heaterOn) {
            double gainPerSec = gainPerMin / 60.0
            pendingTempIncrease = Math.min(pendingTempIncrease + gainPerSec, lagTimeSecs * gainPerSec)
        }

        // if the temp has gone up then reduce the pending increase
        double delta = measuredTemp - prevTemp
        if (delta > 0.0) pendingTempIncrease = Math.max(pendingTempIncrease - delta, 0.0)

        this.prevTemp = measuredTemp

        double error = targetTemp - (measuredTemp + pendingTempIncrease)
        return error > 0.0
    }

}
