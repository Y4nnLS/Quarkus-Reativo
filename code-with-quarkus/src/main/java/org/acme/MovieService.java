package org.acme;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.transaction.Transactional;
import io.quarkus.runtime.StartupEvent;
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

    @Transactional
    void onStart(@Observes StartupEvent ev) {
        String filePath = "C:/Users/Ebenezer/Documents/GitHub/quarkus-trabalho/code-with-quarkus/src/main/java/org/acme/movielist.csv";
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
                    System.err.println("Invalid release year format: " + yearString);
                    continue;
                }
                movie.setTitle(record.get("title"));
                movie.setStudios(record.get("studios"));
                movie.setProducers(record.get("producers"));
                movie.setWinner("yes".equals(record.get("winner")));
                movie.persist();
            }
            System.out.println("File processing completed.");
        } catch (Exception e) {
            System.err.println("Error processing CSV file: " + e.getMessage());
        }
    }

    public List<Movie> getAllMovies() {
        return Movie.listAll();
    }

    @Transactional
    public void addMovie(Movie movie) {
        movie.persist();
    }

    @Transactional
    public Map<String, List<ProducerInterval>> getAwardIntervals() {
        List<Movie> movies = getAllMovies();
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
    }
    public Map<String, List<ProducerInterval>> getMinAndMaxIntervals() {
    Map<String, List<ProducerInterval>> awardIntervals = getAwardIntervals();
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
}
}