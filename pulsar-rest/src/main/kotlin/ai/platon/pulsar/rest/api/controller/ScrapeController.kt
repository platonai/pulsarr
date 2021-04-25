package ai.platon.pulsar.rest.api.controller

import ai.platon.pulsar.rest.api.entities.ScrapeRequest
import ai.platon.pulsar.rest.api.entities.ScrapeResponse
import ai.platon.pulsar.rest.api.entities.ScrapeStatusRequest
import ai.platon.pulsar.rest.api.entities.ScrapeTaskStatus
import ai.platon.pulsar.rest.api.service.ScrapeService
import org.springframework.context.ApplicationContext
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpServletRequest

@RestController
@CrossOrigin
@RequestMapping(
    "x/a",
    consumes = [MediaType.APPLICATION_JSON_VALUE],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
class ScrapeController(
    val applicationContext: ApplicationContext,
    val scrapeService: ScrapeService,
) {
    /**
     * @param request The extract request
     * @return The uuid of the task
     * */
    @PostMapping("q")
    fun execute(@RequestBody request: ScrapeRequest): ScrapeResponse {
        return scrapeService.executeQuery(request)
    }

    /**
     * @param request The extract request
     * @return The uuid of the task
     * */
    @PostMapping("q")
    fun submitJob(@RequestBody request: ScrapeRequest): String {
        return scrapeService.submitJob(request)
    }

    /**
     * @param uuid The uuid of the task last submitted
     * @param authToken The auth token
     * @return The execution result
     * */
    @GetMapping("status", consumes = [MediaType.ALL_VALUE])
    fun status(
        @RequestParam uuid: String,
        @RequestParam authToken: String,
        httpRequest: HttpServletRequest,
    ): ScrapeResponse {
        val request = ScrapeStatusRequest(authToken, uuid)
        return scrapeService.getStatus(request)
    }
}