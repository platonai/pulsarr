package ai.platon.pulsar.common.ai.api

open class ModelResponse(
    val content: String,
    val state: ResponseState,
    val tokenUsage: TokenUsage,
)