package com.gft.desafioapi.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.gft.desafioapi.model.Produto;
import com.gft.desafioapi.repository.ProdutoRepository;
import com.gft.desafioapi.repository.filter.ProdutoFilter;
import com.gft.desafioapi.utils.Constants;
import com.gft.desafioapi.utils.ApiUtils;

@Service
public class ProdutoService {

	@Autowired
	ProdutoRepository produtoRepository;

	@Autowired
	FornecedorService fornecedorService;


	public Page<Produto> pesquisarProdutos(ProdutoFilter filter, Pageable pageable) {

		final String nome = Optional.ofNullable(filter.getNome()).orElse("");
		final String codigoProduto = Optional.ofNullable(filter.getCodigoProduto()).orElse("");
		final BigDecimal valorDe = Optional.ofNullable(filter.getValorDe()).orElse(BigDecimal.ZERO);
		final BigDecimal valorAte = Optional.ofNullable(filter.getValorAte()).orElse(Constants.MAX_DECIMAL);
		final BigDecimal valorPromoDe = Optional.ofNullable(filter.getValorPromoDe()).orElse(BigDecimal.ZERO);
		final BigDecimal valorPromoAte = Optional.ofNullable(filter.getValorPromoAte()).orElse(Constants.MAX_DECIMAL);
		final Long quantidadeDe = Optional.ofNullable(filter.getQuantidadeDe()).orElse(0L);
		final Long quantidadeAte = Optional.ofNullable(filter.getQuantidadeAte()).orElse(Constants.MAX_DECIMAL.longValue());

		return produtoRepository.pesquisarProdutos(nome, codigoProduto, valorDe, valorAte, quantidadeDe, quantidadeAte,
				valorPromoDe, valorPromoAte,
				pageable);
	}


	public Produto findProdutoById(Long id) {
		return produtoRepository.findById(id).orElseThrow(() -> {
			throw new EmptyResultDataAccessException(1);
		});
	}

	public Produto create(Produto produto) {

		ApiUtils.setIdNull(produto);

		checkFornecedor(produto);

		validatePromocao(produto);

		fornecedorService.findFornecedorById(produto.getFornecedor().getId());

		return produtoRepository.save(produto);
	}

	public Produto update(Long id, Produto produto) {

		Produto produtoAtualizado = produto.coalesce(findProdutoById(id), id);

		validatePromocao(produtoAtualizado);

		return produtoRepository.save(produtoAtualizado);
	}

	public ResponseEntity<Map<String, Boolean>> delete(Long id) {
		produtoRepository.deleteById(id);

		return Constants.MAP_SUCCESS_TRUE;
	}

	public Produto salvarImagem(MultipartFile imagem, Long id) throws IOException {

		Produto produto = findProdutoById(id);

		String fileName = getRandomString() + "_"
				+ StringUtils.cleanPath(Optional.ofNullable(imagem.getOriginalFilename())
						.orElse("no-name"));

		Path fileLocation = Paths.get("src\\main\\resources\\static\\uploads\\" + fileName);

		Files.write(fileLocation, imagem.getBytes());

		produto.setImagem(fileName);

		return produtoRepository.save(produto);

	}

	private String getRandomString() {
		return new Random().nextInt(999999) + "_" + System.currentTimeMillis();
	}

	private void validatePromocao(Produto produto) {
		if (Boolean.FALSE.equals(produto.isPromocao()) && Objects.nonNull(produto.getValorPromo())) {
			throw new DataIntegrityViolationException(Constants.PRODUTO_VALOR_PROMO_MESSAGE);
		}

		if (Boolean.TRUE.equals(produto.isPromocao()) && Objects.isNull(produto.getValorPromo())) {
			throw new DataIntegrityViolationException(Constants.PRODUTO_VALOR_PROMO_MESSAGE);
		}
	}

	private void checkFornecedor(Produto produto) {
		if (Objects.isNull(produto.getFornecedor())) {
			throw new EmptyResultDataAccessException(Constants.FORNECEDOR_INEXISTENTE, 1);
		}
	}


}
