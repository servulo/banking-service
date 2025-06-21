package br.com.alura.agencias.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import br.com.alura.agencias.domain.Agencia;
import br.com.alura.agencias.domain.http.AgenciaHttp;
import br.com.alura.agencias.domain.http.SituacaoCadastral;
import br.com.alura.agencias.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import br.com.alura.agencias.repository.AgenciaRepository;
import br.com.alura.agencias.service.http.SituacaoCadastralHttpService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgenciaService {

	@RestClient
	SituacaoCadastralHttpService situacaoCadastralHttpService;

	private final AgenciaRepository agenciaRepository;
	
	private final MeterRegistry meterRegistry;

	public AgenciaService(AgenciaRepository agenciaRepository, MeterRegistry meterRegistry) {
		this.agenciaRepository = agenciaRepository;
		this.meterRegistry = meterRegistry;
	}

	public void cadastrar(Agencia agencia) {
		AgenciaHttp agenciaHttp = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());
		if ((agenciaHttp != null) && agenciaHttp.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
			Log.info("A agencia com CNPJ " + agencia.getCnpj() + " foi cadastrada.");
			meterRegistry.counter("agencia_adicionada_counter").increment();
			agenciaRepository.persist(agencia);
		} else {
			Log.info("A agencia com CNPJ " + agencia.getCnpj() + " não foi cadastrada.");
			meterRegistry.counter("agencia_nao_adicionada_counter").increment();
			throw new AgenciaNaoAtivaOuNaoEncontradaException();
		}
	}

	public Agencia buscarPorId(Long id) {
		return agenciaRepository.findById(id);
	}

	public void deletar(Long id) {
		Log.info("A agencia com id " + id + " foi deletada.");
		agenciaRepository.deleteById(id);
	}

	public void alterar(Agencia agencia) {
		Log.info("A agencia com CNPJ " + agencia.getCnpj() + " foi alterada.");
		agenciaRepository.update("nome = ?1, razaoSocial = ?2, cnpj = ?3 where id = ?4", agencia.getNome(),
				agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getId());
	}

}
