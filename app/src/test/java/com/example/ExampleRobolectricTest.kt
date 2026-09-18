package com.example

import android.content.Context
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.example.ui.theme.MyApplicationTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Template App", appName)
  }

  @Test
  fun `template screen renders and verify button interacts`() {
    composeTestRule.setContent {
      MyApplicationTheme {
        TemplateAppScreen()
      }
    }

    composeTestRule.onNodeWithTag("hero_header_card").assertIsDisplayed()
    composeTestRule.onNodeWithTag("btn_verify_template").assertIsDisplayed()

    // Test interaction: click verify button to advance health check cycle
    composeTestRule.onNodeWithTag("btn_verify_template").performClick()
    composeTestRule.onNodeWithText("Check #2", substring = true).assertIsDisplayed()
  }
}

