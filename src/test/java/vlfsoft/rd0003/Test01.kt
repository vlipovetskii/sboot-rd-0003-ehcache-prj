package vlfsoft.rd0003

import com.nhaarman.mockitokotlin2.spy
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.amshove.kluent.`should equal`
import org.amshove.kluent.any
import org.amshove.kluent.shouldNotBeNull
import org.junit.jupiter.api.*
import org.springframework.beans.factory.BeanFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.support.GenericApplicationContext
import org.springframework.context.support.beans
import org.springframework.test.context.TestPropertySource

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
@TestPropertySource(properties = [
    "spring.main.web-environment-type=none",
    "context.initializer.classes=vlfsoft.rd0003.Test01.AppBeansInitializer"
])
class Test01 : KBeanFactoryA.Holder {

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

        fun find(id: Int) : String?

        val findInvokeCount: Int

    }

    class DataQryApi : DataQryApiA {

        override var findInvokeCount = 0

        private val store = testStore

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

                bean{ spy(DataQryApi()) }

            }.initialize(context)
        }

    }

    @Order(1)
    @TestFactory
    fun testFactory() = kTestFactory {

        beanFactory.run {

            "Given listOf(${testIdList.joinToString()}) - when find without caching - then find is called times(${testIdList.size})" {

                val dataQryApi = beanOf<DataQryApiA>()

                testIdList.forEach { id ->

                    dataQryApi.find(id).also {
                        shouldNotBeNull()
                        log.info("find($id) -> '$it'")
                    }

                }
                dataQryApi.findInvokeCount `should equal` testIdList.size
                verify(dataQryApi, times(testIdList.size)).find(any())
            }

        }

    }

}