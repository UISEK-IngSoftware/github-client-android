# Guía de Implementación: Cliente GitHub con API REST

## Introducción
Esta guía explica paso a paso cómo implementamos la conexión con la API de GitHub para mostrar repositorios reales en nuestra aplicación Android. Antes de estos cambios, la app solo mostraba datos de prueba (hardcoded).

---

## 1. Creación de Modelos de Datos

### ¿Qué son los modelos?
Los modelos son clases que representan la estructura de los datos que recibimos de la API de GitHub. Son como "moldes" que definen qué información contiene cada repositorio.

### Archivos creados:

#### `models/Repo.kt`
```kotlin
data class Repo(
    val id: Long,
    val name: String,
    val description: String?,
    val owner: RepoOwner,
    val language: String?
)
```

**Propósito**: Representa un repositorio de GitHub con sus propiedades principales.

**Puntos clave**:
- `data class`: Tipo especial de clase en Kotlin para almacenar datos
- `String?`: El signo `?` significa que el valor puede ser nulo (no todos los repos tienen descripción)
- `@SerializedName`: Anotación que mapea los nombres del JSON de la API a nuestras propiedades

#### `models/RepoOwner.kt`
```kotlin
data class RepoOwner(
    val id: Long,
    val login: String,
    val avatarUrl: String
)
```

**Propósito**: Representa al dueño del repositorio (usuario de GitHub).

**Por qué es importante**: La API de GitHub devuelve los datos del propietario dentro del repositorio, así que necesitamos un modelo separado para organizarlo mejor.

---

## 2. Creación de Servicios (Paquete `services`)

### ¿Qué es un servicio?
Los servicios son las clases que se encargan de comunicarse con la API de GitHub. Aquí definimos qué información queremos obtener y cómo pedirla.

### Archivos creados:

#### `services/GitHubApiService.kt`
```kotlin
interface GitHubApiService {
    @GET("user/repos")
    fun getAuthenticatedUserRepositories(): Call<List<Repo>>
}
```

**Propósito**: Define los "endpoints" (direcciones) de la API que vamos a usar.

**Conceptos importantes**:
- `interface`: Define un contrato de lo que el servicio debe hacer
- `@GET("user/repos")`: Anotación de Retrofit que indica que haremos una petición GET a la ruta `user/repos`
- `Call<List<Repo>>`: El tipo de respuesta que esperamos (una lista de repositorios)

**Por qué `/user/repos`**: Este endpoint devuelve los repositorios del usuario autenticado (quien inició sesión con el token), no necesitamos pasar un nombre de usuario.

#### `services/RetrofitClient.kt`
```kotlin
object RetrofitClient {
    private const val BASE_URL = "https://api.github.com/"
    
    val gitHubApiService: GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }
}
```

**Propósito**: Configura Retrofit (la librería que usamos para hacer peticiones HTTP) y proporciona una instancia del servicio lista para usar.

**Componentes clave**:

1. **AuthInterceptor**: 
   - Agrega automáticamente el token de GitHub a cada petición
   - Formato: `Authorization: token TU_TOKEN`
   - Sin esto, la API no sabría quién está pidiendo los datos

2. **LoggingInterceptor**:
   - Muestra información de las peticiones en Logcat (solo en modo DEBUG)
   - Útil para depurar problemas

3. **OkHttpClient**:
   - Cliente HTTP que maneja las conexiones
   - Usa los interceptors configurados

4. **Retrofit**:
   - Convierte las respuestas JSON de la API a objetos Kotlin
   - Usa Gson para la conversión

---

## 3. Modificación del Adaptador

### Archivo modificado: `ReposAdapter.kt`

**Antes**:
```kotlin
fun bind(position: Int) {
    binding.repoName.text = "Repositorio #${position + 1}"
    binding.repoDescription.text = "Descripción de ejemplo"
}

override fun getItemCount(): Int = 3  // Siempre 3 elementos
```

**Después**:
```kotlin
fun bind(repo: Repo) {
    binding.repoName.text = repo.name
    binding.repoDescription.text = repo.description ?: "Sin descripción"
    binding.repoLanguage.text = repo.language ?: "No especificado"
    
    Glide.with(binding.root.context)
        .load(repo.owner.avatarUrl)
        .into(binding.repoOwnerImage)
}

override fun getItemCount(): Int = repositories.size  // Tamaño dinámico
```

