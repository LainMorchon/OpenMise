package com.morchon.lain.ui.login

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.morchon.lain.domain.model.Usuario
import com.morchon.lain.ui.core.components.OpenMiseLogo
import com.morchon.lain.ui.core.components.OpenMiseTextField
import com.morchon.lain.ui.theme.OpenMiseTheme
import androidx.compose.foundation.BorderStroke
import com.morchon.lain.ui.theme.OpenGreen
import com.morchon.lain.ui.theme.MiseOrange
import openmise.composeapp.generated.resources.Res
import openmise.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
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
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            // LOGO DE LA APLICACIÓN
            Icon(
                painter = painterResource(Res.drawable.ic_logo_app),
                contentDescription = "Logo OpenMise",
                modifier = Modifier.size(120.dp),
                tint = Color.Unspecified
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            OpenMiseLogo(
                style = MaterialTheme.typography.displayMedium
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
                        CardUsuario(
                            usuario = usuario, 
                            onClick = { alSeleccionarUsuario(usuario) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(32.dp))
            }

            // FORMULARIO
            OpenMiseTextField(
                value = estado.email,
                onValueChange = alEscribirEmail,
                label = "Email",
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OpenMiseTextField(
                value = estado.contrasena,
                onValueChange = alEscribirContrasena,
                label = "Contraseña",
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White
                    )
                ) {
                    Text(
                        text = "Iniciar Sesión",
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextButton(
                    onClick = alPulsarRegistrar,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Crear nueva cuenta")
                }
            }
        }
    }
}

@Composable
fun CardUsuario(
    usuario: Usuario, 
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = OpenGreen.copy(alpha = 0.1f)
        ),
        border = BorderStroke(2.dp, OpenGreen)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(Res.drawable.ic_chef),
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = usuario.nombre,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    OpenMiseTheme {
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
