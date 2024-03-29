package com.gft.desafioapi.repository.serializer;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.gft.desafioapi.dto.IdDto;
import com.gft.desafioapi.repository.FornecedorRepository;
import com.gft.desafioapi.utils.Constants;

@Component
public class CustomProdutoFornecedorDeserializer extends StdDeserializer<IdDto> {

	@Autowired
	transient FornecedorRepository fornecedorRepository;

	private static final long serialVersionUID = -839983745171929649L;

	public CustomProdutoFornecedorDeserializer() {
		this(null);
	}

	public CustomProdutoFornecedorDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public IdDto deserialize(JsonParser jsonparser, DeserializationContext context)
			throws IOException {

		Long id = null;
		JsonNode node = jsonparser.getCodec().readTree(jsonparser);

		if (!node.has("id")) {
			throw new EmptyResultDataAccessException(Constants.FORNECEDOR_INEXISTENTE, 1);
		}

		id = node.get("id")
				.asLong();

		fornecedorRepository.findById(id)
		.orElseThrow(() -> {
			throw new EmptyResultDataAccessException(1);
		});

		return new IdDto(id);
	}

}
