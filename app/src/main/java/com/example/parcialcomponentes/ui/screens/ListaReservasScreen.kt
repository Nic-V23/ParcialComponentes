package com.golf.reservas.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.golf.reservas.data.model.Reserva
import com.golf.reservas.viewmodel.ReservaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaReservasScreen(
    viewModel: ReservaViewModel,
    onEditar: (Reserva) -> Unit,
    onNuevaReserva: () -> Unit
) {
    val reservas by viewModel.reservas.collectAsState()
    val query by viewModel.query.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val mensaje by viewModel.mensaje.collectAsState(initial = "")
    var reservaAEliminar by remember { mutableStateOf<Reserva?>(null) }

    LaunchedEffect(mensaje) {
        if (mensaje.isNotEmpty()) snackbarHostState.showSnackbar(mensaje)
    }

    reservaAEliminar?.let { r ->
        AlertDialog(
            onDismissRequest = { reservaAEliminar = null },
            title = { Text("Eliminar reserva", fontWeight = FontWeight.Bold) },
            text = { Text("¿Seguro que deseas eliminar la reserva de ${r.nombreCliente}?") },
            confirmButton = {
                Button(onClick = { viewModel.eliminar(r); reservaAEliminar = null },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                    shape = RoundedCornerShape(8.dp)) { Text("Eliminar") }
            },
            dismissButton = {
                OutlinedButton(onClick = { reservaAEliminar = null },
                    shape = RoundedCornerShape(8.dp)) { Text("Cancelar") }
            }
        )
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Reservas de Golf", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E),
                    titleContentColor = Color.White
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNuevaReserva,
                containerColor = Color(0xFF1A237E),
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nueva reserva")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .background(Color(0xFFF5F7FA))
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = viewModel::onQueryChange,
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                placeholder = { Text("Buscar por nombre...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White
                )
            )

            if (reservas.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(if (query.isBlank()) "No hay reservas registradas" else "Sin resultados para \"$query\"",
                            fontSize = 16.sp, color = Color(0xFF9E9E9E), fontWeight = FontWeight.SemiBold)
                        if (query.isBlank())
                            Text("Toca + para agregar una nueva", fontSize = 13.sp, color = Color(0xFFBDBDBD))
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(reservas, key = { it.id }) { reserva ->
                        ReservaCard(reserva = reserva,
                            onEditar = { onEditar(reserva) },
                            onEliminar = { reservaAEliminar = reserva })
                    }
                }
            }
        }
    }
}

@Composable
fun ReservaCard(reserva: Reserva, onEditar: () -> Unit, onEliminar: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically) {
                Text(reserva.nombreCliente, fontSize = 17.sp, fontWeight = FontWeight.Bold,
                    color = Color(0xFF212121), modifier = Modifier.weight(1f))
                Surface(shape = RoundedCornerShape(20.dp),
                    color = if (reserva.estado == "Activa") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)) {
                    Text(reserva.estado,
                        color = if (reserva.estado == "Activa") Color(0xFF2E7D32) else Color(0xFFC62828),
                        fontSize = 11.sp, fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF0F0F0))
            Spacer(modifier = Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFF5F7FA)) {
                    Text("Cancha ${reserva.cancha}", fontSize = 12.sp, color = Color(0xFF616161),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFF5F7FA)) {
                    Text(reserva.fecha, fontSize = 12.sp, color = Color(0xFF616161),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
                Surface(shape = RoundedCornerShape(6.dp), color = Color(0xFFF5F7FA)) {
                    Text(reserva.hora, fontSize = 12.sp, color = Color(0xFF616161),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = onEditar, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)) { Text("Editar") }
                Button(onClick = onEliminar, modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828))) {
                    Text("Eliminar") }
            }
        }
    }
}