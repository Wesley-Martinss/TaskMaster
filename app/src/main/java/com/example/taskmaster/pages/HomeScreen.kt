package com.example.taskmaster.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.taskmaster.model.enuns.Prioridade
import com.example.taskmaster.model.enuns.StatusTarefa
import com.example.taskmaster.viewmodel.TarefaViewModel
import androidx.compose.runtime.*
import com.example.taskmaster.model.Tarefa

@Composable
fun HomeScreen(
    onNavegarParaAdicionar: () -> Unit,
    viewModel: TarefaViewModel = viewModel()
) {
    var statusSelecionado by remember {
        mutableStateOf<StatusTarefa?>(null)
    }

    var prioridadeSelecionada by remember {
        mutableStateOf<Prioridade?>(null)
    }

    val tarefasFiltradas = viewModel.tarefas.filter { tarefa ->

        val statusValido =
            statusSelecionado == null ||
                    tarefa.statusTarefa == statusSelecionado

        val prioridadeValida =
            prioridadeSelecionada == null ||
                    tarefa.prioridade == prioridadeSelecionada

        statusValido && prioridadeValida
    }

    Scaffold (
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {onNavegarParaAdicionar()}
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "Adicionar Tarefa")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "TaskMaster",
                fontSize = 36.sp,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            exibirFiltroStatus(
                statusSelecionado = statusSelecionado,
                onStatusChange = {
                    statusSelecionado = it
                }
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            exibirFiltroPrioridade(
                prioridadeSelecionada = prioridadeSelecionada,
                onPrioridadeChange = {
                    prioridadeSelecionada = it
                }
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            cardAtividades(
                modifier = Modifier.weight(1f),
                tarefas = tarefasFiltradas
            )
        }
    }
}

@Composable
fun cardAtividades(
    modifier: Modifier = Modifier,
    tarefas: List<Tarefa>
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
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

            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    //abrir a tarefa pelo id dela aqui
                }

            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Row {
                            Icon(
                                imageVector = icone,
                                contentDescription = null
                            )

                            Spacer(
                                modifier = Modifier.width(8.dp)
                            )

                            Text(
                                text = tarefa.titulo,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(
                            color = corStatus.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(50)
                        ) {
                            Text(
                                text = tarefa.statusTarefa.name,
                                color = corStatus,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp,
                                    vertical = 4.dp
                                )
                            )
                        }
                    }

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = tarefa.descricao
                    )

                    Spacer(
                        modifier = Modifier.height(12.dp)
                    )

                    Text(
                        text = "${tarefa.dataCadastro}"
                    )

                    tarefa.dataInicio?.let {
                        Text(
                            text = "Início: $it"
                        )
                    }

                    tarefa.dataFinal?.let {
                        Text(
                            text = "Fim: $it"
                        )
                    }

                    Spacer(
                        modifier = Modifier.height(16.dp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {

                        Button(
                            onClick = {
                                // deletar tarefa
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Deletar"
                            )
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun exibirFiltroStatus(
    statusSelecionado: StatusTarefa?,
    onStatusChange: (StatusTarefa?) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {

        item {
            Button(
                onClick = { onStatusChange(null) }
            ) {
                Text("Todos")
            }
        }

        item {
            Button(
                onClick = { onStatusChange(StatusTarefa.PENDENTE) }
            ) {
                Text("Pendente")
            }
        }

        item {
            Button(
                onClick = { onStatusChange(StatusTarefa.CONCLUIDO) }
            ) {
                Text("Concluído")
            }
        }

        item {
            Button(
                onClick = { onStatusChange(StatusTarefa.AGUARDANDO) }
            ) {
                Text("Aguardando")
            }
        }
    }
}

@Composable
fun exibirFiltroPrioridade(
    prioridadeSelecionada: Prioridade?,
    onPrioridadeChange: (Prioridade?) -> Unit
) {
    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Button(
                onClick = {
                    onPrioridadeChange(null)
                }
            ) {
                Text("Todas")
            }
        }

        item {
            Button(
                onClick = {
                    onPrioridadeChange(Prioridade.NORMAL)
                }
            ) {
                Text("Normal")
            }
        }

        item {
            Button(
                onClick = {
                    onPrioridadeChange(Prioridade.LEMBRETE)
                }
            ) {
                Text("Lembrete")
            }
        }

        item {
            Button(
                onClick = {
                    onPrioridadeChange(Prioridade.IMPORTANTE)
                }
            ) {
                Text("Importante")
            }
        }
    }
}