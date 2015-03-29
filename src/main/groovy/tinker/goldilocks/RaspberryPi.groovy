package tinker.goldilocks
/**
 * Raspberry Pi IO. Implemented by {@link FakeRaspberryPi} for web app development on PC.
 */
interface RaspberryPi {

    /**
     * List the ids of all installed w1 temp probes.
     */
    List<String> listTempProbes() throws IOException

    /**
     * Read the temperature of the probe.
     */
    double readTemp(String probeId) throws IOException

    /**
     * List the ids of all possible pins.
     */
    List<String> listPins() throws IOException;

    /**
     * Change the state of the pin.
     */
    void setPin(String pinId, boolean on) throws IOException;

    /**
     * Read the state of the pin.
     */
    boolean getPin(String pinId) throws IOException;

}
