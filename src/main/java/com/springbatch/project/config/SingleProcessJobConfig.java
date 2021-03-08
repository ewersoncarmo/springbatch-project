package com.springbatch.project.config;

import com.springbatch.project.business.reader.EmployeeXmlItemReader;
import com.springbatch.project.model.dto.EmployeeDTO;
import com.springbatch.project.model.entity.Employee;
import org.springframework.batch.core.ItemReadListener;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

@Configuration
public class SingleProcessJobConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public StaxEventItemReader<EmployeeDTO> singleProcessItemReader(@Value("#{jobParameters['file']}") String file) {
        return new EmployeeXmlItemReader()
                .read(new FileSystemResource(file));
    }

    @Bean
    public Step singleProcessStep(ItemReader<EmployeeDTO> singleProcessItemReader,
                                  ItemProcessor<EmployeeDTO, Employee> employeeItemProcessor,
                                  ItemWriter<Employee> employeeItemWriter,
                                  ItemReadListener<EmployeeDTO> employeeItemListener) {
        return stepBuilderFactory.get("singleProcessStep")
                .<EmployeeDTO, Employee>chunk(2)
                .reader(singleProcessItemReader)
                .processor(employeeItemProcessor)
                .writer(employeeItemWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(employeeItemListener)
                .build();
    }

    @Bean
    public Job singleProcessJob(JobBuilderFactory jobBuilderFactory,
                                Step singleProcessStep) {
        return jobBuilderFactory.get("singleProcessJob")
                .incrementer(new RunIdIncrementer())
                .start(singleProcessStep)
                .build();
    }

}
