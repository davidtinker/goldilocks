package tinker.goldilocks.model

import java.security.SecureRandom

class Chart {

    int id
    Integer minutes
    List<Control> controls = []

    Control addControl() {
        byte[] a = new byte[8]
        new SecureRandom().nextBytes(a)
        def ans = new Control(id: new BigInteger(1, a).toString(36), autoTune: true)
        controls << ans
        return ans
    }
}
