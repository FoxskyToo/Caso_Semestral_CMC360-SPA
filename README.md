RackInspector 
RackInspector es una aplicación móvil nativa para Android desarrollada en Kotlin, diseñada para la inspección, levantamiento y registro en terreno de la infraestructura tecnológica ubicada en gabinetes/racks de Data Centers.

1. Características Principales
Autenticación y Registro de Usuarios: Registro e inicio de sesión local con persistencia de credenciales e integración en base de datos.

Captura de Evidencia Fotográfica: Integración con la cámara nativa mediante FileProvider y selección de imágenes desde la galería mediante Glide.

Declaración e Inspección de Componentes: Registro detallado de componentes por categoría (Switch, Servidor, Patch Panel, UPS, Router, PDU) y cantidades.

Gestión de Componentes (CRUD Completo):

Agregar nuevos componentes tanto al momento de declarar como desde el detalle de un levantamiento ya guardado.

Editar categoría y cantidad de componentes en tiempo real.

Eliminar componentes individuales.

Historial y Detalle:

Lista de levantamientos previos guardados con vista en miniatura y metadatos (fecha, hora, ubicación y usuario).

Pantalla de detalle interactiva con refresco automático de datos.

Eliminación permanente de un levantamiento completo desde su vista de detalle.

Ajustes: Configuración de preferencias como el interruptor para Modo Oscuro (AppCompatDelegate) e información general.

2. Tecnologías y Librerías
Lenguaje: Kotlin

Target SDK: Android 14 (API 34) | Min SDK: Android 8.0 (API 26)

Base de Datos Local: Room Database 2.6.1

Serialización de Datos: Gson (TypeConverters para listas en Room)

Carga de Imágenes: Glide 4.16.0

Diseño e Interfaz: Material Design Components, ViewBinding, ConstraintLayout, RecyclerView y CardView

Concurrencia: Kotlin Coroutines & lifecycleScope

3. Estructura del Proyecto
Plaintext
app/src/main/java/com/duoc/rackinspector/
├── adapters/
│   ├── ComponentAdapter.kt   # Adaptador para lista de componentes con callbacks de edición/borrado
│   └── HistoryAdapter.kt     # Adaptador para historial de levantamientos
├── data/
│   ├── local/
│   │   ├── AppDatabase.kt    # Instancia de Room DB
│   │   ├── AssessmentDao.kt  # Métodos de acceso a datos (Insert, Query, Update, Delete)
│   │   ├── UserDao.kt        # Consultas de usuario y autenticación
│   │   └── Converters.kt     # Convertidor JSON para la lista de componentes
│   └── model/
│       ├── RackAssessment.kt # Entidad de Room para levantamientos
│       ├── ComponentItem.kt  # Modelo para ítems/equipos declarados
│       └── User.kt           # Entidad para usuarios del sistema
├── ui/
│   ├── auth/                 # LoginActivity & RegisterActivity
│   ├── capture/              # CaptureActivity (Cámara / Galería)
│   ├── declaration/          # DeclarationActivity & ConfirmationActivity
│   ├── history/              # HistoryActivity & DetailActivity
│   └── settings/             # SettingsActivity (Ajustes y Modo Oscuro)
└── MainActivity.kt           # Menú principal y navegación
4. Requisitos e Instalación
Prerrequisitos
Android Studio: Jellyfish / Koala o superior.

JDK: Java 11 o Java 17.

Dispositivo físico o emulador con Android 8.0 (API 26) o superior.

Pasos para ejecutar
Clonar el repositorio:

Bash
git clone https://github.com/FoxskyToo/Caso_Semestral_CMC360-SPA.git
Abrir en Android Studio: Selecciona la opción Open y navega hasta la carpeta del proyecto.

Sincronizar Gradle: Presiona el botón Sync Project with Gradle Files.

Ejecutar la aplicación: Selecciona tu dispositivo o emulador y haz clic en Run 'app' ▶️.
