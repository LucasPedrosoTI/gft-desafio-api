package com.gft.desafioapi.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Pageable;

import com.gft.desafioapi.model.Fornecedor;
import com.gft.desafioapi.repository.FornecedorRepository;
import com.gft.desafioapi.repository.filter.FornecedorFilter;
import com.gft.desafioapi.utils.Constants;

public class FornecedorServiceTest {

	@Mock
	private FornecedorRepository fornecedorRepository;

	@Mock
	private Pageable pageable;

	@InjectMocks
	private FornecedorService fornecedorService;

	private Fornecedor fornecedor;
	private FornecedorFilter filter;

	@BeforeEach
	void setup() throws Exception {
		fornecedor = new Fornecedor(1L, "Nome", "12345678912345", null);
		filter = new FornecedorFilter();
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void deveCriarFornecedorSetandoIdNull() throws Exception {

		Fornecedor fornecedorFinal = new Fornecedor(null, "Nome", "12345678912345", null);

		fornecedorService.create(fornecedor);

		verify(fornecedorRepository).save(fornecedorFinal);
	}

	@Test
	void deveAtualizaFornecedor() throws Exception {

		Fornecedor atualizacao = new Fornecedor(null, "Apple", null, null);

		when(fornecedorRepository.findById(1L)).thenReturn(Optional.of(fornecedor));

		fornecedor.setNome("Apple");

		fornecedorService.update(1L, atualizacao);

		verify(fornecedorRepository).save(fornecedor);
	}

	@Test
	void deveRetornarMapSuccessTrue() throws Exception {

		doNothing().when(fornecedorRepository).deleteById(1L);

		assertEquals(Constants.MAP_SUCCESS_TRUE, fornecedorService.delete(1L));
	}

	@Test
	void deveNormalizarFilterDaPesquisa() throws Exception {
		filter.setNome(null);

		fornecedorService.pesquisarFornecedores(filter, pageable);

		verify(fornecedorRepository).findByNomeContainingAndCnpjContaining("", "", pageable);
	}

	@Test
	void deveManterAtributosDoFilterQuandoInformados() throws Exception {
		filter.setNome("Lucas");

		fornecedorService.pesquisarFornecedores(filter, pageable);

		verify(fornecedorRepository).findByNomeContainingAndCnpjContaining("Lucas", "", pageable);
	}

}
