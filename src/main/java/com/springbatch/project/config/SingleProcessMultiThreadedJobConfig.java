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
import org.springframework.batch.item.support.SynchronizedItemStreamReader;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.task.TaskExecutor;

@Configuration
public class SingleProcessMultiThreadedJobConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public SynchronizedItemStreamReader<EmployeeDTO> singleProcessMultiThreadedItemReader(@Value("#{jobParameters['file']}") String file) {
        StaxEventItemReader itemReader = new EmployeeXmlItemReader()
                .read(new FileSystemResource(file));

        SynchronizedItemStreamReader synchronizedItemStreamReader = new SynchronizedItemStreamReader();
        synchronizedItemStreamReader.setDelegate(itemReader);

        return synchronizedItemStreamReader;
    }

    @Bean
    public Step singleProcessMultiThreadedStep(ItemReader<EmployeeDTO> singleProcessMultiThreadedItemReader,
                                               ItemProcessor<EmployeeDTO, Employee> employeeItemProcessor,
                                               ItemWriter<Employee> employeeItemWriter,
                                               ItemReadListener<EmployeeDTO> employeeItemListener,
                                               TaskExecutor taskExecutor) {
        return stepBuilderFactory.get("singleProcessMultiThreadedStep")
                .<EmployeeDTO, Employee>chunk(2)
                .reader(singleProcessMultiThreadedItemReader)
                .processor(employeeItemProcessor)
                .writer(employeeItemWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(employeeItemListener)
                .taskExecutor(taskExecutor)
                .throttleLimit(2)
                .build();
    }

    @Bean
    public Job singleProcessMultiThreadedJob(JobBuilderFactory jobBuilderFactory,
                                             Step singleProcessMultiThreadedStep) {
        return jobBuilderFactory.get("singleProcessMultiThreadedJob")
                .incrementer(new RunIdIncrementer())
                .start(singleProcessMultiThreadedStep)
                .build();
    }
}
