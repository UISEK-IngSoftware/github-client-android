package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding
import ec.edu.uisek.githubclient.models.Repo

// 1. Clase ViewHolder: Contiene las referencias a las vistas de un solo ítem.
//    Usa la clase de ViewBinding generada para fragment_repo_item.xml.
class RepoViewHolder(private val binding: FragmentRepoItemBinding) : RecyclerView.ViewHolder(binding.root) {

    // 2. Función para vincular datos reales del repositorio a las vistas del ítem.
    fun bind(repo: Repo) {
        // Establece el nombre del repositorio
        binding.repoName.text = repo.name
        
        // Establece la descripción (o un texto por defecto si es nula)
        binding.repoDescription.text = repo.description ?: "Sin descripción"
        
        // Establece el lenguaje (o un texto por defecto si es nulo)
        binding.repoLanguage.text = repo.language ?: "No especificado"
        
        // Carga la imagen del avatar del propietario usando Glide
        Glide.with(binding.root.context)
            .load(repo.owner.avatarUrl)
            .placeholder(R.mipmap.ic_launcher) // Imagen mientras carga
            .error(R.mipmap.ic_launcher) // Imagen si hay error
            .circleCrop() // Hace la imagen circular
            .into(binding.repoOwnerImage)
    }
}

// 3. Clase Adapter: Gestiona la creación y actualización de los ViewHolders.
class ReposAdapter : RecyclerView.Adapter<RepoViewHolder>() {

    // Lista de repositorios que se mostrarán
    private var repositories: List<Repo> = emptyList()

    // Retorna el número de elementos en la lista
    override fun getItemCount(): Int = repositories.size

    // Se llama para crear un nuevo ViewHolder cuando el RecyclerView lo necesita.
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepoViewHolder {
        // Infla la vista del ítem usando ViewBinding
        val binding = FragmentRepoItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return RepoViewHolder(binding)
    }

    // Se llama para vincular los datos a un ViewHolder en una posición específica.
    override fun onBindViewHolder(holder: RepoViewHolder, position: Int) {
        holder.bind(repositories[position])
    }
    
    // Función para actualizar la lista de repositorios y refrescar la vista
    fun updateRepositories(newRepositories: List<Repo>) {
        repositories = newRepositories
        notifyDataSetChanged()
    }
}
