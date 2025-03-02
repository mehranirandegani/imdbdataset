package com.example.imdbdataset.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TitlePrincipal {
    // Getters and setters
    private String tconst;      // alphanumeric unique identifier of the title
    private int ordering;       // a number to uniquely identify rows for a given titleId
    private String nconst;      // alphanumeric unique identifier of the name/person
    private String category;    // the category of job that person was in
    private String job;         // the specific job title if applicable
    private String characters;  // the name of the character played if applicable


}
