package ingest.adapters

import shared.model.IntegrationRequest
import shared.model.RequestStatus
import shared.repository.RequestRepository
import java.time.Instant
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class InMemoryRequestRepository : RequestRepository {

  private val requestsById = ConcurrentHashMap<UUID, IntegrationRequest>()

  override fun findById(id: String): IntegrationRequest? {
    val uuid: UUID = try {
      UUID.fromString(id)
    } catch (ex: IllegalArgumentException) {
      return null
    }
    return requestsById[uuid]
  }

  override fun findByIdempotencyKey(
    tenant: String,
    country: String,
    idempotencyKey: String
  ): IntegrationRequest? {
    return requestsById.values.firstOrNull { request ->
      request.tenant == tenant &&
        request.country == country &&
        request.idempotencyKey == idempotencyKey
    }
  }

  override fun save(request: IntegrationRequest): IntegrationRequest {
    requestsById[request.id] = request
    return request
  }

  override fun updateStatus(id: UUID, status: RequestStatus): IntegrationRequest? {
    val existing = requestsById[id] ?: return null
    val updated = existing.copy(status = status, updatedAt = Instant.now())
    requestsById[id] = updated
    return updated
  }

}
