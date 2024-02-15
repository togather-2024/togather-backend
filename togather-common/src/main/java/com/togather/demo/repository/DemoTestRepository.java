package com.togather.demo.repository;

import com.togather.demo.entity.DemoTestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DemoTestRepository extends JpaRepository<DemoTestEntity, Long> {
}
