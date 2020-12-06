package ktor.simple.rest.service.notifications

import org.slf4j.Logger
import org.slf4j.LoggerFactory

object NotificationService {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)

    fun sendNotification(target: String, message: String) {
        logger.info("""
            
            Hello $target,
            
            $message
            
        """.trimIndent())
    }

}