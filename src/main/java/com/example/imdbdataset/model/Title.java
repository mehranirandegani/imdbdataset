package com.example.imdbdataset.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Set;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Title {
    private String tconst;          // alphanumeric unique identifier
    private String titleType;       // type of title (movie, series, etc.)
    private String primaryTitle;    // title in most popular form
    private String originalTitle;   // original title in original language
    private boolean isAdult;        // 0: non-adult title; 1: adult title
    private Integer startYear;      // release year
    private Integer endYear;        // end year for series
    private Integer runtimeMinutes; // runtime in minutes
    private Set<String> genres;     // genres separated by comma
    private Float rating;           // IMDb rating
    private Integer numVotes;       // number of votes
    private List<Person> directors; // directors
    private List<Person> writers;   // writers
    private List<Person> actors;    // actors

    // Constructors, getters, and setters


    public Title(String tconst, String titleType, String primaryTitle, String originalTitle,
                 boolean isAdult, Integer startYear, Integer endYear, Integer runtimeMinutes,
                 Set<String> genres) {
        this.tconst = tconst;
        this.titleType = titleType;
        this.primaryTitle = primaryTitle;
        this.originalTitle = originalTitle;
        this.isAdult = isAdult;
        this.startYear = startYear;
        this.endYear = endYear;
        this.runtimeMinutes = runtimeMinutes;
        this.genres = genres;
    }

}
