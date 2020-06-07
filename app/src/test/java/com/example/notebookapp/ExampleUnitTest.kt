package com.example.notebookapp

import org.junit.Test

import org.junit.Assert.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun unit_test() {
        val date = Date()

        print(SimpleDateFormat( "E HH:mm:ss dd/mm/yyyy").format(date))
    }
}
