@file:JvmName("Himawari")

package io.sureshg

import java.awt.image.BufferedImage
import java.io.File
import java.net.URL
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.imageio.ImageIO

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
        val text = URL("$baseURL/latest.json").readText()
        println("Fetching Earth captured by the Himawari Satellite on $text...")
        val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        val date = LocalDateTime.parse(text.replace("{\"date\":\"", "").split("\"")[0], fmt)

        // Each tile is 550 x 550 px in size.
        val bi = BufferedImage(width * scale, height * scale, BufferedImage.TYPE_INT_ARGB)
        val g = bi.graphics

        println("Fetching tiles..Please wait.")
        for (x in 0..scale - 1) {
            for (y in 0..scale - 1) {
                val tileURL = URL(pathFor(date, x, y))

                val tile = ImageIO.read(tileURL)
                println("Fetching (x:$x,y:$y) of (${scale - 1},${scale - 1})")
                g.drawImage(tile, width * x, height * y, null)
            }
        }

        ImageIO.write(bi, format, outFile)
        println("Live earth image: ${outFile.absolutePath}")

        runCmd("osascript", "-e", "tell application \"Finder\" to set desktop picture to POSIX file \"${outFile.absolutePath}\"")
        runCmd("killall", "Dock")

    } catch(t: Throwable) {
        println("Crap..Got some error!")
        t.printStackTrace()
    }

}

fun runCmd(vararg cmds: String) {
    val p = ProcessBuilder(cmds.toList())
    p.inheritIO()
    p.start().waitFor()
}

/**
 * Tiled image URL.
 * Eg: http://himawari8.nict.go.jp/img/D531106/8d/550/2016/02/10/215000_7_3.png
 * for time : 2016-02-10 21:50:05.858945+00:00, x: 7, y: 3
 */

fun pathFor(t: LocalDateTime, x: Int, y: Int) = "%s/%dd/%d/%d/%02d/%02d/%02d%02d00_%d_%d.%s".format(baseURL, scale, width, t.year, t.monthValue, t.dayOfMonth, t.hour, t.minute, x, y, format)

