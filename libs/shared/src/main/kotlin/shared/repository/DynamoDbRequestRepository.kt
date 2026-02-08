package shared.repository

import shared.model.IntegrationRequest
import shared.model.RequestStatus
import software.amazon.awssdk.services.dynamodb.DynamoDbClient
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
import java.time.Instant
import java.util.UUID


class DynamoDbRequestRepository(
  private val dynamoDbClient: DynamoDbClient,
  private val tableName: String
) : RequestRepository {

  override fun findById(id: String): IntegrationRequest? {
    val item = dynamoDbClient.getItem { b ->
      b.tableName(tableName)
      b.key(
        mapOf(
          "pk" to AttributeValue.fromS("REQ#$id"),
          "sk" to AttributeValue.fromS("META")
        )
      )
    }.item()

    if (item == null || item.isEmpty()) return null
    return item.toIntegrationRequest()
  }

  override fun findByIdempotencyKey(
    tenant: String,
    country: String,
    idempotencyKey: String
  ): IntegrationRequest? {
    val items = dynamoDbClient.scan { b ->
      b.tableName(tableName)
      b.filterExpression("tenant = :tenant AND country = :country AND idempotencyKey = :idem")
      b.expressionAttributeValues(
        mapOf(
          ":tenant" to AttributeValue.fromS(tenant),
          ":country" to AttributeValue.fromS(country),
          ":idem" to AttributeValue.fromS(idempotencyKey)
        )
      )
      b.limit(1)
    }.items()

    return items.firstOrNull()?.toIntegrationRequest()
  }

  override fun save(request: IntegrationRequest): IntegrationRequest {
    dynamoDbClient.putItem { b ->
      b.tableName(tableName)
      b.item(request.toItem())
    }
    return request
  }

  override fun updateStatus(id: UUID, status: RequestStatus): IntegrationRequest? {
    val existing = findById(id.toString()) ?: return null
    val updated = existing.copy(status = status, updatedAt = Instant.now())
    save(updated)
    return updated
  }

  private fun IntegrationRequest.toItem(): Map<String, AttributeValue> = mapOf(
    "pk" to AttributeValue.fromS("REQ#$id"),
    "sk" to AttributeValue.fromS("META"),
    "id" to AttributeValue.fromS(id.toString()),
    "country" to AttributeValue.fromS(country),
    "tenant" to AttributeValue.fromS(tenant),
    "type" to AttributeValue.fromS(type),
    "idempotencyKey" to AttributeValue.fromS(idempotencyKey),
    "status" to AttributeValue.fromS(status.name),
    "createdAt" to AttributeValue.fromS(createdAt.toString()),
    "updatedAt" to AttributeValue.fromS(updatedAt.toString())
  )

  private fun Map<String, AttributeValue>.toIntegrationRequest(): IntegrationRequest = IntegrationRequest(
    id = UUID.fromString(getValue("id").s()),
    country = getValue("country").s(),
    tenant = getValue("tenant").s(),
    type = getValue("type").s(),
    idempotencyKey = getValue("idempotencyKey").s(),
    status = RequestStatus.valueOf(getValue("status").s()),
    createdAt = Instant.parse(getValue("createdAt").s()),
    updatedAt = Instant.parse(getValue("updatedAt").s())
  )
}
