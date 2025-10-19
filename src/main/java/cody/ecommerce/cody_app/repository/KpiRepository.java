package cody.ecommerce.cody_app.repository;

import cody.ecommerce.cody_app.constant.KpiStatus;
import cody.ecommerce.cody_app.entity.Kpi;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface KpiRepository extends JpaRepository<Kpi, String> {

    @Query("SELECT k FROM Kpi k WHERE k.assignToId = ?1")
    List<Kpi> findByAssignToId(String assignToId);

    @Query("SELECT k FROM Kpi k WHERE k.createById = ?1")
    List<Kpi> findByCreateById(String createById);

    @Query("SELECT k FROM Kpi k WHERE k.status = ?1")
    List<Kpi> findByStatus(KpiStatus status);

    @Query("SELECT k FROM Kpi k WHERE k.dueDate BETWEEN ?1 AND ?2")
    List<Kpi> findByDueDateBetween(LocalDateTime start, LocalDateTime end);

    Page<Kpi> findAll(Specification<Kpi> spec, Pageable pageable);
}
