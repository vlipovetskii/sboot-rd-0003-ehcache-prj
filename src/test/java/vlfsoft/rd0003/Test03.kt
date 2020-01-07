package vlfsoft.rd0003

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.`should equal`
import org.amshove.kluent.any
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.*
import org.mockito.AdditionalAnswers.delegatesTo
import org.mockito.Mockito
import org.mockito.Mockito.mock
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.annotation.Cacheable
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestPropertySource(properties = [
    "spring.main.web-environment-type=none",
    "context.initializer.classes=vlfsoft.rd0003.Test03.AppBeansInitializer"
])
class Test03 : KBeanFactoryA.Holder {

    companion object {

        @Suppress("JAVA_CLASS_ON_COMPANION")
        @JvmStatic
        val log by lazy { classLogger }

        @JvmStatic
        @Suppress("unused")
        @BeforeAll
        fun beforeAllTests() {
        }

    }

    interface DataQryApiA {

        @Cacheable(cacheNames = ["DataQryApiA3"]) //, key = "#id")
        fun find(id: Int): String?

        val findInvokeCount: Int

        interface NotCacheable : DataQryApiA {

            @Suppress("unused")
            interface Holder {
                val notCacheable: NotCacheable
            }

        }

    }

    /**
     * Pros: Kotlin Implementation by Delegation https://kotlinlang.org/docs/reference/delegation.html#implementation-by-delegation
     * can be leveraged to avoid boilerplate delegation code
     * Cons: [Cacheable] is on interface method.
     * Cons: It's impossible to switch between cacheable and not cacheable implementations easily.
     */
    class DataQryApi(override val notCacheable: DataQryApiA.NotCacheable) :
            DataQryApiA by notCacheable,
            DataQryApiA.NotCacheable.Holder {


        class NotCacheable : DataQryApiA.NotCacheable {

            class Holder(override val notCacheable: DataQryApiA.NotCacheable) : DataQryApiA.NotCacheable.Holder

            override var findInvokeCount = 0

            private val store = testStore

            override fun find(id: Int) = store[id].also {
                log.info("[${++findInvokeCount}] $this: find($id) -> '$it'")
            }

        }

    }

    @Autowired
    override lateinit var beanFactory: BeanFactory

    @Suppress("unused")
    open class AppBeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
        override fun initialize(context: GenericApplicationContext) {
            beans {

                bean { DataQryApi.NotCacheable.Holder(spy(DataQryApi.NotCacheable())) }
                bean(isPrimary = true) { DataQryApi(ref<DataQryApi.NotCacheable.Holder>().notCacheable) }

            }.initialize(context)
        }

    }

    @Order(1)
    @TestFactory
    fun testFactory() = kTestFactory {

        beanFactory.run {

            "Given listOf(${testIdList.joinToString()}) - when find with caching - then find is called times(${testIdList.distinct().size})" {

                val dataQryApi = beanOf<DataQryApiA>()

                testIdList.forEach { id ->


                    dataQryApi.find(id).also {
                        shouldNotBeNull()
                        log.info("find($id) -> '$it'")
                    }

                }
                dataQryApi.findInvokeCount `should equal` testIdList.distinct().size

                val notCacheableSpy = beanOf<DataQryApi.NotCacheable.Holder>().notCacheable
                verify(notCacheableSpy, times(testIdList.distinct().size)).find(any())
            }

        }

    }

}