fun <T : Comparable<T>> ArrayList<T>.bubbleSort(showPasses: Boolean = false) {
    // There’s no need to sort the collection when it has less than two elements. One element is sorted by itself; zero elements don’t require an order.
    if (this.size < 2) return
    // A single-pass will bubble the largest value to the end of the collection. Every pass needs to compare one less value than in the previous pass, so you shorten the array by one with each pass.
    for (end in this.lastIndex downTo 1) {
        var swapped = false
        // This loop performs a single pass starting from the first element and going up until the last element not already sorted. It compares every element with the adjacent value.
        for (current in 0 until end) {
            if (this[current] > this[current + 1]) {
                // Next, the algorithm swaps the values if needed and marks this in swapped. This is important later because it’ll allow you to exit the sort as early as you can detect the list is sorted.
                this.swapAt(current, current + 1)
                swapped = true
            }
        }
        // This prints out how the list looks after each pass. This step has nothing to do with the sorting algorithm, but it will help you visualize how it works. You can remove it (and the function parameter) after you understand the sorting algorithm.
        if (showPasses) println(this)
        // If no values were swapped this pass, the collection is assumed sorted, and you can exit early.
        if (!swapped) return
    }
}