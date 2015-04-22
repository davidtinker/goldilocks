package tinker.goldilocks

/**
 * Simulates the temperature response of a hot liquor tank with heating element. There is a lag after the heater is
 * turned on before the temperature starts rising at full speed and a similar lag after the heater is turned off
 * before the temperature starts falling.
 */
class FakeHltRamp {

    private final int lagSecs
    private final int overshootSecs
    private final int secondsPerTick

    int ticks
    double temp

    private boolean heaterOn
    private int heaterChangeTime

    FakeHltRamp(double temp, int lagSecs, int overshootSecs, int secondsPerTick) {
        this.temp = temp
        this.lagSecs = lagSecs
        this.overshootSecs = overshootSecs
        this.secondsPerTick = secondsPerTick
    }

    void setHeaterOn(boolean heaterOn) {
        if (this.heaterOn != heaterOn) {
            this.heaterOn = heaterOn
            heaterChangeTime = ticks
        }
    }

    boolean getHeaterOn() {
        return heaterOn
    }

    /**
     * Update temp.
     */
    void tick() {
        int secs = (ticks - heaterChangeTime) * secondsPerTick
        double degreesPerMin
        if (heaterOn) {
            if (secs > 120) secs = 120
            degreesPerMin = secs / 120.0
        } else {
            if (secs > 180) secs = 180
            degreesPerMin = (90 - secs) / 90.0
        }

        temp += (degreesPerMin / 60.0) * secondsPerTick

        if (temp < 22.0) temp = 22.0
        else if (temp > 100.0) temp = 100.0

        ++ticks
    }

}
