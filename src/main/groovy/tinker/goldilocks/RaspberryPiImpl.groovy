package tinker.goldilocks

import com.pi4j.io.gpio.Pin
import com.pi4j.io.gpio.RaspiPin
import com.pi4j.wiringpi.Gpio
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import java.lang.reflect.Modifier

/**
 * Uses pi4j library.
 */
@CompileStatic
@Slf4j
class RaspberryPiImpl implements RaspberryPi {

    static {
        def rc = Gpio.wiringPiSetupGpio()
        if (rc) throw new IOException("Gpio.wiringPiSetupGpio() failed with code " + rc)
    }

    private static final File TEMP_PROBE_DIR = new File("/sys/bus/w1/devices")

    private final Map<String, Pin> pins

    RaspberryPiImpl() {
        Map<String, Pin> m = [:]
        RaspiPin.fields.findAll {
            f -> Modifier.isStatic(f.modifiers) && f.name.startsWith("GPIO_") && Pin.class.isAssignableFrom(f.type)
        }.sort { it.name }.each { m.put(it.name, (Pin)it.get(null)) }
        this.pins = m
    }

    @Override
    List<String> getTempProbes() throws IOException {
        if (!TEMP_PROBE_DIR.isDirectory()) return []
        return TEMP_PROBE_DIR.list(new FilenameFilter() {
            boolean accept(File dir, String name) { return !name.startsWith("w1_") }
        }) as List;
    }

    @Override
    double readTemp(String probeId) throws IOException {
        def f = new File(new File(TEMP_PROBE_DIR, probeId), "/w1_slave")
        def s = f.text
        int i = s.indexOf('\n')
        if (i < 0) throw new IOException("Expected 2 lines of text from ${f}: [${s}]")
        def l1 = s.substring(0, i)
        if (!l1.endsWith("YES")) throw new IOException("Bad CRC reading ${f}: [${s}]")
        i = s.indexOf('t=', i + 1)
        if (i < 0) throw new IOException("Expected 't=' from ${f}: [${s}]")
        try {
            return Integer.parseInt(s.substring(i + 2, i + 7)) / 1000.0
        } catch (Exception ignore) {
            throw new IOException("Error reading temp from ${f}: [${s}]")
        }
    }

    @Override
    List<String> getPins() throws IOException {
        return pins.keySet().asList()
    }

    @Override
    void setPin(String pinId, boolean on) throws IOException {
        Pin p = lookupPin(pinId)
        Gpio.pinMode(p.address, Gpio.OUTPUT)
        Gpio.digitalWrite(p.address, on)
    }

    @Override
    boolean getPin(String pinId) throws IOException {
        return Gpio.digitalRead(lookupPin(pinId).address)
    }

    private Pin lookupPin(String pinId) {
        Pin p = pins[pinId]
        if (!p) throw new IllegalArgumentException("Invalid pinId [${pinId}]")
        return p
    }
}
