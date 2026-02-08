# Multi-Country Integration Hub

A backend integration service designed for **multi-country / multi-tenant systems**, built with **Kotlin, Spring Boot and AWS**.

The goal of this project is to demonstrate **real-world backend patterns** commonly found in product companies:
- asynchronous processing
- country-based isolation
- tenant-based isolation
- secure configuration per tenant
- reliability over features

This is a **small but production-oriented demo**

---

## Problem Statement

In real backend systems, different countries or tenants often require:
- different external providers
- different credentials
- independent configuration
- safe handling of retries, duplicates and failures

This project simulates that scenario with a **country-based integration layer**.

---

## High-Level Architecture


The API acts only as an **entry point**.
All country-specific logic is handled asynchronously.

Client :

→ REST API (Spring Boot)
→ Async Queue
→ Worker
→ Country-specific configuration
→ External integration

---

## Key Concepts Demonstrated

- Country-based isolation (`X-Country` header)
- Tenant-based isolation  (`X-Tenant` header)
- Asynchronous processing
- Idempotent request handling
- Failure management and retries
- Infrastructure as Code

---

## Domain Example: Notification Integration Hub

In this implementation, the integration hub routes notification requests
to different providers based on country:

- Europe (ES, FR, IT, GR) → Email provider
- LATAM (MX, AR, CO) → WhatsApp provider
- North America (US, CA, UK) → SMS provider

The domain is intentionally simple, but the routing and execution model
matches real-world multi-provider integration platforms.

## Worker Responsibilities

The worker acts as the execution engine of the integration hub.

It is responsible for:
- consuming request IDs from the async queue
- loading request state from persistence
- enforcing idempotency guarantees
- transitioning request status (RECEIVED → PROCESSING → DONE / FAILED)
- resolving the appropriate integration provider based on country
- executing the integration logic
- applying a limited retry strategy on failures

Retry handling is intentionally implemented at the consumer level,
keeping domain logic clean and isolated.

## Project Structure

- **infra/** – AWS infrastructure (IaC)
- **services/**
  - **ingest-api** – Request ingestion (producer)
  - **consumer** – Async worker and integrations
- **docs/** – Architecture and design notes


---

## Tech Stack

- Kotlin
- Spring Boot
- AWS (API Gateway, SQS, Lambda, DynamoDB)
- Gradle (Kotlin DSL)

---

## Status
✅ Completed (v1)

This project implements a complete asynchronous integration hub:
- request ingestion
- idempotent processing
- country-based routing
- retry handling in the consumer

Further improvements (DLQ, backoff strategies, metrics) are intentionally left out to keep the core design focused and readable.

📘 For detailed setup and operational notes, see [Runbook](runbook.md)

