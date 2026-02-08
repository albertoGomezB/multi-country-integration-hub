package consumer.integration.resolver

import consumer.integration.NotificationHandler
import consumer.integration.impl.EmailNotificationHandler
import consumer.integration.impl.WhatsappNotificationHandler
import org.springframework.stereotype.Component
import shared.model.IntegrationRequest

@Component
class NotificationResolver {

  fun resolve(request: IntegrationRequest): NotificationHandler =
    when (request.country.uppercase()) {
      "ES", "FR", "IT", "GR" -> EmailNotificationHandler()
      "MX", "AR", "CO" -> WhatsappNotificationHandler()
      else -> throw IllegalArgumentException("Unsupported country: ${request.country}")
    }
}
