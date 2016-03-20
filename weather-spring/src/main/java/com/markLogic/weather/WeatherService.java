package com.markLogic.weather;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.DatabaseClientFactory;
import com.marklogic.client.document.JSONDocumentManager;
import com.marklogic.client.io.SearchHandle;
import com.marklogic.client.io.StringHandle;
import com.marklogic.client.query.MatchDocumentSummary;
import com.marklogic.client.query.QueryManager;
import com.marklogic.client.query.StringQueryDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

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
        logger.info("destroy");
        client.release();
    }

    @Override
    public String getEvent(String id) {
        return docMgr.read("/events/" + id, new StringHandle()).get();
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
}
