CREATE TABLE IF NOT EXISTS transacao (
  id SERIAL primary key,
  tipo int,
  data date,
  valor decimal,
  cpf_cnpj varchar(255),
  cartao varchar(255),
  hora time,
  dono_loja varchar(255),
  nome_loja varchar(255)
);