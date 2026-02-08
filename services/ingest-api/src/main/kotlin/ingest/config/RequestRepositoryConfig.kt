package ingest.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import shared.config.SsmParameterStore
import shared.repository.DynamoDbRequestRepository
import shared.repository.RequestRepository
import software.amazon.awssdk.services.dynamodb.DynamoDbClient

@Configuration
class RequestRepositoryConfig(
  private val ssm: SsmParameterStore,
  @Value("\${app.env:dev}") private val env: String
) {

  /**
   * Configure the RequestRepository to use DynamoDB for storing request details. The DynamoDB table name is retrieved from SSM Parameter Store.
   * This allows the application to store request details in DynamoDB, which can then be queried
   */
  @Bean
  fun requestRepository(dynamoDbClient: DynamoDbClient): RequestRepository {
    val tableName = ssm.get("/integration-hub/$env/ddb/requests/tableName")
    return DynamoDbRequestRepository(dynamoDbClient, tableName)
  }
}
