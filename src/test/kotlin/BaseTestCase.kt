package robo

import org.junit.Before
import org.mockito.MockitoAnnotations

open class BaseTestCase {

    @Before
    fun setUp() {
        MockitoAnnotations.initMocks(this)
    }

}