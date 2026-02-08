package consumer.adapters

import shared.repository.QueryRepository
import software.amazon.awssdk.services.sqs.SqsClient
import java.util.UUID

class SqsQueryRepository(
  private val sqsClient: SqsClient,
  private val queueUrl: String
) : QueryRepository {

  override fun enqueue(requestId: UUID) {
    error("Not supported in consumer (produces is ingest-api")
  }

  override fun dequeue(batchSize: Int): List<UUID> {

    // Receive a batch of messages from SQS (long polling enabled)
    val response = sqsClient.receiveMessage { b ->
      b.queueUrl(queueUrl)
      b.maxNumberOfMessages(batchSize)
      b.waitTimeSeconds(10)
    }
    val messages = response.messages()
    val result = mutableListOf<UUID>()
    if(messages.isEmpty()) return emptyList()

    // extract the requestId from the message body and delete the message from the queue
    for (message in messages) {
      val requestId = UUID.fromString(message.body())
      result.add(requestId)

      // Delete the message immediately after reading it
      sqsClient.deleteMessage { d ->
        d.queueUrl(queueUrl)
        d.receiptHandle(message.receiptHandle())
      }
    }
    return result
  }

}
