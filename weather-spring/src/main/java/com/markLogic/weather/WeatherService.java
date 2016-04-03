package com.markLogic.weather;

import com.google.gson.Gson;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.DocumentMetadataHandle;
import com.marklogic.client.io.InputStreamHandle;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.query.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.StringJoiner;

/**
 * Created by ariellev on 20.03.16.
 */
@Service
public class WeatherService implements IWeatherService {
    private static final String EVENT_OPTIONS = "weather-event-options";
    private DatabaseClient client;
    private JSONDocumentManager docMgr;
    private QueryManager queryMgr;

    private EventBuilder eventBuilder;
    private PlaceBuilder placeBuilder;

    @Value("${weather.user}")
    private String user;

    @Value("${weather.password}")
    private String password;

    @Value("${weather.host}")
    private String host;

    @Value("${weather.port}")
    private int port;

    private static final Logger logger = LoggerFactory.getLogger(WeatherService.class);

    @PostConstruct
    private void init() {
        logger.info("init");
        logger.info("user={}, password={}, host={}, port={}", user, password, host, port);
        client = DatabaseClientFactory.newClient(host, port, user, password, DatabaseClientFactory.Authentication.DIGEST);
        docMgr = client.newJSONDocumentManager();
        queryMgr = client.newQueryManager();

        eventBuilder = new EventBuilder();
        eventBuilder.setDocumentManager(docMgr);

        placeBuilder = new PlaceBuilder(docMgr);
    }

    @PreDestroy
    private void destory() {
        logger.info("releasing markLogic client");
        client.release();
    }

    @Override
    public Event getEvent(String id) {
        return eventBuilder.getEvent(getUri(id));
    }

    @Override
    public Event[] getEventsByType(String type) {
        StringQueryDefinition query = queryMgr.newStringDefinition();//.newRawQueryByExampleDefinition(qbeHandle, "default");
        query.setCollections(type);

        //StringHandle resultsHandle = queryMgr.search(query, new StringHandle().withFormat(Format.JSON));

        SearchHandle resultsHandle = queryMgr.search(query, new SearchHandle());
        return parseEvents(resultsHandle, type);
    }

    private String makeSnippet(MatchDocumentSummary docSummary, String excludedSnippets) {
        String snippet = "";
        // get the list of match locations for this result
        MatchLocation[] locations = docSummary.getMatchLocations();
        System.out.println("Matched " + locations.length + " locations in " + docSummary.getUri() + ":");

        for (MatchLocation location : locations) {

            // iterate over the snippets at a match location
            for (MatchSnippet match : location.getSnippets()) {
                boolean isHighlighted = match.isHighlighted();
                String text = match.getText();
                if (!excludedSnippets.contains(text)) {
                    if (isHighlighted) {
                        snippet += String.format(" <span class=\"highlight\">%s</span>", text);
                    } else snippet += text;
                }
            }
        }

        return snippet;
    }


    private Event[] parseEvents(SearchHandle resultsHandle, String excludedSnippets) {
        // all matched documents
        MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
        logger.info("Listing " + docSummaries.length + " documents");

        Event[] events = new Event[docSummaries.length];
        if (docSummaries.length > 0) {
            int i = 0;
            for (MatchDocumentSummary docSummary : docSummaries) {
                // read constituent documents
                Event event = eventBuilder.getEvent(docSummary.getUri());
                if (!event.getRemarks().isEmpty()) {
                    event.setSnippet(makeSnippet(docSummary, excludedSnippets));
                }
                logger.info("event.snippet={}", event.getSnippet());
                events[i] = event;
                i++;
            }
        }
        return events;
    }

    @Override
    public Event[] searchEvents(String queryString, Long fromDate, Long toDate, String type, String state,
                                int pageNum, int pageLength) {
        logger.info("searchEvents, queryString={}", queryString);
        logger.info("searchEvents, from={}, to={}", new Date(fromDate), new Date(toDate));
        logger.info("searchEvents, type={}, state={}", type, state);

        int start = pageLength * (pageNum - 1) + 1;
        logger.info("searchEvents, pageNum={}, pageLength={}, start={}", pageNum, pageLength, start);

        Set<String> query = new HashSet<String>();
        StringJoiner joiner = new StringJoiner(" AND ");

        GeoQueryParser geoQueryParser = new GeoQueryParser(placeBuilder, queryString);
        if (geoQueryParser.isGeoQuery()) {
            joiner.add(geoQueryParser.format());
            queryString = geoQueryParser.getNonGeoQuery();
        }

        if (!queryString.trim().isEmpty()) {
            joiner.add(queryString);
        }

        // + "AND epoch_time GT " + fromDate + " AND epoch_time LT " + toDate;
        if (!type.isEmpty()) {
            joiner.add("event_type:" + type);
        }
        if (!state.isEmpty()) {
            joiner.add("state:" + state);
        }

        String qString = joiner.toString();
        logger.info("searchEvents, qString={}", qString);

        queryMgr.setPageLength(pageLength);
        StringQueryDefinition querydef = queryMgr.newStringDefinition(EVENT_OPTIONS);
        querydef.setCriteria(qString);

        String collection = "all-events";
        querydef.setCollections(collection);
        SearchHandle resultsHandle = queryMgr.search(querydef, new SearchHandle(), start);

        logger.info("Matched {} documents in collection '{}'", resultsHandle.getTotalResults(), querydef.getCollections());

        Event[] events = parseEvents(resultsHandle, type);
        return events;
    }

    private String getUri(String id) {
        return "/events/" + id;
    }

    @Override
    public void updateEvent(Event event) {
        if (event.getId() == null) {
            String id = event.generateId();
            event.setId(id);
        }

        String uri = getUri(event.getId());
        logger.info("updateEvent, uri={}", uri);

        Gson gson = new Gson();
        String eventAsString = gson.toJson(event, Event.class);
        InputStream in = new ByteArrayInputStream(eventAsString.getBytes(StandardCharsets.UTF_8));
        InputStreamHandle handle = new InputStreamHandle(in);

        DocumentMetadataHandle metadata = new DocumentMetadataHandle().withCollections("all-events", event.getEventType(), event.getState());

        docMgr.write(uri, metadata, handle);

        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void deleteEvent(String id) {
        String uri = getUri(id);
        docMgr.delete(uri);
    }

    @Override
    public Place getPlace(String name) {
        return placeBuilder.getPlace(name);
    }
}
