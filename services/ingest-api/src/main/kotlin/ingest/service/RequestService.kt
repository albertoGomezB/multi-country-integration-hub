package ingest.service

import org.springframework.stereotype.Service
import shared.model.CreateRequest
import shared.model.IntegrationRequest
import shared.model.RequestStatus
import shared.repository.QueryRepository
import shared.repository.RequestRepository
import java.time.Instant
import java.util.UUID

@Service
class RequestService(
  private val requestRepository: RequestRepository,
  private val queryRepository: QueryRepository
) {

  /**
   * This method handles the creation of a new integration request with idempotency support,
   * saves it to the database, and returns the request ID into the queue for processing
   */
  fun create(tenant: String, country: String, body: CreateRequest): UUID {

    // 1. Build idempotency key
    val idempotencyKey = buildIdempotencyKey(tenant, country, body)

    // 2. Check if request with same idempotency key exists
    val existing = requestRepository.findByIdempotencyKey(
      tenant = tenant,
      country = country,
      idempotencyKey = idempotencyKey
    )

    // 3. If exists, return existing request ID
    if(existing != null) {
      return existing.id
    }

    val requestId = UUID.randomUUID()

    // 4. Create new Integration Request with status RECEIVED
    val request = IntegrationRequest(
      id = requestId,
      country = country,
      tenant = tenant,
      type = body.type,
      idempotencyKey = buildIdempotencyKey(tenant, country, body),
      status = RequestStatus.RECEIVED,
      createdAt = Instant.now(),
      updatedAt = Instant.now()
    )

    // 5. Save request and enqueue for processing and enqueue the request ID for processing
    requestRepository.save(request)
    queryRepository.enqueue(requestId)

    return requestId
  }

  /**
   * Builds an idempotency key based on tenant, country, request type and a hash of the payload.
   * This ensures that identical requests will have the same idempotency key, allowing us to prevent
   * duplicate processing of the same request. The hash of the payload is used to capture the content of the request
   *
   */
  private fun buildIdempotencyKey(
    tenant: String,
    country: String,
    body: CreateRequest
  ): String {
    return "$tenant|$country|${body.type}|${body.payload.hashCode()}"
  }
}
