package org.acme;

// import io.quarkus.panache.common.Sort;
// import io.quarkus.hibernate.reactive.panache.common.WithSession;
// import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
// import jakarta.ws.rs.Produces;
// import jakarta.ws.rs.core.MediaType;
import java.util.List;

import org.jboss.logging.Logger;


@Path("/movies")
@ApplicationScoped

public class MovieResource {

    private static final Logger LOGGER = Logger.getLogger(MovieResource.class.getName());
    
    @GET
    public Uni<List<Movie>> getAll() {
        return Movie.listAll();
    }
}
