# ASPM Database Design

## Sprint 1 - Initial Database/API Design

This document describes the initial database structure for the ASPM platform.

## Core Entities

The initial entities identified from the ASPM system design are:

- User
- Application
- Scanner
- Scanning Client
- Scan
- Finding
- Report

## Entity Relationships

- A User can create and manage Applications.
- An Application can have multiple Scans.
- A Scanner can be used for multiple Scans.
- A Scanning Client can run multiple Scans.
- A Scan can produce multiple Findings.
- Reports are generated from security scan information.

## Initial API Structure

The initial API will support:

- User authentication and management
- Application management
- Scanner and scanning client management
- Starting and viewing scans
- Viewing security findings
- Generating and viewing reports

## Sprint 1 Note

This is the initial database and API design. The database implementation and connections can be expanded in later sprints.


## Initial Entity Fields

### User
- user_id
- name
- email
- password
- role

### Application
- application_id
- name
- repository_url
- owner
- status

### Scanner
- scanner_id
- name
- type
- status

### Scanning Client
- client_id
- name
- status

### Scan
- scan_id
- application_id
- scanner_id
- client_id
- status
- started_at
- completed_at

### Finding
- finding_id
- scan_id
- title
- severity
- status
- description

### Report
- report_id
- generated_by
- report_type
- format
- generated_at

- ## Example API Endpoints

- POST /api/login
- GET /api/applications
- POST /api/applications
- GET /api/scanners
- POST /api/scans
- GET /api/scans
- GET /api/findings
- GET /api/reports
- POST /api/reports
