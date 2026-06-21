package com.example.taskmaster.pages

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.taskmaster.model.enuns.Prioridade
import com.example.taskmaster.model.enuns.StatusTarefa
import com.example.taskmaster.util.copiarImagemParaStorage
import com.example.taskmaster.viewmodel.TarefaViewModel
import java.io.File
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaDetalheTarefaScreen(
    viewModel: TarefaViewModel,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current
    val tarefa = viewModel.tarefaSelecionada
    val isEdicao = viewModel.modoEdicao
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    var datePickerAlvo by remember { mutableStateOf<String?>(null) }
    var mostrarDialogoDeletar by remember { mutableStateOf(false) }
    var imagemSelecionada by remember {
        mutableStateOf<String?>(null)
    }
    val imagemLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        if (tarefa != null && uris.isNotEmpty()) {
            val paths = uris.map { uri -> copiarImagemParaStorage(context, uri) }
            viewModel.adicionarImagensNaTarefa(tarefa.id, paths)
        }
    }

    if (tarefa == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Nenhuma tarefa encontrada.")
        }
        return
    }

    if (mostrarDialogoDeletar) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoDeletar = false },
            title = { Text("Excluir tarefa") },
            text = { Text("Deseja excluir \"${tarefa.titulo}\"? Essa ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletarTarefa(tarefa.id)
                        mostrarDialogoDeletar = false
                        onVoltar()
                    }
                ) {
                    Text("Excluir", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoDeletar = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    if (datePickerAlvo != null) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = when (datePickerAlvo) {
                "inicio" -> tarefa.dataInicio
                    ?.atStartOfDay(ZoneId.of("UTC"))
                    ?.toInstant()
                    ?.toEpochMilli()
                "final" -> tarefa.dataFinal
                    ?.atStartOfDay(ZoneId.of("UTC"))
                    ?.toInstant()
                    ?.toEpochMilli()
                else -> null
            }
        )

        DatePickerDialog(
            onDismissRequest = { datePickerAlvo = null },
            confirmButton = {
                TextButton(onClick = {
                    val millis = datePickerState.selectedDateMillis
                    if (millis != null) {
                        val dataSelecionada = Instant.ofEpochMilli(millis)
                            .atZone(ZoneId.of("UTC"))
                            .toLocalDate()
                        when (datePickerAlvo) {
                            "inicio" -> viewModel.atualizarDataInicio(tarefa.id, dataSelecionada)
                            "final" -> viewModel.atualizarDataFinal(tarefa.id, dataSelecionada)
                        }
                    }
                    datePickerAlvo = null
                }) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(onClick = { datePickerAlvo = null }) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdicao) "Editar Tarefa" else "Detalhes da Tarefa") },
                navigationIcon = {
                    IconButton(onClick = onVoltar) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar")
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarDialogoDeletar = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Excluir tarefa",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                    IconButton(onClick = {
                        if (isEdicao) viewModel.salvarEdicao()
                        else viewModel.alternarModoEdicao()
                    }) {
                        Icon(
                            imageVector = if (isEdicao) Icons.Default.Check else Icons.Default.Edit,
                            contentDescription = if (isEdicao) "Salvar" else "Editar"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
        ) {

            OutlinedTextField(
                value = tarefa.titulo,
                onValueChange = { viewModel.atualizarCamposTexto(it, tarefa.descricao) },
                label = { Text("Título") },
                enabled = isEdicao,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = tarefa.descricao,
                onValueChange = { viewModel.atualizarCamposTexto(tarefa.titulo, it) },
                label = { Text("Descrição") },
                enabled = isEdicao,
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Status:", style = MaterialTheme.typography.titleMedium)
            if (isEdicao) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusTarefa.entries.forEach { status ->
                        FilterChip(
                            selected = tarefa.statusTarefa == status,
                            onClick = { viewModel.trocarStatus(tarefa.id, status) },
                            label = { Text(status.name) }
                        )
                    }
                }
            } else {
                SuggestionChip(onClick = {}, label = { Text(tarefa.statusTarefa.name) })
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Prioridade:", style = MaterialTheme.typography.titleMedium)
            if (isEdicao) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Prioridade.entries.forEach { prioridade ->
                        FilterChip(
                            selected = tarefa.prioridade == prioridade,
                            onClick = { viewModel.trocarPrioridade(tarefa.id, prioridade) },
                            label = { Text(prioridade.name) }
                        )
                    }
                }
            } else {
                SuggestionChip(onClick = {}, label = { Text(tarefa.prioridade.name) })
            }

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Data de Cadastro: ${tarefa.dataCadastro.format(dateFormatter)}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            CampoData(
                label = "Data de Início",
                data = tarefa.dataInicio,
                formatter = dateFormatter,
                isEdicao = isEdicao,
                onSelecionarData = { datePickerAlvo = "inicio" },
                onLimparData = { viewModel.atualizarDataInicio(tarefa.id, null) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            CampoData(
                label = "Prazo Final",
                data = tarefa.dataFinal,
                formatter = dateFormatter,
                isEdicao = isEdicao,
                onSelecionarData = { datePickerAlvo = "final" },
                onLimparData = { viewModel.atualizarDataFinal(tarefa.id, null) }
            )

            Spacer(modifier = Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(16.dp))

            Text("Imagens:", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))

            if (isEdicao) {
                OutlinedButton(
                    onClick = { imagemLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Adicionar Imagens")
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (tarefa.imagens.isEmpty()) {
                Text(
                    text = "Nenhuma imagem adicionada",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                MiniaturaImagens(
                    paths = tarefa.imagens,
                    onRemover = if (isEdicao) { index ->
                        viewModel.removerImagemDaTarefa(tarefa.id, index)
                    } else null,
                    onImagemClick = { path -> imagemSelecionada = path }

                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        if (imagemSelecionada != null) {
            Dialog(onDismissRequest = { imagemSelecionada = null }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { imagemSelecionada = null }, // toque fora/na imagem fecha
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = File(imagemSelecionada!!),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f), // ou .height(400.dp), o que preferir
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }

    }
}

@Composable
private fun CampoData(
    label: String,
    data: LocalDate?,
    formatter: DateTimeFormatter,
    isEdicao: Boolean,
    onSelecionarData: () -> Unit,
    onLimparData: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = "$label:", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(6.dp))

        if (isEdicao) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onSelecionarData,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.DateRange,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = data?.format(formatter) ?: "Selecionar data")
                }
                if (data != null) {
                    TextButton(onClick = onLimparData) {
                        Text("Limpar", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        } else {
            if (data != null) {
                SuggestionChip(
                    onClick = {},
                    label = { Text(data.format(formatter)) }
                )
            } else {
                Text(
                    text = "Não definida",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
