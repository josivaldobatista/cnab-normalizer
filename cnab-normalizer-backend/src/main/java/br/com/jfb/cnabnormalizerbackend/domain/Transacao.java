package br.com.jfb.cnabnormalizerbackend.domain;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public record Transacao(
    Long id,
    Integer tipo,
    Date data,
    BigDecimal valor,
    String cpfCnpj,
    String cartao,
    Time hora,
    String donoDaLoja,
    String nomeDaLoja
) {

  // Esse método vai apenas alterar o valor seguindo a convenção de 'Wither pattern'
  public Transacao withValor(BigDecimal valor) {
    return new Transacao(
        id, tipo, data, valor,
        cpfCnpj, cartao, hora,
        donoDaLoja, nomeDaLoja);
  }

  public Transacao withData(String data) throws ParseException {
    var dateFormat = new SimpleDateFormat("yyyMMdd");
    var date = dateFormat.parse(data);

    return new Transacao(
        id, tipo, new Date(date.getTime()),
        valor, cpfCnpj,
        cartao, hora, donoDaLoja,
        nomeDaLoja);
  }

  public Transacao withHora(String hora) throws ParseException {
    var dateFormat = new SimpleDateFormat("HHmmss");
    var date = dateFormat.parse(hora);

    return new Transacao(
        id, tipo, data, valor,
        cpfCnpj, cartao, new Time(date.getTime()),
        donoDaLoja, nomeDaLoja);
  }
}
