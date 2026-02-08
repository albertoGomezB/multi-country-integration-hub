package consumer.integration

import shared.model.IntegrationRequest

fun interface NotificationHandler {
  fun send(request: IntegrationRequest)
}
