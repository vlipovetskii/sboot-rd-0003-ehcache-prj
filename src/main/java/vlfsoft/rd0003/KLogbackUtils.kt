package vlfsoft.rd0003

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import kotlin.reflect.KClass

/**
 * Deriving the Class Name With Reflection
 */
val <T : Any> T.classLogger: Logger
// PRB: javaClass.classLogger -> recursion
// WO:
    get() = LoggerFactory.getLogger(javaClass)

val <T : Any> KClass<T>.classLogger: Logger
    get() = LoggerFactory.getLogger(java)

