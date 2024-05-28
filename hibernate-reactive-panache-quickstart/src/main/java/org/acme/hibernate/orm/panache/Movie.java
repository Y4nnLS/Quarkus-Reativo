package org.acme.hibernate.orm.panache;

import io.quarkus.hibernate.reactive.panache.PanacheEntity;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;

@Entity
@Cacheable
public class Movie extends PanacheEntity{

    @Column(name = "release_year")
    public int releaseYear;

    @Column(name = "title", nullable = false, length = 100)
    public String title;

    @Column(name = "studios", nullable = false, length = 100)
    public String studios;

    @Column(name = "producers", nullable = false, length = 500)
    public String producers;

    @Column(name = "winner")
    public boolean winner;

    public Movie(){
    }

    public Movie(int releaseYear, String title,  String studios, String producers, boolean winner) {
        this.releaseYear = releaseYear;
        this.title = title;
        this.studios = studios;
        this.producers = producers;
        this.winner = winner;
    }

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
        return this.winner;
    }

    public void setWinner(boolean winner) {
        this.winner = winner;
    }
    
}
