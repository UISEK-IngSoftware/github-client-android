package ec.edu.uisek.githubclient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
// 1. Importa la clase de binding generada automáticamente
import ec.edu.uisek.githubclient.databinding.FragmentRepoItemBinding

/**
 * A simple [Fragment] subclass.
 * Use the [RepoItem.newInstance] factory method to
 * create an instance of this fragment.
 */
class RepoItem : Fragment() {
    // 2. Declara una variable para la clase de enlace.
    // La hacemos nullable para poder limpiarla en onDestroyView y evitar fugas de memoria.
    private var _binding: FragmentRepoItemBinding? = null

    // Esta propiedad es solo para conveniencia, para evitar escribir '!!' cada vez.
    // Solo se debe usar entre onCreateView y onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 3. Infla el layout usando la clase de binding en lugar de inflater.inflate()
        _binding = FragmentRepoItemBinding.inflate(inflater, container, false)
        // 4. Retorna la vista raíz del binding
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 5. ¡Listo! Ahora puedes acceder a todas tus vistas con ID directamente desde el objeto 'binding'.
        // Por ejemplo, si tu XML tiene un TextView con android:id="@+id/repo_name"
        // y un ImageView con android:id="@+id/repo_owner_image", puedes hacer esto:

        binding.repoName.text = "Mi Repositorio Genial"
        binding.repoDescription.text = "Esta descripción se ha establecido desde el código Kotlin."
        binding.repoOwnerImage.setImageResource(R.drawable.ic_launcher_foreground) // Ejemplo de imagen
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // 6. Es muy importante limpiar la referencia al binding para evitar fugas de memoria.
        _binding = null
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RepoItem.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RepoItem().apply {
                arguments = Bundle().apply {
                }
            }
    }
}