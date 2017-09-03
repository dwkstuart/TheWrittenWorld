package com.example.dwks.thewrittenworld;

import android.os.Parcel;
import android.support.test.runner.AndroidJUnit4;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.TreeSet;

/**
 * Created by User on 02/09/2017.
 */
@RunWith(AndroidJUnit4.class)
public class SearchTest extends ExampleInstrumentedTest{
    private PlaceObject testObject;
    private JSONObject testJsonObject = new JSONObject("  {\n" +
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
    private JSONArray inputJArray = null;
    private TreeSet<PlaceObject> testList = new TreeSet<>();
    private String testJson = "[ {\n" +
            "    \"db_key\": 1,\n" +
            "    \"title\": \"The Cutting Room\",\n" +
            "    \"author\": \"Louise Welsh\",\n" +
            "    \"location\": \"University Avenue\",\n" +
            "    \"latitude\": 55.87292,\n" +
            "    \"longitude\": -4.29239,\n" +
            "    \"quote\": \"I pulled up the collar of my raincoat and waled on. Climbing the rise of University Avenue, towards the illuminated towers of the university, their haze clouding any view of the stars.\",\n" +
            "    \"describtion\": \"Rilke, the protagonist of Welsh's crime thriller walks through the West End in the early hours of the morning after a night's drinking. - The novel, set in Glasgow, revolves around the central character, Rilke, an auctioneer who has agreed to quickly process and sell an inventory of largely valuable contents belonging to a recently deceased old man in exchange for a considerable fee.\",\n" +
            "    \"isbn\": \"978-1841954042\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"db_key\": 2,\n" +
            "    \"title\": \"Garnethill\",\n" +
            "    \"author\": \"Denise Mina\",\n" +
            "    \"location\": \"Glasgow University Library\",\n" +
            "    \"latitude\": 55.8733,\n" +
            "    \"longitude\": -4.2884,\n" +
            "    \"quote\": \"The walls are floor-to-ceiling smoke-tinted glass, giving the sprawled city below and unreal quality.\",\n" +
            "    \"describtion\": \"Mauren vists the library to consult the university newspaper collection, looking for answers.\",\n" +
            "    \"isbn\": \"\"\n" +
            "  },\n" +
            "  {\n" +
            "    \"db_key\": 3,\n" +
            "    \"title\": \"Still Dark\",\n" +
            "    \"author\": \"Alex Gray\",\n" +
            "    \"location\": \"University Gardens\",\n" +
            "    \"latitude\": 55.87277,\n" +
            "    \"longitude\": -4.29012,\n" +
            "    \"quote\": \"A quick glance upwards showed the stained-glass windows of his office that looked out over the crest of the hill and the old university buildings, the place that Solly now whimsically regarded as his second home\",\n" +
            "    \"describtion\": \"Professor Brightman arrives at University for a normal day's worked before a telephone call from Detective Lorimer draws him into the case.\",\n" +
            "    \"isbn\": \"\"\n" +
            "  }]";

    public SearchTest() throws JSONException {
    }

    @Test
    public void jsonToInputArray(){

        try {
            inputJArray = new JSONArray(testJson);
        }
        catch (JSONException e){

        }

        assert(inputJArray.length()==3);
    }

    @Before
    public void setUpTest(){
        this.jsonToInputArray();
    }

    @Test
    public void setTestList(){
        assert(testList.isEmpty());

        for (int i = 0; i < inputJArray.length(); i++){
            JSONObject testObject = null;
            try {
                testObject = inputJArray.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            PlaceObject testPlaceObject = new PlaceObject(testObject);
            testList.add(testPlaceObject);
        }

        assert (testList.size()==3);
        PlaceObject test = testList.first();
        assert test.isVisited()==false;
        assert test.compareTo(testList.last())!=0;


    }

    @Before
    public void testCreatePlaceObject(){
        testObject = new PlaceObject(testJsonObject);
         }
    @Test
    public void testPlaceObjectIsPacelable(){
        assert testObject.getBookTitle().equals("The Cutting Room");
        assert testObject.isVisited()==false;

        Parcel testParcel = Parcel.obtain();
        testObject.writeToParcel(testParcel,0);
        testParcel.setDataPosition(0);

        PlaceObject placeFromParcel = PlaceObject.CREATOR.createFromParcel(testParcel);

        assert testObject.equals(placeFromParcel);
        assert placeFromParcel.getBookTitle().equals("The Cutting Room");

        assert placeFromParcel.getAuthorName().equals("Bobby Davro");
   }


}