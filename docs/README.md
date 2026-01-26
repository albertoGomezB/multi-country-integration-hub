# Multi-Country Integration Hub

A backend integration service designed for **multi-country / multi-tenant systems**, built with **Kotlin, Spring Boot and AWS**.

The goal of this project is to demonstrate **real-world backend patterns** commonly found in product companies:
- asynchronous processing
- country-based isolation
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

Client

→ REST API (Spring Boot)
→ Async Queue
→ Worker
→ Country-specific configuration
→ External integration

---

## Key Concepts Demonstrated

- Country-based isolation (`X-Country` header)
- Asynchronous processing
- Idempotent request handling
- Failure management and retries
- Infrastructure as Code

---

## Project Structure

- **infra/** – AWS infrastructure (IaC)
- **services/** – Backend services (Spring Boot)
- **docs/** – Architecture and design notes


---

## Tech Stack

- Kotlin
- Spring Boot
- AWS (API Gateway, SQS, Lambda, DynamoDB)
- Gradle (Kotlin DSL)

---

## Status

🚧 Work in progress
This project is built incrementally, commit by commit.

