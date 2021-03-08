package com.springbatch.project.config;

import com.springbatch.project.business.reader.EmployeeCsvItemReader;
import com.springbatch.project.business.reader.EmployeeXmlItemReader;
import com.springbatch.project.model.dto.EmployeeDTO;
import com.springbatch.project.model.entity.Employee;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class SingleProcessParallelStepsJobConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public FlatFileItemReader<EmployeeDTO> singleProcessCsvFlowItemReader(@Value("#{jobParameters['csvFile']}") String csvFile) {
        return new EmployeeCsvItemReader()
                .read(new FileSystemResource(csvFile));
    }

    @Bean
    public Step singleProcessCsvFlowStep(ItemReader<EmployeeDTO> singleProcessCsvFlowItemReader,
                                         ItemProcessor<EmployeeDTO, Employee> employeeItemProcessor,
                                         ItemWriter<Employee> employeeItemWriter,
                                         ItemReadListener<EmployeeDTO> employeeItemListener,
                                         TaskExecutor taskExecutor) {
        return stepBuilderFactory.get("singleProcessCsvFlowStep")
                .<EmployeeDTO, Employee>chunk(2)
                .reader(singleProcessCsvFlowItemReader)
                .processor(employeeItemProcessor)
                .writer(employeeItemWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(employeeItemListener)
                .build();
    }

    @Bean
    @StepScope
    public StaxEventItemReader<EmployeeDTO> singleProcessXmlFlowItemReader(@Value("#{jobParameters['xmlFile']}") String xmlFile) {
        return new EmployeeXmlItemReader()
                .read(new FileSystemResource(xmlFile));
    }

    @Bean
    public Step singleProcessXmlFlowStep(ItemReader<EmployeeDTO> singleProcessXmlFlowItemReader,
                                         ItemProcessor<EmployeeDTO, Employee> employeeItemProcessor,
                                         ItemWriter<Employee> employeeItemWriter,
                                         ItemReadListener<EmployeeDTO> employeeItemListener,
                                         TaskExecutor taskExecutor) {
        return stepBuilderFactory.get("singleProcessXmlFlowStep")
                .<EmployeeDTO, Employee>chunk(2)
                .reader(singleProcessXmlFlowItemReader)
                .processor(employeeItemProcessor)
                .writer(employeeItemWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(employeeItemListener)
                .build();
    }

    @Bean
    public Flow csvFlow(Step singleProcessCsvFlowStep) {
        return new FlowBuilder<SimpleFlow>("csvFlow")
                .start(singleProcessCsvFlowStep)
                .build();
    }

    @Bean
    public Flow xmlFlow(Step singleProcessXmlFlowStep) {
        return new FlowBuilder<SimpleFlow>("xmlFlow")
                .start(singleProcessXmlFlowStep)
                .build();
    }

    @Bean
    public Flow splitFlow(TaskExecutor taskExecutor,
                          Flow csvFlow,
                          Flow xmlFlow) {
        return new FlowBuilder<SimpleFlow>("splitFlow")
                .split(taskExecutor)
                .add(csvFlow, xmlFlow)
                .build();
    }

    @Bean
    public Job singleProcessParallelStepsJob(JobBuilderFactory jobBuilderFactory,
                                             Flow splitFlow) {
        return jobBuilderFactory.get("singleProcessParallelStepsJob")
                .incrementer(new RunIdIncrementer())
                .start(splitFlow).build()
                .build();
    }
}
