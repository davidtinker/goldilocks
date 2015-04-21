package tinker.goldilocks;

/**
 * Temperature control algorithm. Adapted from http://www.vandelogt.nl/htm/regelen_pid_uk.htm
 */
public class PidTempController {

    private final double kc; // Controller gain

    private double k0;  // k0 value for PID controller
    private double k1;  // k1 value for PID controller

    private double xk_1;
    private double xk_2;

    /**
     * k0 = Kc.Ts / Ti     (for I-term)
     * k1 = Kc . Td / Ts   (for D-term)
     * The LPF parameters are also initialised here: lpf[k] = lpf1 * lpf[k-1] + lpf2 * lpf[k-2]
     *
     * @param kc Controller gain
     * @param ti Time-constant for I action (secs)
     * @param td Time-constant for D action (secs)
     * @param ts Sample time [sec.]
     */
    public PidTempController(double kc, double ti, double td, double ts) {
        this.kc = kc;
        k0 = ti == 0.0 ? 0.0 : kc * ts / ti;
        k1  = kc * td / ts;
    }

    /**
     * This function implements the Takahashi PID controller, which is a type C controller: the P and D term are no
     * longer dependent on the set-point, only on PV (which is Thlt). The D term is NOT low-pass filtered.
     * This function should be called once every TS seconds.
     *
     * @param measuredTemp The input variable x[k] (= measured temperature)
     * @param targetTemp The setpoint value for the temperature
     *
     * @return The output variable y[k] (= gamma value for power electronics)
     */
    public double update(double measuredTemp, double targetTemp) {
        double ek = targetTemp - measuredTemp; // calculate e[k] = SP[k] - PV[k]

        double pp = kc * (xk_1 - measuredTemp);
        double pi = k0 * ek;
        double pd = k1 * (2.0 * xk_1 - measuredTemp - xk_2);

        double yk = pp + pi + pd;
        if (yk > 100.0) yk = 100.0;
        else if (yk < 0.0) yk = 0.0;

        xk_2 = xk_1;  // PV[k-2] = PV[k-1]
        xk_1 = measuredTemp;    // PV[k-1] = PV[k]

        return yk;
    }
}
