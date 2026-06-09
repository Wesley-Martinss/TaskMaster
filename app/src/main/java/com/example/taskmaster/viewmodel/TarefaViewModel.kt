package com.example.taskmaster.viewmodel

import android.view.View
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.taskmaster.model.Tarefa
import com.example.taskmaster.model.enuns.Prioridade
import com.example.taskmaster.model.enuns.StatusTarefa
import com.example.taskmaster.repository.TarefaRepository

class TarefaViewModel : ViewModel() {
    val tarefas = mutableStateListOf<Tarefa>()

    init {
        carregarTarefas()
    }

    fun carregarTarefas(){
        tarefas.clear()
        tarefas.addAll(TarefaRepository.listar())
    }

    fun trocarStatus(idAtividade: Int, statusNovo: StatusTarefa) {
        val indice = tarefas.indexOfFirst { it.id == idAtividade }

        if (indice != -1) {
            val tarefaAtualizada = tarefas[indice].copy(
                statusTarefa = statusNovo
            )

            tarefas[indice] = tarefaAtualizada
            TarefaRepository.atualizar(tarefaAtualizada)
        }
    }

    fun trocarPrioridade(idAtividade: Int, prioridadeNova: Prioridade) {
        val indice = tarefas.indexOfFirst { it.id == idAtividade }

        if (indice != -1) {
            val tarefaAtualizada = tarefas[indice].copy(
                prioridade = prioridadeNova
            )

            tarefas[indice] = tarefaAtualizada
            TarefaRepository.atualizar(tarefaAtualizada)
        }
    }
}