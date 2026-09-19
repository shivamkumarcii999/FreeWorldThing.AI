package com.freeworldthing.repository;

import com.freeworldthing.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findBySubjectIdOrderByCreatedAtDesc(Long subjectId);
    boolean existsByContractIdAndAuthorId(Long contractId, Long authorId);
}
