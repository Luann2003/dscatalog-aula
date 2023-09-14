package com.devsuperior.aula.services;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.devsuperior.aula.entities.Category;
import com.devsuperior.aula.entities.Product;
import com.devsuperior.aula.entities.Dto.ProductDTO;
import com.devsuperior.aula.repositories.CategoryRepository;
import com.devsuperior.aula.repositories.ProductRepository;
import com.devsuperior.aula.repositories.tests.Factory;
import com.devsuperior.aula.services.exceptions.DatabaseException;
import com.devsuperior.aula.services.exceptions.ResourceNotFoundException;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class)
public class ProductServiceTests {

	@InjectMocks
	private ProductService service;
	
	@Mock
	private ProductRepository repository;
	
	@Mock
	private CategoryRepository categoryRepository;
	
	private long existingId;
	private long noExistingId;
	private long dependentId;
	private PageImpl<Product> page;
	private Product product;
	private Category category;

	
	@BeforeEach
	 void setUp() throws Exception {
		existingId = 1L;
		noExistingId = 1000L;
		dependentId = 3L;
		product = Factory.createProduct();
		category = Factory.createCategory();
		page = new PageImpl<>(List.of(product));
		
		doNothing().when(repository).deleteById(existingId);
		doThrow(EmptyResultDataAccessException.class)
		.when(repository).deleteById(noExistingId);
		doThrow(DataIntegrityViolationException.class).when(repository).deleteById(dependentId);
		

		Mockito.when(repository.getReferenceById(existingId)).thenReturn(product);
		Mockito.when(repository.getReferenceById(noExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
		Mockito.when(categoryRepository.getReferenceById(noExistingId)).thenThrow(EntityNotFoundException.class);
		
		Mockito.when(repository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);
		Mockito.when(repository.save(ArgumentMatchers.any())).thenReturn(product);
		
		Mockito.when(repository.findById(existingId)).thenReturn(Optional.of(product));
		Mockito.when(repository.findById(noExistingId)).thenReturn(Optional.empty());
		
		Mockito.when(repository.existsById(existingId)).thenReturn(true);
		Mockito.when(repository.existsById(noExistingId)).thenReturn(false);
		Mockito.when(repository.existsById(dependentId)).thenReturn(true);
	}
	
	@Test
	public void UpdateshouldResourceNotFoundExceptionWhenIdNoExisting() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {

			ProductDTO dto = Factory.createProductDTO();
			
			service.update(noExistingId, dto);
			
			Assertions.assertNotNull(dto);
		});
	}
	
	@Test
	public void updateShouldProductDtoWhenIdExisting() {
		
		ProductDTO dto = Factory.createProductDTO();
		
		service.update(existingId, dto);
		
		Assertions.assertNotNull(dto);
		
	}
	
	
	@Test
	public void findByIdshouldResourceNotFoundExceptionWhenIdNoExisting() {
		
		Assertions.assertThrows(ResourceNotFoundException.class, () -> {
			ProductDTO dto = service.findById(noExistingId);
			Assertions.assertNotNull(dto);
		});
	}
	
	
	@Test
	public void findByIdshouldProductDtoWhenIdExisting() {
		
		ProductDTO dto = service.findById(existingId);
		
		Assertions.assertNotNull(dto);
	}
	
	@Test
	public void findAllPagedShouldReturnPage() {
		
		Pageable pageable = PageRequest.of(0, 10);
		
		Page<ProductDTO> result = service.findAllPaged(pageable);
		
		Assertions.assertNotNull(result);
		Mockito.verify(repository, Mockito.times(1)).findAll(pageable);
	}
	
	@Test
	public void deleteShouldThrowDatabaseExceptionWhenDependentId() {
		Assertions.assertThrows(DatabaseException.class, () -> {
			service.delete(dependentId);
		});
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
