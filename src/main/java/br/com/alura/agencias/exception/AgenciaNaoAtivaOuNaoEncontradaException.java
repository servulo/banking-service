package br.com.alura.agencias.exception;

import br.com.alura.agencias.domain.http.SituacaoCadastral;

public class AgenciaNaoAtivaOuNaoEncontradaException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	@Override
	public String getMessage() {
		return "O status da agência é " + SituacaoCadastral.INATIVO + " ou não foi encontrada.";
	}

}
