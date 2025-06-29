package br.com.alura.agencias.repository;

import br.com.alura.agencias.domain.Agencia;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class AgenciaRepository implements PanacheRepository<Agencia> {

}
