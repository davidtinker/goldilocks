package tinker.goldilocks

/**
 * Uses pi4j library.
 */
class RaspberryPiImpl implements RaspberryPi {

    private static final File TEMP_PROBE_DIR = new File("/sys/bus/w1/devices")

    @Override
    List<String> getTempProbes() throws IOException {
        if (!TEMP_PROBE_DIR.isDirectory()) return []
        return TEMP_PROBE_DIR.list(new FilenameFilter() {
            boolean accept(File dir, String name) { return !name.startsWith("w1_") }
        });
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
        return (0..20).collect { "GPIO_" + String.format("%02d", it) }
    }

    @Override
    void setPin(String pinId, boolean on) throws IOException {

    }

    @Override
    boolean getPin(String pinId) throws IOException {
        return false
    }
}
