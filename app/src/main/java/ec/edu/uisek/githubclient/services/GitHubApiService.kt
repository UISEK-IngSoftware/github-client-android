package ec.edu.uisek.githubclient.services

import ec.edu.uisek.githubclient.models.Repo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * Interfaz que define los endpoints de la API de GitHub
 */
interface GitHubApiService {
    
    /**
     * Obtiene los repositorios del usuario autenticado
     * Endpoint: GET /user/repos
     * Requiere autenticación mediante token
     * 
     * @return Call con la lista de repositorios del usuario autenticado
     */
    @GET("user/repos")
    fun getAuthenticatedUserRepositories(): Call<List<Repo>>
    
    /**
     * Obtiene los repositorios de un usuario específico
     * Endpoint: GET /users/{username}/repos
     * 
     * @param username Nombre de usuario de GitHub
     * @return Call con la lista de repositorios del usuario
     */
    @GET("users/{username}/repos")
    fun getUserRepositories(@Path("username") username: String): Call<List<Repo>>
}
