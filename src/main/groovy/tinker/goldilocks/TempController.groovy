package tinker.goldilocks

import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

/**
 * Temperature control algorithm. Attempts to hit a set point temperature by turning a tank heater on and off in
 * response to the measured temperature in the tank. Can auto tune its parameters.
 */
@CompileStatic
@Slf4j
class TempController {

    /** How fast does the temp in the tank rise in degrees C / min when the heater is on? */
    double gainPerMin

    /** How long in seconds does it take for the temp to start rising after the heater is turned on? */
    int lagPeriodSecs

    /** Adjust {@link #gainPerMin} and {@link #lagPeriodSecs} automatically */
    boolean autoTune

    int ticks                       // seconds since we started operating
    double pendingTempIncrease      // temp gain we still expect to see that hasn't happened yet due to lag time
    double prevTemp                 // measured temp at previous tick

    int heaterOnTime                // when did the heater last transition from off to on?
    double heaterOnTemp             // measured temp when the heater was turned on

    int lagEndTime                  // when did the lag time last end?
    double lagEndTemp               // measured temp at the end of the lag time

    int heaterOffTime               // when did the heater last transition from on to off?
    double heaterOffTemp            // measured temp when the heater was turned off

    /**
     * Should the heater be on or off? This method should be called once per second.
     *
     * @param measuredTemp of the tank
     * @param heaterOn current state of the heater
     * @param targetTemp temp we are trying to hit or null if we are not controlling the heater
     */
    boolean tick(double measuredTemp, boolean heaterOn, Double targetTemp) {
        ++ticks

        if (prevTemp == 0.0d) prevTemp = measuredTemp

        if (heaterOn) {
            if (heaterOffTime >= heaterOnTime) {
                heaterOnTime = ticks
                heaterOnTemp = measuredTemp
                log.debug("Heater on at ${heaterOnTime} temp ${heaterOnTemp}")
            }
        } else {
            if (heaterOnTime >= heaterOffTime) {
                heaterOffTime = ticks
                heaterOffTemp = measuredTemp
                log.debug("Heater off at ${heaterOffTime} temp ${heaterOffTemp}")
            }
        }

        // don't do auto tuning unless heater was off for a while before being turned on or we won't be able to
        // calc lag period accurately
        boolean auto = autoTune && heaterOffTime > 0 && (heaterOnTime - heaterOffTime) >= lagPeriodSecs

        if (heaterOn && lagEndTime < heaterOnTime) {
            if (auto) { // use temperature rise detection to spot the end of lag time
                if (measuredTemp - heaterOnTemp >= 0.5) {
                    lagEndTime = ticks
                    lagEndTemp = measuredTemp
                    lagPeriodSecs = lagEndTime - heaterOnTime
                    log.debug("Lag over after ${lagPeriodSecs} secs at ${lagEndTime} temp ${lagEndTemp}")
                }
            } else if ((ticks - heaterOnTime) >= lagPeriodSecs) {
                lagEndTime = ticks
                lagEndTemp = measuredTemp
                log.debug("Lag time (${lagPeriodSecs} secs) over at ${lagEndTime} temp ${lagEndTemp}")
            }
        }

        double pp = pendingTempIncrease

        // re-calc gain if lag is over and temp has risen by 4 degrees since end of lag
        if (auto && heaterOn && lagEndTime >= heaterOnTime && (measuredTemp - lagEndTemp) >= 4.0) {
            double g = (measuredTemp - lagEndTemp) / (ticks - lagEndTime)
            g = Math.floor(g * 60.0 * 100.0) / 100.0
            if (g != gainPerMin) {
                gainPerMin = g
                pendingTempIncrease = gainPerMin / 60.0 * lagPeriodSecs
                log.debug("gainPerMin now ${gainPerMin}")
            }
        }

        if (heaterOn) {
            double gainPerSec = gainPerMin / 60.0
            pendingTempIncrease = Math.min(pendingTempIncrease + gainPerSec, lagPeriodSecs * gainPerSec)
        }

        // if the temp has gone up then reduce the pending increase
        double delta = measuredTemp - prevTemp
        if (delta > 0.0) pendingTempIncrease = Math.max(pendingTempIncrease - delta, 0.0)

        if (log.debugEnabled && pp != pendingTempIncrease) log.debug("Pending temp increase ${pendingTempIncrease}")

        this.prevTemp = measuredTemp

        if (targetTemp == null) return heaterOn // not controlling the heater so go with current state

        double error = targetTemp - (measuredTemp + pendingTempIncrease)
        boolean on = error > 0.0
        if (log.debugEnabled && on != heaterOn) log.debug("Turning heater ${on ? 'on' : 'off'}")
        return on
    }

}
