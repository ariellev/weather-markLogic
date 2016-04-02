package com.markLogic.weather;

import com.google.gson.Gson;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ariellev on 01.04.16.
 */
public class GeoQueryParserTest {

    //{"place":"New York City", "state":"New York", "state_code":"NY", "latitude":40.7069, "longitude":-73.6731}

    private class MockPlaceBuilder implements IPlaceBuilder {

        private Place place;

        public MockPlaceBuilder(String json) {
            place = parse(json);
        }

        @Override
        public Place getPlace(String placeName) {
            if (placeName.equalsIgnoreCase("New York City")) {
                return this.place;
            } else return null;
        }

        @Override
        public Place parse(String json) {
            Gson gson = new Gson();
            Place place = gson.fromJson(json, Place.class);
            return place;
        }
    }

    @Test
    public void testNoGeoQuery() {
        String q = "in new york city and injuries GT 20";
        MockPlaceBuilder mockPlaceBuilder = new MockPlaceBuilder("{\"place\":\"New York City\", \"state\":\"New York\", \"state_code\":\"NY\", \"latitude\":40.7069, \"longitude\":-73.6731}");
        GeoQueryParser parser = new GeoQueryParser(mockPlaceBuilder, q);
        Assert.assertFalse(parser.isGeoQuery());
        Assert.assertNull(parser.getGeoQuery());
        Assert.assertEquals("in new york city and injuries GT 20", parser.getNonGeoQuery());
    }
    @Test
    public void testSimpleGeo() {
        GeoQueryParser parser = new GeoQueryParser("IN (500, 30.4, -88.12)");
        Assert.assertEquals(30.4, parser.getLat(), 0);
        Assert.assertEquals(-88.12, parser.getLon(), 0);
        Assert.assertEquals(500, parser.getRadius(), 0);
        Assert.assertTrue(parser.isGeoQuery());
        Assert.assertEquals("geo:\"@500 30.4,-88.12\"", parser.format());
    }

    @Test
    public void testPlace() {
        MockPlaceBuilder mockPlaceBuilder = new MockPlaceBuilder("{\"place\":\"New York City\", \"state\":\"New York\", \"state_code\":\"NY\", \"latitude\":40.7069, \"longitude\":-73.6731}");
        GeoQueryParser parser = new GeoQueryParser(mockPlaceBuilder, "in 80, New York City");
        Assert.assertEquals(40.7069, parser.getLat(), 0);
        Assert.assertEquals(-73.6731, parser.getLon(), 0);
        Assert.assertEquals(80, parser.getRadius(), 0);
        Assert.assertTrue(parser.isGeoQuery());
        Assert.assertEquals("geo:\"@80 40.7069,-73.6731\"", parser.format());

        parser = new GeoQueryParser(mockPlaceBuilder, "in New York City");
        Assert.assertEquals(40.7069, parser.getLat(), 0);
        Assert.assertEquals(-73.6731, parser.getLon(), 0);
        Assert.assertEquals(50, parser.getRadius(), 0);
        Assert.assertTrue(parser.isGeoQuery());
        Assert.assertEquals("geo:\"@50 40.7069,-73.6731\"", parser.format());
    }

    @Test
    public void testExtractGeoQuery() {
        String q = "before OR in (80, New York City)  AND injuries GT 50";
        GeoQueryParser parser = new GeoQueryParser(q);
        Assert.assertEquals("80, New York City", parser.extractGeoQuery(q));
        Assert.assertEquals("before OR injuries GT 50", parser.extractNonGeoQuery(q));

        q = "before and IN (500, 30.4, -88.12) or something else";
        Assert.assertEquals("500, 30.4, -88.12", parser.extractGeoQuery(q));
        Assert.assertEquals("before and something else", parser.extractNonGeoQuery(q));

        q = "in (New York City) or injuries GT 10";
        Assert.assertEquals("New York City", parser.extractGeoQuery(q));
        Assert.assertEquals("injuries GT 10", parser.extractNonGeoQuery(q));

        q = "in (New York City) and injuries GT 10";
        Assert.assertEquals("New York City", parser.extractGeoQuery(q));
        Assert.assertEquals("injuries GT 10", parser.extractNonGeoQuery(q));
    }

    @Test
    public void testEmptyQuery() {
        GeoQueryParser parser = new GeoQueryParser("");
        Assert.assertFalse(parser.isGeoQuery());
    }
}
