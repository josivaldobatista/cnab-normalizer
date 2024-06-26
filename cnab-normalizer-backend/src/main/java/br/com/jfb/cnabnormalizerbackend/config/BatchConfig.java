package br.com.jfb.cnabnormalizerbackend.config;

import br.com.jfb.cnabnormalizerbackend.domain.Transacao;
import br.com.jfb.cnabnormalizerbackend.domain.TransacaoCNAB;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.TaskExecutorJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.transform.Range;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.math.BigDecimal;

@Configuration
public class BatchConfig {

  private final PlatformTransactionManager transactionManager;
  private final JobRepository jobRepository;

  public BatchConfig(
      PlatformTransactionManager transactionManager,
      JobRepository jobRepository) {
    this.transactionManager = transactionManager;
    this.jobRepository = jobRepository;
  }

  @Bean
  Job job(Step step) {
    return new JobBuilder("job", jobRepository)
        .start(step)
        .incrementer(new RunIdIncrementer())
        .build();
  }

  @Bean
  Step step(
      ItemReader<TransacaoCNAB> itemReader,
      ItemProcessor<TransacaoCNAB, Transacao> itemProcessor,
      ItemWriter<Transacao> itemWriter) {
    return new StepBuilder("step", jobRepository)
        .<TransacaoCNAB, Transacao>chunk(1000, transactionManager)
        .reader(itemReader)
        .processor(itemProcessor)
        .writer(itemWriter)
        .build();
  }

  @Bean
  @StepScope
  FlatFileItemReader<TransacaoCNAB> reader(
      @Value("#{jobParameters['cnabFile']}") Resource resource) {
    return new FlatFileItemReaderBuilder<TransacaoCNAB>()
        .name("reader")
        .resource(resource)
        .fixedLength()
        .columns(
            new Range(1, 1), new Range(2, 9),
            new Range(10, 19), new Range(20, 30),
            new Range(31, 42), new Range(43, 48),
            new Range(49, 62), new Range(63, 80))
        .names(
            "tipo", "data", "valor", "cpfCnpj",
            "cartao", "hora", "donoDaLoja", "nomeDaLoja")
        .targetType(TransacaoCNAB.class)
        .build();
  }

  @Bean
  ItemProcessor<TransacaoCNAB, Transacao> processor() {
    /*
     *  Wither pattern: Faz com que objetos que não mudam eu possa mudar algumas propriedade dentro dele,
     * um bom exemplo são os record. Ele vai recriar a transação mudando apenas as propriedades que eu
     * informei que pode ser mudada.
     * */
    return item -> {
      return new Transacao(
          null, item.tipo(), null,
          item.valor().divide(BigDecimal.valueOf(100)), item.cpfCnpj(),
          item.cartao(), null, item.donoDaLoja().trim(),
          item.nomeDaLoja().trim())
          .withData(item.data())
          .withHora(item.hora());
    };
  }

  @Bean
  JdbcBatchItemWriter<Transacao> writer(DataSource dataSource) {
    return new JdbcBatchItemWriterBuilder<Transacao>()
        .dataSource(dataSource)
        .sql("""
            INSERT INTO transacao (
              tipo, data, valor, cpf_cnpj, cartao,
              hora, dono_loja, nome_loja
            ) VALUES (
            :tipo, :data, :valor, :cpfCnpj, :cartao,
            :hora, :donoDaLoja, :nomeDaLoja)
            """)
        .beanMapped()
        .build();
  }

  @Bean
  JobLauncher jobLauncherAsync(JobRepository jobRepository) throws Exception {
    var jobLauncher = new TaskExecutorJobLauncher();
    jobLauncher.setJobRepository(jobRepository);
    jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());
    jobLauncher.afterPropertiesSet();

    return jobLauncher;
  }

  // https://www.youtube.com/watch?v=1eb7VNQT4EY&list=PLiFLtuN04BS1c-JvhKFxYyeD-GVtnwUcx&index=3

}
