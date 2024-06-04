package br.com.jfb.cnabnormalizerbackend.service;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@Service
public class CnabService {

  private final Path fileStorageLocation;
  private final Job job;
  private final JobLauncher jobLauncher;

  public CnabService(
      @Value("${file.upload-dir}") String fileUploadDir,
      @Qualifier("jobLauncherAsync") JobLauncher jobLauncher,
      Job job) {
    this.fileStorageLocation = Paths.get(fileUploadDir);
    this.job = job;
    this.jobLauncher = jobLauncher;
  }

  public void uploadCnabFile(MultipartFile file) throws Exception {
    var fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
    var targetLocation = fileStorageLocation.resolve(fileName);
    file.transferTo(targetLocation);

    var jobParameters = new JobParametersBuilder()
        .addJobParameter("cnab", file.getOriginalFilename(), String.class, true)
        .addJobParameter("cnabFile", "file:" + targetLocation.toString(), String.class)
        .toJobParameters();

    jobLauncher.run(job, jobParameters);
  }
}
