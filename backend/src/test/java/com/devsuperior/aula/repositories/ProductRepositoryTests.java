package com.devsuperior.aula.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.devsuperior.aula.entities.Product;
import com.devsuperior.aula.repositories.tests.Factory;

@DataJpaTest
public class ProductRepositoryTests {

	@Autowired
	private ProductRepository repository;
	
	private long existingId;
	private long countTotalProducts;
	private long noExistingId;
	
	@BeforeEach
	void setUp () throws Exception {
		existingId = 1L;
		countTotalProducts = 25L;
	}
	
	@Test
	public void saveShouldPersistWithAutoincreamentWhenIdIsNull() {
		
		Product product = Factory.createProduct();
		product.setId(null);
		
		product = repository.save(product);
		
		Assertions.assertNotNull(product.getId());
		Assertions.assertEquals(countTotalProducts + 1, product.getId());
	}
	
	@Test
	public void deleteShouldDeleteObjectWhenIdExist() {
		
		existingId = 1L;
		
		repository.deleteById(existingId);
		
		Optional<Product> result = repository.findById(1L);
		Assertions.assertFalse(result.isPresent());
	}
	
	@Test
	public void returnShouldIdWhenExistingId() {
		existingId = 1L;
		
	
		Optional<Product> result = repository.findById(existingId);
		Assertions.assertTrue(result.isPresent());
	}
	
	@Test
	public void returnShouldIdWhenNoExistingId() {
		noExistingId = 1000L;
		
		Optional<Product> result = repository.findById(noExistingId);
		Assertions.assertTrue(result.isEmpty());
	}
}