**Cambios importantes**:

1. **De posición a objeto Repo**: 
   - Antes recibíamos solo la posición (0, 1, 2...)
   - Ahora recibimos el objeto completo con todos los datos del repositorio

2. **Lista dinámica**:
   - Agregamos `private var repositories: List<Repo> = emptyList()`
   - El tamaño ahora depende de cuántos repositorios recibamos de la API

3. **Método `updateRepositories()`**:
   - Permite actualizar la lista cuando lleguen los datos de la API
   - Llama a `notifyDataSetChanged()` para refrescar la vista

4. **Glide para imágenes**:
   - Librería que descarga y muestra imágenes desde URLs
   - Maneja el caché automáticamente
   - Muestra placeholders mientras carga

---

## 4. Modificación del MainActivity

### Archivo modificado: `MainActivity.kt`

**Cambios principales**:

#### Nuevo método: `fetchRepositories()`

```kotlin
private fun fetchRepositories() {
    val apiService = RetrofitClient.gitHubApiService
    val call = apiService.getAuthenticatedUserRepositories()
    
    call.enqueue(object : Callback<List<Repo>> {
        override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
            if (response.isSuccessful) {
                val repositories = response.body()
                reposAdapter.updateRepositories(repositories ?: emptyList())
            } else {
                // Manejar error
            }
        }
        
        override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
            // Manejar fallo de conexión
        }
    })
}
```

**¿Qué hace este código?**

1. **Obtiene el servicio**: `RetrofitClient.gitHubApiService`
2. **Crea la llamada**: `getAuthenticatedUserRepositories()`
3. **Ejecuta de forma asíncrona**: `call.enqueue()` (no bloquea la UI)
4. **Maneja la respuesta**:
   - Si es exitosa (`response.isSuccessful`): actualiza el adaptador con los datos
   - Si falla: muestra un mensaje de error al usuario

**Manejo de errores**:
- **401**: Token inválido o faltante
- **403**: Token sin permisos suficientes
- **404**: Endpoint no encontrado
- Otros: Muestra el código de error

---

## 5. Configuración de Variables de Entorno

### ¿Por qué usar variables de entorno?
Los tokens de acceso son información sensible que NO debe subirse al repositorio. Las variables de entorno nos permiten mantenerlos seguros y fuera del código.

### Archivos involucrados:

#### `.env`
```
GITHUB_API_TOKEN=ghp_tu_token_aqui
```

**Propósito**: Almacena el token de GitHub de forma local (no se sube al repositorio).

**Cómo obtener un token**:
1. Ve a https://github.com/settings/tokens
2. Click en "Generate new token (classic)"
3. Selecciona permisos: `repo` y `user`
4. Copia el token generado
5. Pégalo en el archivo `.env`

#### `app/build.gradle.kts`
```kotlin
val envFile = rootProject.file(".env")
val githubToken = if (envFile.exists()) {
    envFile.readLines()
        .firstOrNull { it.startsWith("GITHUB_API_TOKEN=") }
        ?.substringAfter("GITHUB_API_TOKEN=")
        ?.trim()
        ?: ""
} else {
    ""
}

buildConfigField("String", "GITHUB_API_TOKEN", "\"$githubToken\"")
```

**¿Qué hace?**
1. Lee el archivo `.env`
2. Busca la línea que empieza con `GITHUB_API_TOKEN=`
3. Extrae el valor del token
4. Lo guarda en `BuildConfig.GITHUB_API_TOKEN` para usarlo en el código

#### `.gitignore`
```
.env
```

**Propósito**: Asegura que el archivo `.env` (con tu token) nunca se suba al repositorio.

#### `.env.example`
```
GITHUB_API_TOKEN=tu_token_aqui
```

**Propósito**: Plantilla para otros desarrolladores. Este archivo SÍ se sube al repo como referencia.

---

## 6. Permisos y Dependencias

### `AndroidManifest.xml`
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

**Por qué**: Sin estos permisos, la app no puede hacer peticiones HTTP.

