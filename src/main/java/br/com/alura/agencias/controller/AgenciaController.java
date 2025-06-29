package br.com.alura.agencias.controller;

import org.jboss.resteasy.reactive.RestResponse;

import br.com.alura.agencias.domain.Agencia;
import br.com.alura.agencias.service.AgenciaService;
import io.smallrye.common.annotation.NonBlocking;
import io.smallrye.mutiny.Uni;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.UriInfo;

@Path("/agencias")
public class AgenciaController {

	private final AgenciaService agenciaService;

	AgenciaController(AgenciaService agenciaService) {
		this.agenciaService = agenciaService;
	}

	@POST
	@NonBlocking
	@Transactional
	public Uni<RestResponse<Void>> cadastrar(Agencia agencia, @Context UriInfo uriInfo) {
		return this.agenciaService.cadastrar(agencia)
				.replaceWith(RestResponse.created(uriInfo.getAbsolutePathBuilder().build()));
	}

	@GET
	@Path("{id}")
	public Uni<RestResponse<Agencia>> buscarPorId(Long id) {
		return this.agenciaService.buscarPorId(id).onItem().transform(agencia -> RestResponse.ok(agencia));
	}

	@DELETE
	@Path("{id}")
	@Transactional
	public Uni<RestResponse<Void>> deletar(Long id) {
		return this.agenciaService.deletar(id).replaceWith(RestResponse.ok());
	}

	@PUT
	@Transactional
	public Uni<RestResponse<Void>> alterar(Agencia agencia) {
		return this.agenciaService.alterar(agencia).replaceWith(RestResponse.ok());
	}

}
