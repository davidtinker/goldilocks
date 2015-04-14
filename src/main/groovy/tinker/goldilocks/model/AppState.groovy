package tinker.goldilocks.model

import groovy.transform.CompileStatic

/**
 * The persistent state of our application with transient fields for the dynamic state (current temp of vessels etc.).
 * The non-transient fields are stored in JSON on disk by {@link tinker.goldilocks.SetupRepo}.
 */
@CompileStatic
class AppState {

    String title
    Boolean fahrenheit
    List<Chart> charts = []
    List<AppTimer> timers = []

    transient Date updated

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

    AppTimer findTimer(Integer id) {
        AppTimer t = timers.find { it.id == id }
        if (!t) throw new IllegalArgumentException("Timer not found for id ${id}")
        return t
    }

    AppTimer addTimer() {
        int max = 0
        timers.each { if (it.id > max) max = it.id }
        def ans = new AppTimer(id: max + 1)
        timers << ans
        return ans
    }
}
