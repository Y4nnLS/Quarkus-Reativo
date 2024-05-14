package org.acme;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import io.quarkus.hibernate.orm.panache.PanacheEntity;

@Entity
@Table(name = "movies")
public class Movie extends PanacheEntity {
    @Column(name = "release_year")
    private int releaseYear;

    @Column(name = "title", nullable = false, length = 100)
    private String title;

    @Column(name = "studios", nullable = false, length = 100)
    private String studios;

    @Column(name = "producers", nullable = false, length = 500)
    private String producers;

    @Column(name = "winner")
    private boolean winner;

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStudios() {
        return studios;
    }

    public void setStudios(String studios) {
        this.studios = studios;
    }

    public String getProducers() {
        return producers;
    }

    public void setProducers(String producers) {
        this.producers = producers;
    }

    public boolean isWinner() {
        return winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
    
    @Override
    public String toString(){
        return "Filme [year=" + releaseYear + ", title=" + title + ", studios=" + studios + ", producers=" + producers + ", winner=" + winner + "]";    }
}
