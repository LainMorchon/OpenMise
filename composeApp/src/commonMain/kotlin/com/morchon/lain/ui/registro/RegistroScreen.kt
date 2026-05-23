package com.morchon.lain.ui.registro

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.foundation.text.KeyboardOptions
import com.morchon.lain.ui.core.components.OpenMiseTextField
import com.morchon.lain.ui.theme.MiseOrange
import openmise.composeapp.generated.resources.Res
import openmise.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroScreen(
    onNavigateBack: () -> Unit,
    viewModel: RegistroViewModel = koinViewModel()
) {
    val estado by viewModel.estado.collectAsState()

    if (estado.registroExitoso) {
        LaunchedEffect(Unit) {
            onNavigateBack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Crear Cuenta") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_chevron_left_square),
                            contentDescription = "Atrás",
                            modifier = Modifier.size(30.dp),
                            tint = MiseOrange
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OpenMiseTextField(
                value = estado.nombre,
                onValueChange = { viewModel.alCambiarNombre(it) },
                label = "Nombre",
                modifier = Modifier.fillMaxWidth()
            )

            OpenMiseTextField(
                value = estado.email,
                onValueChange = { viewModel.alCambiarEmail(it) },
                label = "Email",
                modifier = Modifier.fillMaxWidth(),
                isError = estado.errorEmail != null,
                supportingText = estado.errorEmail?.let { { Text(it) } }
            )

            OpenMiseTextField(
                value = estado.contrasena,
                onValueChange = { viewModel.alCambiarContrasena(it) },
                label = "Contraseña",
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                isError = estado.errorContrasena != null,
                supportingText = estado.errorContrasena?.let { { Text(it) } }
            )

            if (estado.error != null) {
                Text(
                    text = estado.error!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (estado.estaCargando) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.registrarUsuario() },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Registrarse")
                }
            }
        }
    }
}