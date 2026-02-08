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

  fun create(tenant: String, country: String, body: CreateRequest): UUID {

    val idempotencyKey = buildIdempotencyKey(tenant, country, body)

    val existing = requestRepository.findByIdempotencyKey(
      tenant = tenant,
      country = country,
      idempotencyKey = idempotencyKey
    )

    if(existing != null) {
      return existing.id
    }

    val requestId = UUID.randomUUID()

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

    requestRepository.save(request)
    queryRepository.enqueue(requestId)

    return requestId
  }

  private fun buildIdempotencyKey(
    tenant: String,
    country: String,
    body: CreateRequest
  ): String {
    return "$tenant|$country|${body.type}|${body.payload.hashCode()}"
  }
}
