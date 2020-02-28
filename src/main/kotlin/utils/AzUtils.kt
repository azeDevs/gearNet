package utils

import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.net.URI
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*
import kotlin.random.Random
import kotlin.system.exitProcess


/**
 * A shortcut value for providing the path to the
 * application's root directory.
 *
 * @return the Path to the application's root directory
 */
val pathHome: Path = Paths.get(System.getProperty("user.dir"))


/**
 *strToInt
 *
 * @return Int
 */
fun strToInt(param: String): Int { for (c in param.toCharArray()) if (!Character.isDigit(c)) return -1; return Integer.valueOf(param) }


/**
 * Concatenate an "s" to the end of an existing String, based on
 * the value of an incoming Integer being greater than 1.
 *
 * @param text the text to be modified
 * @param size the value to evaluate
 * @return the (un)modified text as a String
 */
fun plural(text: String, size: Int) = "$text${if (size > 1) "s" else ""}"


/**
 * A shortcut method for printing to a row
 * and breaking at the end of the line
 *
 * @param text the `String`, or collection of `Strings`,
 * that will be print to the System console
 */
fun prLn(vararg text: String) = text.forEach { s -> print("\n$s") }


/**
 * A shortcut method for printing to a row
 * in the System console that does not end with a line break,
 * but does end with a space.
 *
 * @param text the `String`, or collection of `Strings`,
 * that will be printed to the System console
 */
fun prSp(vararg text: String) = text.forEach { s -> print("$s ") }


/**
 * A shortcut method for printing to a row
 * in the System console that does not end with a line break
 *
 * @param text the `String`, or collection of `Strings`,
 * that will be print to the System console
 */
fun prnt(vararg text: String) = text.forEach { s -> print(s) }


/**
 * Used to retrieve a `String` input from the System console.
 *
 * @return input from the console as a String
 */
fun input(): String { print("▶"); return Scanner(System.`in`).next() }


/**
 * Gets an authentication token as a `String` from a remote directory.
 *
 * @param path  each directory and finally the name of the
 * file that contains the token to be read
 * @return the token as a `String`
 */
fun getTokenFromFile(vararg path: String): String { val sb = StringBuilder()
    try { val fileScan = Scanner(FileReader(Paths.get("$pathHome", *path).toFile())); while (fileScan.hasNext()) sb.append(fileScan.next()); fileScan.close()
    } catch (e: FileNotFoundException) { e.printStackTrace() }; return sb.toString() }


/**
 * Convert millis into a formatted time string.
 *
 * @param epochMilli the time as epoch milli
 * @return the formatted date and time as a `String`
 */
fun time(epochMilli: Long) = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.US).withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(epochMilli)) ?: ""


/**
 * Get a preformatted time string.
 *
 * @return the formatted date and time as a `String`
 */
fun time() = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT).withLocale(Locale.US).withZone(ZoneId.systemDefault()).format(Instant.ofEpochMilli(timeMillis())) ?: ""


/**
 * Get the current date and time in the form of milliseconds.
 *
 * @return the time in millis
 */
fun timeMillis() = System.currentTimeMillis()


/**
 * Table format a `String`.
 *
 * @param width the width of the table
 * @param text  the text to be formatted
 * @return the formatted table as a `String`
 */
fun table(width: Int, vararg text: String): String { val column = StringBuilder()
    text.forEach { s -> column.append(truncate(s,width)); repeat((0 until width - s.length).count()) { column.append(" ") } }
    return column.toString() }


/**
 * Exit the application with a Shutdown.
 *
 * @param code the exit code to display after Shutdown
 * @param text the text to display before shutdown
 */
fun exit(code: Int, text: String) { println("\uD83D\uDED1 $text"); exitProcess(code) }


/**
 * Add a Shutdown Hook to the application.
 *
 * @param text the text to display before shutdown
 */
fun addShutdownHook(text: String) = Runtime.getRuntime().addShutdownHook(Thread { println(text) })


/**
 * Consume a Runnable, and loop it for the interval provided, converted from milliseconds.
 *
 * @param interval the interval
 * @param runnable the runnable
 */
fun loopRun(interval: Long, runnable: Runnable) = Timer().schedule(object : TimerTask() { override fun run() { runnable.run() } }, interval, interval)


/**
 * Limit the length of a String by length
 *
 * @param name the name to be truncated
 * @param length the maximum number of characters allowed in the String
 * @return the truncated name as a String
 */
fun truncate(name: String, length: Int): String {
    val re = Regex("[^A-Za-z0-9_!@#$%^&*()“”`'~|.,\"\\-=+\\[\\]{}\\\\<>/「」 ａｂｃｄｅｆｇｈｉｊｋｌｍｎｏｐｑｒｓｔｕｖｗｘｙｚＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ０１２３４５６７８９]")
    return if (name.length > length) re.replace(name, "?").substring(0, length) else re.replace(name, "?")
}


/**
 * Adds commas to a given Integer every 3 digits.
 *
 * @param inInt the Int representing the number to be formatted
 * @return the number formatted with commas as a String
 */
fun addCommas(inInt: Int): String {
    var outStr = ""
    val inStr = inInt.toString()
    val commas = if (inStr.length % 3 == 0) (inStr.length/3)-1 else inStr.length/3
    for (i in 0 until commas) outStr = if (inStr.length > 3) ",${inStr.substring(inStr.length-(3*(i+1)), inStr.length-(3*i))}${outStr}" else "${inStr.substring(inStr.length-(3*(i+1)), inStr.length-(3*i))}${outStr}"
    return inStr.substring(0, inStr.length-(3*commas)) + outStr
}


