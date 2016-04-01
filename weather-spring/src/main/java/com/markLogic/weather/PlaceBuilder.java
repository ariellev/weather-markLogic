package com.markLogic.weather;

import com.google.gson.Gson;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.StringHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ariellev on 01.04.16.
 */
public class PlaceBuilder implements IPlaceBuilder {

    private static final Logger logger = LoggerFactory.getLogger(PlaceBuilder.class);

    private JSONDocumentManager docMgr;

    public PlaceBuilder(JSONDocumentManager docMgr) {
        this.docMgr = docMgr;
    }

    @Override
    public Place getPlace(String placeName) {
        String uri = "/places/" + placeName;
        logger.info("getting place, uri={}", uri);
        String json = docMgr.read(uri, new StringHandle()).get();
        return parse(json);
    }

    @Override
    public Place parse(String json) {
        Gson gson = new Gson();
        Place place = gson.fromJson(json, Place.class);
        return place;
    }
}
