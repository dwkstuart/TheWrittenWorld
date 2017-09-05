package com.example.dwks.thewrittenworld;

import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.example.dwks.thewrittenworld.firstLoadTest.childAtPosition;
import static org.hamcrest.Matchers.allOf;

/**
 * Created by User on 01/09/2017.
 */

public class MapDisplayTest extends ExampleInstrumentedTest{

    private ViewInteraction appCompatButton;
    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void skipLoginDisplay(){
        appCompatButton = onView(
                allOf(withId(R.id.setup), withText("Skip Login"), isDisplayed()));
    }
    @Before
    public void setAppCompatButton(){
        appCompatButton = onView(
                allOf(withId(R.id.setup), withText("Skip Login"), isDisplayed()));

    }

    @Test
    public void goesToMapTest(){
        appCompatButton.perform(click());

        // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        ViewInteraction relativeLayout = onView(
                allOf(childAtPosition(
                        childAtPosition(
                                withId(R.id.map_display),
                                0),
                        0),
                        isDisplayed()));
        relativeLayout.check(matches(isDisplayed()));


    }

    @Test
    public void loginTest(){
        assert  FirebaseAuth.getInstance().getCurrentUser() == null;
    }

}