package br.com.alura.agencias.service;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import br.com.alura.agencias.domain.Agencia;
import br.com.alura.agencias.domain.http.AgenciaHttp;
import br.com.alura.agencias.domain.http.SituacaoCadastral;
import br.com.alura.agencias.exception.AgenciaNaoAtivaOuNaoEncontradaException;
import br.com.alura.agencias.repository.AgenciaRepository;
import br.com.alura.agencias.service.http.SituacaoCadastralHttpService;
import io.micrometer.core.instrument.MeterRegistry;
import io.quarkus.hibernate.reactive.panache.common.WithSession;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.logging.Log;
import io.smallrye.mutiny.Uni;
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

	@WithTransaction
	public Uni<Void> cadastrar(Agencia agencia) {
		Uni<AgenciaHttp> agenciaHttp = situacaoCadastralHttpService.buscarPorCnpj(agencia.getCnpj());
		return agenciaHttp
				.onItem()
				.ifNull()
				.failWith(new AgenciaNaoAtivaOuNaoEncontradaException())
				.onItem()
				.transformToUni(item -> persistirSeAtiva(agencia, item));
	}

	private Uni<Void> persistirSeAtiva(Agencia agencia, AgenciaHttp agenciaHttp) {
		if ((agenciaHttp != null) && agenciaHttp.getSituacaoCadastral().equals(SituacaoCadastral.ATIVO)) {
			Log.info("A agencia com CNPJ " + agencia.getCnpj() + " foi cadastrada.");
			meterRegistry.counter("agencia_adicionada_counter").increment();
			return agenciaRepository.persist(agencia).replaceWithVoid();
		} else {
			Log.info("A agencia com CNPJ " + agencia.getCnpj() + " não foi cadastrada.");
			meterRegistry.counter("agencia_nao_adicionada_counter").increment();
			return Uni.createFrom().failure(new AgenciaNaoAtivaOuNaoEncontradaException());
		}
	}

	@WithSession
	public Uni<Agencia> buscarPorId(Long id) {
		return agenciaRepository.findById(id);
	}

	@WithTransaction
	public Uni<Void> deletar(Long id) {
		Log.info("A agencia com id " + id + " foi deletada.");
		return agenciaRepository.deleteById(id).replaceWithVoid();
	}

	@WithTransaction
	public Uni<Void> alterar(Agencia agencia) {
		Log.info("A agencia com CNPJ " + agencia.getCnpj() + " foi alterada.");
		return agenciaRepository.update("nome = ?1, razaoSocial = ?2, cnpj = ?3 where id = ?4", agencia.getNome(),
				agencia.getRazaoSocial(), agencia.getCnpj(), agencia.getId()).replaceWithVoid();
	}

}
