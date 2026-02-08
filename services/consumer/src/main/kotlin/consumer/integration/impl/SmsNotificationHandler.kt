package consumer.integration.impl

import consumer.integration.NotificationHandler
import org.slf4j.LoggerFactory
import shared.model.IntegrationRequest

class SmsNotificationHandler : NotificationHandler {

  private val log = LoggerFactory.getLogger(javaClass)

  override fun send(request: IntegrationRequest) {
    log.info(
      "Sending SMS notification for tenant={} country={} requestId={}",
      request.tenant,
      request.country,
      request.id
    )
  }
}
