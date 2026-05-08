package com.morchon.lain.ui.perfil

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.morchon.lain.ui.core.components.OpenMiseTextField
import com.morchon.lain.ui.theme.MiseOrange
import openmise.composeapp.generated.resources.Res
import openmise.composeapp.generated.resources.*
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PerfilScreen(
    viewModel: PerfilViewModel = koinViewModel(),
    onNavigateBack: () -> Unit
) {
    val estado by viewModel.estado.collectAsState()

    LaunchedEffect(estado.exito) {
        if (estado.exito) {
            onNavigateBack()
            viewModel.resetExito()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Configurar Objetivos") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_chevron_left_square),
                            contentDescription = "Atrás",
                            modifier = Modifier.size(30.dp),
                            tint = MiseOrange
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.guardarCambios() }) {
                        Icon(
                            painter = painterResource(Res.drawable.ic_bookmark),
                            contentDescription = "Guardar",
                            modifier = Modifier.size(32.dp),
                            tint = MiseOrange
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Establece tus metas diarias para que OpenMise calcule tu progreso.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            OpenMiseTextField(
                value = estado.kcal,
                onValueChange = viewModel::onKcalChange,
                label = "Calorías Diarias (kcal)",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OpenMiseTextField(
                    value = estado.proteinas,
                    onValueChange = viewModel::onProteinasChange,
                    label = "Proteínas (g)",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OpenMiseTextField(
                    value = estado.carbohidratos,
                    onValueChange = viewModel::onCarbohidratosChange,
                    label = "Carbohidratos (g)",
                    modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }

            OpenMiseTextField(
                value = estado.grasas,
                onValueChange = viewModel::onGrasasChange,
                label = "Grasas (g)",
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = { viewModel.guardarCambios() },
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium
            ) {
                Text("Guardar Objetivos", fontWeight = FontWeight.Bold)
            }
        }
    }
}
