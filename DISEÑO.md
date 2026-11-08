# Diseño aprobado y alcance de esta versión

Pantalla horizontal 16:9, beige cálido, oliva y dorado, detalle floral. Saludo «Bienvenida, Amanda». Reloj 24 h y fecha es-UY. Columna izquierda con cita bíblica, referencia y reflexión; derecha con presentación de fotos. Fila inferior fija: TV, YouTube, Disney+, Netflix, Prime Video, HBO Max. Sin Himnos. Íconos de las apps instaladas, sin recolorear. Foco de control remoto visible con relleno oliva y borde dorado. Márgenes de seguridad para TV.

Se adapta una superficie de diseño 1920×1080 al tamaño de pantalla manteniendo la proporción. Las citas largas se muestran con puntos suspensivos y OK abre el devocional completo.

Sin fotos, se muestra un paisaje vectorial neutral con instrucciones. El protector nativo del sistema se configura por separado para Google Fotos; no hay OAuth ni sincronización de esos álbumes dentro del recuadro. No se modifica el launcher ni se instala software en la tele desde este proyecto.

Criterios de aceptación: compilar APK; pasar pruebas de fecha/día Uruguay; pasar Android Lint sin errores; conservar los seis accesos aunque falten apps; rechazar fotos inválidas o mayores de 15 MB; ofrecer ruta manual si no existe selector o ajustes de protector. La prueba física con control remoto sigue en PRUEBAS-EN-TV.md.

Actualización 1.2: se agrega el botón secundario «Inicio y ajustes» y la capacidad de ser elegido como launcher HOME, sin cambiar automáticamente la aplicación predeterminada ni desactivar el launcher original. El acceso de YouTube prioriza SmartTube al instalarlo después y solicita confirmación cada vez que abre la app oficial.
