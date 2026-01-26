package shared.repository

import java.util.UUID

interface QueryRepository {
  fun enqueue(requestId: UUID)
  fun dequeue(batchSize: Int = 10): List<UUID>
}
