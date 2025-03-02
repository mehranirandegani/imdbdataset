package com.example.imdbdataset.service;


import com.example.imdbdataset.exception.DataImportException;
import com.example.imdbdataset.exception.InvalidParameterException;
import com.example.imdbdataset.exception.ResourceNotFoundException;
import com.example.imdbdataset.model.*;
import com.example.imdbdataset.model.dto.BestTitlesByYear;
import com.example.imdbdataset.model.dto.TitleDTO;
import com.example.imdbdataset.util.ResourceReader;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;
import java.util.zip.GZIPOutputStream;

@Service
@Getter
public class ImdbDataService {
    // In-memory data structures
    private Map<String, Title> titles = new HashMap<>();
    private Map<String, Person> people = new HashMap<>();
    private Map<String, List<TitlePrincipal>> principalsByTitle = new HashMap<>();
    private Map<String, List<TitlePrincipal>> principalsByPerson = new HashMap<>();
    private Map<String, TitleCrew> crews = new HashMap<>();
    private Map<String, Rating> ratings = new HashMap<>();

    // Counters for data loading
    private long titlesLoaded = 0;
    private long peopleLoaded = 0;
    private long principalsLoaded = 0;
    private long crewsLoaded = 0;
    private long ratingsLoaded = 0;

    /**
     * Initializes the IMDB data by loading and linking the data.
     * This method is annotated with {@link PostConstruct} to ensure it is executed after the bean is constructed.
     *
     * @throws DataImportException If an error occurs while loading or linking the IMDB data.
     */
    @PostConstruct
    public void init() {
        try {
            loadData();
            linkData();
        } catch (IOException e) {
            throw new DataImportException("Failed to load IMDB data", e);
        }
    }

    /**
     * Loads data from demo files into the respective maps.
     *
     * @throws IOException If there's an error reading the files or if the resource cannot be found.
     */
    public void loadData() throws IOException {
        try {
            // Now load from demo files
//            loadTitles("/dataset/title.basics_demo.tsv.gz");
//            loadPeople("/dataset/name.basics_demo.tsv.gz");
//            loadPrincipals("/dataset/title.principals_demo.tsv.gz");
//            loadCrews("/dataset/title.crew_demo.tsv.gz");
//            loadRatings("/dataset/title.ratings_demo.tsv.gz");

            loadTitles("/dataset/title.basics_demo.tsv");
            loadPeople("/dataset/name.basics_demo.tsv");
            loadPrincipals("/dataset/title.principals_demo.tsv");
            loadCrews("/dataset/title.crew_demo.tsv");
            loadRatings("/dataset/title.ratings_demo.tsv");

            System.out.println("Data loaded from demo files: " +
                    titlesLoaded + " titles, " +
                    peopleLoaded + " people, " +
                    principalsLoaded + " principals, " +
                    crewsLoaded + " crews, " +
                    ratingsLoaded + " ratings");
        } catch (IOException e) {
            System.err.println("Error loading or saving IMDB data: " + e.getMessage());
            throw new DataImportException("Error loading or saving IMDB data", e);
        }
    }

