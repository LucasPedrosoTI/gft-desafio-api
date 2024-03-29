package com.gft.desafioapi.utils;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

import java.util.Objects;
import java.util.Random;

import org.springframework.data.domain.Page;

import com.gft.desafioapi.dto.AbstractDTOResponse;
import com.gft.desafioapi.model.AbstractEntity;

@SuppressWarnings("unchecked")
public class ApiUtils {

	private ApiUtils() {}

	public static <T> T coalesce(T one, T two) {
		return one != null ? one : two;
	}

	public static <T extends AbstractEntity> void setIdNull(T entity) {
		if (Objects.nonNull(entity.getId())) {
			entity.setId(null);
		}
	}

	public static <T extends AbstractDTOResponse> T createRelListAllLink(T dto, Class<?> resource) {
		return (T) dto.add(linkTo(resource).withRel("listAll"));
	}


	public static <T extends AbstractDTOResponse> T createSelfLink(T dto, Class<?> resource) {
		return (T) dto.add(linkTo(resource).slash(dto.getId())
				.withSelfRel());
	}


	public static <T extends AbstractDTOResponse> Page<T> createSelfLink(Page<T> dtos, Class<?> resource) {
		return dtos.map(dto -> createSelfLink(dto, resource));
	}

	public static String getRandomString() {
		return new Random().nextInt(999999) + "_" + System.currentTimeMillis();
	}
}
