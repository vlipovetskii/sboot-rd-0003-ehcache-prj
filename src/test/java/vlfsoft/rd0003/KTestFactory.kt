package vlfsoft.rd0003

import org.junit.jupiter.api.DynamicTest
import java.util.*

@KTestDSL
class KTestFactoryBuilder {

    private val mutableList = LinkedList<DynamicTest>()
    val list get() = mutableList.toList()

    operator fun String.invoke(testBlock: (displayName: String) -> Unit) {
        mutableList += DynamicTest.dynamicTest(this) { testBlock(this) }
    }

}

@KTestDSL
fun kTestFactory(block: KTestFactoryBuilder.() -> Unit): List<DynamicTest> = KTestFactoryBuilder().apply(block).list
