package com.devsuperior.aula.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.aula.repositories.ProductRepository;
import com.devsuperior.aula.services.exceptions.ResourceNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	private long existingId;
	private long noExistingId;
	
	@BeforeEach
	 void setUp() throws Exception {
		existingId = 1L;
		noExistingId = 1000L;
		doNothing().when(repository).deleteById(existingId);
		doThrow(EmptyResultDataAccessException.class)
		.when(repository).deleteById(noExistingId);
		
		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.when(repository.existsById(noExistingId)).thenReturn(false);
	}
	
	@Test
	public void deleteShouldResourceNotFoundExceptionWhenIdDOesNotExist() {
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			service.delete(noExistingId);
		});
	}
	
	@Test
	public void deleteShouldDoNothingIdExists() {
			
		Assertions.assertDoesNotThrow(() -> {
			service.delete(existingId);
		});
		
		verify(repository, times(1)).deleteById(existingId);
	}
}
