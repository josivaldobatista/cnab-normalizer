package br.com.jfb.cnabnormalizerbackend.controller;

import br.com.jfb.cnabnormalizerbackend.service.CnabService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/cnab")
public class CnabController {

  @Autowired
  private CnabService cnabService;

  @PostMapping("/upload")
  public String upload(@RequestParam("file") MultipartFile file) throws Exception {
    cnabService.uploadCnabFile(file);
    return "Processamento iniciado.";
  }
}
