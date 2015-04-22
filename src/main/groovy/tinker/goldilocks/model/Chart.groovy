package tinker.goldilocks.model

import java.security.SecureRandom

class Chart {

    int id
    Integer minutes
    List<Control> controls = []

    Control findControl(String itemId) {
        Control i = controls.find { it.id == itemId }
        if (!i) throw new IllegalArgumentException("Item not found for id ${itemId} in Chart ${id}")
        return i
    }

    Control addControl() {
        byte[] a = new byte[8]
        new SecureRandom().nextBytes(a)
        def ans = new Control(id: new BigInteger(1, a).toString(36))
        controls << ans
        return ans
    }
}
