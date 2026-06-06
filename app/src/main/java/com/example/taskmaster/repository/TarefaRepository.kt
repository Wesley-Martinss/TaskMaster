package com.example.taskmaster.repository

import android.R.bool
import com.example.taskmaster.model.Tarefa
import com.example.taskmaster.model.enuns.Prioridade
import com.example.taskmaster.model.enuns.StatusTarefa
import java.time.LocalDate

object TarefaRepository {
    private val tarefas = mutableListOf<Tarefa>(
        Tarefa(
            id = 1,
            titulo = "Estudar Jetpack Compose",
            descricao = "Praticar a criação de telas e LazyColumn",
            dataCadastro = LocalDate.now(),
            dataInicio = null,
            dataFinal = null
        ),
        Tarefa(
            id = 2,
            titulo = "Ajustar o TaskMaster",
            descricao = "Corrigir os erros de compilação do projeto",
            dataCadastro = LocalDate.now(),
            dataInicio = null,
            dataFinal = null
        ),
        Tarefa(
            id = 3,
            titulo = "Fazer trabalho da faculdade",
            descricao = "Finalizar o projeto de Sistemas para Internet",
            statusTarefa = StatusTarefa.PENDENTE,
            prioridade = Prioridade.IMPORTANTE,
            dataCadastro = LocalDate.now(),
            dataInicio = LocalDate.now(),
            dataFinal = LocalDate.now().plusDays(7)
        ),

        Tarefa(
            id = 4,
            titulo = "Pagar conta de energia",
            descricao = "Vencimento na próxima semana",
            statusTarefa = StatusTarefa.AGUARDANDO,
            prioridade = Prioridade.LEMBRETE,
            dataCadastro = LocalDate.now(),
            dataInicio = null,
            dataFinal = LocalDate.now().plusDays(5)
        ),

        Tarefa(
            id = 5,
            titulo = "Treinar Kotlin",
            descricao = "Resolver exercícios de Compose e ViewModel",
            statusTarefa = StatusTarefa.CONCLUIDO,
            prioridade = Prioridade.NORMAL,
            dataCadastro = LocalDate.now().minusDays(3),
            dataInicio = LocalDate.now().minusDays(2),
            dataFinal = LocalDate.now().minusDays(1)
        ),

        Tarefa(
            id = 6,
            titulo = "Comprar peças para o drone",
            descricao = "Pesquisar ESP32, motores e controladora",
            statusTarefa = StatusTarefa.PENDENTE,
            prioridade = Prioridade.IMPORTANTE,
            dataCadastro = LocalDate.now(),
            dataInicio = LocalDate.now().plusDays(1),
            dataFinal = LocalDate.now().plusDays(10)
        )

    )
    fun listar() : List<Tarefa>{
        return tarefas
    }

    fun add(tarefa: Tarefa) : Boolean {
        if(tarefa != null){
            tarefas.add(tarefa)
            return true
        } else {
            return false
        }
    }

    fun deletar(id: Int) : Boolean{
        val tarefa: Tarefa? = findById(id)

        if(tarefa != null){
            tarefas.remove(tarefa)
            return true
        }

        return false
    }

    fun findById(id: Int) : Tarefa?{
        return tarefas.find { it.id == id }
    }

    fun atualizar(tarefaAtualizada: Tarefa) {
        val indice = tarefas.indexOfFirst { it.id == tarefaAtualizada.id }

        if (indice != -1) {
            tarefas[indice] = tarefaAtualizada
        }
    }

}