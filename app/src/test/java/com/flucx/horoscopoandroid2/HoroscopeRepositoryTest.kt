package com.flucx.horoscopoandroid2

import com.flucx.horoscopoandroid2.data.HoroscopeRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test

class HoroscopeRepositoryTest {
    @Test
    fun repositoryContainsAllZodiacSigns() {
        assertEquals(12, HoroscopeRepository.getAll().size)
    }

    @Test
    fun repositoryFindsSignById() {
        assertNotNull(HoroscopeRepository.getById("aries"))
    }
}
