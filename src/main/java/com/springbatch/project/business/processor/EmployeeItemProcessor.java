package com.springbatch.project.business.processor;

import com.springbatch.project.model.dto.EmployeeDTO;
import com.springbatch.project.model.entity.Address;
import com.springbatch.project.model.entity.Employee;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Component
public class EmployeeItemProcessor implements ItemProcessor<EmployeeDTO, Employee> {

    @Override
    public Employee process(EmployeeDTO employeeDTO) throws Exception {
        return Employee.builder()
                .firstName(employeeDTO.getFirstName())
                .lastName(employeeDTO.getLastName())
                .address(Address.builder()
                        .street(employeeDTO.getAddress().getStreet())
                        .build())
                .build();
    }
}
