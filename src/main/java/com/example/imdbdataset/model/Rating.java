package com.example.imdbdataset.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Rating {
    // Getters and setters
    private String tconst;      // alphanumeric unique identifier
    private Float averageRating; // weighted average of all individual user ratings
    private Integer numVotes;    // number of votes the title has received

}