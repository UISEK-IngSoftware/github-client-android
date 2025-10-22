package ec.edu.uisek.githubclient

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding

// 1. Clase ViewHolder: Contiene las referencias a las vistas de un solo ítem.
//    Usa la clase de ViewBinding generada para fragment_repo_item.xml.
class RepoViewHolder(private val binding: FragmentRepoItemBinding) : RecyclerView.ViewHolder(binding.root) {

    // 2. Función para vincular datos a las vistas del ítem.
    //    Por ahora, usaremos datos de ejemplo.
    fun bind(position: Int) {
        binding.repoOwnerImage.setImageResource(R.mipmap.ic_launcher) // Usa una imagen que tengas
        binding.repoName.text = "Repositorio #${position + 1}"
        binding.repoDescription.text = "Esta es la descripción del elemento número ${position + 1} en la lista."
        binding.repoLanguage.text = if (position % 2 == 0) "Kotlin" else "Java"
    }
}

// 3. Clase Adapter: Gestiona la creación y actualización de los ViewHolders.
class ReposAdapter : RecyclerView.Adapter<RepoViewHolder>() {

    // Por ahora, simplemente le diremos que muestre 3 ítems.
    override fun getItemCount(): Int = 3

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
        holder.bind(position)
    }
}