/**
 * Return a clamped minimum / maximum value of a given number.
 *
 * @param value the value to be checked and changed
 * @param minimum the minimum the return value can be
 * @param maximum the maximum the return value can be
 * @param retainMax should the value be over maximum, return the maximum
 * @return the clamped value
 */
fun keepInRange(value:Int, minimum:Int = -2147483647, maximum:Int = 2147483647, retainMax:Boolean = false): Int = if (value > maximum || value < minimum) { if (retainMax) maximum else minimum } else value


/**
 * Check if value is equal to or between 2 given numbers.
 *
 * @param value the value to be checked
 * @param minimum the minimum value while still returning true
 * @param maximum the maximum value while still returning true
 * @return boolean for whether or not the value was within the indicated range
 */
fun isInRange(value:Int, minimum:Int = 0, maximum:Int = 2147483647): Boolean = !(value > maximum || value < minimum)
fun isWithin(value:Int, maximum:Int = 1): Boolean = isInRange(value, 0, maximum)

/**
 * Write a String to a local text file
 *
 * @param fileName the name of the file
 * @param text the text to be written
 */
fun writeToFile(fileName: String, text: String) = File(fileName).writeText(text)


/**
 * Get a URI for a local project resource
 *
 * @param fileName the file to be retrieved from resources package.
 * @return the file path as a `URI`
 */
fun getRes(fileName: String): URI = URI("${pathHome.toUri().toURL()}src/main/resources/$fileName")


/**
 * getRandomName
 *
 * @return a semi-random/semi-pronouncable name
 */
fun getRandomName():String {
    val rn1 = arrayListOf("a","ze","st","ar","Koov","er","Te","chno","Lost","Ill","usion","isio","avi","La","bryz","Cath","at","icus","gry","phen","Soff","ish","Aoi","Mai","den","epo","ck","robo","sting","ray","sw","eet","X","jam","Tar","kus","Ev","ir","Dwa","jio","Big","bow","sa","TK","sha","dow","Del","rian","son","ny","wort","zik","Bon","bei","beez","uz","agri","guck","le","Jub","Kiz","zer","Day","men","dou","Pep","pery","Sp","lash","Kuro","gane","Ri","ven","Whoo","Boo","st","Whom","Sput","nik","Mk0","Cre","amy","Shits","Poo","Lord","Shin","Mun","chy","Mad","ao","Pan","Je","yu","dus","Sin","Pom","pa","dude","Riss","ay","Ja","yne","Mk1","Bea","Whi","Octo","pimp","Bon","bei","eez","us","Guck","le","oki","zeme","69","420","XxX","xXx","Seph","iroth","Sex","Haver","Weed","Pan","zee","boo","ties","Der","win","Sla","Elv","Sha","dow","Bla","ck","Sna","ke","Pru","sha","Cute","Miku","Rock","Man","Girl","Boy","Bitch","Bro","tato","seph","heim","Free","Wind","Jutsu","Ninja","obi","chan","kun","Kami","Poke","Kill","mon","Digi","Ahe","gao","Face","ken","dojo","Dead","State","God","Gren","dy","lici","ous","Love","Fire","Flame","Ice","Elf","Fair","Drag","Devil","Jin","Muge","Moog","Lock","Kara","Sol","Badguy","Jelly","Rich","Fake","Fraud","Pro","Sport","Spice","Butt","Blood","Evil","Goo","HUE","HUEHUE","HURR","HNNG","Gai","jen","Ota","ku","con","Fucker","Fast","Sonic","Blur","Red","Black","Green","Blue","Pink","White","Macha","mito","Anji","Flash","Shad","Eden","War","ior","Priest","ess","Fal","len","Ang","ing","est","ery"," the ","Fl","ip","up","Throw","Tier","Nerf","Ost","ia","Rhi","ne","hart","Swag","Sal","Faul","ty","Def","ense","Goku"); val rn2 = arrayListOf("b","c","d","f","g","h","j","k","l","m","n","p","q","r","s","t","v","w","x","z"); val rn3 = arrayListOf("a","e","i","o","u","a","e","i","o","u","y")
    val out = StringBuilder("${rn2[Random.nextInt(rn2.size)].toUpperCase()}${rn3[Random.nextInt(rn3.size)]}"); var nameStep = 0
    repeat((0..Random.nextInt(2,4)).count()) { var part = StringBuilder()
        if (nameStep++ % 2 == 0) part.append("${rn2[Random.nextInt(rn2.size)]}${rn3[Random.nextInt(rn3.size)]}")
        else part.append(rn1[Random.nextInt(rn1.size)])
        when (Random.nextInt(15)) {
            1 -> part.append(" ${rn2[Random.nextInt(rn2.size)].toUpperCase()}${rn3[Random.nextInt(rn3.size)]}")
            2 -> part.append("_${rn2[Random.nextInt(rn2.size)].toUpperCase()}${rn3[Random.nextInt(rn3.size)]}")
            3 -> part.append("${rn3[Random.nextInt(rn3.size)]}${rn2[Random.nextInt(rn2.size)]}")
            4 -> part.append(Random.nextInt(10,1000))
            5 -> part = StringBuilder(part.toString().toUpperCase())
            6 -> part = StringBuilder(part.toString().toLowerCase())
            7 -> { val pick = Random.nextInt(part.length); part.replace(pick,pick+1, if (Random.nextBoolean()) part.get(pick).toString().toUpperCase() else part.get(pick).toString().toLowerCase()) }
            else -> part.append("") }; out.append(part)
    }; return out.toString()
}


