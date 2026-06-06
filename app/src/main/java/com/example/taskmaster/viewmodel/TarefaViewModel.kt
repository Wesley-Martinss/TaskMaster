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
            val tarefa = tarefas[indice]
            tarefa.statusTarefa = statusNovo

            tarefas[indice] = tarefa
            TarefaRepository.atualizar(tarefa)
        }
    }

    fun trocarPrioridade(idAtividade: Int, prioridadeNova: Prioridade) {
        val indice = tarefas.indexOfFirst { it.id == idAtividade }

        if (indice != -1) {
            val tarefa = tarefas[indice]
            tarefa.prioridade = prioridadeNova

            tarefas[indice] = tarefa
            TarefaRepository.atualizar(tarefa)
        }
    }
}