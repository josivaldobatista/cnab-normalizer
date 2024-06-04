package br.com.jfb.cnabnormalizerbackend.domain;

import java.math.BigDecimal;

public record TransacaoCNAB(
    Integer tipo,
    String data,
    BigDecimal valor,
    String cpfCnpj,
    String cartao,
    String hora,
    String donoDaLoja,
    String nomeDaLoja
) {
}
