package com.example.dwks.thewrittenworld;


import android.support.test.espresso.ViewInteraction;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class searchBoxTest extends ExampleInstrumentedTest {

    @Before
    public void constantPopulate(){
        JSONObject testJsonObject = null;
        try {
            testJsonObject = new JSONObject("  {\n" +
                    "    \"db_key\": 1,\n" +
                    "    \"title\": \"The Cutting Room\",\n" +
                    "    \"author\": \"Louise Welsh\",\n" +
                    "    \"location\": \"University Avenue\",\n" +
                    "    \"latitude\": 55.87292,\n" +
                    "    \"longitude\": -4.29239,\n" +
                    "    \"quote\": \"I pulled up the collar of my raincoat and waled on. Climbing the rise of University Avenue, towards the illuminated towers of the university, their haze clouding any view of the stars.\",\n" +
                    "    \"describtion\": \"Rilke, the protagonist of Welsh's crime thriller walks through the West End in the early hours of the morning after a night's drinking. - The novel, set in Glasgow, revolves around the central character, Rilke, an auctioneer who has agreed to quickly process and sell an inventory of largely valuable contents belonging to a recently deceased old man in exchange for a considerable fee.\",\n" +
                    "    \"isbn\": \"978-1841954042\"\n" +
                    "  }");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Constants.placeObjects.add(new PlaceObject(testJsonObject));

    }
    @Rule
    public ActivityTestRule<Search> mActivityTestRule = new ActivityTestRule<>(Search.class);



    @Test
    public void searchBoxTest() {

          // Added a sleep statement to match the app's execution delay.
        // The recommended way to handle such scenarios is to use Espresso idling resources:
        // https://google.github.io/android-testing-support-library/docs/espresso/idling-resource/index.html
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.view_selection), withText("View and Confirm Selection"), isDisplayed()));


      }



    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
