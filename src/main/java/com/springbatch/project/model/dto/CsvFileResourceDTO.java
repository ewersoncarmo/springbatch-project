package com.springbatch.project.model.dto;

import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;

@Setter
@Getter
public class CsvFileResourceDTO {

    private Path file;
}
