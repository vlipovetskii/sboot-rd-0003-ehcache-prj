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
    "context.initializer.classes=vlfsoft.rd0003.Test02.AppBeansInitializer"
])
class Test02 : KBeanFactoryA.Holder {

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

        fun find(id: Int): String?

        val findInvokeCount: Int

    }

    class DataQryApi : DataQryApiA {

        override var findInvokeCount = 0

        private val store = testStore

        @Cacheable(cacheNames = ["DataQryApiA2"]) //, key = "#id")
        override fun find(id: Int) = store[id].also {
            log.info("[${++findInvokeCount}] $this: find($id) -> '$it'")
        }
    }

    @Autowired
    override lateinit var beanFactory: BeanFactory

    @Suppress("unused")
    open class AppBeansInitializer : ApplicationContextInitializer<GenericApplicationContext> {
        override fun initialize(context: GenericApplicationContext) {
            beans {

                // bean{ spy(DataQryApi()) }
                bean { DataQryApi() }

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
                /**
                 * verify(dataQryApi, times(testIdList.distinct().size)).find(any())
                 *
                 * PRB: org.mockito.exceptions.misusing.NotAMockException: Argument passed to verify() is of type $Proxy76 and is not a mock!
                 *
                 * https://docs.spring.io/spring/docs/4.3.x/spring-framework-reference/html/cache.html
                 * The default advice mode for processing caching annotations is "proxy" which allows for interception of calls through the proxy only
                 *
                 * Delegation pattern can be leveraged as a workaround.
                 *
                 * How to Mock, Spy, and Fake Spring Beans https://dzone.com/articles/how-to-mock-spring-bean-version-2
                 * Spy on a Spring Bean Proxied by Spring AOP
                 * ...
                 * @Getter    private AddressService spyDelegate;
                 * ...
                 * spyDelegate = Mockito.spy(new AddressService(addressDao));
                 * ...
                 *
                 */
            }

        }

    }

}