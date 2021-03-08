package com.springbatch.project.business.reader;

import com.springbatch.project.model.dto.AddressDTO;
import com.springbatch.project.model.dto.EmployeeDTO;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.mapping.PatternMatchingCompositeLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.item.file.transform.LineTokenizer;
import org.springframework.core.io.Resource;

import java.util.HashMap;
import java.util.Map;

public class EmployeeCsvItemReader {

    private EmployeeDTO employeeDTO;

    public FlatFileItemReader<EmployeeDTO> read(Resource resource) {
        System.out.println("reader tread " + Thread.currentThread().getName() + " - " + resource.getFilename());

        Map<String, LineTokenizer> lineTokenizers = new HashMap<>();
        lineTokenizers.put("EMPLOYEE*", employeeLineTokenizer());
        lineTokenizers.put("ADDRESS*", addressLineTokenizer());

        Map<String, FieldSetMapper> fieldSetMappers = new HashMap<>();
        fieldSetMappers.put("EMPLOYEE*", this::employeeFieldSetMapper);
        fieldSetMappers.put("ADDRESS*", this::addressFieldSetMapper);

        PatternMatchingCompositeLineMapper lineMapper = new PatternMatchingCompositeLineMapper();
        lineMapper.setTokenizers(lineTokenizers);
        lineMapper.setFieldSetMappers(fieldSetMappers);

        return new FlatFileItemReaderBuilder<EmployeeDTO>()
                .name("employeeCsvItemReader")
                .resource(resource)
                .encoding("utf-8")
                .lineMapper(lineMapper)
                .build();
    }

    private EmployeeDTO employeeFieldSetMapper(FieldSet fieldSet) {
        employeeDTO = new EmployeeDTO();
        employeeDTO.setFirstName(fieldSet.readString("firstName"));
        employeeDTO.setLastName(fieldSet.readString("lastName"));

        return employeeDTO;
    }

    private AddressDTO addressFieldSetMapper(FieldSet fieldSet) {
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setStreet(fieldSet.readString("street"));

        employeeDTO.setAddress(addressDTO);

        return employeeDTO.getAddress();
    }

    private DelimitedLineTokenizer employeeLineTokenizer() {
        DelimitedLineTokenizer employeeeLineTokenizer = new DelimitedLineTokenizer(";");
        employeeeLineTokenizer.setNames("EMPLOYEE","firstName","lastName");

        return employeeeLineTokenizer;
    }

    private DelimitedLineTokenizer addressLineTokenizer() {
        DelimitedLineTokenizer addressLineTokenizer = new DelimitedLineTokenizer(";");
        addressLineTokenizer.setNames("ADDRESS","street");

        return addressLineTokenizer;
    }
}
