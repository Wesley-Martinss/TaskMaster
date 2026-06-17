package com.example.taskmaster.pages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmaster.model.Tarefa
import com.example.taskmaster.model.enuns.FiltroPrazo
import com.example.taskmaster.model.enuns.Prioridade
import com.example.taskmaster.model.enuns.StatusTarefa
import com.example.taskmaster.viewmodel.TarefaViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HomeScreen(
    onNavegarParaAdicionar: () -> Unit,
    onNavegarParaDetalhes: (Tarefa) -> Unit,
    viewModel: TarefaViewModel = viewModel()
) {
    var statusSelecionado by remember { mutableStateOf<StatusTarefa?>(null) }
    var prioridadeSelecionada by remember { mutableStateOf<Prioridade?>(null) }
    var filtroPrazo by remember { mutableStateOf<FiltroPrazo?>(null) }
    var tarefaParaDeletar by remember { mutableStateOf<Tarefa?>(null) }

    if (tarefaParaDeletar != null) {
        AlertDialog(
            onDismissRequest = { tarefaParaDeletar = null },
            title = { Text("Excluir tarefa") },
            text = { Text("Deseja excluir \"${tarefaParaDeletar!!.titulo}\"? Essa ação não pode ser desfeita.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deletarTarefa(tarefaParaDeletar!!.id)
                        tarefaParaDeletar = null
                    }
                ) {
                    Text("Excluir", color = androidx.compose.ui.graphics.Color(0xFFF44336))
                }
            },
            dismissButton = {
                TextButton(onClick = { tarefaParaDeletar = null }) {
                    Text("Cancelar")
                }
            }
        )
    }

    val hoje = LocalDate.now()

    val tarefasFiltradas = viewModel.tarefas.filter { tarefa ->
        val statusValido = statusSelecionado == null || tarefa.statusTarefa == statusSelecionado
        val prioridadeValida = prioridadeSelecionada == null || tarefa.prioridade == prioridadeSelecionada

        val dataFinal = tarefa.dataFinal  // <-- captura aqui

        val prazoValido = if (filtroPrazo == null || dataFinal == null) {
            true
        } else {
            val limite = when (filtroPrazo) {
                FiltroPrazo.VINTE_QUATRO_HORAS -> hoje.plusDays(1)
                FiltroPrazo.TRES_DIAS -> hoje.plusDays(3)
                FiltroPrazo.UMA_SEMANA -> hoje.plusDays(7)
                null -> hoje
            }
            !dataFinal.isAfter(limite)  // <-- usa a variável local
        }
        statusValido && prioridadeValida && prazoValido
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { onNavegarParaAdicionar() },
                containerColor = MaterialTheme.colorScheme.primary,
                elevation = FloatingActionButtonDefaults.elevation(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Adicionar Tarefa",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "TaskMaster",
                fontSize = 30.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Gerencie suas tarefas com facilidade",
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            ExibirQuantidadePorStatus(
                modifier = Modifier,
                tarefas = viewModel.tarefas
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Status",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            exibirFiltroStatus(
                statusSelecionado = statusSelecionado,
                onStatusChange = { statusSelecionado = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Prioridade",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            exibirFiltroPrioridade(
                prioridadeSelecionada = prioridadeSelecionada,
                onPrioridadeChange = { prioridadeSelecionada = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Prazo",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Start,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )

            Spacer(modifier = Modifier.height(4.dp))

            exibirFiltroPrazo(
                filtroPrazo = filtroPrazo,
                onFiltroChange = { filtroPrazo = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tarefas",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${tarefasFiltradas.size} encontrada(s)",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            cardAtividades(
                modifier = Modifier.weight(1f),
                tarefas = tarefasFiltradas,
                viewModel = viewModel,
                onTarefaClick = onNavegarParaDetalhes,
                onTarefaExcluir = { tarefaParaDeletar = it }
            )
        }
    }
}

@Composable
fun cardAtividades(
    modifier: Modifier = Modifier,
    tarefas: List<Tarefa>,
    viewModel: TarefaViewModel,
    onTarefaClick: (Tarefa) -> Unit,
    onTarefaExcluir: (Tarefa) -> Unit
) {
    val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    val hoje = LocalDate.now()

    if (tarefas.isEmpty()) {
        Box(
            modifier = modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Nenhuma tarefa encontrada",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
                Text(
                    text = "Toque em + para adicionar",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                )
            }
        }
        return
    }

    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(tarefas) { tarefa ->

            val icone = when (tarefa.prioridade) {
                Prioridade.NORMAL -> Icons.Default.Info
                Prioridade.LEMBRETE -> Icons.Default.Notifications
                Prioridade.IMPORTANTE -> Icons.Default.Warning
            }

            val corStatus = when (tarefa.statusTarefa) {
                StatusTarefa.PENDENTE -> Color(0xFFFFA000)
                StatusTarefa.CONCLUIDO -> Color(0xFF4CAF50)
                StatusTarefa.AGUARDANDO -> Color(0xFFF44336)
            }

            val corPrioridade = when (tarefa.prioridade) {
                Prioridade.NORMAL -> Color(0xFF2196F3)
                Prioridade.LEMBRETE -> Color(0xFFFFA000)
                Prioridade.IMPORTANTE -> Color(0xFFF44336)
            }

            val dataFinal = tarefa.dataFinal

            val prazoProximo = dataFinal != null &&
                    tarefa.statusTarefa != StatusTarefa.CONCLUIDO &&
                    !dataFinal.isBefore(hoje) &&
                    dataFinal.isBefore(hoje.plusDays(3))

            val prazoVencido = dataFinal != null &&
                    tarefa.statusTarefa != StatusTarefa.CONCLUIDO &&
                    dataFinal.isBefore(hoje)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                onClick = {
                    onTarefaClick(tarefa)
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = corPrioridade.copy(alpha = 0.12f),
                                modifier = Modifier
                                    .size(34.dp)
                                    .clickable {
                                        val novaPrioridade = when (tarefa.prioridade) {
                                            Prioridade.NORMAL -> Prioridade.LEMBRETE
                                            Prioridade.LEMBRETE -> Prioridade.IMPORTANTE
                                            Prioridade.IMPORTANTE -> Prioridade.NORMAL
                                        }
                                        viewModel.trocarPrioridade(tarefa.id, novaPrioridade)
                                    }
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = icone,
                                        contentDescription = "Prioridade",
                                        tint = corPrioridade,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            Text(
                                text = tarefa.titulo,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                maxLines = 1
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            modifier = Modifier.clickable {
                                val novoStatus = when (tarefa.statusTarefa) {
                                    StatusTarefa.PENDENTE -> StatusTarefa.AGUARDANDO
                                    StatusTarefa.AGUARDANDO -> StatusTarefa.CONCLUIDO
                                    StatusTarefa.CONCLUIDO -> StatusTarefa.PENDENTE
                                }
                                viewModel.trocarStatus(tarefa.id, novoStatus)
                            },
                            color = corStatus.copy(alpha = 0.12f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = tarefa.statusTarefa.name,
                                color = corStatus,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Descrição
                    Text(
                        text = tarefa.descricao,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                        maxLines = 2
                    )

                    // Aviso de prazo vencido
                    if (prazoVencido) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFFF44336).copy(alpha = 0.10f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = Color(0xFFF44336),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Prazo vencido!",
                                    color = Color(0xFFF44336),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    // Aviso de prazo próximo (só mostra se não está vencido)
                    if (prazoProximo && !prazoVencido) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Surface(
                            color = Color(0xFFFFA000).copy(alpha = 0.10f),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Notifications,
                                    contentDescription = null,
                                    tint = Color(0xFFFFA000),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Prazo próximo! Vence em menos de 3 dias.",
                                    color = Color(0xFFFFA000),
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Datas: cadastro + início + fim na mesma área
                    Text(
                        text = "Cadastro: ${tarefa.dataCadastro.format(formatter)}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                    )

                    if (tarefa.dataInicio != null || tarefa.dataFinal != null) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            tarefa.dataInicio?.let { inicio ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color(0xFF2196F3).copy(alpha = 0.08f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "▶ Início",
                                            fontSize = 10.sp,
                                            color = Color(0xFF2196F3),
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = inicio.format(formatter),
                                            fontSize = 11.sp,
                                            color = Color(0xFF2196F3)
                                        )
                                    }
                                }
                            }

                            tarefa.dataFinal?.let { fim ->
                                val corData = when {
                                    prazoVencido -> Color(0xFFF44336)
                                    prazoProximo -> Color(0xFFFFA000)
                                    else -> Color(0xFF4CAF50)
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = corData.copy(alpha = 0.08f)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "⏹ Fim",
                                            fontSize = 10.sp,
                                            color = corData,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = fim.format(formatter),
                                            fontSize = 11.sp,
                                            color = corData
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Botão deletar
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { onTarefaExcluir(tarefa) },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = Color(0xFFF44336)
                            ),
                            border = BorderStroke(1.dp, Color(0xFFF44336).copy(alpha = 0.4f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Excluir", fontSize = 13.sp)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun exibirFiltroStatus(
    statusSelecionado: StatusTarefa?,
    onStatusChange: (StatusTarefa?) -> Unit
) {
    val opcoes = listOf(
        null to "Todos",
        StatusTarefa.PENDENTE to "Pendente",
        StatusTarefa.CONCLUIDO to "Concluído",
        StatusTarefa.AGUARDANDO to "Aguardando"
    )

    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        items(opcoes) { (status, label) ->
            val selecionado = statusSelecionado == status
            Surface(
                shape = RoundedCornerShape(50),
                color = if (selecionado) MaterialTheme.colorScheme.primary
                else MaterialTheme.colorScheme.surface,
                modifier = Modifier.clickable { onStatusChange(status) }
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selecionado) Color.White
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
fun exibirFiltroPrioridade(
    prioridadeSelecionada: Prioridade?,
    onPrioridadeChange: (Prioridade?) -> Unit
) {
    val opcoes = listOf(
        null to "Todas",
        Prioridade.NORMAL to "Normal",
        Prioridade.LEMBRETE to "Lembrete",
        Prioridade.IMPORTANTE to "Importante"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(opcoes) { (prioridade, label) ->
            val selecionado = prioridadeSelecionada == prioridade
            Surface(
                shape = RoundedCornerShape(50),
                color = if (selecionado) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.surface,
                modifier = Modifier.clickable { onPrioridadeChange(prioridade) }
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selecionado) Color.White
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
fun exibirFiltroPrazo(
    filtroPrazo: FiltroPrazo?,
    onFiltroChange: (FiltroPrazo?) -> Unit
) {
    val opcoes = listOf(
        null to "Todos",
        FiltroPrazo.VINTE_QUATRO_HORAS to "24h",
        FiltroPrazo.TRES_DIAS to "3 dias",
        FiltroPrazo.UMA_SEMANA to "1 semana"
    )

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        items(opcoes) { (prazo, label) ->
            val selecionado = filtroPrazo == prazo
            Surface(
                shape = RoundedCornerShape(50),
                color = if (selecionado) Color(0xFF2196F3)
                else MaterialTheme.colorScheme.surface,
                modifier = Modifier.clickable { onFiltroChange(prazo) }
            ) {
                Text(
                    text = label,
                    fontSize = 13.sp,
                    fontWeight = if (selecionado) FontWeight.SemiBold else FontWeight.Normal,
                    color = if (selecionado) Color.White
                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 7.dp)
                )
            }
        }
    }
}

@Composable
fun ExibirQuantidadePorStatus(
    modifier: Modifier,
    tarefas: List<Tarefa>
) {
    val quantidadeTotal = tarefas.size
    val quantidadePendente = tarefas.count {
        it.statusTarefa == StatusTarefa.PENDENTE }
    val quantidadeAguardando = tarefas.count { it.statusTarefa == StatusTarefa.AGUARDANDO }
    val quantidadeConcluido = tarefas.count { it.statusTarefa == StatusTarefa.CONCLUIDO }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        contadorCard(
            modifier = modifier.weight(1f),
            label = "Total",
            valor = quantidadeTotal,
            cor = MaterialTheme.colorScheme.primary,
            corFundo = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        )
        contadorCard(
            modifier = Modifier.weight(1f),
            label = "Concluídas",
            valor = quantidadeConcluido,
            cor = Color(0xFF4CAF50),
            corFundo = Color(0xFF4CAF50).copy(alpha = 0.08f)
        )
        contadorCard(
            modifier = Modifier.weight(1f),
            label = "Pendentes",
            valor = quantidadePendente,
            cor = Color(0xFFFFA000),
            corFundo = Color(0xFFFFA000).copy(alpha = 0.08f)
        )
        contadorCard(
            modifier = Modifier.weight(1f),
            label = "Aguardando",
            valor = quantidadeAguardando,
            cor = Color(0xFFF44336),
            corFundo = Color(0xFFF44336).copy(alpha = 0.08f)
        )
    }
}

@Composable
fun contadorCard(
    modifier: Modifier = Modifier,
    label: String,
    valor: Int,
    cor: Color,
    corFundo: Color
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = corFundo
    ) {
        Column(
            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = valor.toString(),
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                color = cor
            )
            Text(
                text = label,
                fontSize = 11.sp,
                color = cor.copy(alpha = 0.8f),
                fontWeight = FontWeight.Medium
            )
        }
    }
}