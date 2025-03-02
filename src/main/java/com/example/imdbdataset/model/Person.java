package com.example.imdbdataset.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    // Getters and setters
    private String nconst;          // alphanumeric unique identifier
    private String primaryName;     // name by which the person is most often credited
    private Integer birthYear;      // birth year in YYYY format
    private Integer deathYear;      // death year in YYYY format, null if still alive
    private String[] primaryProfessions; // primary professions (comma-separated)
    private String[] knownForTitles;    // titles the person is known for (comma-separated)

    public boolean isAlive() {
        return deathYear == null;
    }
}
