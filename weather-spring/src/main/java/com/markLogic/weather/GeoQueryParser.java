package com.markLogic.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by ariellev on 01.04.16.
 */
public class GeoQueryParser {
    private String place;
    private double lat, lon;
    private int radius = 50;
    private String placeName;
    private String geoQuery;

    private static final Logger logger = LoggerFactory.getLogger(GeoQueryParser.class);
    private String nonGeoQuery;

    private double roundMe(String d) {
        return Math.round(Double.parseDouble(d) * 10000.0) / 10000.0;
    }

    private final String GEO_QUERY_PATTERN = "(?i)[^a-zA-Z]+(in[ \\(]+[^\\)]*)";

    public String extractNonGeoQuery(String query) {
        Pattern r = Pattern.compile(GEO_QUERY_PATTERN);
        Matcher m = r.matcher(" " + query);
        if (m.find()) {
            String geo = m.group(1);
            geo = geo.replace("(", "\\(").replace(")", "\\)");
            String nonGeo = query.replaceAll(geo + "(?i)[\\) ]*(AND\\s+|OR\\s+)?", "");
            return nonGeo;
        } else {
            return query;
        }
    }

    public String extractGeoQuery(String query) {
        String geo = null;
        Pattern r = Pattern.compile(GEO_QUERY_PATTERN);
        Matcher m = r.matcher(" " + query);
        if (m.find()) {
            geo = m.group(1).replace("(", "").replace(")", "").replaceAll("(?i)in", "").trim();
        }
        return geo;
    }

    private void parse(IPlaceBuilder placeBuilder, String query) {
        geoQuery = extractGeoQuery(query);
        nonGeoQuery = extractNonGeoQuery(query);

        if (geoQuery == null)
            return;

        String pattern = "(?i)in\\s*\\(\\s*([0-9]+)\\s*,\\s*([0-9\\.\\-]+)\\s*,\\s*([0-9\\.\\-]+)\\s*\\)";
        Pattern r = Pattern.compile(pattern);

        String[] params = geoQuery.split(",");
        try {
            radius = Integer.parseInt(params[0]);
            lat = roundMe(params[1]);
            lon = roundMe(params[2]);
        } catch (Exception e) {
            logger.debug("no match.. fallback to place");
            try {
                placeName = params[0].trim();
                if (params.length > 1) {
                    radius = Integer.parseInt(params[0]);
                    placeName = params[1].trim();
                }

                logger.info("looking up place=" + placeName);
                Place place = placeBuilder.getPlace(placeName);
                lat = roundMe(place.getLatitude());
                lon = roundMe(place.getLongitude());
            } catch (Exception e2) {
                logger.warn("NO MATCH.");
                geoQuery = null;
                nonGeoQuery = query;
            }
        }
        logger.info("lat={}, lon={}, radius={}", lat, lon, radius);
    }

    public GeoQueryParser(String query) {
        parse(null, query);
    }

    public GeoQueryParser(IPlaceBuilder placeBuilder, String query) {
        parse(placeBuilder, query);
    }

    public String getPlace() {
        return place;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public int getRadius() {
        return radius;
    }

    public boolean isGeoQuery() {
        return geoQuery != null && lat != 0 && lon != 0;
    }

    public String getGeoQuery() {
        return geoQuery;
    }

    public String format() {
        return String.format("geo:\"@%s %s,%s\"", radius, lat, lon);
    }


    public String getNonGeoQuery() {
        return nonGeoQuery;
    }
}