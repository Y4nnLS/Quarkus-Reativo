package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.runtime.StartupEvent;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import io.vertx.core.Vertx;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.CSVParser;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

@ApplicationScoped
public class MovieService {

    void onStart(@Observes StartupEvent ev) {
        Vertx vertx = CDI.current().select(Vertx.class).get();
        vertx.runOnContext(v -> processCSV()
                .emitOn(Infrastructure.getDefaultExecutor())
                .subscribe().with(
                        item -> System.out.println("Processamento do arquivo concluído......"),
                        failure -> System.err
                                .println("Erro ao processar o arquivo CSV::::::::::::::::::::::: "
                                        + failure.getMessage())));
    }

    Uni<Void> processCSV() {
        return Uni.createFrom().voidItem().onItem().invoke(() -> {
            String filePath = "C:/Users/Ebenezer/Documents/GitHub/Quarkus-Reativo/code-with-quarkus/src/main/java/org/acme/movielist copy.csv";
            try (Reader reader = new FileReader(filePath);
                    CSVParser csvParser = new CSVParser(reader,
                            CSVFormat.DEFAULT.withDelimiter(';').withFirstRecordAsHeader())) {
                for (CSVRecord record : csvParser) {
                    Movie movie = new Movie();
                    String yearString = record.get("year");
                    try {
                        int releaseYear = Integer.parseInt(yearString);
                        movie.setReleaseYear(releaseYear);
                    } catch (NumberFormatException e) {
                        System.err.println("Formato de ano de lançamento inválido: " + yearString);
                        continue;
                    }
                    movie.setTitle(record.get("title"));
                    movie.setStudios(record.get("studios"));
                    movie.setProducers(record.get("producers"));
                    movie.setWinner("yes".equals(record.get("winner")));

                    // System.out.println(movie);
                    persistMovie(movie);
                }
                System.out.println("Processamento do arquivo concluído.");
            } catch (Exception e) {

                System.err.println(":::Erro ao processar o arquivo CSV: " + e.getMessage());
            }
        });
    }

    // @Transactional
    void persistMovie(Movie movie) {
        Vertx vertx = CDI.current().select(Vertx.class).get();
        vertx.runOnContext(v -> {
            System.out.println("Início do método persistMovie");

            if (movie == null) {
                System.err.println("Erro: o objeto movie é nulo");
                return;
            } else {
                System.out.println(movie);
            }

            try {
                // Mova a transação para um thread de worker
                Infrastructure.getDefaultExecutor().execute(() -> {
                    Movie.persist(movie).subscribe().with(
                            persisted -> System.out.println("Filme persistido com sucesso"),
                            failure -> {
                                System.err.println("Erro ao persistir o filme: " + failure.getMessage());
                                System.out.println("Falha na persistência do filme");
                            });
                });
            } catch (Exception e) {
                System.err.println("Exceção ao persistir o filme: " + e.getMessage());
            }

            System.out.println("Final do método persistMovie");
        });
    }

    @GET
    @Path("movies")
    @WithTransaction
    public Uni<List<Movie>> getAllMovies() {
        return Movie.listAll();
    }

    @Transactional
    public void addMovie(Movie movie) {
        movie.persist();
    }

    @Transactional
    public Uni<Map<String, List<ProducerInterval>>> getAwardIntervals() {
        return getAllMovies().onItem().transform(movies -> {
            Map<String, List<Movie>> moviesByProducer = movies.stream()
                    .filter(Movie::isWinner)
                    .flatMap(movie -> Arrays.stream(movie.getProducers().split(";| e | and "))
                            .map(producer -> new AbstractMap.SimpleEntry<>(producer.strip(), movie)))
                    .collect(Collectors.groupingBy(Map.Entry::getKey,
                            Collectors.mapping(Map.Entry::getValue, Collectors.toList())));

            Map<String, List<ProducerInterval>> intervalsByProducer = new HashMap<>();
            for (Map.Entry<String, List<Movie>> entry : moviesByProducer.entrySet()) {
                String producer = entry.getKey();
                List<Movie> producerMovies = entry.getValue();
                producerMovies.sort(Comparator.comparing(Movie::getReleaseYear));

                List<ProducerInterval> intervals = new ArrayList<>();
                for (int i = 1; i < producerMovies.size(); i++) {
                    Movie previousMovie = producerMovies.get(i - 1);
                    Movie currentMovie = producerMovies.get(i);
                    int interval = currentMovie.getReleaseYear() - previousMovie.getReleaseYear();
                    intervals.add(new ProducerInterval(producer, interval, previousMovie.getReleaseYear(),
                            currentMovie.getReleaseYear()));
                }
                intervalsByProducer.put(producer, intervals);
            }

            return intervalsByProducer;
        });
    }

    public Uni<Map<String, List<ProducerInterval>>> getMinAndMaxIntervals() {
        return getAwardIntervals().onItem().transform(awardIntervals -> {
            List<ProducerInterval> minIntervals = new ArrayList<>();
            List<ProducerInterval> maxIntervals = new ArrayList<>();
            int minInterval = Integer.MAX_VALUE;
            int maxInterval = Integer.MIN_VALUE;

            for (List<ProducerInterval> intervals : awardIntervals.values()) {
                for (ProducerInterval interval : intervals) {
                    if (interval.getInterval() < minInterval) {
                        minInterval = interval.getInterval();
                        minIntervals.clear();
                        minIntervals.add(interval);
                    } else if (interval.getInterval() == minInterval) {
                        minIntervals.add(interval);
                    }

                    if (interval.getInterval() > maxInterval) {
                        maxInterval = interval.getInterval();
                        maxIntervals.clear();
                        maxIntervals.add(interval);
                    } else if (interval.getInterval() == maxInterval) {
                        maxIntervals.add(interval);
                    }
                }
            }

            Map<String, List<ProducerInterval>> result = new HashMap<>();
            result.put("min", minIntervals);
            result.put("max", maxIntervals);
            return result;
        });
    }

}