    /**
     * Retrieves a list of Title objects where both the specified actors have played together.
     * The function first retrieves the sets of titles for each actor, then finds the common titles.
     *
     * @param actor1Id The unique identifier of the first actor. Must not be null or empty.
     * @param actor2Id The unique identifier of the second actor. Must not be null or empty.
     * @return A list of Title objects where both actors have played together.
     * @throws InvalidParameterException If either actor1Id or actor2Id is null or empty.
     * @throws ResourceNotFoundException If either actor1 or actor2 is not found in the database.
     * @throws ResourceNotFoundException If no titles are found where both actors played together.
     */
    public List<Title> getTitlesWithBothActors(String actor1Id, String actor2Id) {
        if (actor1Id == null || actor1Id.trim().isEmpty()) {
            throw new InvalidParameterException("actor1 parameter cannot be null or empty");
        }
        if (actor2Id == null || actor2Id.trim().isEmpty()) {
            throw new InvalidParameterException("actor2 parameter cannot be null or empty");
        }

        Person actor1 = people.get(actor1Id);
        Person actor2 = people.get(actor2Id);

        if (actor1 == null) {
            throw new ResourceNotFoundException("Actor", "id", actor1Id);
        }
        if (actor2 == null) {
            throw new ResourceNotFoundException("Actor", "id", actor2Id);
        }

        // Get all titles for actor1 (including knownForTitles)
        Set<String> actor1Titles = new HashSet<>();
        if (actor1.getKnownForTitles() != null) {
            actor1Titles.addAll(Arrays.asList(actor1.getKnownForTitles()));
        }
        principalsByPerson.getOrDefault(actor1Id, Collections.emptyList())
                .stream()
                .filter(p -> "actor".equals(p.getCategory()) || "actress".equals(p.getCategory()))
                .map(TitlePrincipal::getTconst)
                .forEach(actor1Titles::add);

        // Get all titles for actor2 (including knownForTitles)
        Set<String> actor2Titles = new HashSet<>();
        if (actor2.getKnownForTitles() != null) {
            actor2Titles.addAll(Arrays.asList(actor2.getKnownForTitles()));
        }
        principalsByPerson.getOrDefault(actor2Id, Collections.emptyList())
                .stream()
                .filter(p -> "actor".equals(p.getCategory()) || "actress".equals(p.getCategory()))
                .map(TitlePrincipal::getTconst)
                .forEach(actor2Titles::add);

        // Find common titles
        List<Title> result = actor1Titles.stream()
                .filter(actor2Titles::contains)
                .map(titles::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Title::getPrimaryTitle))
                .collect(Collectors.toList());

        if (result.isEmpty()) {
            throw new ResourceNotFoundException(
                    "No titles found where both actors " + actor1.getPrimaryName() +
                            " and " + actor2.getPrimaryName() + " played together");
        }

