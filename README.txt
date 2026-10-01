ESCANER DE RED 
==============
¿COMO INICIAR LA APP?
Asegurate de tener instalado JAVA (version 8 o superior).
Ejecuta el archivo Main.java desde  tu entorno de trabajo.
Luego se abrira automaticamente la ventana orincipal de la aplicacion.

¿COMO UTILIZAR LA INTERFAZ?
Panel de Configuración: Ubicado en la parte superior. Contiene los campos de texto necesarios para especificar la dirección IP o el rango de red que deseas analizar.

BOTONES de CONTROL:
	1)Iniciar: Comienza el proceso de escaneo de red de forma asíncrona.
	
	2)Detener: Interrumpe un escaneo que se encuentre en curso.
	
	3)Limpiar: Borra la tabla de resultados actuales para iniciar una nueva consulta.	
	
TABLA DE RESULTADOS:
Ocupa el centro de la ventana. Muestra en tiempo real las IPs analizadas, su estado de conexión (activo/inactivo), y métricas adicionales como el tiempo de respuesta (latencia).

¿COMO INICIAR UN ESCANEO?
1)Ingresar direccion IP (por ejemplo, 192.168.1.1).
2)Si ingresaste una direccion IP con un formato incorrecto se notificara visualmente con  un error. En caso de que esto no ocurra se sigue al proximo paso.
3)Hacer click en INICIAR ESCANEO, El sistema comenzará a procesar las peticiones utilizando hilos concurrentes en segundo plano, lo que garantiza que la interfaz no se congele.
3)Ver los resultados, Observa cómo la tabla se actualiza dinámicamente conforme cada dispositivo responde o falla al intento de conexión.
4)Finalizar o cancelar el escaneo, Puedes esperar a que el escaneo finalice por completo o hacer clic en Detener en cualquier momento si deseas abortar la operación.

CONSEJOS
Red Local: Para obtener mejores resultados y latencias precisas, asegúrate de ejecutar la herramienta dentro de una red local o privada autorizada.

Permisos de Red: Dependiendo de la configuración de tu sistema operativo o cortafuegos (firewall), es posible que debas permitir que Java realice conexiones de red salientes.