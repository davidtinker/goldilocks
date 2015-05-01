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

    private FakeHltRamp hlt = new FakeHltRamp(22.0, 120, 180, 10)
    private double mashTemp = 67.5

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
        if (probeId == HLT_PROBE) return digitize(hlt.temp)
        if (probeId == MASH_PROBE) return digitize(mashTemp)
        throw new FileNotFoundException(probeId)
    }

    private double digitize(double v) {
        return Math.floor(v * 16) / 16
    }

    @Override
    List<String> getPins() throws IOException {
        return (0..20).collect { "GPIO_" + String.format("%02d", it) }
    }

    @Override
    void setPin(String pinId, boolean on) throws IOException {
        if (pinId == HEATER_PIN) hlt.heaterOn = on
    }

    @Override
    boolean getPin(String pinId) throws IOException {
        if (pinId == HEATER_PIN) return hlt.heaterOn
        return false
    }

    private void updateState() {
        hlt.tick()
        mashTemp += (Math.random() - 0.5) / 10
    }
}
