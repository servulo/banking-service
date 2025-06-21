package br.com.alura.agencias.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import br.com.alura.agencias.domain.Agencia;
import br.com.alura.agencias.domain.Endereco;
import br.com.alura.agencias.domain.http.AgenciaHttp;
import br.com.alura.agencias.domain.http.SituacaoCadastral;
import br.com.alura.agencias.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import br.com.alura.agencias.repository.AgenciaRepository;
import br.com.alura.agencias.service.http.SituacaoCadastralHttpService;
import br.com.alura.agencias.utils.AgenciaFixture;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;

@QuarkusTest
public class AgenciaServiceTest {
	
	@InjectMock
	private AgenciaRepository agenciaRepository;

	@InjectMock
	@RestClient
	private SituacaoCadastralHttpService situacaoCadastralHttpService;

	@Inject
	private AgenciaService agenciaService;

	@BeforeEach
	public void setUp() {
		Mockito.doNothing().when(agenciaRepository).persist(Mockito.any(Agencia.class));
	}

	@Test	
	public void deveNaoCadastrarQuandoClientRetornarNull() {
		Agencia agencia = AgenciaFixture.criarAgencia();
		Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(null);
		Assertions.assertThrows(AgenciaNaoAtivaOuNaoEncontradaException.class, () -> agenciaService.cadastrar(agencia));
		Mockito.verify(agenciaRepository, Mockito.never()).persist(agencia);
	}
	
	@Test
	public void deveCadastrarQuandoClientRetornarSituacaoCadastralAtiva() {
		Agencia agencia = AgenciaFixture.criarAgencia();
		Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(AgenciaFixture.criarAgenciaHttp(SituacaoCadastral.ATIVO));
		agenciaService.cadastrar(agencia);
		Mockito.verify(agenciaRepository).persist(agencia);
	}
	
	@Test
	public void deveNaoCadastrarQuandoClientRetornarAgenciaInativa() {
		Agencia agencia = AgenciaFixture.criarAgencia();
		Mockito.when(situacaoCadastralHttpService.buscarPorCnpj("123")).thenReturn(AgenciaFixture.criarAgenciaHttp(SituacaoCadastral.INATIVO));
		Assertions.assertThrows(AgenciaNaoAtivaOuNaoEncontradaException.class, () -> agenciaService.cadastrar(agencia));
		Mockito.verify(agenciaRepository, Mockito.never()).persist(agencia);
		
	}
	
	private Agencia criarAgencia() {
		Endereco endereco = new Endereco(1, "Quadra", "Teste", "Teste", 1);
		return new Agencia(1, "Agencia Teste", "Razao Agencia Teste", "123", endereco);
	}
	
	private AgenciaHttp criarAgenciaHttp() {
		return new AgenciaHttp("Agencia Teste", "Razao Agencia Teste", "123", SituacaoCadastral.ATIVO);
	}
	
	
	private AgenciaHttp criarAgenciaHttpInativa() {
		return new AgenciaHttp("Agencia Teste", "Razao Agencia Teste", "123", SituacaoCadastral.INATIVO);
	}

	

}