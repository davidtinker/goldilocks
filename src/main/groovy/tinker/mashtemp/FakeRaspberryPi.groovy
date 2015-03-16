package tinker.mashtemp

import com.pi4j.io.gpio.RaspiPin

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy

/**
 * Simulates a Pi for testing on PC.
 */
class FakeRaspberryPi implements RaspberryPi {

    private static String HLT_PROBE = "28-00000657cb8b"
    private static String MASH_PROBE = "28-00000657cbe6"

    private double hltTemp = 60.0
    private double mashTemp = 67.5
    private boolean heaterOn

    private Timer timer

    FakeRaspberryPi() {
    }

    @PostConstruct
    private void init() {
        timer.schedule(new TimerTask() {
            void run() { updateState() }
        }, 1000, 30000)
    }

    @PreDestroy
    private void destroy() {
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
    void setGpio(RaspiPin pin, int on) throws IOException {
        if (pin == RaspiPin.GPIO_17) heaterOn = true
    }

    private void updateState() {
        if (heaterOn) hltTemp += Math.random() * 2
        else hltTemp -= Math.random() / 2
        mashTemp += Math.random() - 0.5
    }
}
