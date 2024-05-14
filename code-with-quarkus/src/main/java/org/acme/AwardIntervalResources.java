package org.acme;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

@Path("/awardInterval")
public class AwardIntervalResources {
    private final MovieService movieService;

    public AwardIntervalResources(MovieService movieService){
        this.movieService = movieService;
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<ProducerInterval>> getAll(){
        return movieService.getAwardIntervals();
    }

    @GET
    @Path("/minmax")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, List<ProducerInterval>> getMinAndMax() {
        return movieService.getMinAndMaxIntervals();
}

}

