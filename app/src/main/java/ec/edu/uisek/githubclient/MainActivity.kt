package ec.edu.uisek.githubclient

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import ec.edu.uisek.githubclient.databinding.ActivityMainBinding
import ec.edu.uisek.githubclient.models.Repo
import ec.edu.uisek.githubclient.services.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var reposAdapter: ReposAdapter
    
    // Tag para logs
    private val TAG = "MainActivity"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Infla el layout usando ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Configura el RecyclerView
        setupRecyclerView()
        
        // Obtiene los repositorios de GitHub
        fetchRepositories()
    }

    private fun setupRecyclerView() {
        reposAdapter = ReposAdapter()
        binding.reposRecyclerView.adapter = reposAdapter
    }
    
    /**
     * Obtiene los repositorios del usuario autenticado usando la API de GitHub
     * Usa el token configurado en BuildConfig.GITHUB_API_TOKEN
     */
    private fun fetchRepositories() {
        // Obtiene la instancia del servicio
        val apiService = RetrofitClient.gitHubApiService
        
        // Hace la llamada a la API para obtener los repos del usuario autenticado
        val call = apiService.getAuthenticatedUserRepositories()
        
        call.enqueue(object : Callback<List<Repo>> {
            override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                if (response.isSuccessful) {
                    // La petición fue exitosa
                    val repositories = response.body()
                    if (repositories != null && repositories.isNotEmpty()) {
                        Log.d(TAG, "Repositorios obtenidos: ${repositories.size}")
                        // Actualiza el adaptador con los datos
                        reposAdapter.updateRepositories(repositories)
                    } else {
                        Log.d(TAG, "No se encontraron repositorios")
                        showMessage("No tienes repositorios en tu cuenta de GitHub")
                    }
                } else {
                    // La petición falló
                    val errorMsg = when (response.code()) {
                        401 -> "Error de autenticación. Verifica tu token de GitHub en el archivo .env"
                        403 -> "Acceso prohibido. Verifica los permisos de tu token"
                        404 -> "Endpoint no encontrado"
                        else -> "Error ${response.code()}: ${response.message()}"
                    }
                    Log.e(TAG, errorMsg)
                    showMessage(errorMsg)
                }
            }

            override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                // Hubo un error de red o al procesar la petición
                val errorMsg = "Error de conexión: ${t.message}"
                Log.e(TAG, errorMsg, t)
                showMessage(errorMsg)
            }
        })
    }
    
    /**
     * Muestra un mensaje Toast al usuario
     */
    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}