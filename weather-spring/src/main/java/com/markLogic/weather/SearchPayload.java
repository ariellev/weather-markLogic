package com.markLogic.weather;

/**
 * Created by ariellev on 22.03.16.
 */
public class SearchPayload {

    String query;
    Long fromDate, toDate;
    String state, eventType;

    public SearchPayload() {
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public String toString() {
        return "SearchPayload{" +
                "query='" + query + '\'' +
                ", fromDate=" + fromDate +
                ", toDate=" + toDate +
                ", state='" + state + '\'' +
                ", eventType='" + eventType + '\'' +
                '}';
    }
}
