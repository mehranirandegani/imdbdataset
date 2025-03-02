package com.example.imdbdataset.service;

import com.example.imdbdataset.exception.InvalidParameterException;
import com.example.imdbdataset.exception.ResourceNotFoundException;
import com.example.imdbdataset.model.TitlePrincipal;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import com.example.imdbdataset.model.Person;
import com.example.imdbdataset.model.Title;
class ImdbDataServiceTest {

//    @Test
//    void loadData_shouldLoadCorrectNumberOfTitles() throws IOException {
//        ImdbDataService imdbDataService = new ImdbDataService();
//        imdbDataService.loadData();
//
//        assertEquals(94269, imdbDataService.getTitlesLoaded());
//        assertNotNull(imdbDataService.getTitles().get("tt0000001")); // Example title
//    }
//
//    @Test
//    void getPersonById() throws IOException {
//        ImdbDataService imdbDataService = new ImdbDataService();
//        imdbDataService.loadData();
//
//        // Test invalid parameters
//        assertThrows(InvalidParameterException.class, () ->
//                imdbDataService.getPersonById(null));
//        assertThrows(InvalidParameterException.class, () ->
//                imdbDataService.getPersonById(""));
//        assertThrows(ResourceNotFoundException.class, () ->
//                imdbDataService.getPersonById("invalid_id"));
//
//        // Get first person id from the loaded data
//        String validPersonId = imdbDataService.getPeople().keySet().iterator().next();
//        Person person = imdbDataService.getPersonById(validPersonId);
//
//        assertNotNull(person);
//        assertEquals(validPersonId, person.getNconst());
//        assertNotNull(person.getPrimaryName());
//    }
//
//    @Test
//    void getTitlesWithBothActors() throws IOException {
//        ImdbDataService imdbDataService = new ImdbDataService();
//        imdbDataService.loadData();
//
//        // Test invalid parameters
//        assertThrows(InvalidParameterException.class, () ->
//                imdbDataService.getTitlesWithBothActors(null, "nm0000001"));
//        assertThrows(InvalidParameterException.class, () ->
//                imdbDataService.getTitlesWithBothActors("", "nm0000001"));
//        assertThrows(ResourceNotFoundException.class, () ->
//                imdbDataService.getTitlesWithBothActors("invalid1", "invalid2"));
//
//        // Test with actors that have no movies together
//        String actor1 = "nm0000001";
//        String actor2 = "nm0000002";
//        assertThrows(ResourceNotFoundException.class, () ->
//                imdbDataService.getTitlesWithBothActors(actor1, actor2));
//
//        // Test with actors that have common movies
//        List<TitlePrincipal> actor1Movies = imdbDataService.getPrincipalsByPerson().get(actor1);
//        if (actor1Movies != null && !actor1Movies.isEmpty()) {
//            String titleId = actor1Movies.get(0).getTconst();
//            List<TitlePrincipal> titlePrincipals = imdbDataService.getPrincipalsByTitle().get(titleId);
//
//            if (titlePrincipals != null && titlePrincipals.size() > 1) {
//                String actor2WithCommonMovie = titlePrincipals.get(1).getNconst();
//                List<Title> commonTitles = imdbDataService.getTitlesWithBothActors(actor1, actor2WithCommonMovie);
//
//                assertNotNull(commonTitles);
//                assertFalse(commonTitles.isEmpty());
//                assertTrue(commonTitles.stream().anyMatch(t -> t.getTconst().equals(titleId)));
//            }
//        }
//    }
//
//    @Test
//    void loadData() throws IOException {
//        ImdbDataService imdbDataService = new ImdbDataService();
//        imdbDataService.loadData();
//
//        // Verify exact counts match expected values
//        assertEquals(94269, imdbDataService.getTitlesLoaded());
//        assertEquals(97868, imdbDataService.getPeopleLoaded());
//        assertEquals(499999, imdbDataService.getPrincipalsLoaded());
//        assertEquals(99999, imdbDataService.getCrewsLoaded());
//        assertEquals(75347, imdbDataService.getRatingsLoaded());
//
//        // Verify data structures are populated
//        assertEquals(imdbDataService.getTitlesLoaded(), imdbDataService.getTitles().size());
//        assertEquals(imdbDataService.getPeopleLoaded(), imdbDataService.getPeople().size());
//
//        // Verify principals mappings
//        int totalPrincipalsInTitleMap = imdbDataService.getPrincipalsByTitle().values()
//                .stream()
//                .mapToInt(List::size)
//                .sum();
//        int totalPrincipalsInPersonMap = imdbDataService.getPrincipalsByPerson().values()
//                .stream()
//                .mapToInt(List::size)
//                .sum();
//
//        assertEquals(imdbDataService.getPrincipalsLoaded(), totalPrincipalsInTitleMap);
//        assertEquals(imdbDataService.getPrincipalsLoaded(), totalPrincipalsInPersonMap);
//
//        // Verify related data mappings
//        assertTrue(imdbDataService.getCrews().size() >= imdbDataService.getTitlesLoaded());
//        assertTrue(imdbDataService.getRatings().size() <= imdbDataService.getTitlesLoaded());
//    }

