package com.example.taskmaster.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.taskmaster.model.Tarefa
import com.example.taskmaster.model.enuns.Prioridade
import com.example.taskmaster.model.enuns.StatusTarefa
import com.example.taskmaster.repository.TarefaRepository
import kotlinx.coroutines.launch
import java.time.LocalDate


class TarefaViewModel(
    private val repository: TarefaRepository
) : ViewModel() {


    val tarefas =
        mutableStateListOf<Tarefa>()


    var tarefaSelecionada by
    mutableStateOf<Tarefa?>(null)
        private set


    var modoEdicao by
    mutableStateOf(false)
        private set


    init {


        viewModelScope.launch {

            repository.inicializar()

        }


        carregarTarefas()


    }


    private fun carregarTarefas() {


        viewModelScope.launch {


            repository.tarefas.collect { lista ->


                tarefas.clear()

                tarefas.addAll(lista)


            }


        }


    }


    fun selecionarTarefa(
        tarefa: Tarefa
    ) {


        tarefaSelecionada =
            tarefa


        modoEdicao =
            false


    }


    fun alternarModoEdicao() {


        modoEdicao =
            !modoEdicao


    }


    fun atualizarCamposTexto(
        novoTitulo: String,
        novaDescricao: String
    ) {


        tarefaSelecionada =
            tarefaSelecionada?.copy(

                titulo = novoTitulo,

                descricao = novaDescricao

            )


    }


    fun salvarEdicao() {


        tarefaSelecionada?.let { tarefa ->


            viewModelScope.launch {


                repository.atualizar(
                    tarefa
                )


                modoEdicao =
                    false


            }


        }


    }


    fun trocarStatus(
        idAtividade: Int,
        statusNovo: StatusTarefa
    ) {


        val tarefa =
            tarefas.find {

                it.id == idAtividade

            }



        tarefa?.let {


            val atualizada =
                it.copy(
                    statusTarefa = statusNovo
                )



            viewModelScope.launch {


                repository.atualizar(
                    atualizada
                )


            }




            if (
                tarefaSelecionada?.id ==
                idAtividade
            ) {

                tarefaSelecionada =
                    atualizada

            }


        }


    }


    fun trocarPrioridade(
        idAtividade: Int,
        prioridadeNova: Prioridade
    ) {


        val tarefa =
            tarefas.find {

                it.id == idAtividade

            }



        tarefa?.let {


            val atualizada =
                it.copy(
                    prioridade = prioridadeNova
                )



            viewModelScope.launch {


                repository.atualizar(
                    atualizada
                )


            }




            if (
                tarefaSelecionada?.id ==
                idAtividade
            ) {

                tarefaSelecionada =
                    atualizada

            }


        }


    }


    fun atualizarDataInicio(
        idAtividade: Int,
        novaData: LocalDate?
    ) {


        val tarefa =
            tarefas.find {

                it.id == idAtividade

            }



        tarefa?.let {


            val atualizada =
                it.copy(
                    dataInicio = novaData
                )



            viewModelScope.launch {


                repository.atualizar(
                    atualizada
                )


            }



            if (
                tarefaSelecionada?.id ==
                idAtividade
            ) {

                tarefaSelecionada =
                    atualizada

            }


        }


    }


    fun atualizarDataFinal(
        idAtividade: Int,
        novaData: LocalDate?
    ) {


        val tarefa =
            tarefas.find {

                it.id == idAtividade

            }



        tarefa?.let {


            val atualizada =
                it.copy(
                    dataFinal = novaData
                )



            viewModelScope.launch {


                repository.atualizar(
                    atualizada
                )


            }



            if (
                tarefaSelecionada?.id ==
                idAtividade
            ) {

                tarefaSelecionada =
                    atualizada

            }


        }


    }


    fun adicionarTarefa(
        tarefa: Tarefa
    ) {


        viewModelScope.launch {


            repository.adicionar(
                tarefa
            )


        }


    }


    fun deletarTarefa(
        id: Int
    ) {


        viewModelScope.launch {


            repository.deletar(
                id
            )


        }


    }


    fun adicionarImagensNaTarefa(
        tarefaId: Int,
        novosPaths: List<String>
    ) {

        val tarefa = tarefas.find { it.id == tarefaId } ?: return

        val atualizada = tarefa.copy(
            imagens = tarefa.imagens + novosPaths
        )

        viewModelScope.launch {
            repository.atualizar(atualizada)
        }

        if (tarefaSelecionada?.id == tarefaId) {
            tarefaSelecionada = atualizada
        }

    }


    fun removerImagemDaTarefa(
        tarefaId: Int,
        index: Int
    ) {

        val tarefa = tarefas.find { it.id == tarefaId } ?: return

        val novasImagens = tarefa.imagens
            .toMutableList()
            .also { it.removeAt(index) }

        val atualizada = tarefa.copy(imagens = novasImagens)

        viewModelScope.launch {
            repository.atualizar(atualizada)
        }

        if (tarefaSelecionada?.id == tarefaId) {
            tarefaSelecionada = atualizada
        }

    }


    suspend fun proximoId(): Int {


        return repository.proximoId()


    }


}