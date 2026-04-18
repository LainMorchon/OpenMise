package com.morchon.lain.ui.login

import androidx.compose.foundation.content.MediaType.Companion.Text
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(
    viewModel: LoginViewModel,
    alNavegarAlHome: () -> Unit
) {
    // Escuchamos el estado del ViewModel de forma reactiva
    val estado by viewModel.estado.collectAsState()

    //Si el login fue exitoso, lanzamos la navegación
    if (estado.loginExitoso) {
        alNavegarAlHome()
    }

    //Pasamos el estado desglosado al componente visual (Stateless)ç
    ContenidoLogin(
        estado = estado,
        alEscribirEmail = {viewModel.alCambiarEmail(it)},
        alEscribirContrasena = {viewModel.alCambiarContrasena(it)},
        alPulsarLogin = {viewModel.hacerLogin()}
    )
}

@Composable
fun ContenidoLogin(
   estado: LoginState,
   alEscribirEmail: (String) -> Unit,
   alEscribirContrasena: (String) -> Unit,
   alPulsarLogin: () -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ){
        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = estado.email,
            onValueChange = alEscribirEmail,
            label = { Text("Email")},
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = estado.contrasena,
            onValueChange = alEscribirContrasena,
            label = { Text("Contraseña")},
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        //Mostrar error si lo hay
        if (estado.error != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = estado.error!!,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        //Mostrar botón o indicador de carga
        if(estado.estaCargando) {
            CircularProgressIndicator()
        } else {
            Button(
                onClick = alPulsarLogin,
                modifier = Modifier.fillMaxWidth()
            ){
                Text("Iniciar Sesión")
            }
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
            alPulsarLogin = {}
        )
    }
}