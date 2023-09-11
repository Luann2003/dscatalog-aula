package com.devsuperior.aula.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.devsuperior.aula.entities.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

}
