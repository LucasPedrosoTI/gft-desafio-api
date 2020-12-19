package com.gft.desafioapi.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.validation.Valid;

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
import com.gft.desafioapi.utils.ApiUtils;
import com.gft.desafioapi.utils.Constants;

@Service
public class ProdutoService {

	@Autowired
	ProdutoRepository produtoRepository;

	@Autowired
	FornecedorService fornecedorService;

	public Page<Produto> pesquisarProdutos(ProdutoFilter filter, Pageable pageable) {

		Map<String, Map<String, Object>> verifiedFilter = filter.removeNullValues(filter, new ProdutoFilter());

		return this.produtoRepository.pesquisarProdutos(
				filter.getValueFrom("nome", verifiedFilter),
				filter.getValueFrom("codigoProduto", verifiedFilter),
				new BigDecimal(filter.getValueFrom("valorDe", verifiedFilter)),
				new BigDecimal(filter.getValueFrom("valorAte", verifiedFilter)),
				Long.parseLong(filter.getValueFrom("quantidadeDe", verifiedFilter)),
				Long.parseLong(filter.getValueFrom("quantidadeAte", verifiedFilter)),
				new BigDecimal(filter.getValueFrom("valorPromoDe", verifiedFilter)),
				new BigDecimal(filter.getValueFrom("valorPromoAte", verifiedFilter)),
				pageable);

	}


	public Produto findProdutoById(Long id) {
		return this.produtoRepository.findById(id).orElseThrow(() -> {
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

	public Produto update(Long id, @Valid Produto produto) {

		Produto produtoAtualizado = produto.coalesce(findProdutoById(id), id);

		validatePromocao(produtoAtualizado);

		return produtoRepository.save(produtoAtualizado);
	}

	public ResponseEntity<Map<String, Boolean>> delete(Long id) {
		this.produtoRepository.deleteById(id);

		return Constants.MAP_SUCCESS_TRUE;
	}

	public Produto salvarImagem(MultipartFile imagem, Long id) throws IOException {

		Produto produto = findProdutoById(id);

		String fileName = ApiUtils.getRandomString() + "_"
				+ StringUtils.cleanPath(Optional.ofNullable(imagem.getOriginalFilename()).orElse("no-name"));

		Path fileLocation = Paths.get("src\\main\\resources\\static\\uploads\\" + fileName);

		Files.write(fileLocation, imagem.getBytes());

		produto.setImagem(fileName);

		return produtoRepository.save(produto);

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
