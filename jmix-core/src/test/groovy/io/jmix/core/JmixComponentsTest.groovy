package io.jmix.core

import com.sample.addon1.TestAddon1Configuration
import com.sample.app.TestAppConfiguration
import com.sample.app.TestBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.test.context.ContextConfiguration
import spock.lang.Specification

@ContextConfiguration(classes = [TestAppConfiguration, TestAddon1Configuration, JmixCoreConfiguration])
class JmixComponentsTest extends Specification {

    @Autowired
    private JmixComponents components

    @Autowired
    private TestBean testBean

    def "test dependencies"() {
        expect:

        components != null
        components.components.size() == 3
        components.components[0].id == 'io.jmix.core'

        def jmixCore = components.get('io.jmix.core')
        def addon1 = components.get('com.sample.addon1')
        def app = components.get('com.sample.app')

        addon1.dependsOn(jmixCore)
        app.dependsOn(addon1)
        app.dependsOn(jmixCore)
    }

    def "configuration properties of components"() {
        expect:

        def jmixCore = components.get('io.jmix.core')
        def addon1 = components.get('com.sample.addon1')
        def app = components.get('com.sample.app')

        jmixCore.getProperty('jmix.metadataConfig') == 'io/jmix/core/metadata.xml'
        addon1.getProperty('jmix.metadataConfig') == 'com/sample/addon1/metadata.xml'
        app.getProperty('jmix.metadataConfig') == 'com/sample/app/metadata.xml'

    }

    def "resulting configuration properties"() {
        expect:

        components.getProperty('jmix.metadataConfig') == 'io/jmix/core/metadata.xml com/sample/addon1/metadata.xml com/sample/app/metadata.xml'
        components.getProperty('prop1') == 'addon1_prop1 app_prop1'
        components.getProperty('prop2') == 'app_prop2'
        components.getProperty('prop3') == 'app_prop3'
    }

    def "using configuration properties"() {
        expect:

        testBean.prop1 == 'addon1_prop1 app_prop1'
    }
}
