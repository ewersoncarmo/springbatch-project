package com.springbatch.project.model.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class MultiFileResourceDTO {

    private CsvFileResourceDTO csvFile;
    private XmlFileResourceDTO xmlFile;
}
