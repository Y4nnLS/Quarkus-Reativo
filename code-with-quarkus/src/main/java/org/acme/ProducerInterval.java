package org.acme;

public class ProducerInterval {
    private String producer;
    private int interval;
    private int previousYear;
    private int currentYear;

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }
    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }
    public int getPreviousYear() {
        return previousYear;
    }

    public void setPreviousYear(int previousYear) {
        this.previousYear = previousYear;
    }
    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }
    public ProducerInterval(String producer, int interval, int previousYear, int currentYear) {
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
