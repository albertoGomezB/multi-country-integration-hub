package ingest.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import shared.repository.QueryRepository
import java.util.UUID

@RestController
@RequestMapping("/api/requests")
class RequestsController(
  private val queryRepository: QueryRepository
) {
  @PostMapping
  fun create(): ResponseEntity<Map<String, String>> {
    val requestId = UUID.randomUUID()
    queryRepository.enqueue(requestId)

    return ResponseEntity.status(HttpStatus.ACCEPTED)
      .body(mapOf("requestId" to requestId.toString()))
  }



}
