package utils

class The {
    private val value: String
    constructor(value: Boolean) { this.value = if (value) "1" else "0" }
    constructor(value: String) { this.value = value }
    constructor(value: Long) { this.value = value.toString() }
    constructor(value: Int) { this.value = value.toString() }
    private fun logParseFailure(reason:String) = prLn(String.format("The value \"%s\" failed #$reason", value))

    /** The value [filtersTo] comparison
     * @return Boolean */
    fun filtersTo(regex: String, comparison: String): Boolean = value.replace(regex.toRegex(), "").equals(comparison, ignoreCase = true)
    override fun toString(): String = value

    /** The value [toInt]
     * @return Int */
    fun toBool(defaultTo:Boolean = false): Boolean { for (c in value.toCharArray()) if (!Character.isDigit(c)) return defaultTo; return Integer.valueOf(value) > 0 }

    /** The value [toInt]
     * @return Int */
    fun toInt(defaultTo:Int = -1): Int {
        if(value.replace("[^\\d]".toRegex(), "").equals("-", true)) {
            for (c in value.replace("[\\d]".toRegex(), "").toCharArray()) if (!Character.isDigit(c)) return defaultTo; return -Integer.valueOf(value)
        } else for (c in value.toCharArray()) if (!Character.isDigit(c)) return defaultTo;
        return Integer.valueOf(value)
    }

    fun isInt(): Boolean {
        for (c in value.replace("[-]".toRegex(), "")) if (!Character.isDigit(c)) return false
        return true
    }

    /** The value [toLong]
     * @return Long */
    fun toLong(defaultTo: Long = -1L): Long = try {
        val numbers = value.replace("[^\\d]".toRegex(), "")
        if (numbers.isBlank()) defaultTo else numbers.toLong()
    } catch (e: NumberFormatException) { logParseFailure("toLong"); defaultTo }

    /** The value [toDouble]
     * @return Double */
    fun toDouble(defaultTo: Double = -1.0): Double = try {
        java.lang.Double.valueOf(value.replace("[^\\d]".toRegex(), ""))
    } catch (e: NumberFormatException) { logParseFailure("toDouble"); defaultTo }

}