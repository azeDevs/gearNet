package utils

class Queue<T>(max:Int = -1, private val default:T) {
    private val max = if (max < 0) Int.MAX_VALUE else max
    private val items:MutableList<T> = arrayListOf()
    val size = items.count()
    fun put(item:T) {
        items.add(item)
        if (size == max) pop()
        prLn("ASDF :::: $size / $max :::: ASDF")
    }
    fun pop():T = if (!isEmpty()) items.removeAt(0) else default
    fun get(index:Int = 0):T = if (isWithin(index, size)) items[index] else default
    fun getItems():List<T> = items
    fun isEmpty():Boolean = items.isEmpty()
    override fun toString() = items.toString()
}


/** [Duo]
 * This is basically the same as Pair(), except mutable and unsafe.
 * @param f1
 * @param f2
*/
class Duo<T>(
    var f1: T,
    var f2: T = f1
)