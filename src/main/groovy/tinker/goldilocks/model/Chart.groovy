package tinker.goldilocks.model

class Chart {

    int id
    List<Item> items = []

    Item findItem(Integer itemId) {
        Item i = items.find { it.id == itemId }
        if (!i) throw new IllegalArgumentException("Item not found for id ${itemId} in Chart ${id}")
        return i
    }

    Item addItem() {
        int max = 0
        items.each { if (it.id > max) max = it.id }
        def ans = new Item(id: max + 1)
        items << ans
        return ans
    }
}
