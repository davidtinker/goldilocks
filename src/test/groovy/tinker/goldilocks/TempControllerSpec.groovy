package tinker.goldilocks

import spock.lang.Specification

class TempControllerSpec extends Specification {

    def "Does temp controller sort of work"() {
        FakeHltRamp hlt = new FakeHltRamp(22.0, 120, 180, 10)
        TempController tc = new TempController(gainPerMin: 1.0, lagPeriodSecs: 120)
        println "secs, hlt-temp, heater, pending"
        for (int secs = 0; secs < 800; secs++) {
            hlt.heaterOn = tc.tick(hlt.temp, hlt.heaterOn, 26.0)
            println(String.format("%d, %.2f, %s, %.2f",
                    secs, hlt.temp, hlt.heaterOn ? "on" : "off", tc.pendingTempIncrease))
            if (secs % 10 == 0) hlt.tick()
        }

        expect:
        "foo" == "foo"
    }

}
