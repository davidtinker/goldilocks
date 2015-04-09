package tinker.goldilocks.model

class Chart {

    int id
    Integer minutes
    List<Control> controls = []

    Control findControl(Integer itemId) {
        Control i = controls.find { it.id == itemId }
        if (!i) throw new IllegalArgumentException("Item not found for id ${itemId} in Chart ${id}")
        return i
    }

    Control addControl() {
        int max = 0
        controls.each { if (it.id > max) max = it.id }
        def ans = new Control(id: max + 1)
        controls << ans
        return ans
    }
}
