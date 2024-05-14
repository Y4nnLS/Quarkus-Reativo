package org.acme;

import java.util.List;

public class Producer {
    private String name;
    private List<Integer> winningYears;

    // Getters and setters
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getWinningYears() {
        return this.winningYears;
    }

    public void setWinningYears(List<Integer> winningYears) {
        this.winningYears = winningYears;
    }
}

