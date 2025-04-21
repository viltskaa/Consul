package com.example.consul.repositories;
import com.example.consul.models.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApiKeyRepository extends JpaRepository<ApiKey, Integer> {
}
