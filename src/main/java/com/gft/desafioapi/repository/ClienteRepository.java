package com.gft.desafioapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;

import com.gft.desafioapi.model.Cliente;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
	public Page<Cliente> findByNomeContaining(String nome, Pageable pageable);

	@Query("select c from Cliente c order by c.nome asc")
	public Page<Cliente> findAllOrderByNomeAsc(Pageable pageable);

	@Query("select c from Cliente c order by c.nome desc")
	public Page<Cliente> findAllOrderByNomeDesc(Pageable pageable);

	public Cliente findByEmail(String email);

	@Query("select c from Cliente c where c.nome like %?1% and c.email like  %?2% and c.documento like %?3% and c.dataCadastro >= ?4 and c.dataCadastro <= ?5")
	public Page<Cliente> pesquisarClientes(String nome, String email, String documento, LocalDate dataCadastroDe,
			LocalDate dataCadastroAte, Pageable pageable);
}
