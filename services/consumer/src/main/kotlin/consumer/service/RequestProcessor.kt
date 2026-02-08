package consumer.service

import consumer.integration.resolver.NotificationResolver
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import shared.model.RequestStatus
import shared.repository.RequestRepository
import java.util.UUID

@Component
class RequestProcessor(
  private val repository: RequestRepository,
  private val notificationResolver: NotificationResolver
) {

  private val log = LoggerFactory.getLogger(javaClass)

  fun process(requestId: UUID) {

    // 1. Load IntegrationRequest
    val request = repository.findById(requestId.toString())
    if (request == null) {
      log.error("Request {} not found", requestId)
      throw IllegalStateException("Request not found")
    }

    // 2. Validate current status (idempotency)
    if (request.status != RequestStatus.RECEIVED) {
      log.info(
        "Request {} already processed or in progress (status={})",
        requestId,
        request.status
      )
      return
    }

    // 3. Transition to PROCESSING
    repository.updateStatus(requestId, RequestStatus.PROCESSING)
    log.info("Request {} moved to PROCESSING", requestId)

    try {
      // // 4. Resolve and execute integration
      val handler = notificationResolver.resolve(request)
      handler.send(request)

      // 5. Transition to DONE
      repository.updateStatus(requestId, RequestStatus.DONE)
      log.info("Request {} completed successfully", requestId)

    } catch (ex: Exception) {
      // 6. Transition to FAILED
      repository.updateStatus(requestId, RequestStatus.FAILED)
      log.error("Request {} failed", requestId, ex)
      throw ex
    }
  }
}
