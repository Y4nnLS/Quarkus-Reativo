package org.acme;

import io.smallrye.mutiny.Uni;
import java.util.List;

public class Producer {
    private Uni<String> name;
    private Uni<List<Integer>> winningYears;

    // Getters and setters
    public Uni<String> getName() {
        return this.name;
    }

    public void setName(Uni<String> name) {
        this.name = name;
    }

    public Uni<List<Integer>> getWinningYears() {
        return this.winningYears;
    }

    public void setWinningYears(Uni<List<Integer>> winningYears) {
        this.winningYears = winningYears;
    }
}
