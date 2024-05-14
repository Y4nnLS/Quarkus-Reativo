package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/movies")
public class MovieResource {

    private final MovieService movieService;

    public MovieResource(MovieService movieService) {
        this.movieService = movieService;
    }
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Movie> getAll() {
        return movieService.getAllMovies();
    }
}
