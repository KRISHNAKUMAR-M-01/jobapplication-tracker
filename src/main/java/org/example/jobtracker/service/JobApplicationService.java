package org.example.jobtracker.service;

import org.example.jobtracker.model.ApplicationStatus;
import org.example.jobtracker.model.JobApplication;
import org.example.jobtracker.repository.JobApplicationRepository;
import org.example.jobtracker.repository.StatusCount;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class JobApplicationService {

    private final JobApplicationRepository repository;
    private final Clock clock;

    public JobApplicationService(JobApplicationRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public List<JobApplication> getAll(ApplicationStatus status, String keyword) {
        boolean hasKeyword = keyword != null && !keyword.isBlank();

        if(status != null && hasKeyword) {
            return repository.searchByStatusAndKeyword(status,keyword.trim());
        }

        if(status != null) {
            return repository.findByStatus(status);
        }

        if(hasKeyword) {
            return repository.findByCompanyNameContainingIgnoreCase(keyword.trim());
        }
        return repository.findAll();
    }

    public List<JobApplication> getByDateRange(LocalDate from, LocalDate to) {
        if(from.isAfter(to)) {
            return List.of();
        }
        return repository.findByAppliedDateBetweenOrderByAppliedDateDesc(from, to);
    }

    public List<StatusCount> getStatusSummary() {
        return repository.countApplicationsPerStatus();
    }

    public Optional<JobApplication> getById(Long id) {
        return repository.findById(id);
    }

    public JobApplication create(JobApplication application) {
        application.setId(null);
        if(application.getAppliedDate() == null) {
            application.setAppliedDate(LocalDate.now(clock));
        }
        if(application.getStatus() == null) {
            application.setStatus(ApplicationStatus.APPLIED);
        }
        return repository.save(application);
    }

    public Optional<JobApplication> update(Long id, JobApplication updated) {
        return repository.findById(id).map(existing -> {
            existing.setCompanyName(updated.getCompanyName());
            existing.setPosition(updated.getPosition());
            existing.setStatus(updated.getStatus());
            existing.setAppliedDate(updated.getAppliedDate());
            return repository.save(existing);
        });
    }

    public boolean delete(Long id) {
        if (!repository.existsById(id)) {
            return false;
        }
        repository.deleteById(id);
        return true;
    }
}
