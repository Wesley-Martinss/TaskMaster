package com.example.taskmaster.pages

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.taskmaster.model.Tarefa
import com.example.taskmaster.model.enuns.Prioridade
import com.example.taskmaster.model.enuns.StatusTarefa
import com.example.taskmaster.util.copiarImagemParaStorage
import com.example.taskmaster.viewmodel.TarefaViewModel
import java.io.File
import java.time.LocalDate
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.rememberDatePickerState
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TelaAdicionarTarefaScreen(
    viewModel: TarefaViewModel,
    onVoltar: () -> Unit
) {
    val context = LocalContext.current

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }
    var prioridade by remember { mutableStateOf(Prioridade.NORMAL) }
    var status by remember { mutableStateOf(StatusTarefa.PENDENTE) }

    var dataInicio by remember { mutableStateOf<LocalDate?>(LocalDate.now()) }
    var dataFinal by remember { mutableStateOf<LocalDate?>(null) }

    var datePickerAlvo by remember { mutableStateOf<String?>(null) }

    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    val imagensPaths = remember { mutableStateListOf<String>() }

    val imagemLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) { uris ->
        uris.forEach { uri ->
            val path = copiarImagemParaStorage(context, uri)
            imagensPaths.add(path)
        }
    }

    if (datePickerAlvo != null) {

        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = when (datePickerAlvo) {
                "inicio" -> dataInicio
                    ?.atStartOfDay(ZoneId.of("UTC"))
                    ?.toInstant()
                    ?.toEpochMilli()

                "final" -> dataFinal
                    ?.atStartOfDay(ZoneId.of("UTC"))
                    ?.toInstant()
                    ?.toEpochMilli()

                else -> null
            }
        )

        DatePickerDialog(
            onDismissRequest = {
                datePickerAlvo = null
            },
            confirmButton = {
                TextButton(
                    onClick = {

                        val millis = datePickerState.selectedDateMillis

                        if (millis != null) {

                            val dataSelecionada =
                                Instant.ofEpochMilli(millis)
                                    .atZone(ZoneId.of("UTC"))
                                    .toLocalDate()

                            when (datePickerAlvo) {
                                "inicio" -> dataInicio = dataSelecionada
                                "final" -> dataFinal = dataSelecionada
                            }
                        }

                        datePickerAlvo = null
                    }
                ) {
                    Text("Confirmar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        datePickerAlvo = null
                    }
                ) {
                    Text("Cancelar")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {

        Text(
            text = "Nova Tarefa",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = titulo,
            onValueChange = { titulo = it },
            label = { Text("Título") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = descricao,
            onValueChange = { descricao = it },
            label = { Text("Descrição") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text("Prioridade")

        Row {
            Prioridade.entries.forEach { item ->
                Button(
                    onClick = { prioridade = item },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (prioridade == item)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (prioridade == item)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(item.name)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("Status")

        Row {
            StatusTarefa.entries.forEach { item ->
                Button(
                    onClick = { status = item },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (status == item)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = if (status == item)
                            MaterialTheme.colorScheme.onPrimary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                ) {
                    Text(item.name)
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Datas",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoData (
            label = "Data de Início",
            data = dataInicio,
            formatter = dateFormatter,
            isEdicao = true,
            onSelecionarData = {
                datePickerAlvo = "inicio"
            },
            onLimparData = {
                dataInicio = null
            }
        )

        Spacer(modifier = Modifier.height(12.dp))

        CampoData (
            label = "Prazo Final",
            data = dataFinal,
            formatter = dateFormatter,
            isEdicao = true,
            onSelecionarData = {
                datePickerAlvo = "final"
            },
            onLimparData = {
                dataFinal = null
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        HorizontalDivider()

        Spacer(modifier = Modifier.height(16.dp))

        Text("Imagens", style = MaterialTheme.typography.titleMedium)

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = {
                imagemLauncher.launch("image/*")
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.width(6.dp))

            Text("Adicionar Imagens")
        }

        if (imagensPaths.isNotEmpty()) {

            Spacer(modifier = Modifier.height(12.dp))

            MiniaturaImagens(
                paths = imagensPaths,
                onRemover = { index ->
                    imagensPaths.removeAt(index)
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                if (titulo.isBlank()) return@Button

                val novaTarefa = Tarefa(
                    id = (System.currentTimeMillis() % Int.MAX_VALUE).toInt(),
                    titulo = titulo,
                    descricao = descricao,
                    statusTarefa = status,
                    prioridade = prioridade,
                    dataCadastro = LocalDate.now(),
                    dataInicio = dataInicio,
                    dataFinal = dataFinal,
                    imagens = imagensPaths.toList()
                )

                viewModel.adicionarTarefa(novaTarefa)
                onVoltar()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Salvar")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedButton(
            onClick = onVoltar,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cancelar")
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}
@Composable
fun MiniaturaImagens(
    paths: List<String>,
    onRemover: ((Int) -> Unit)? = null,
    onImagemClick: ((String) -> Unit)? = null
) {
    val rows = paths.chunked(3)
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEachIndexed { colIndex, path ->
                    val globalIndex = rowIndex * 3 + colIndex
                    Box {
                        AsyncImage(
                            model = File(path),
                            contentDescription = "Imagem ${globalIndex + 1}",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .clickable(enabled = onImagemClick != null) {
                                    onImagemClick?.invoke(path)
                                }
                        )
                        if (onRemover != null) {
                            IconButton(
                                onClick = { onRemover(globalIndex) },
                                modifier = Modifier
                                    .size(24.dp)
                                    .align(Alignment.TopEnd)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remover imagem",
                                    tint = MaterialTheme.colorScheme.error,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
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
