package com.golf.reservas.data.repository

import io.mockk.MockKAnnotations
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.just
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class ReservaRepositoryTest {

    @MockK
    private lateinit var dao: ReservaDao
    private lateinit var repo: ReservaRepository

    private val reservaEjemplo = Reserva(
        id = 1,
        nombreCliente = "Carlos Pérez",
        cancha = 3,
        fecha = "2026-03-20",
        hora = "10:00",
        estado = "Activa"
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repo = ReservaRepository(dao)
    }

    @Test
    fun `guardar reserva sin conflicto devuelve success`() = runTest {
        coEvery { dao.buscarConflicto(any(), any(), any()) } returns null
        coEvery { dao.insertar(any()) } just Runs

        val resultado = repo.guardarReserva(reservaEjemplo)

        assertTrue(resultado.isSuccess)
        coVerify(exactly = 1) { dao.insertar(reservaEjemplo) }
    }

    @Test
    fun `guardar reserva con conflicto devuelve failure`() = runTest {
        coEvery { dao.buscarConflicto(3, "2026-03-20", "10:00") } returns reservaEjemplo

        val resultado = repo.guardarReserva(
            reservaEjemplo.copy(id = 0, nombreCliente = "Otro Cliente")
        )

        assertTrue(resultado.isFailure)
        coVerify(exactly = 0) { dao.insertar(any()) }
    }

    @Test
    fun `actualizar reserva con el mismo id no es conflicto`() = runTest {
        coEvery { dao.buscarConflicto(3, "2026-03-20", "10:00") } returns reservaEjemplo
        coEvery { dao.actualizar(any()) } just Runs

        val resultado = repo.actualizarReserva(reservaEjemplo)

        assertTrue(resultado.isSuccess)
        coVerify(exactly = 1) { dao.actualizar(reservaEjemplo) }
    }

    @Test
    fun `actualizar reserva con conflicto de otro id devuelve failure`() = runTest {
        val otraReserva = reservaEjemplo.copy(id = 99)
        coEvery { dao.buscarConflicto(3, "2026-03-20", "10:00") } returns otraReserva

        val resultado = repo.actualizarReserva(reservaEjemplo)

        assertTrue(resultado.isFailure)
    }

    @Test
    fun `eliminar reserva llama al dao correctamente`() = runTest {
        coEvery { dao.eliminar(any()) } just Runs

        repo.eliminarReserva(reservaEjemplo)

        coVerify(exactly = 1) { dao.eliminar(reservaEjemplo) }
    }

    @Test
    fun `buscar por nombre devuelve flow con resultados`() = runTest {
        every { dao.buscarPorNombre("Carlos") } returns flowOf(listOf(reservaEjemplo))

        repo.buscarPorNombre("Carlos").collect { resultado ->
            assertEquals(1, resultado.size)
            assertEquals("Carlos Pérez", resultado[0].nombreCliente)
        }
    }
}