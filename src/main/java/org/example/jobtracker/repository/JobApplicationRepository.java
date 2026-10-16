package org.example.jobtracker.repository;

import org.example.jobtracker.model.ApplicationStatus;
import org.example.jobtracker.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.xml.validation.Validator;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication,Long> {

    List<JobApplication> findByStatus(ApplicationStatus status);
    List<JobApplication> findByCompanyNameContainingIgnoreCase(String keyword);
    List<JobApplication> findByAppliedDateBetweenOrderByAppliedDateDesc(LocalDate from, LocalDate to);

    long countByStatus(ApplicationStatus status);

    @Query("SELECT a FROM JobApplication a WHERE a.status = :status AND LOWER(a.companyName) LIKE LOWER (CONCAT('%',:keyword,'%'))")
    List<JobApplication> searchByStatusAndKeyword(@Param("status") ApplicationStatus status, @Param("keyword") String keyword);

    @Query(value = "SELECT status AS status, COUNT(*) AS total FROM job_application GROUP BY status", nativeQuery = true)
    List<StatusCount> countApplicationsPerStatus();
}
