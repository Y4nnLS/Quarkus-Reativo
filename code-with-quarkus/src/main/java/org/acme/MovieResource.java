package org.acme;

import io.smallrye.mutiny.Multi;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/movies")
public class MovieResource {

    private final MovieService movieService;

    public MovieResource(MovieService movieService) {
        this.movieService = movieService;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<Movie> getAll() {
        return movieService.getAllMovies().onItem().transformToMulti(Multi.createFrom()::iterable);
    }
}
