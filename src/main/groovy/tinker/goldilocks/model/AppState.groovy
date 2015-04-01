package tinker.goldilocks.model

import groovy.transform.CompileStatic
import tinker.goldilocks.TempLogRepo

/**
 * The persistent state of our application with transient fields for the dynamic state (current temp of vessels etc.).
 * The non-transient fields are stored in JSON on disk by {@link tinker.goldilocks.SetupRepo}.
 */
@CompileStatic
class AppState {

    String title
    List<Chart> charts = []

    boolean fahrenheit

    transient Date updated

    double fixTemp(double c) {
        return fahrenheit ? c * 9 / 5 + 32 : c
    }

    List<TempLogRepo.Record> fixTemp(List<TempLogRepo.Record> list) {
        if (fahrenheit) list.each { it.temp = fixTemp(it.temp) }
        return list
    }

    Chart findChart(Integer id) {
        Chart c = charts.find { it.id == id }
        if (!c) throw new IllegalArgumentException("Chart not found for id ${id}")
        return c
    }

    Chart addChart() {
        int max = 0
        charts.each { if (it.id > max) max = it.id }
        def ans = new Chart(id: max + 1)
        charts << ans
        return ans
    }

}
