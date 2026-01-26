package shared.model

import java.time.Instant
import java.util.UUID

data class IntegrationRequest(
  val id: UUID,
  val country: String,
  val tenant: String,
  val type: String,
  val idempotencyKey: String,
  val status: RequestStatus,
  val createdAt: Instant,
  val updatedAt: Instant
)
