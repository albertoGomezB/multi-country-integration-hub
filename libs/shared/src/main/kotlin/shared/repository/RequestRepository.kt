package shared.repository

import shared.model.IntegrationRequest
import shared.model.RequestStatus
import java.util.UUID

interface RequestRepository {

  fun findById(id: String): IntegrationRequest?

  fun findByIdempotencyKey(
    tenant: String,
    country: String,
    idempotencyKey: String
  ): IntegrationRequest?

  fun save(request: IntegrationRequest): IntegrationRequest

  fun updateStatus(
    id: UUID, status: RequestStatus): IntegrationRequest?
}