### `app/build.gradle.kts` - Dependencias nuevas
```kotlin
// Retrofit para networking
implementation("com.squareup.retrofit2:retrofit:2.9.0")
implementation("com.squareup.retrofit2:converter-gson:2.9.0")

// Logging de peticiones HTTP
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

// Glide para cargar imágenes
implementation("com.github.bumptech.glide:glide:4.16.0")
```

**Para qué sirve cada una**:
- **Retrofit**: Librería para hacer peticiones HTTP de forma simple
- **Gson Converter**: Convierte JSON a objetos Kotlin automáticamente
- **Logging Interceptor**: Muestra información de las peticiones en Logcat
- **Glide**: Carga y almacena en caché imágenes desde URLs

---

## 7. Flujo Completo de la Aplicación

### Cuando la app se inicia:

1. **MainActivity.onCreate()**
   - Se infla el layout con ViewBinding
   - Se configura el RecyclerView con el adaptador
   - Se llama a `fetchRepositories()`

2. **fetchRepositories()**
   - Obtiene el servicio de `RetrofitClient`
   - Crea la llamada a `/user/repos`
   - El interceptor agrega automáticamente el token

3. **Petición HTTP**
   - Retrofit hace la petición GET a `https://api.github.com/user/repos`
   - Header: `Authorization: token TU_TOKEN`
   - La API valida el token y devuelve los repositorios

4. **Respuesta exitosa (onResponse)**
   - Se parsea el JSON a objetos `Repo`
   - Se actualiza el adaptador con `updateRepositories()`
   - El RecyclerView muestra los repositorios

5. **Para cada ítem del RecyclerView**
   - `RepoViewHolder.bind()` recibe un objeto `Repo`
   - Establece nombre, descripción, lenguaje
   - Glide descarga y muestra la imagen del avatar

### En caso de error:
- Se muestra un Toast con el mensaje de error
- Se hace log del error en Logcat

---

## Conceptos Clave para Entender

### 1. **Retrofit**
- Librería que simplifica las peticiones HTTP
- Usa interfaces con anotaciones (`@GET`, `@POST`, etc.)
- Convierte automáticamente JSON ↔ Objetos

### 2. **Callback asíncrono**
- `call.enqueue()` ejecuta la petición en segundo plano
- No bloquea la interfaz de usuario
- Llama a `onResponse()` cuando termina o a `onFailure()` si falla

### 3. **ViewBinding**
- Forma segura de acceder a las vistas del XML
- Evita `findViewById()` y posibles errores
- Se genera automáticamente desde el XML

### 4. **RecyclerView + Adapter**
- RecyclerView: Vista eficiente para listas largas
- Adapter: Conecta los datos con las vistas
- ViewHolder: Reutiliza las vistas para mejor rendimiento

### 5. **API REST**
- Forma estándar de comunicación entre apps y servidores
- Usa HTTP (GET, POST, PUT, DELETE)
- Intercambia datos en formato JSON

---

## Resumen de lo que Logramos

✅ **Conexión real con la API de GitHub**  
✅ **Autenticación segura con token**  
✅ **Mostrar repositorios del usuario autenticado**  
✅ **Cargar imágenes de avatares desde URLs**  
✅ **Manejo de errores y mensajes al usuario**  
✅ **Código organizado y bien estructurado**  
✅ **Variables de entorno seguras**  

---

## Próximos Pasos (Opcional)

- Agregar pull-to-refresh para actualizar la lista
- Implementar búsqueda de repositorios
- Agregar navegación a los detalles de cada repositorio
- Implementar paginación para muchos repositorios
- Agregar animaciones a las transiciones

---

## Troubleshooting Común

**Error 401**: Token inválido
- Verifica que el token esté correctamente copiado en `.env`
- Asegúrate de que el token no haya expirado

**No se muestran imágenes**: 
- Verifica el permiso de INTERNET en el manifest
- Revisa Logcat para ver errores de Glide

**No se cargan repositorios**:
- Verifica tu conexión a internet
- Revisa Logcat para ver el error específico
- Asegúrate de que el token tenga permisos `repo` y `user`

**BuildConfig.GITHUB_API_TOKEN vacío**:
- Limpia y recompila el proyecto: `./gradlew clean build`
- Verifica que el archivo `.env` esté en la raíz del proyecto

---

**¡Felicidades!** Ahora tienes una aplicación Android completamente funcional que consume una API REST real. 🎉
