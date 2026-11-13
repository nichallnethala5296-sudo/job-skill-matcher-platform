# Job Skill Matcher Platform with AI-Based Skill Gap Analysis

Built with Java 21, Spring Boot 3.3.4, and MySQL 8.x.

## Quick Start (Dev Profile with in-memory H2)
```bash
mvn clean spring-boot:run -Dspring-boot.run.profiles=dev
```
Open: `http://localhost:8080`

## Run with MySQL
1. Create schema:
   ```sql
   CREATE DATABASE job_matcher_db;
   ```
2. Start application:
   ```bash
   mvn clean spring-boot:run -Dspring-boot.run.profiles=mysql
   ```

## Pre-seeded Credentials
- **Candidate:** `alex.candidate@gmail.com` / `Password123!`
- **Recruiter:** `recruiter@techcorp.com` / `Password123!`
- **Admin:** `admin@jobmatcher.com` / `Password123!`