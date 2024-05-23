package org.acme;

import java.util.List;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/movies")
@Produces(MediaType.APPLICATION_JSON)
public class MovieResource {

    @Inject
    MovieRepository movieRepository;

    @GET
    public Response getAll() {
        List<Movie> movies = movieRepository.listAll();
        
        if (movies.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                           .entity("Nenhum filme encontrado no banco de dados.")
                           .build();
        } else {
            Movie firstMovie = movies.get(0);
            return Response.ok(firstMovie).build();
        }
    }
}
