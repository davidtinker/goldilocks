package tinker.goldilocks

import groovy.transform.CompileStatic

import java.text.DecimalFormat
import java.text.SimpleDateFormat

/**
 * Static utility methods for formatting and so on.
 */
@CompileStatic
class Html {

    static DecimalFormat TEMP_FMT = new DecimalFormat("0.0")

    static String temp(Double c, boolean fahrenheit) {
        String s
        if (c == null) {
            s = "?"
        } else {
            if (fahrenheit) c = Util.toFahrenheit(c)
            s = TEMP_FMT.format(c)
        }
        return  s + ' \u00B0' + (fahrenheit ? 'F' : 'C')
    }

    private static SimpleDateFormat TIME_FMT = new SimpleDateFormat('h:mm:ss')

    static String time(Date date) {
        return TIME_FMT.format(date)
    }

}