        return result;
    }

    /**
     * Retrieves a list of the best titles by year for a given genre, with pagination.
     *
     * @param genre The genre for which to retrieve the best titles.
     * @param page  The page number for pagination. Must be >= 0.
     * @param size  The number of titles per page. Must be > 0.
     * @return A list of BestTitlesByYear objects, representing the best titles by year for the given genre.
     * @throws InvalidParameterException If the genre parameter is null or empty, or if page or size are invalid.
     * @throws ResourceNotFoundException If no titles are found for the given genre.
     */
    public List<BestTitlesByYear> getBestTitlesByYearForGenre(String genre, int page, int size) {
        if (genre == null || genre.trim().isEmpty()) {
            throw new InvalidParameterException("genre parameter cannot be null or empty");
        }
        if (page < 0 || size <= 0) {
            throw new InvalidParameterException("Page must be >= 0 and size must be > 0");
        }

        // Filter titles by genre
        List<Title> genreTitles = titles.values().stream()
                .filter(title -> title.getGenres() != null && title.getGenres().contains(genre))
                .filter(title -> title.getRating() != null && title.getNumVotes() != null)
                .filter(title -> title.getStartYear() != null)
                .collect(Collectors.toList());

        if (genreTitles.isEmpty()) {
            throw new ResourceNotFoundException("No titles found for genre: " + genre);
        }

        // Group by year
        Map<Integer, List<Title>> titlesByYear = genreTitles.stream()
                .collect(Collectors.groupingBy(Title::getStartYear));

        // Get sorted list of BestTitlesByYear
        List<BestTitlesByYear> allYears = titlesByYear.entrySet().stream()
                .map(entry -> {
                    Integer year = entry.getKey();
                    List<Title> yearTitles = entry.getValue();

                    // Sort by rating and number of votes
                    List<TitleDTO> bestTitles = yearTitles.stream()
                            .sorted(Comparator
                                    .comparing(Title::getRating, Comparator.reverseOrder())
                                    .thenComparing(Title::getNumVotes, Comparator.reverseOrder()))
                            .limit(5) // Get top 5
                            .map(t -> new TitleDTO(
                                    t.getTconst(),
                                    t.getPrimaryTitle(),
                                    t.getStartYear(),
                                    t.getRating(),
                                    t.getNumVotes()))
                            .collect(Collectors.toList());

                    return new BestTitlesByYear(year, bestTitles);
                })
                .sorted(Comparator.comparing(BestTitlesByYear::getYear))
                .collect(Collectors.toList());

        // Apply pagination
        return allYears.stream()
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the total number of unique years for which titles exist in the database,
     * given a specific genre. This method is used for pagination in the context of retrieving
     * the best titles by year for a given genre.
     *
     * @param genre The genre for which to retrieve the total number of unique years.
     *              Must not be null or empty.
     * @return The total number of unique years for which titles exist in the database,
     * given the specified genre.
     * @throws InvalidParameterException If the genre parameter is null or empty.
     */
    public long getTotalYearsForGenre(String genre) {
        if (genre == null || genre.trim().isEmpty()) {
            throw new InvalidParameterException("genre parameter cannot be null or empty");
        }

        return titles.values().stream()
                .filter(title -> title.getGenres() != null && title.getGenres().contains(genre))
                .filter(title -> title.getRating() != null && title.getNumVotes() != null)
                .filter(title -> title.getStartYear() != null)
                .map(Title::getStartYear)
                .distinct()
                .count();
    }

    /**
     * Retrieves a person from the database by their unique identifier.
     *
     * @param personId The unique identifier of the person to retrieve.
     *                 This parameter cannot be null or empty.
     * @return The person with the specified unique identifier.
     * If no person with the given identifier exists in the database,
     * a ResourceNotFoundException is thrown.
     * @throws InvalidParameterException If the personId parameter is null or empty.
     * @throws ResourceNotFoundException If no person with the given identifier exists in the database.
     */
    public Person getPersonById(String personId) {
        if (personId == null || personId.trim().isEmpty()) {
            throw new InvalidParameterException("personId parameter cannot be null or empty");
        }

        Person person = people.get(personId);
        if (person == null) {
            throw new ResourceNotFoundException("Person", "id", personId);
        }

        return person;
    }

    /**
     * Loads title data from a TSV file into the titles map.
     * This method reads the file line by line, parsing each line into a Title object
     * and storing it in the titles map. It also keeps track of the number of titles loaded.
     *
     * @param resourcePath The path to the TSV file containing title data.
     *                     This should be a resource path that can be loaded via getResourceAsStream.
     * @throws IOException If there's an error reading the file or if the resource cannot be found.
     */
    private void loadTitles(String resourcePath) throws IOException {
        try (BufferedReader reader = ResourceReader.getReader(resourcePath, false)) { // false for non-gzipped
            // Skip header
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t");
                if (fields.length >= 9) {
                    String tconst = fields[0];
                    String titleType = fields[1];
                    String primaryTitle = fields[2];
                    String originalTitle = fields[3];
                    boolean isAdult = "1".equals(fields[4]);

                    Integer startYear = "\\N".equals(fields[5]) ? null : Integer.parseInt(fields[5]);
                    Integer endYear = "\\N".equals(fields[6]) ? null : Integer.parseInt(fields[6]);
                    Integer runtimeMinutes = "\\N".equals(fields[7]) ? null : Integer.parseInt(fields[7]);

                    Set<String> genres = "\\N".equals(fields[8]) ?
                            new HashSet<>() :
                            Arrays.stream(fields[8].split(",")).collect(Collectors.toSet());

                    Title title = new Title(tconst, titleType, primaryTitle, originalTitle,
                            isAdult, startYear, endYear, runtimeMinutes, genres);

                    titles.put(tconst, title);
                    titlesLoaded++;

                    // Only load a subset for demo purposes
                    if (titlesLoaded >= 100000) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Loads person data from a TSV file into the people map.
     * This method reads the file line by line, parsing each line into a Person object
     * and storing it in the people map. It also keeps track of the number of people loaded.
     *
     * @param resourcePath The path to the TSV file containing person data.
     *                     This should be a resource path that can be loaded via getResourceAsStream.
     * @throws IOException If there's an error reading the file or if the resource cannot be found.
     */
    private void loadPeople(String resourcePath) throws IOException {
        try (BufferedReader reader = ResourceReader.getReader(resourcePath, false)) { // false for non-gzipped
            // Skip header
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t");
                if (fields.length >= 6) {
                    String nconst = fields[0];
                    String primaryName = fields[1];

                    Integer birthYear = "\\N".equals(fields[2]) ? null : Integer.parseInt(fields[2]);
                    Integer deathYear = "\\N".equals(fields[3]) ? null : Integer.parseInt(fields[3]);

                    String[] primaryProfessions = "\\N".equals(fields[4]) ?
                            new String[0] : fields[4].split(",");

                    String[] knownForTitles = "\\N".equals(fields[5]) ?
                            new String[0] : fields[5].split(",");

                    Person person = new Person(nconst, primaryName, birthYear, deathYear,
                            primaryProfessions, knownForTitles);

                    people.put(nconst, person);
                    peopleLoaded++;

                    // Only load a subset for demo purposes
                    if (peopleLoaded >= 100000) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Loads title principal data from a TSV file into the principalsByTitle and principalsByPerson maps.
     * This method reads the file line by line, parsing each line into a TitlePrincipal object,
     * and storing it in the principalsByTitle and principalsByPerson maps. It also keeps track of the number of principals loaded.
     *
     * @param resourcePath The path to the TSV file containing title principal data.
     *                     This should be a resource path that can be loaded via getResourceAsStream.
     * @throws IOException If there's an error reading the file or if the resource cannot be found.
     */
    private void loadPrincipals(String resourcePath) throws IOException {
        try (BufferedReader reader = ResourceReader.getReader(resourcePath, false)) { // false for non-gzipped
            // Skip header
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t");
                if (fields.length >= 6) {
                    String tconst = fields[0];
                    int ordering = Integer.parseInt(fields[1]);
                    String nconst = fields[2];
                    String category = fields[3];
                    String job = "\\N".equals(fields[4]) ? null : fields[4];
                    String characters = "\\N".equals(fields[5]) ? null : fields[5];

                    TitlePrincipal principal = new TitlePrincipal(tconst, ordering, nconst,
                            category, job, characters);

                    principalsByTitle.computeIfAbsent(tconst, k -> new ArrayList<>()).add(principal);
                    principalsByPerson.computeIfAbsent(nconst, k -> new ArrayList<>()).add(principal);
                    principalsLoaded++;

                    // Only process principals for titles we have loaded
                    if (!titles.containsKey(tconst)) {
                        continue;
                    }

                    // Only load a subset for demo purposes
                    if (principalsLoaded >= 500000) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Loads title crew data from a TSV file into the crewsByTitle and crewsByPerson maps.
     * This method reads the file line by line, parsing each line into a TitleCrew object,
     * and storing it in the crewsByTitle and crewsByPerson maps. It also keeps track of the number of crews loaded.
     *
     * @param resourcePath The path to the TSV file containing title crew data.
     *                     This should be a resource path that can be loaded via getResourceAsStream.
     * @throws IOException If there's an error reading the file or if the resource cannot be found.
     */
    private void loadCrews(String resourcePath) throws IOException {
        try (BufferedReader reader = ResourceReader.getReader(resourcePath, false)) { // false for non-gzipped
            // Skip header
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t");
                if (fields.length >= 3) {
                    String tconst = fields[0];
                    String[] directors = "\\N".equals(fields[1]) ? new String[0] : fields[1].split(",");
                    String[] writers = "\\N".equals(fields[2]) ? new String[0] : fields[2].split(",");

                    TitleCrew crew = new TitleCrew(tconst, directors, writers);
                    crews.put(tconst, crew);
                    crewsLoaded++;

                    // Only process crews for titles we have loaded
                    if (!titles.containsKey(tconst)) {
                        continue;
                    }

                    // Only load a subset for demo purposes
                    if (crewsLoaded >= 100000) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Loads title rating data from a TSV file into the ratings map.
     * This method reads the file line by line, parsing each line into a Rating object
     * and storing it in the ratings map. It also keeps track of the number of ratings loaded.
     *
     * @param resourcePath The path to the TSV file containing title rating data.
     *                     This should be a resource path that can be loaded via getResourceAsStream.
     * @throws IOException If there's an error reading the file or if the resource cannot be found.
     */
    private void loadRatings(String resourcePath) throws IOException {
        try (BufferedReader reader = ResourceReader.getReader(resourcePath, false)) { // false for non-gzipped
            // Skip header
            reader.readLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split("\t");
                if (fields.length >= 3) {
                    String tconst = fields[0];

                    // Only process ratings for titles we have loaded
                    if (!titles.containsKey(tconst)) {
                        continue;
                    }

                    Float averageRating = Float.parseFloat(fields[1]);
                    Integer numVotes = Integer.parseInt(fields[2]);

                    Rating rating = new Rating(tconst, averageRating, numVotes);
                    ratings.put(tconst, rating);
                    ratingsLoaded++;

                    // Apply rating to title
                    Title title = titles.get(tconst);
                    title.setRating(averageRating);
                    title.setNumVotes(numVotes);

                    // Only load a subset for demo purposes
                    if (ratingsLoaded >= 100000) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Links title data with their directors, writers, and actors.
     * This method iterates through all titles, retrieves their corresponding crew and principal data,
     * and adds the directors, writers, and actors to the title objects.
     */
    private void linkData() {
        // Link titles with their directors, writers, and actors
        for (Title title : titles.values()) {
            String tconst = title.getTconst();

            // Add crew (directors and writers)
            TitleCrew crew = crews.get(tconst);
            if (crew != null) {
                // Add directors
                List<Person> directors = new ArrayList<>();
                for (String nconst : crew.getDirectors()) {
                    Person director = people.get(nconst);
                    if (director != null) {
                        directors.add(director);
                    }
                }
                title.setDirectors(directors);

                // Add writers
                List<Person> writers = new ArrayList<>();
                for (String nconst : crew.getWriters()) {
                    Person writer = people.get(nconst);
                    if (writer != null) {
                        writers.add(writer);
                    }
                }
                title.setWriters(writers);
            }

            // Add actors
            List<TitlePrincipal> principals = principalsByTitle.get(tconst);
            if (principals != null) {
                List<Person> actors = principals.stream()
                        .filter(p -> "actor".equals(p.getCategory()) || "actress".equals(p.getCategory()))
                        .map(p -> people.get(p.getNconst()))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toList());

                title.setActors(actors);
            }
        }
    }

    /**
     * Returns the total number of titles that have the same director and writer.
     *
     * @return the total number of titles with the same director and writer.
     */
    public long getTotalTitlesWithSameDirectorAndWriter() {
        return titles.values().stream()
                .filter(title -> {
                    List<Person> directors = title.getDirectors();
                    List<Person> writers = title.getWriters();

                    if (directors == null || writers == null || directors.isEmpty() || writers.isEmpty()) {
                        return false;
                    }

                    return directors.stream()
                            .filter(Person::isAlive)
                            .anyMatch(director -> writers.contains(director));
                })
                .count();
    }

    /**
     * Retrieves a list of titles that have the same director and writer,
     * sorted by primary title in ascending order.
     *
     * @param page The page number to retrieve (0-indexed).
     * @param size The number of titles to retrieve per page.
     * @return A list of titles that have the same director and writer.
     * The list is sorted by primary title in ascending order.
     * Only titles with at least one director and one writer are included.
     */
    public List<Title> getTitlesWithSameDirectorAndWriter(int page, int size) {
        return titles.values().stream()
                .filter(title -> {
                    List<Person> directors = title.getDirectors();
                    List<Person> writers = title.getWriters();

                    if (directors == null || writers == null || directors.isEmpty() || writers.isEmpty()) {
                        return false;
                    }

                    return directors.stream()
                            .filter(Person::isAlive)
                            .anyMatch(director -> writers.contains(director));
                })
                .sorted(Comparator.comparing(Title::getPrimaryTitle))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves a list of titles that have both the specified actors,
     * sorted by primary title in ascending order.
     *
     * @param actor1Key The ID or name of the first actor.
     * @param actor2Key The ID or name of the second actor.
     * @param page      The page number to retrieve (0-indexed).
     * @param size      The number of titles to retrieve per page.
     * @return A list of titles that have both the specified actors.
     * The list is sorted by primary title in ascending order.
     * @throws InvalidParameterException If either actor1Key or actor2Key is null or empty.
     * @throws ResourceNotFoundException If either actor1 or actor2 is not found.
     */
    public List<Title> getTitlesWithBothActors(String actor1Key, String actor2Key, int page, int size) {
        if (actor1Key == null || actor1Key.trim().isEmpty()) {
            throw new InvalidParameterException("actor1 parameter cannot be null or empty");
        }
        if (actor2Key == null || actor2Key.trim().isEmpty()) {
            throw new InvalidParameterException("actor2 parameter cannot be null or empty");
        }

        // Find actors by ID or name
        Person actor1 = findActor(actor1Key);
        Person actor2 = findActor(actor2Key);

        if (actor1 == null) {
            throw new ResourceNotFoundException("Actor", "id/name", actor1Key);
        }
        if (actor2 == null) {
            throw new ResourceNotFoundException("Actor", "id/name", actor2Key);
        }

        // Get all titles including knownForTitles
        Set<String> actor1Titles = getAllActorTitles(actor1);
        Set<String> actor2Titles = getAllActorTitles(actor2);

        // Find common titles and convert to Title objects
        return actor1Titles.stream()
                .filter(actor2Titles::contains)
                .map(titles::get)
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(Title::getPrimaryTitle))
                .skip((long) page * size)
                .limit(size)
                .collect(Collectors.toList());
    }

    /**
     * Finds an actor in the people map based on the provided key (ID or name).
     *
     * @param key The ID or name of the actor to find.
     * @return The found actor, or null if not found.
     * The search is performed first by ID, and if not found, by name.
     */
    private Person findActor(String key) {
        // Try to find by ID first
        Person actor = people.get(key);
        if (actor != null) {
            return actor;
        }

        // If not found by ID, try to find by name
        return people.values().stream()
                .filter(p -> p.getPrimaryName().equalsIgnoreCase(key))
                .findFirst()
                .orElse(null);
    }


    /**
     * Retrieves a set of all titles that the specified actor has acted in.
     * The set includes titles from both the 'knownForTitles' field of the actor
     * and the 'principals' map where the actor has acted.
     *
     * @param actor The actor for whom to retrieve the titles.
     * @return A set of all titles that the specified actor has acted in.
     * The set is empty if the actor has not acted in any titles.
     */
    private Set<String> getAllActorTitles(Person actor) {
        Set<String> allTitles = new HashSet<>();

        // Add known for titles
        if (actor.getKnownForTitles() != null) {
            allTitles.addAll(Arrays.asList(actor.getKnownForTitles()));
        }

        // Add titles from principals where they acted
        principalsByPerson.getOrDefault(actor.getNconst(), Collections.emptyList())
                .stream()
                .filter(p -> "actor".equals(p.getCategory()) || "actress".equals(p.getCategory()))
                .map(TitlePrincipal::getTconst)
                .forEach(allTitles::add);

        return allTitles;
    }


    /**
     * Retrieves the total number of titles that both the specified actors have acted in.
     *
     * @param actor1Key The ID or name of the first actor.
     * @param actor2Key The ID or name of the second actor.
     * @return The total number of titles that both the specified actors have acted in.
     * @throws InvalidParameterException If either actor1Key or actor2Key is null or empty.
     * @throws ResourceNotFoundException If either actor1 or actor2 is not found.
     */
    public long getTotalTitlesWithBothActors(String actor1Key, String actor2Key) {
        if (actor1Key == null || actor1Key.trim().isEmpty()) {
            throw new InvalidParameterException("actor1 parameter cannot be null or empty");
        }
        if (actor2Key == null || actor2Key.trim().isEmpty()) {
            throw new InvalidParameterException("actor2 parameter cannot be null or empty");
        }

        Person actor1 = findActor(actor1Key);
        Person actor2 = findActor(actor2Key);

        if (actor1 == null) {
            throw new ResourceNotFoundException("Actor", "id/name", actor1Key);
        }
        if (actor2 == null) {
            throw new ResourceNotFoundException("Actor", "id/name", actor2Key);
        }

        Set<String> actor1Titles = getAllActorTitles(actor1);
        Set<String> actor2Titles = getAllActorTitles(actor2);

        return actor1Titles.stream()
                .filter(actor2Titles::contains)
                .map(titles::get)
                .filter(Objects::nonNull)
                .count();
    }
}
