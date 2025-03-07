package com.example.imdbdataset.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BestTitlesByYear {
    private Integer year;
    private List<TitleDTO> bestTitles;

}