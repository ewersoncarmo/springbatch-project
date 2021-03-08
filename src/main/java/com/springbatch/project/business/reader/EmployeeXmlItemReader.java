package com.springbatch.project.business.reader;

import com.springbatch.project.model.dto.EmployeeDTO;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.batch.item.xml.builder.StaxEventItemReaderBuilder;
import org.springframework.core.io.Resource;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

public class EmployeeXmlItemReader {

    public StaxEventItemReader read(Resource resource) {
        Jaxb2Marshaller unmarshaller = new Jaxb2Marshaller();
        unmarshaller.setClassesToBeBound(EmployeeDTO.class);

        System.out.println("reader tread " + Thread.currentThread().getName() + " - " + resource.getFilename());

        return new StaxEventItemReaderBuilder<EmployeeDTO>()
                .name("employeeXmlItemReader")
                .resource(resource)
                .addFragmentRootElements("employee")
                .unmarshaller(unmarshaller)
                .build();
    }
}
