package com.markLogic.weather;

import com.google.gson.Gson;
import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.*;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryDefinition;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
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

/**
 * Created by ariellev on 20.03.16.
 */
@Service
public class WeatherService implements IWeatherService {
    private DatabaseClient client;
    private JSONDocumentManager docMgr;
    private QueryManager queryMgr;

    private EventBuilder eventBuilder;

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
    }

    @PreDestroy
    private void destory() {
        logger.info("releasing markLogic client");
        client.release();
    }

    @Override
    public String getEvent(String id) {
        return docMgr.read(getUri(id), new StringHandle()).get();
    }

    @Override
    public Event[] getEventsByType(String type) {
        StringQueryDefinition query = queryMgr.newStringDefinition();//.newRawQueryByExampleDefinition(qbeHandle, "default");
        query.setCollections(type);

        //StringHandle resultsHandle = queryMgr.search(query, new StringHandle().withFormat(Format.JSON));

        SearchHandle resultsHandle = queryMgr.search(query, new SearchHandle());
        return parseEvents(resultsHandle);
    }

    private Event[] parseEvents(SearchHandle resultsHandle) {
        // all matched documents
        MatchDocumentSummary[] docSummaries = resultsHandle.getMatchResults();
        logger.info("Listing " + docSummaries.length + " documents");

        Event[] events = new Event[docSummaries.length];
        if (docSummaries.length > 0) {
            int i = 0;
            for (MatchDocumentSummary docSummary : docSummaries) {
                // read constituent documents
                Event event = eventBuilder.getEvent(docSummary.getUri());
                events[i] = event;
                i++;
            }
        }
        return events;
    }

    private Event[] genericSearch(String arg, long start, int pageLength, boolean facetsOnly) {
        logger.info("Performing search() with arg = " + (arg == "" ? " EMPTY_STRING" : arg));
        String options = null;

/*        if (facetsOnly == "TRUEALL") {
        }*/

        logger.info(" options uri set to " + options);

        // create a search definition
        StringQueryDefinition querydef = queryMgr.newStringDefinition(options);

        // set the search argument
        querydef.setCriteria(arg);

        // create a handle for the search results
        SearchHandle resultsHandle = new SearchHandle();

        // set the page length
        queryMgr.setPageLength(pageLength);

        // run the search
        queryMgr.search(querydef, resultsHandle, start);

        logger.info("Matched {} documents with '{}'", resultsHandle.getTotalResults(), querydef.getCriteria());

        Event[] events = parseEvents(resultsHandle);
        return events;
    }

    @Override
    public Event[] searchEvents(String queryString, Long fromDate, Long toDate, String type, String state, long start, int pageLength) {
        logger.info("searchEvents, queryString={}", queryString);
        logger.info("searchEvents, from={}, to={}", new Date(fromDate), new Date(toDate));
        logger.info("searchEvents, type={}, state={}", type, state);
        logger.info("searchEvents, start={}, pageLength={}", start, pageLength);

        QueryDefinition querydef;
        if (queryString.isEmpty()) {
            logger.info("Empty text field, using query by example");
            //String rawJSONQuery = "{\"$query\": { \"$and\":[{\"epoch_time\":{\"$lt\":1458685818883}},{\"epoch_time\":{\"$ge\":-621907200000}}]}}";
            String rawJSONQuery = "{\"$query\": {}}";
            StringHandle rawHandle = new StringHandle();
            rawHandle.withFormat(Format.JSON).set(rawJSONQuery);

            querydef = queryMgr.newRawQueryByExampleDefinition(rawHandle);
        } else {
            logger.info("Non empty text field, using string query");
            StringQueryDefinition qd = queryMgr.newStringDefinition();
            qd.setCriteria(queryString);
            querydef = qd;
        }

        String collection = type.isEmpty() ? "all-events" : type;
        if (collection.equals("all-events")) {
            collection = state.isEmpty() ? collection : state;
        }

        querydef.setCollections(collection);
        SearchHandle resultsHandle = queryMgr.search(querydef, new SearchHandle());

        logger.info("Matched {} documents in collection '{}'", resultsHandle.getTotalResults(), querydef.getCollections());

        Event[] events = parseEvents(resultsHandle);
        return events;
    }

    @Override
    public Event[] searchEventsByPlace(String place, Long fromDate, Long toDate, String type, String state, long start, int pageLength) {
        logger.info("searchEventsByPlace, place={}", place);
        String rawJSONQuery = "\"{\"$query\": { \"place\": \"" + place + "\" }}";
        StringHandle rawHandle = new StringHandle();
        rawHandle.withFormat(Format.JSON).set(rawJSONQuery);

        QueryDefinition querydef = queryMgr.newRawQueryByExampleDefinition(rawHandle);
        querydef.setCollections("raw-metadata");

        SearchHandle resultsHandle = queryMgr.search(querydef, new SearchHandle());

        logger.info("Matched {} documents in collection '{}'", resultsHandle.getTotalResults(), querydef.getCollections());

        Event[] events = parseEvents(resultsHandle);

        return new Event[0];
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
}
