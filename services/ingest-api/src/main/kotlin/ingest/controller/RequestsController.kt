package ingest.controller

import ingest.service.RequestService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import shared.model.CreateRequest

@RestController
@RequestMapping("/api/requests")
class RequestsController(
  private val requestService: RequestService
) {
  @PostMapping
  fun create(
    @RequestHeader("X-Tenant") tenant: String,
    @RequestHeader("X-Country") country: String,
    @RequestBody body: CreateRequest
  ): ResponseEntity<Map<String, String>> {

    val requestId = requestService.create(
      tenant = tenant,
      country = country,
      body = body)

    return ResponseEntity.status(HttpStatus.ACCEPTED)
      .body(mapOf("requestId" to requestId.toString()))
  }


}
