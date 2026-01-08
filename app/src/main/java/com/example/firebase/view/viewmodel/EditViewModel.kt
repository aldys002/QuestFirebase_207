package com.example.firebase.view.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.firebase.modeldata.DetailSiswa
import com.example.firebase.modeldata.UiStateSiswa
import com.example.firebase.modeldata.toDataSiswa
import com.example.firebase.modeldata.toUiStateSiswa
import com.example.firebase.repositori.RepositorySiswa
import com.example.firebase.view.route.DestinasiDetail
import kotlinx.coroutines.launch

class EditViewModel (savedStateHandle: SavedStateHandle, private val repositorySiswa: RepositorySiswa) : ViewModel() {
    var uiStateSiswa by mutableStateOf(UiStateSiswa())
        private set

    private val idSiswa: Long =
        savedStateHandle.get<String>(DestinasiDetail.itemIdArg)?.toLong()
            ?: error("idSiswa tidak ditemukan di SavedStateHandle")
    init {
        viewModelScope.launch {
            uiStateSiswa = repositorySiswa.getSatuSiswa(idSiswa)!!
                .toUiStateSiswa(true)
        }
    }

    fun updateUiState(detailSiswa: DetailSiswa) {
        uiStateSiswa =
            UiStateSiswa(detailSiswa = detailSiswa, isEntryValid = validasiInput(detailSiswa))
    }

    private fun validasiInput(uiState: DetailSiswa = uiStateSiswa.detailSiswa): Boolean{
        return with(uiState){
            nama.isNotBlank() && alamat.isNotBlank() && telpon.isNotBlank()
        }
    }

    suspend fun editSatuSiswa(){
        if (validasiInput(uiStateSiswa.detailSiswa)){
            try {
                repositorySiswa.editSatuSiswa(idSiswa, uiStateSiswa.detailSiswa.toDataSiswa())
                println("Update Sukses: $idSiswa")
            }catch (e: Exception){
                println("Update error: ${e.message}")
            }
        }
    }
}