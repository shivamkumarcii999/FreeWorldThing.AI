package com.freeworldthing.repository;

import com.freeworldthing.model.ServiceListing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceListingRepository extends JpaRepository<ServiceListing, Long> {
    List<ServiceListing> findByFreelancerId(Long freelancerId);
    List<ServiceListing> findByCategoryIgnoreCase(String category);
}
