package com.freeworldthing.repository;

import com.freeworldthing.model.ProjectMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjectMessageRepository extends JpaRepository<ProjectMessage, Long> {
    List<ProjectMessage> findByWorkspaceIdOrderByCreatedAtAsc(Long workspaceId);
}
