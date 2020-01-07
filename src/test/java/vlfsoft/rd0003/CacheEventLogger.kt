package vlfsoft.rd0003

import org.ehcache.event.CacheEvent
import org.ehcache.event.CacheEventListener

class CacheEventLogger : CacheEventListener<Any, Any> {

    companion object {
        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        val log by lazy { classLogger }
    }

    override fun onEvent(event: CacheEvent<out Any, out Any>) {
        log.info("${event.type}, ${event.key}, ${event.oldValue}, ${event.newValue}")
    }
}