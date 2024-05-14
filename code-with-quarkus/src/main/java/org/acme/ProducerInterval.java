package org.acme;

// import io.smallrye.mutiny.Uni;

public class ProducerInterval {
    private String producer;
    private Integer interval;
    private Integer previousYear;
    private Integer currentYear;

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public Integer getInterval() {
        return interval;
    }

    public void setInterval(Integer interval) {
        this.interval = interval;
    }

    public Integer getPreviousYear() {
        return previousYear;
    }

    public void setPreviousYear(Integer previousYear) {
        this.previousYear = previousYear;
    }

    public Integer getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(Integer currentYear) {
        this.currentYear = currentYear;
    }

    public ProducerInterval(String producer, Integer interval, Integer previousYear, Integer currentYear) {
        this.producer = producer;
        this.interval = interval;
        this.previousYear = previousYear;
        this.currentYear = currentYear;
    }

    @Override
    public String toString() {
        return "{"
                + "\"producer\": \"" + producer + "\","
                + "\"interval\": " + interval + ","
                + "\"previousWin\": " + previousYear + ","
                + "\"followingWin\": " + currentYear
                + "}";
    }
}
