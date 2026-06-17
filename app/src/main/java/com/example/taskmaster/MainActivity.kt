package com.example.taskmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.taskmaster.pages.HomeScreen
import com.example.taskmaster.pages.TelaAdicionarTarefaScreen
import com.example.taskmaster.pages.TelaDetalheTarefaScreen
import com.example.taskmaster.repository.TarefaRepository
import com.example.taskmaster.repository.TarefaStorage
import com.example.taskmaster.ui.theme.TaskMasterTheme
import com.example.taskmaster.viewmodel.TarefaViewModel
import com.example.taskmaster.viewmodel.TarefaViewModelFactory


class MainActivity : ComponentActivity() {


    override fun onCreate(
        savedInstanceState: Bundle?
    ) {

        super.onCreate(savedInstanceState)


        setContent {


            TaskMasterTheme {


                val navController =
                    rememberNavController()


                /*
                    Criação do armazenamento
                    e do Repository
                */

                val storage =
                    TarefaStorage(
                        applicationContext
                    )


                val repository =
                    TarefaRepository(
                        storage
                    )


                val factory =
                    TarefaViewModelFactory(
                        repository
                    )


                val tarefaViewModel:
                        TarefaViewModel =
                    viewModel(
                        factory = factory
                    )






                NavHost(

                    navController = navController,

                    startDestination = "home"

                ) {


                    composable(
                        "home"
                    ) {


                        HomeScreen(

                            viewModel =
                            tarefaViewModel,


                            onNavegarParaAdicionar = {


                                navController.navigate(
                                    "adicionar_tarefa"
                                )


                            },


                            onNavegarParaDetalhes = { tarefa ->


                                tarefaViewModel
                                    .selecionarTarefa(
                                        tarefa
                                    )


                                navController.navigate(
                                    "detalhes_tarefa"
                                )


                            }

                        )


                    }








                    composable(
                        "adicionar_tarefa"
                    ) {

                        TelaAdicionarTarefaScreen(
                            viewModel = tarefaViewModel,
                            onVoltar = {
                                navController.popBackStack()
                            }
                        )

                    }









                    composable(
                        "detalhes_tarefa"
                    ) {


                        TelaDetalheTarefaScreen(

                            viewModel =
                            tarefaViewModel,


                            onVoltar = {


                                navController.popBackStack()


                            }

                        )


                    }


                }


            }


        }


    }


    @Preview(
        showBackground = true,
        showSystemUi = true
    )
    @Composable
    fun HomeScreenPreview() {


        TaskMasterTheme {


            /*
                O Preview não usa o DataStore,
                então deixamos um ViewModel vazio.

                Se der erro no Preview,
                podemos criar um Mock depois.
            */


        }


    }


}