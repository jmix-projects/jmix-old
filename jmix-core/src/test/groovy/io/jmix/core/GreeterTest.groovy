package io.jmix.core

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = JmixCoreConfiguration)
class GreeterTest extends Specification {

    @Autowired
    private Greeter greeter

    def "test"() {
        when:

        println greeter.sayHello("you")

        then:

        noExceptionThrown()
    }
}
