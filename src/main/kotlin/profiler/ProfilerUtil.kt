package profiler

import java.awt.Desktop
import java.io.File

fun listJvmProcesses(): List<Pair<String, String>> {
    val output = ProcessBuilder("jps", "-l").start().inputStream.bufferedReader().readLines()
    return output.mapNotNull {
        val parts = it.split("\\s+".toRegex(), 2)
        if (parts.size == 2) parts[0] to parts[1] else null
    }
}

fun runAsyncProfiler(
    profilerPath: String, pid: String, event: String, duration: Int, output: File
): Boolean {
    val pb = ProcessBuilder(
        profilerPath, "-e", event, "-d", duration.toString(), "-f", output.absolutePath, pid
    ).inheritIO()
    val proc = pb.start()
    return proc.waitFor() == 0
}

fun openHtmlInBrowser(file: File) {
    Desktop.getDesktop().browse(file.toURI())
}
