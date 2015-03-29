package tinker.goldilocks

import groovy.transform.CompileStatic

import java.text.DecimalFormat

@CompileStatic
class Util {

    static double toFahrenheit(double c) { return c * 9 / 5 + 32 }

}
