package com.markLogic.weather;

import com.google.gson.Gson;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.StringHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ariellev on 20.03.16.
 */
public class EventBuilder {

    private static final Logger logger = LoggerFactory.getLogger(EventBuilder.class);
    private JSONDocumentManager docMgr;

    public Event getEvent(String uri) {
        logger.info("getting event, uri={}", uri);
        String json = docMgr.read(uri, new StringHandle()).get();
        Gson gson = new Gson();
        Event event = gson.fromJson(json, Event.class);
        logger.info("parsed event={}", event);
        return event;
    }

    public void setDocumentManager(JSONDocumentManager docMgr) {
        this.docMgr = docMgr;
    }

}
