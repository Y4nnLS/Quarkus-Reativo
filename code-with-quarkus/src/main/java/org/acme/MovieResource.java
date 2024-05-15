package org.acme;

// import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
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
    public Uni<List<Movie>> getAll() {
        return movieService.getAllMovies().onItem().transformToMulti(Multi.createFrom()::iterable).collect().asList();
    }
}
