package com.example.wt

import androidx.test.espresso.Espresso.closeSoftKeyboard
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.wt.ui.activity.CalculationsActivity

import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@LargeTest
@RunWith(AndroidJUnit4::class)
class CalculationInstrumentedTest {
    @get:Rule
    val testRule = ActivityScenarioRule(CalculationsActivity::class.java)
    @Test
    fun checkSum() {
        onView(withId(R.id.firstvalue)).perform(
            typeText("1")
        )

        onView(withId(R.id.secondvalue)).perform(
            typeText("1")
        )

        closeSoftKeyboard()

        Thread.sleep(1500)

        onView(withId(R.id.buttoncalculate)).perform(
            click()
        )

        Thread.sleep(1500)
        onView(withId(R.id.displaysum)).check(matches(withText("2")))
    }

}
