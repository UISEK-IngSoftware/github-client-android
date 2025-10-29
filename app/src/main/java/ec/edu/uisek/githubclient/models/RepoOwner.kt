package ec.edu.uisek.githubclient.models

import com.google.gson.annotations.SerializedName

// Representa el objeto 'owner' anidado dentro del repositorio
data class RepoOwner(
    val id: Long,
    val login: String,
    @SerializedName("avatar_url") // Mapea 'avatar_url' a 'avatarUrl'
    val avatarUrl: String
)
