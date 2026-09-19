package com.freeworldthing.repository;

import com.freeworldthing.model.TaskItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskItemRepository extends JpaRepository<TaskItem, Long> {
    List<TaskItem> findByWorkspaceId(Long workspaceId);
    List<TaskItem> findByMilestoneId(Long milestoneId);
}
