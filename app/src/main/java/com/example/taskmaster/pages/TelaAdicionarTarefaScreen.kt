package com.example.taskmaster.pages


import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.taskmaster.model.Tarefa
import com.example.taskmaster.model.enuns.Prioridade
import com.example.taskmaster.model.enuns.StatusTarefa
import com.example.taskmaster.viewmodel.TarefaViewModel
import java.time.LocalDate

@Composable
fun TelaAdicionarTarefaScreen(
    viewModel: TarefaViewModel,
    onVoltar: () -> Unit
) {

    var titulo by remember { mutableStateOf("") }
    var descricao by remember { mutableStateOf("") }

    var prioridade by remember {
        mutableStateOf(Prioridade.NORMAL)
    }

    var status by remember {
        mutableStateOf(StatusTarefa.PENDENTE)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
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
                    onClick = {
                        prioridade = item
                    }
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
                    onClick = {
                        status = item
                    }
                ) {
                    Text(item.name)
                }

                Spacer(modifier = Modifier.width(8.dp))
            }
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

                    dataInicio = LocalDate.now(),

                    dataFinal = null
                )

                viewModel.adicionarTarefa(
                    novaTarefa
                )

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
    }
}