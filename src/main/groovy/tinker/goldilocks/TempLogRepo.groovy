package tinker.goldilocks

import com.google.inject.name.Named
import groovy.transform.CompileStatic
import groovy.util.logging.Slf4j

import javax.inject.Inject
import java.text.DecimalFormat
import java.text.SimpleDateFormat

/**
 * Remembers temperature measurements over time. These are stored in fixed record length CSV files, one per day
 * in a directory named after the traceId. Uses the system default timezone.
 */
@CompileStatic
@Slf4j
class TempLogRepo {

    private final File dataDir

    private static final SimpleDateFormat DATE_FMT = new SimpleDateFormat("yyyyMMdd")
    private static final SimpleDateFormat TIME_FMT = new SimpleDateFormat("HH:mm:ss") // must change list() as well
    private static final DecimalFormat TEMP_FMT = new DecimalFormat("000.000")

    static class Record {
        long date
        double temp

        Record(long date, double temp) {
            this.date = date
            this.temp = temp
        }
    }

    @Inject
    TempLogRepo(@Named("data.dir") File dataDir) {
        this.dataDir = dataDir
    }

    private File ensureDir(String tempProbe) {
        File d = new File(dataDir, tempProbe)
        if (!d.directory && !d.mkdirs()) throw new IOException("Unable to create " + d)
        if (!d.canWrite()) throw new IOException("Unable to write to " + d)
        return d
    }

    /**
     * Record temperature for the probe.
     */
    synchronized void save(String traceId, double temp) throws IOException {
        Date now = new Date()
        File f = new File(ensureDir(traceId), DATE_FMT.format(now) + ".csv")
        f.append(TIME_FMT.format(now) + "," + TEMP_FMT.format(temp) + "\n", "UTF8")
    }

    /**
     * List recorded measurements for the probe and date range,
     */
    List<Record> list(String traceId, Date start, Date end, boolean fahrenheit) {
        List<Record> ans = []
        GregorianCalendar gc = new GregorianCalendar()
        gc.time = start
        while (true) {
            def d = gc.time
            if (!d.before(end)) break
            File f = new File(new File(dataDir, traceId), DATE_FMT.format(d) + ".csv")
            if (f.exists()) {
                for (String line : f.readLines("UTF8")) {
                    // HH:mm:ss,000.000
                    // 0123456789
                    gc.set(Calendar.HOUR_OF_DAY, Integer.parseInt(line.substring(0, 2)))
                    gc.set(Calendar.MINUTE, Integer.parseInt(line.substring(3, 5)))
                    gc.set(Calendar.SECOND, Integer.parseInt(line.substring(6, 8)))
                    def date = gc.time
                    if (date.before(start)) continue
                    if (date.after(end)) break
                    def c = Double.parseDouble(line.substring(9))
                    ans << new Record(date.time, fahrenheit ? Util.toFahrenheit(c) : c)
                }
            }
            gc.add(Calendar.DAY_OF_YEAR, 1)
        }
        return ans
    }
}
