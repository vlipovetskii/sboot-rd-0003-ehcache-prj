package vlfsoft.rd0003

import org.springframework.beans.factory.BeanFactory

interface KBeanFactoryA {

    /**
     * Typical usage:
     * : [KBeanFactoryA.Holder]
     * ...
     * @Autowired
     * override lateinit var beanFactory: BeanFactory
     */
    @Suppress("unused")
    interface Holder {
        val beanFactory: BeanFactory
    }

}

@KSpringDSL
inline fun <reified T> BeanFactory.beanOf(): T = getBean(T::class.java)
