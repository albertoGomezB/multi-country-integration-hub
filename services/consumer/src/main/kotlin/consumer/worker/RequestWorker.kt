package consumer.worker

import consumer.service.RequestProcessor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import shared.repository.QueryRepository
import software.amazon.awssdk.services.sqs.SqsClient

@Component
class RequestWorker(
  private val queueRepository: QueryRepository,
  private val processor: RequestProcessor,
  private val sqsClient: SqsClient,
  @Value("\${worker.queueUrl}") private val queueUrl: String
) {
  private val log = LoggerFactory.getLogger(javaClass)
  private val maxRetries = 3
  private val retryCounter = mutableMapOf<String, Int>()

  /**
   * Poll the SQS queue for new request IDs every 3 seconds and process them.
   */
  @Scheduled(fixedDelay = 3000)
  fun pollQueue() {

    val requestIds = queueRepository.dequeue(batchSize = 5)
    if(requestIds.isEmpty()) return

    for (requestId in requestIds) {
      val key = requestId.toString()
      try {
        processor.process(requestId)
        retryCounter.remove(key)
        log.info("Request processed successfully: {}", requestId)

      } catch (ex: Exception) {
        val retries = retryCounter.getOrDefault(key, 0) + 1
        retryCounter[key] = retries

        if (retries >= maxRetries) {
          log.error(
            "Request {} failed after {} retries. Marked as FAILED.",
            requestId,
            retries,
            ex
          )
          retryCounter.remove(key)
        } else {
          log.warn(
            "Retry {}/{} for request {}",
            retries,
            maxRetries,
            requestId,
            ex
          )
        }
      }
    }
  }
}

