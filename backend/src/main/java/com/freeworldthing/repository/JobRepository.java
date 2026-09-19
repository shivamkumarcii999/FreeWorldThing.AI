package com.freeworldthing.repository;

import com.freeworldthing.model.Job;
import com.freeworldthing.model.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(Job.Status status);
    List<Job> findByClientId(Long clientId);
    List<Job> findByCategory(Skill.Category category);
    Page<Job> findByStatusOrderByCreatedAtDesc(Job.Status status, Pageable pageable);
    Page<Job> findByClientIdOrderByCreatedAtDesc(Long clientId, Pageable pageable);

    @Query("select j from Job j where j.status = 'OPEN' and (" +
           "lower(j.title) like lower(concat('%', :q, '%')) or " +
           "lower(j.description) like lower(concat('%', :q, '%')))")
    Page<Job> search(@Param("q") String q, Pageable pageable);
}
