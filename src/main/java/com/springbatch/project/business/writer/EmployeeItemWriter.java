package com.springbatch.project.business.writer;

import com.springbatch.project.model.entity.Employee;
import com.springbatch.project.repository.EmployeeRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeItemWriter implements ItemWriter<Employee> {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Override
    public void write(List<? extends Employee> list) throws Exception {
        list.forEach(item -> System.out.println("writer thread " + Thread.currentThread().getName() + " - " + item));

        employeeRepository.saveAll(list);
    }
}
