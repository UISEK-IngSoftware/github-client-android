package ec.edu.uisek.githubclient.models

import com.google.gson.annotations.SerializedName

// Representa un único repositorio de GitHub
data class Repo(
    val id: Long,
    val name: String,
    val description: String?, // La descripción puede ser nula
    @SerializedName("owner") // Mapea el campo 'owner' del JSON a la propiedad 'owner'
    val owner: RepoOwner,
    val language: String? // El lenguaje también puede ser nulo
)