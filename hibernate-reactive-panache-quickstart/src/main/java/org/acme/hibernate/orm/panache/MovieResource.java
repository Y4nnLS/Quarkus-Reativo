package org.acme.hibernate.orm.panache;

import static jakarta.ws.rs.core.Response.Status.CREATED;
import static jakarta.ws.rs.core.Response.Status.NOT_FOUND;
import static jakarta.ws.rs.core.Response.Status.NO_CONTENT;

import java.util.List;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

import org.jboss.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import io.quarkus.hibernate.reactive.panache.Panache;
import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.CompositeException;
import io.smallrye.mutiny.Uni;
import io.vertx.core.Vertx;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import java.util.ArrayList;

@Path("/movies")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class MovieResource {

    @Inject
    Vertx vertx;

    private static final Logger LOGGER = Logger.getLogger(MovieResource.class.getName());

    @GET
    public Uni<List<Movie>> get() {
        return Movie.listAll();
    }

    @GET
    @Path("{id}")
    public Uni<Movie> getSingle(Long id) {
        return Movie.findById(id);
    }

    @Transactional
    @POST
    public Uni<Response> create(Movie movie) {
        if (movie == null || movie.id != null) {
            throw new WebApplicationException("Id was invalidly set on request.", 422);
        }
        return Panache.withTransaction(movie::persist)
                .replaceWith(Response.ok(movie).status(CREATED)::build);
    }

    @PUT
    @Path("{id}")
    public Uni<Response> update(Long id, Movie movie) {
        if (movie == null || movie.title == null) {
            throw new WebApplicationException("Movie title was not set on request.", 422);
        }

        return Panache
                .withTransaction(() -> Movie.<Movie>findById(id)
                        .onItem().ifNotNull().invoke(entity -> entity.title = movie.title))
                .onItem().ifNotNull().transform(entity -> Response.ok(entity).build())
                .onItem().ifNull().continueWith(Response.ok().status(NOT_FOUND)::build);
    }

    @DELETE
    @Path("{id}")
    public Uni<Response> delete(Long id) {
        return Panache.withTransaction(() -> Movie.deleteById(id))
                .map(deleted -> deleted
                        ? Response.ok().status(NO_CONTENT).build()
                        : Response.ok().status(NOT_FOUND).build());
    }

    @POST
    @Path("bulk")
    public Uni<List<Movie>> createFromCsv() {
        String arquivoCsv = "src/main/java/org/acme/hibernate/orm/panache/movielist.csv";
        String linha;
        String separadorCsv = ";";

        List<Movie> movies = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(arquivoCsv))) {
            br.readLine();
            while ((linha = br.readLine()) != null) {
                // use comma as separator
                String[] dataMovie = linha.split(separadorCsv);
                boolean winner = false;
                if (dataMovie.length > 4) {
                    winner = "yes".equalsIgnoreCase(dataMovie[4]);
                }
                Movie movie = new Movie(Integer.parseInt(dataMovie[0]), dataMovie[1], dataMovie[2], dataMovie[3],
                        winner);
                movies.add(movie);
            }
        } catch (IOException e) {
            throw new WebApplicationException("Erro ao ler o arquivo CSV." + e, 500);
        }

        return Panache.withTransaction(() -> Movie.persist(movies))
                .replaceWith(Uni.createFrom().item(movies));
    }

    @GET
    @Path("load-csv")
    public Uni<List<Movie>> loadCsv() {
        return createFromCsv();
    }

    @GET
    @Path("awardInterval")
    public Uni<Map<String, List<Map<String, Object>>>> getAwardInterval() {
        return Movie.listAll()
                .onItem().transform(movies -> {
                    Map<String, List<Integer>> producerAwards = new HashMap<>();
                    for (PanacheEntityBase entity : movies) {
                        Movie movie = (Movie) entity;
                        if (movie.isWinner()) {
                            List<Integer> awards = producerAwards.getOrDefault(movie.getProducers(), new ArrayList<>());
                            awards.add(movie.getReleaseYear());
                            producerAwards.put(movie.getProducers(), awards);
                        }
                    }

                    List<Map<String, Object>> minList = new ArrayList<>();
                    List<Map<String, Object>> maxList = new ArrayList<>();
                    for (Map.Entry<String, List<Integer>> entry : producerAwards.entrySet()) {
                        if (entry.getValue().size() >= 2) {
                            List<Integer> sortedAwards = entry.getValue();
                            Collections.sort(sortedAwards);
                            int shortestInterval = sortedAwards.get(1) - sortedAwards.get(0);
                            int longestInterval = sortedAwards.get(sortedAwards.size() - 1) - sortedAwards.get(0);

                            Map<String, Object> minMap = new HashMap<>();
                            minMap.put("producer", entry.getKey());
                            minMap.put("interval", shortestInterval);
                            minMap.put("previousWin", sortedAwards.get(0));
                            minMap.put("followingWin", sortedAwards.get(1));
                            minList.add(minMap);

                            Map<String, Object> maxMap = new HashMap<>();
                            maxMap.put("producer", entry.getKey());
                            maxMap.put("interval", longestInterval);
                            maxMap.put("previousWin", sortedAwards.get(0));
                            maxMap.put("followingWin", sortedAwards.get(sortedAwards.size() - 1));
                            maxList.add(maxMap);
                        }
                    }

                    Map<String, List<Map<String, Object>>> intervals = new HashMap<>();
                    intervals.put("min", minList);
                    intervals.put("max", maxList);

                    return intervals;
                });
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<Exception> {

        @Inject
        ObjectMapper objectMapper;

        @Override
        public Response toResponse(Exception exception) {
            LOGGER.error("Failed to handle request", exception);

            Throwable throwable = exception;

            int code = 500;
            if (throwable instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            if (throwable instanceof CompositeException) {
                throwable = ((CompositeException) throwable).getCause();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", throwable.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", throwable.getMessage());
            }

            return Response.status(code)
                    .entity(exceptionJson)
                    .build();
        }

    }

}
