package ingest.adapters

import shared.repository.QueryRepository
import software.amazon.awssdk.services.sqs.SqsClient
import java.util.UUID

class SqsQueryRepository(
  private val sqsClient: SqsClient,
  private val queueUrl: String
): QueryRepository {
  /**
   * Enqueue a request ID to the SQS queue
   * @param requestId The request ID to enqueue
   */
  override fun enqueue(requestId: UUID) {
    sqsClient.sendMessage {b ->
      b.queueUrl(queueUrl)
      b.messageBody(requestId.toString())
    }
  }

  /**
   * Dequeue a batch of request IDs from the SQS queue
   * @param batchSize The number of request IDs to dequeue
   * @return A list of request IDs
   */
  override fun dequeue(batchSize: Int): List<UUID> {
   error("Not implemented in ingest-api (use consumer")
  }

}
