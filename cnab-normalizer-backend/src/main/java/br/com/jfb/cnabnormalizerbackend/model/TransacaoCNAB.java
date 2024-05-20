package br.com.jfb.cnabnormalizerbackend.model;

import java.math.BigDecimal;

public record TransacaoCNAB(
    Integer tipo,
    String data,
    BigDecimal valor,
    Long cpfCnpj,
    String cartao,
    String hora,
    String donoDaLoja,
    String nomeDaLoja
) {
}
