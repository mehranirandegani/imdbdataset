package com.example.imdbdataset.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TitleCrew {
    // Getters and setters
    private String tconst;          // alphanumeric unique identifier
    private String[] directors;     // director(s) of the title (comma-separated nconst)
    private String[] writers;       // writer(s) of the title (comma-separated nconst)

}
