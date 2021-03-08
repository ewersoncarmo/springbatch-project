package com.springbatch.project.service;

import com.springbatch.project.model.dto.JobResponseDTO;
import com.springbatch.project.model.dto.MultiFileResourceDTO;
import com.springbatch.project.model.dto.XmlDirectoryResourceDTO;
import com.springbatch.project.model.dto.XmlFileResourceDTO;
import org.springframework.batch.core.*;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZoneId;

@Service
public class EmployeeService {

    @Autowired
    private JobLauncher asyncJobLauncher;

    @Autowired
    private JobExplorer jobExplorer;

    @Autowired
    private Job singleProcessJob;

    @Autowired
    private Job singleProcessMultiThreadedJob;

    @Autowired
    private Job singleProcessParallelStepsJob;

    @Autowired
    private Job partitioningSingleProcessJob;

    public JobResponseDTO singleProcess(XmlFileResourceDTO xmlFileResourceDTO) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("file", xmlFileResourceDTO.getFile().toString(), false)
                .toJobParameters();

        JobExecution jobExecution = asyncJobLauncher.run(singleProcessJob, jobParameters);

        return mapJobResponseDTO(jobExecution);
    }

    public JobResponseDTO singleProcessMultiThreaded(XmlFileResourceDTO xmlFileResourceDTO) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("file", xmlFileResourceDTO.getFile().toString(), false)
                .toJobParameters();

        JobExecution jobExecution = asyncJobLauncher.run(singleProcessMultiThreadedJob, jobParameters);

        return mapJobResponseDTO(jobExecution);
    }

    public JobResponseDTO singleProcessParallelSteps(MultiFileResourceDTO multiFileResourceDTO) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("csvFile", multiFileResourceDTO.getCsvFile().getFile().toString(), false)
                .addString("xmlFile", multiFileResourceDTO.getXmlFile().getFile().toString(), false)
                .toJobParameters();

        JobExecution jobExecution = asyncJobLauncher.run(singleProcessParallelStepsJob, jobParameters);

        return mapJobResponseDTO(jobExecution);
    }

    public JobResponseDTO partitioningSingleProcess(XmlDirectoryResourceDTO xmlDirectoryResourceDTO) throws JobParametersInvalidException, JobExecutionAlreadyRunningException, JobRestartException, JobInstanceAlreadyCompleteException {
        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .addString("directory", xmlDirectoryResourceDTO.getDirectory().toString(), false)
                .toJobParameters();

        JobExecution jobExecution = asyncJobLauncher.run(partitioningSingleProcessJob, jobParameters);

        return mapJobResponseDTO(jobExecution);
    }

    public JobResponseDTO getJob(Long id) {
        JobExecution jobExecution = jobExplorer.getJobExecution(id);

        int records = jobExecution.getStepExecutions().stream()
                .filter(stepExecution -> stepExecution.getStepName().equals("partitioningSingleProcessMasterStep"))
                .mapToInt(StepExecution::getWriteCount)
                .findFirst()
                .getAsInt();

        JobResponseDTO jobResponseDTO = JobResponseDTO.builder()
                .id(jobExecution.getId())
                .status(jobExecution.getStatus().toString())
                .startTime(jobExecution.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .endTime(jobExecution.getEndTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .records(records)
                .build();

        return jobResponseDTO;
    }

    private JobResponseDTO mapJobResponseDTO(JobExecution jobExecution) {
        JobResponseDTO jobResponseDTO = JobResponseDTO.builder()
                .id(jobExecution.getId())
                .status(jobExecution.getStatus().toString())
                .startTime(jobExecution.getCreateTime().toInstant().atZone(ZoneId.systemDefault()).toLocalDate())
                .build();

        return jobResponseDTO;
    }
}
