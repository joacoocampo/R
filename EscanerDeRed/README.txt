Hasta el dia de hoy creamos los botones, como tambien los campos.
Usamos un patron de texto que nos sirve para validar que un string tenga el formato exacto de una dirección IPv4.
Compenzamos a hacer el "app", con un borderLayout distribuimos las cosas visuales.
Luego con el JPanel construirPanelSuperior() buscamos hacer una grilla de 4 filas × 2 columnas, donde la columna izquierda queda fija y angosta, y la columna derecha se estira si agrandás la ventana.
Tambien hicimos los botonews, despues hacemos verificaciones de el IP.
Comenzamos ha configurar los botones.
Creamos el metodo controlador iniciarEscaneo(), este Antes de arrancar cualquier trabajo, hace una serie de validaciones, si algo falla, corta la ejecución con return y no continúa a la siguiente etapa
Luego pasamos los ids a numero para comparar y despues recorrerlas, verificando que la del inicio no sea mas que la del final
Si el rango es muy grande se advierte al usuario. Intenta convertir el texto de "tiempo de espera" a número entero. Si el usuario escribió algo que no es un número, o puso un número menor o igual a cero, muestra error y corta
Despues de que todos los datos son válidos, se resetea todo lo que quedó de un escaneo anterior: se vacía la tabla, se reinicia el contador de activos y la barra de progreso, y se cambia el estado de los botones.
