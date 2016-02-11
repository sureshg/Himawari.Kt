@file:JvmName("Himawari")

package io.sureshg

import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO
import kotlin.text.RegexOption.IGNORE_CASE

/**
 * Fetch and untile tiled Himawari-8 images from the http://himawari8.nict.go.jp PNG endpoint, then set
 * them as desktop background on OSX. Valid zoom levels seem to be powers of 2, 1..16, and 20.
 *
 * Kotlin version of https://gist.github.com/willwhitney/e9e2c42885385c51843e
 *
 * @author Suresh
 */

val baseURL = "http://himawari8-dl.nict.go.jp/himawari8/img/D531106"
val width = 550
val height = 550
val scale = 8
val format = "png"
val outFile = File("Himawari.$format")


fun main(args: Array<String>) {

    try {
        println("Fetching live images of the Earth captured by the Himawari-8 Satellite...")

        val text = URL("$baseURL/latest.json").readText()
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val date = LocalDateTime.parse(text.replace("{\"date\":\"", "").split("\"")[0], fmt)

        // Each tile is 550 x 550 px in size.
        val bi = BufferedImage(width * scale, height * scale, BufferedImage.TYPE_INT_ARGB)
        val g = bi.graphics

        for (x in 0..scale - 1) {
            for (y in 0..scale - 1) {
                val tileURL = URL(pathFor(date, x, y))
                val tile = ImageIO.read(tileURL)
                print("\u001B[2K\rStitching tile ${scale * x + y + 1} of ${scale * scale}...")
                g.drawImage(tile, width * x, height * y, null)
            }
        }

        ImageIO.write(bi, format, outFile)
        println("\nLive earth image: ${outFile.absolutePath}")

        if (isOSX) {
            runCmd("osascript", "-e", "tell application \"Finder\" to set desktop picture to POSIX file \"${outFile.absolutePath}\"")
            runCmd("killall", "Dock")
        } else {
            println("Setting wallpaper is only supported on MacOSX.")
        }

    } catch(t: Throwable) {
        println("Crap..Something went wrong!")
        t.printStackTrace()
    }

}

/**
 * Tiled image URL. Eg: http://himawari8.nict.go.jp/img/D531106/8d/550/2016/02/10/215000_7_3.png
 */

fun pathFor(t: LocalDateTime, x: Int, y: Int) = "%s/%dd/%d/%d/%02d/%02d/%02d%02d00_%d_%d.%s".format(baseURL, scale, width, t.year, t.monthValue, t.dayOfMonth, t.hour, t.minute, x, y, format)

val isOSX = System.getProperty("os.name").matches("mac os x|darwin".toRegex(IGNORE_CASE))

fun runCmd(vararg cmds: String) {
    val p = ProcessBuilder(cmds.toList())
    p.inheritIO()
    p.start().waitFor()
}


