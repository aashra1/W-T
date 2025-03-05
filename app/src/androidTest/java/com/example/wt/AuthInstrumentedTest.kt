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
import com.example.wt.login.activity.LoginActivity


import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@LargeTest
@RunWith(AndroidJUnit4::class)
class AuthInstrumentedTest {
    @get:Rule
    val testRule = ActivityScenarioRule(LoginActivity::class.java)
    @Test
    fun checkSum() {
        onView(withId(R.id.emailText1)).perform(
            typeText("oshika@gmail.com")
        )

        onView(withId(R.id.passwordText1)).perform(
            typeText("oshika123")
        )

        closeSoftKeyboard()

        Thread.sleep(1500)

        onView(withId(R.id.loginBtn)).perform(
            click()
        )

        Thread.sleep(1500)
        onView(withId(R.id.displayLogin)).check(matches(withText("2")))
    }

}
