package com.example.imdbdataset.controller;

import com.example.imdbdataset.dto.PagedResponse;
import com.example.imdbdataset.model.Title;
import com.example.imdbdataset.model.dto.BestTitlesByYear;
import com.example.imdbdataset.service.ImdbDataService;
import com.example.imdbdataset.service.RequestCounterService;
import com.example.imdbdataset.util.PaginationUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.imdbdataset.exception.InvalidParameterException;
import com.example.imdbdataset.model.Person;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/imdb")
public class ImdbController {
    private final ImdbDataService imdbDataService;
    private final RequestCounterService requestCounterService;

    public ImdbController(ImdbDataService imdbDataService,
                          RequestCounterService requestCounterService) {
        this.imdbDataService = imdbDataService;
        this.requestCounterService = requestCounterService;
    }

    /**
     * Retrieves a paginated list of titles that have the same director and writer.
     *
     * @param page The page number to retrieve (0-indexed). Default is 0.
     * @param size The number of items per page. Default is 10.
     * @return A ResponseEntity containing a PagedResponse of Title objects.
     * The PagedResponse includes the requested page of titles, the total number of pages,
     * and the total number of items.
     */
    @GetMapping("/titles/same-director-writer")
    public ResponseEntity<PagedResponse<Title>> getTitlesWithSameDirectorAndWriter(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        requestCounterService.incrementCounter();
        PaginationUtil.validatePaginationParams(page, size);

        List<Title> titles = imdbDataService.getTitlesWithSameDirectorAndWriter(page, size);
        long totalItems = imdbDataService.getTotalTitlesWithSameDirectorAndWriter();

        return ResponseEntity.ok(PagedResponse.of(titles, page, size, totalItems));
    }

    /**
     * Retrieves a list of titles where both specified actors have acted in.
     *
     * @param actorId1 The unique identifier of the first actor. This parameter is required.
     * @param actorId2 The unique identifier of the second actor. This parameter is required.
     * @return A ResponseEntity containing a List of Title objects.
     * The List contains titles where both specified actors have acted in.
     * If the actorId1 and actorId2 are the same, an InvalidParameterException is thrown.
     * @throws InvalidParameterException If the actorId1 and actorId2 are the same.
     */
    @GetMapping("/titles/both-actors")
    public ResponseEntity<List<Title>> getTitlesWithBothActors(
            @RequestParam(required = true) String actorId1,
            @RequestParam(required = true) String actorId2) {

        requestCounterService.incrementCounter();

        if (actorId1.equals(actorId2)) {
            throw new InvalidParameterException("actor1 and actor2 must be different");
        }

        List<Title> titles = imdbDataService.getTitlesWithBothActors(actorId1, actorId2);
        return ResponseEntity.ok(titles);
    }

    /**
     * Retrieves a paginated list of titles where both specified actors have acted in,
     * using their names instead of their unique identifiers.
     *
     * @param actorName1 The name of the first actor. This parameter is required.
     * @param actorName2 The name of the second actor. This parameter is required.
     * @param page       The page number to retrieve (0-indexed). Default is 0.
     * @param size       The number of items per page. Default is 10.
     * @return A ResponseEntity containing a PagedResponse of Title objects.
     * The PagedResponse includes the requested page of titles, the total number of pages,
     * and the total number of items.
     * @throws InvalidParameterException If the actorName1 and actorName2 are the same.
     */
    @GetMapping("/titles/both-actors-by-names")
    public ResponseEntity<PagedResponse<Title>> getTitlesWithBothActorsByNames(
            @RequestParam(required = true) String actorName1,
            @RequestParam(required = true) String actorName2,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        requestCounterService.incrementCounter();
        PaginationUtil.validatePaginationParams(page, size);

        List<Title> titles = imdbDataService.getTitlesWithBothActors(actorName1, actorName2, page, size);
        long totalItems = imdbDataService.getTotalTitlesWithBothActors(actorName1, actorName2);

        return ResponseEntity.ok(PagedResponse.of(titles, page, size, totalItems));
    }

    /**
     * Retrieves a paginated list of the best titles by year for a specified genre.
     *
     * @param genre The genre for which to retrieve the best titles by year. This parameter is required.
     * @param page  The page number to retrieve (0-indexed). Default is 0.
     * @param size  The number of items per page. Default is 10.
     * @return A ResponseEntity containing a PagedResponse of BestTitlesByYear objects.
     * The PagedResponse includes the requested page of BestTitlesByYear objects,
     * the total number of pages, and the total number of items.
     * Each BestTitlesByYear object contains the year and a list of titles
     * that are considered the best for that year within the specified genre.
     * @throws InvalidParameterException If the pagination parameters (page or size) are invalid.
     */
    @GetMapping("/titles/best-by-genre")
    public ResponseEntity<PagedResponse<BestTitlesByYear>> getBestTitlesByYearForGenre(
            @RequestParam(required = true) String genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        requestCounterService.incrementCounter();
        PaginationUtil.validatePaginationParams(page, size);

        List<BestTitlesByYear> bestTitles = imdbDataService.getBestTitlesByYearForGenre(genre, page, size);
        long totalItems = imdbDataService.getTotalYearsForGenre(genre);

        return ResponseEntity.ok(PagedResponse.of(bestTitles, page, size, totalItems));
    }

    /**
     * Retrieves a person's details by their unique identifier.
     *
     * @param id The unique identifier of the person to retrieve. This parameter is required and must be a non-empty string.
     * @return A ResponseEntity containing a Person object.
     * The Person object represents the details of the person with the specified unique identifier.
     * If the person with the given id does not exist, a 404 Not Found response is returned.
     * @throws InvalidParameterException If the id parameter is null or empty.
     */
    @GetMapping("/person/{id}")
    public ResponseEntity<Person> getPersonById(@PathVariable String id) {
        requestCounterService.incrementCounter();
        Person person = imdbDataService.getPersonById(id);
        return ResponseEntity.ok(person);
    }

    /**
     * Retrieves the total number of requests made to the API.
     *
     * @return A ResponseEntity containing a Map with a single entry.
     * The key is "count" and the value is the total number of requests made to the API.
     * The response is sent with a 200 OK status code.
     */
    @GetMapping("/stats/request-count")
    public ResponseEntity<Map<String, Long>> getRequestCount() {
        requestCounterService.incrementCounter();
        long count = requestCounterService.getCount();
        return ResponseEntity.ok(Map.of("count", count));
    }
}