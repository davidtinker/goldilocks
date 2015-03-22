package tinker.mashtemp

import com.pi4j.io.gpio.RaspiPin
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

    private double hltTemp = 60.0
    private double mashTemp = 67.5
    private boolean heaterOn

    private Timer timer

    FakeRaspberryPi() {
        timer = new Timer("fake-pi", true)
        timer.schedule(new TimerTask() {
            void run() { updateState() }
        }, 1000, 30000)
    }

    @PreDestroy
    private void destroy() {
        timer.cancel()
    }

    List<String> listTempProbes() throws IOException {
        return [HLT_PROBE, MASH_PROBE]
    }

    double readTemp(String probeId) throws IOException {
        if (probeId == HLT_PROBE) return hltTemp
        if (probeId == MASH_PROBE) return mashTemp
        throw new FileNotFoundException(probeId)
    }

    @Override
    List<String> listPins() throws IOException {
        return (0..20).collect { "GPIO_" + String.format("%02d", it) }
    }

    @Override
    void setPin(String pinId, boolean on) throws IOException {
        if (pinId == HEATER_PIN) heaterOn = on
    }

    @Override
    boolean getPin(String pinId) throws IOException {
        if (pinId == HEATER_PIN) return heaterOn
        return false
    }

    private void updateState() {
        if (heaterOn) hltTemp += Math.random() * 2
        else hltTemp -= Math.random() / 2
        mashTemp += Math.random() - 0.5
    }
}
