package org.acme;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
// import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class MovieRepository implements PanacheRepository<Movie> {
    public void persistMovie(Movie movie) {
        System.out.println("entrou no persist: " + movie);
        persist(movie);
    }
}