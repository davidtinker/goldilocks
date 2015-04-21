package tinker.goldilocks

import groovy.util.logging.Slf4j

import javax.annotation.PreDestroy

/**
 * Simulates a Pi for testing on PC.
 */
@Slf4j
class FakeRaspberryPi implements RaspberryPi {

    private static final String HLT_PROBE = "28-00000657cb8b"
    private static final String MASH_PROBE = "28-00000657cbe6"
    private static final String HEATER_PIN = "GPIO_17"

    private double hltTemp = 22.0
    private double mashTemp = 67.5
    private boolean heaterOn

    private long heaterChangeTime

    private Timer timer

    private static final double INTERVAL_SECS = 10.0

    FakeRaspberryPi() {
        timer = new Timer("fake-pi", true)
        timer.schedule(new TimerTask() {
            void run() { updateState() }
        }, 1000, (long)(INTERVAL_SECS * 1000))
    }

    @PreDestroy
    private void destroy() {
        timer.cancel()
    }

    List<String> getTempProbes() throws IOException {
        return [HLT_PROBE, MASH_PROBE]
    }

    double readTemp(String probeId) throws IOException {
        if (probeId == HLT_PROBE) return hltTemp
        if (probeId == MASH_PROBE) return mashTemp
        throw new FileNotFoundException(probeId)
    }

    @Override
    List<String> getPins() throws IOException {
        return (0..20).collect { "GPIO_" + String.format("%02d", it) }
    }

    @Override
    void setPin(String pinId, boolean on) throws IOException {
        if (pinId == HEATER_PIN && heaterOn != on) {
            heaterOn = on
            heaterChangeTime = System.currentTimeMillis()
        }
    }

    @Override
    boolean getPin(String pinId) throws IOException {
        if (pinId == HEATER_PIN) return heaterOn
        return false
    }

    private void updateState() {
        // this tries to produce a 'ramp up' and 'taper off' i.e. it takes a little time before the temperature
        // starts rising after the heater is turned on and a little time to stop rising when it is turned off
        int secs = (int)((System.currentTimeMillis() - heaterChangeTime) / 1000)
        double degreesPerMin
        if (heaterOn) {
            if (secs > 120) secs = 120
            degreesPerMin = secs / 120.0
        } else {
            if (secs > 180) secs = 180
            degreesPerMin = (90 - secs) / 90.0
        }
        degreesPerMin *= 0.95 + Math.random() / 10.0

        hltTemp += (degreesPerMin / 60.0) * INTERVAL_SECS

        if (hltTemp < 22.0) hltTemp = 22.0 + Math.random() / 5
        else if (hltTemp > 100.0) hltTemp = 99.90 + Math.random() / 5

        mashTemp += Math.random() - 0.5
    }
}
