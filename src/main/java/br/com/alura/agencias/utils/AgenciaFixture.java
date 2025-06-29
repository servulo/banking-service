package br.com.alura.agencias.utils;

import br.com.alura.agencias.domain.Agencia;
import br.com.alura.agencias.domain.Endereco;
import br.com.alura.agencias.domain.http.AgenciaHttp;
import br.com.alura.agencias.domain.http.SituacaoCadastral;

public class AgenciaFixture {

	public static AgenciaHttp criarAgenciaHttp(SituacaoCadastral situacaoCadastral) {
		return new AgenciaHttp("Agencia Teste", "Razao social da Agencia Teste", "123", situacaoCadastral);
	}

	public static Agencia criarAgencia() {
		Endereco endereco = new Endereco(1, "Rua de teste", "Logradouro de teste", "Complemento de teste", 1);
		return new Agencia(1L, "Agencia Teste", "Razao social da Agencia Teste", "123", endereco);
	}

}
