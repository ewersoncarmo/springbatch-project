package com.springbatch.project.controller;

import com.springbatch.project.model.dto.JobResponseDTO;
import com.springbatch.project.model.dto.MultiFileResourceDTO;
import com.springbatch.project.model.dto.XmlDirectoryResourceDTO;
import com.springbatch.project.model.dto.XmlFileResourceDTO;
import com.springbatch.project.service.EmployeeService;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/employees")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @PostMapping
    @RequestMapping("/single-process")
    public ResponseEntity<JobResponseDTO> singleProcess(@RequestBody XmlFileResourceDTO xmlFileResourceDTO) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobResponseDTO jobResponseDTO = employeeService.singleProcess(xmlFileResourceDTO);

        return ResponseEntity
                .accepted()
                .body(jobResponseDTO);
    }

    @PostMapping
    @RequestMapping("/single-process-multi-threaded")
    public ResponseEntity<JobResponseDTO> singleProcessMultiThreaded(@RequestBody XmlFileResourceDTO xmlFileResourceDTO) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobResponseDTO jobResponseDTO = employeeService.singleProcessMultiThreaded(xmlFileResourceDTO);

        return ResponseEntity
                .accepted()
                .body(jobResponseDTO);
    }

    @PostMapping
    @RequestMapping("/single-process-parallel-steps")
    public ResponseEntity<JobResponseDTO> singleProcessParallelSteps(@RequestBody MultiFileResourceDTO multiFileResourceDTO) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobResponseDTO jobResponseDTO = employeeService.singleProcessParallelSteps(multiFileResourceDTO);

        return ResponseEntity
                .accepted()
                .body(jobResponseDTO);
    }

    @PostMapping
    @RequestMapping("/partitioning-single-process")
    public ResponseEntity<JobResponseDTO> partitioningSingleProcess(@RequestBody XmlDirectoryResourceDTO xmlDirectoryResourceDTO) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobResponseDTO jobResponseDTO = employeeService.partitioningSingleProcess(xmlDirectoryResourceDTO);

        return ResponseEntity
                .accepted()
                .body(jobResponseDTO);
    }

    @GetMapping
    @RequestMapping("/job/{id}")
    public ResponseEntity<JobResponseDTO> getJob(@PathVariable("id") Long id) {
        JobResponseDTO jobDetailResponseDTO = employeeService.getJob(id);

        return ResponseEntity
                .ok(jobDetailResponseDTO);
    }
}
