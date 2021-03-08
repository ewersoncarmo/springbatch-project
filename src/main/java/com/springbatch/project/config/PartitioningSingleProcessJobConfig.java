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
import org.springframework.batch.core.partition.support.MultiResourcePartitioner;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.task.TaskExecutor;

import java.io.File;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.stream.Stream;

@Configuration
public class PartitioningSingleProcessJobConfig {

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Bean
    @StepScope
    public Partitioner partitioner(@Value("#{jobParameters['directory']}") String directory) {
        MultiResourcePartitioner partitioner = new MultiResourcePartitioner();

        Stream<File> fileStream = Arrays.stream(new File(directory).listFiles());

        FileSystemResource[] resources = fileStream
                .filter(f -> f.isFile() && f.getName().toLowerCase().endsWith(".xml"))
                .map(FileSystemResource::new)
                .toArray(FileSystemResource[]::new);

        partitioner.setResources(resources);

        return partitioner;
    }

    @Bean
    @StepScope
    public StaxEventItemReader<EmployeeDTO> partitioningSingleProcessItemReader(@Value("#{stepExecutionContext['fileName']}") String fileName) throws MalformedURLException {
        return new EmployeeXmlItemReader()
                .read(new UrlResource(fileName));
    }

    @Bean
    public Step partitioningSingleProcessStep(ItemReader<EmployeeDTO> partitioningSingleProcessItemReader,
                                              ItemProcessor<EmployeeDTO, Employee> employeeItemProcessor,
                                              ItemWriter<Employee> employeeItemWriter,
                                              ItemReadListener<EmployeeDTO> employeeItemListener) {
        return stepBuilderFactory.get("partitioningSingleProcessStep")
                .<EmployeeDTO, Employee>chunk(2)
                .reader(partitioningSingleProcessItemReader)
                .processor(employeeItemProcessor)
                .writer(employeeItemWriter)
                .faultTolerant()
                .skip(Exception.class)
                .skipLimit(Integer.MAX_VALUE)
                .listener(employeeItemListener)
                .build();
    }

    @Bean
    public Step partitioningSingleProcessMasterStep(Partitioner partitioner,
                                                    Step partitioningSingleProcessStep,
                                                    TaskExecutor taskExecutor) {
        return stepBuilderFactory.get("partitioningSingleProcessMasterStep")
                .partitioner("partition", partitioner)
                .step(partitioningSingleProcessStep)
                .taskExecutor(taskExecutor)
                .gridSize(3)
                .build();
    }

    @Bean
    public Job partitioningSingleProcessJob(JobBuilderFactory jobBuilderFactory,
                                            Step partitioningSingleProcessMasterStep) {
        return jobBuilderFactory.get("partitioningSingleProcessJob")
                .incrementer(new RunIdIncrementer())
                .start(partitioningSingleProcessMasterStep)
                .build();
    }
}
