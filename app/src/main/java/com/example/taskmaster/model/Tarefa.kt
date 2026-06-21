package com.example.taskmaster.model

import com.example.taskmaster.model.enuns.Prioridade
import com.example.taskmaster.model.enuns.StatusTarefa
import com.example.taskmaster.serializer.LocalDateSerializer
import kotlinx.serialization.Serializable
import java.time.LocalDate


@Serializable
data class Tarefa(

    val id: Int,

    var titulo: String,

    var descricao: String,

    var statusTarefa: StatusTarefa = StatusTarefa.PENDENTE,

    var prioridade: Prioridade = Prioridade.NORMAL,

    @Serializable(with = LocalDateSerializer::class)
    val dataCadastro: LocalDate,

    @Serializable(with = LocalDateSerializer::class)
    var dataInicio: LocalDate?,

    @Serializable(with = LocalDateSerializer::class)
    var dataFinal: LocalDate?,

    var imagens: List<String> = emptyList()

)