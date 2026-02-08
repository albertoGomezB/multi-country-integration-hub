package consumer.worker

import consumer.adapters.SqsQueryRepository
import consumer.service.RequestProcessor
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class RequestWorker(
  private val queueRepository: SqsQueryRepository,
  private val processor: RequestProcessor
) {
  private val log = LoggerFactory.getLogger(javaClass)

  @Scheduled(fixedDelay = 3000)
  fun pollQueue() {

    val requestIds = queueRepository.dequeue(batchSize = 5)
    if(requestIds.isEmpty()) return

    for (requestId in requestIds) {
      try {
        processor.process(requestId)
        log.info("Request processed successfully: {}", requestId)
      } catch (ex: Exception) {
        // In this simplified version, failures are only logged.
        // Retry / DLQ handling will be introduced later.
        log.error("Error processing request {}", requestId, ex)
      }
    }
  }
}

