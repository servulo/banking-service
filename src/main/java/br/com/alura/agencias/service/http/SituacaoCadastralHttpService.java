package br.com.alura.agencias.service.http;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import br.com.alura.agencias.domain.http.AgenciaHttp;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/situacao-cadastral")
@RegisterRestClient(configKey = "situacao-cadastral-api")
public interface SituacaoCadastralHttpService {

	@GET
	@Path("{cnpj}")
	Uni<AgenciaHttp> buscarPorCnpj(String cnpj);

}
