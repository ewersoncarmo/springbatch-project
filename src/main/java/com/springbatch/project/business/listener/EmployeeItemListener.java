package com.springbatch.project.business.listener;

import com.springbatch.project.model.dto.EmployeeDTO;
import com.springbatch.project.model.entity.Employee;
import org.springframework.batch.core.listener.ItemListenerSupport;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EmployeeItemListener extends ItemListenerSupport<EmployeeDTO, Employee> {

    @Override
    public void onReadError(Exception ex) {
        System.out.println("on read error " + ex.getMessage());
    }

    @Override
    public void onProcessError(EmployeeDTO item, Exception e) {
        System.out.println("on process error " + e.getMessage());
    }

    @Override
    public void onWriteError(Exception ex, List<? extends Employee> item) {
        System.out.println("on write error " + ex.getMessage());
    }
}
