package com.example.imdbdataset.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TitleDTO {
    private String tconst;
    private String primaryTitle;
    private Integer startYear;
    private Float rating;
    private Integer numVotes;

}