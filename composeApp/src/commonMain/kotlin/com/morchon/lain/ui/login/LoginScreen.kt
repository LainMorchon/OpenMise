package com.morchon.lain.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.morchon.lain.domain.model.Usuario
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    alNavegarAlHome: () -> Unit,
    alNavegarAlRegistro: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    if (estado.loginExitoso) {
        alNavegarAlHome()
    }

    ContenidoLogin(
        estado = estado,
        alEscribirEmail = { viewModel.alCambiarEmail(it) },
        alEscribirContrasena = { viewModel.alCambiarContrasena(it) },
        alPulsarLogin = { viewModel.hacerLogin() },
        alSeleccionarUsuario = { viewModel.seleccionarUsuario(it) },
        alPulsarRegistrar = alNavegarAlRegistro
    )
}

@Composable
fun ContenidoLogin(
    estado: LoginState,
    alEscribirEmail: (String) -> Unit,
    alEscribirContrasena: (String) -> Unit,
    alPulsarLogin: () -> Unit,
    alSeleccionarUsuario: (Usuario) -> Unit,
    alPulsarRegistrar: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "OpenMise",
            style = MaterialTheme.typography.displaySmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(32.dp))

        // SECCIÓN DE USUARIOS GUARDADOS
        if (estado.usuariosRegistrados.isNotEmpty()) {
            Text(
                text = "Entrar como:",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(estado.usuariosRegistrados) { usuario ->
                    CardUsuario(usuario, onClick = { alSeleccionarUsuario(usuario) })
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }

        // FORMULARIO
        OutlinedTextField(
            value = estado.email,
            onValueChange = alEscribirEmail,
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = estado.contrasena,
            onValueChange = alEscribirContrasena,
            label = { Text("Contraseña") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            supportingText = { Text("Cualquier contraseña sirve (Demo)") }
        )

        if (estado.error != null) {
            Text(
                text = estado.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (estado.estaCargando) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = alPulsarLogin,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Iniciar Sesión")
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            TextButton(
                onClick = alPulsarRegistrar,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Crear nueva cuenta")
            }
        }
    }
}

@Composable
fun CardUsuario(usuario: Usuario, onClick: () -> Unit) {
    OutlinedCard(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.secondary
            )
            Text(
                text = usuario.nombre,
                style = MaterialTheme.typography.labelLarge,
                maxLines = 1
            )
        }
    }
}

//Preview Multiplataforma para ver los cambios sin compilar toda la app!
@Preview
@Composable
fun LoginScreenPreview() {
    MaterialTheme {
        ContenidoLogin(
            estado = LoginState(),
            alEscribirEmail = {},
            alEscribirContrasena = {},
            alPulsarLogin = {},
            alSeleccionarUsuario = {},
            alPulsarRegistrar = {}
        )
    }
}