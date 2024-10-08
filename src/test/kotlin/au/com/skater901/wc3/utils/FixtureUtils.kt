package au.com.skater901.wc3.utils

fun fixture(fixture: String): String = ClassLoader.getSystemResourceAsStream(fixture)
    ?.use { it.reader().use { r -> r.readText() } }
    ?: throw IllegalArgumentException("Fixture [ $fixture ] could not be found.")