    @Test
    void testLoadData() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertNotNull(imdbDataService.getTitles());
        assertNotNull(imdbDataService.getPeople());
        assertNotNull(imdbDataService.getPrincipalsByTitle());
        assertNotNull(imdbDataService.getPrincipalsByPerson());
        assertNotNull(imdbDataService.getCrews());
        assertNotNull(imdbDataService.getRatings());

        assertTrue(imdbDataService.getTitlesLoaded() > 0);
        assertTrue(imdbDataService.getPeopleLoaded() > 0);
        assertTrue(imdbDataService.getPrincipalsLoaded() > 0);
        assertTrue(imdbDataService.getCrewsLoaded() > 0);
        assertTrue(imdbDataService.getRatingsLoaded() > 0);
    }

    @Test
    void getBestTitlesByYearForGenre_shouldThrowInvalidParameterException_whenGenreIsNull() {
        ImdbDataService imdbDataService = new ImdbDataService();
        assertThrows(InvalidParameterException.class, () ->
                imdbDataService.getBestTitlesByYearForGenre(null, 0, 10));
    }

    @Test
    void getBestTitlesByYearForGenre_shouldThrowInvalidParameterException_whenPageIsNegative() {
        ImdbDataService imdbDataService = new ImdbDataService();
        assertThrows(InvalidParameterException.class, () ->
                imdbDataService.getBestTitlesByYearForGenre("Drama", -1, 10));
    }

    @Test
    void getBestTitlesByYearForGenre_shouldThrowInvalidParameterException_whenSizeIsZero() {
        ImdbDataService imdbDataService = new ImdbDataService();
        assertThrows(InvalidParameterException.class, () ->
                imdbDataService.getBestTitlesByYearForGenre("Drama", 0, 0));
    }

    @Test
    void getBestTitlesByYearForGenre_shouldReturnValidData() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        var result = imdbDataService.getBestTitlesByYearForGenre("Drama", 0, 10);

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertTrue(result.size() <= 10);

        // Verify year sorting
        for (int i = 1; i < result.size(); i++) {
            assertTrue(result.get(i).getYear() >= result.get(i - 1).getYear());
        }

        // Verify each year has up to 5 best titles
        for (var yearData : result) {
            assertTrue(yearData.getBestTitles().size() <= 5);
        }
    }


    @Test
    void getTotalTitlesWithSameDirectorAndWriter_shouldReturnValidCount() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        long total = imdbDataService.getTotalTitlesWithSameDirectorAndWriter();
        assertTrue(total >= 0);

        // Verify count matches actual filtered data
        long actualCount = imdbDataService.getTitles().values().stream()
                .filter(title -> {
                    var directors = title.getDirectors();
                    var writers = title.getWriters();
                    if (directors == null || writers == null ||
                            directors.isEmpty() || writers.isEmpty()) {
                        return false;
                    }
                    return directors.stream()
                            .filter(Person::isAlive)
                            .anyMatch(writers::contains);
                })
                .count();

        assertEquals(actualCount, total);
    }

    @Test
    void getTotalTitlesWithBothActors_shouldThrowInvalidParameterException_whenActorIsNull() {
        ImdbDataService imdbDataService = new ImdbDataService();
        assertThrows(InvalidParameterException.class, () ->
                imdbDataService.getTotalTitlesWithBothActors(null, "nm0000001"));
        assertThrows(InvalidParameterException.class, () ->
                imdbDataService.getTotalTitlesWithBothActors("nm0000001", null));
    }


    @Test
    void getTitlesWithSameDirectorAndWriter_shouldReturnValidData() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        var result = imdbDataService.getTitlesWithSameDirectorAndWriter(0, 10);

        assertNotNull(result);
        assertTrue(result.size() <= 10);

        // If there are results, verify their validity
        if (!result.isEmpty()) {
            for (var title : result) {
                var directors = title.getDirectors();
                var writers = title.getWriters();

                assertNotNull(directors);
                assertNotNull(writers);
                assertFalse(directors.isEmpty());
                assertFalse(writers.isEmpty());

                boolean hasCommonPerson = directors.stream()
                        .filter(Person::isAlive)
                        .anyMatch(writers::contains);
                assertTrue(hasCommonPerson);
            }
        }
    }

    @Test
    void getTitlesWithBothActors_shouldReturnValidData() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        // Get two actors who we know have worked together
        String actor1Id = imdbDataService.getPeople().keySet().iterator().next();
        List<TitlePrincipal> actor1Movies = imdbDataService.getPrincipalsByPerson().get(actor1Id);

        if (actor1Movies != null && !actor1Movies.isEmpty()) {
            String titleId = actor1Movies.get(0).getTconst();
            List<TitlePrincipal> titlePrincipals = imdbDataService.getPrincipalsByTitle().get(titleId);

            if (titlePrincipals != null && titlePrincipals.size() > 1) {
                String actor2Id = titlePrincipals.get(1).getNconst();

                List<Title> titles = imdbDataService.getTitlesWithBothActors(actor1Id, actor2Id, 0, 10);
                assertNotNull(titles);
                assertFalse(titles.isEmpty());
                assertTrue(titles.size() <= 10);

                // Verify each title has both actors
                for (var title : titles) {
                    boolean hasActor1 = imdbDataService.getPrincipalsByTitle().get(title.getTconst())
                            .stream()
                            .anyMatch(p -> p.getNconst().equals(actor1Id));
                    boolean hasActor2 = imdbDataService.getPrincipalsByTitle().get(title.getTconst())
                            .stream()
                            .anyMatch(p -> p.getNconst().equals(actor2Id));
                    assertTrue(hasActor1);
                    assertTrue(hasActor2);
                }
            }
        }
    }

    @Test
    void getTotalTitlesWithBothActors_shouldReturnValidCount() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        // Get two actors who we know have worked together
        String actor1Id = imdbDataService.getPeople().keySet().iterator().next();
        List<TitlePrincipal> actor1Movies = imdbDataService.getPrincipalsByPerson().get(actor1Id);

        if (actor1Movies != null && !actor1Movies.isEmpty()) {
            String titleId = actor1Movies.get(0).getTconst();
            List<TitlePrincipal> titlePrincipals = imdbDataService.getPrincipalsByTitle().get(titleId);

            if (titlePrincipals != null && titlePrincipals.size() > 1) {
                String actor2Id = titlePrincipals.get(1).getNconst();

                long total = imdbDataService.getTotalTitlesWithBothActors(actor1Id, actor2Id);
                assertTrue(total > 0);

                // Verify count matches actual data
                List<Title> titles = imdbDataService.getTitlesWithBothActors(actor1Id, actor2Id, 0, 10);
                assertEquals(titles.size(), total);
            }
        }
    }

    @Test
    void testGetTitlesWithBothActors() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertThrows(InvalidParameterException.class, () ->
                imdbDataService.getTitlesWithBothActors(null, "nm0000001", 0, 10));
        assertThrows(InvalidParameterException.class, () ->
                imdbDataService.getTitlesWithBothActors("nm0000001", null, 0, 10));
    }


    @Test
    void testGetPersonById() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        String personId = imdbDataService.getPeople().keySet().iterator().next();
        Person person = imdbDataService.getPersonById(personId);

        assertNotNull(person);
        assertEquals(personId, person.getNconst());
    }

    @Test
    void getTitlesWithSameDirectorAndWriter() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        var result = imdbDataService.getTitlesWithSameDirectorAndWriter(0, 10);
        assertNotNull(result);
        assertTrue(result.size() <= 10);
    }

    @Test
    void testGetTitlesWithBothActors1() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        String actor1Id = imdbDataService.getPeople().keySet().iterator().next();
        List<TitlePrincipal> actor1Movies = imdbDataService.getPrincipalsByPerson().get(actor1Id);

        if (actor1Movies != null && !actor1Movies.isEmpty()) {
            String titleId = actor1Movies.get(0).getTconst();
            List<TitlePrincipal> titlePrincipals = imdbDataService.getPrincipalsByTitle().get(titleId);

            if (titlePrincipals != null && titlePrincipals.size() > 1) {
                String actor2Id = titlePrincipals.get(1).getNconst();
                var result = imdbDataService.getTitlesWithBothActors(actor1Id, actor2Id);
                assertNotNull(result);
            }
        }
    }

    @Test
    void getTitles() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertNotNull(imdbDataService.getTitles());
        assertFalse(imdbDataService.getTitles().isEmpty());
    }

    @Test
    void getPeople() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertNotNull(imdbDataService.getPeople());
        assertFalse(imdbDataService.getPeople().isEmpty());
    }

    @Test
    void getPrincipalsByTitle() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertNotNull(imdbDataService.getPrincipalsByTitle());
        assertFalse(imdbDataService.getPrincipalsByTitle().isEmpty());
    }

    @Test
    void getPrincipalsByPerson() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertNotNull(imdbDataService.getPrincipalsByPerson());
        assertFalse(imdbDataService.getPrincipalsByPerson().isEmpty());
    }

    @Test
    void getCrews() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertNotNull(imdbDataService.getCrews());
        assertFalse(imdbDataService.getCrews().isEmpty());
    }

    @Test
    void getRatings() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertNotNull(imdbDataService.getRatings());
        assertFalse(imdbDataService.getRatings().isEmpty());
    }

    @Test
    void getTitlesLoaded() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertTrue(imdbDataService.getTitlesLoaded() > 0);
        assertEquals(imdbDataService.getTitles().size(), imdbDataService.getTitlesLoaded());
    }

    @Test
    void getPeopleLoaded() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertTrue(imdbDataService.getPeopleLoaded() > 0);
        assertEquals(imdbDataService.getPeople().size(), imdbDataService.getPeopleLoaded());
    }

    @Test
    void getPrincipalsLoaded() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertTrue(imdbDataService.getPrincipalsLoaded() > 0);
        assertEquals(
                imdbDataService.getPrincipalsByTitle().values().stream()
                        .mapToInt(List::size)
                        .sum(),
                imdbDataService.getPrincipalsLoaded()
        );
    }

    @Test
    void getCrewsLoaded() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertTrue(imdbDataService.getCrewsLoaded() > 0);
        assertEquals(imdbDataService.getCrews().size(), imdbDataService.getCrewsLoaded());
    }

    @Test
    void getRatingsLoaded() throws IOException {
        ImdbDataService imdbDataService = new ImdbDataService();
        imdbDataService.loadData();

        assertTrue(imdbDataService.getRatingsLoaded() > 0);
        assertEquals(imdbDataService.getRatings().size(), imdbDataService.getRatingsLoaded());
    }
}