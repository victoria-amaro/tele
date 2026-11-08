# Validación de esta entrega

Fecha: 8 de noviembre de 2026.

- Java 17.0.15, Gradle 8.7, Android Gradle Plugin 8.5.2, Kotlin 1.9.24, SDK 34.
- Comando: `gradle testDebugUnitTest lintDebug assembleDebug --no-daemon`.
- Resultado final: BUILD SUCCESSFUL.
- Cuatro pruebas unitarias aprobadas: medianoche en Uruguay, fecha en español, continuidad al cambiar el año y rechazo de contenido vacío.
- Prueba de sensibilidad: cambiar temporalmente la zona a UTC hizo fallar la prueba de medianoche. Se restauró America/Montevideo y pasó nuevamente.
- Android Lint: sin errores. Conserva advertencias de compatibilidad, internacionalización de una interfaz intencionalmente española y componentes de TV; no se deshabilitaron reglas.
- Validación de JSON y XML: correcta.
- Firma debug del APK verificada con apksigner.
- No se ejecutó en emulador ni en la Panavox: no había emuladores locales configurados. Navegación, escala visual, apps del fabricante y activación del modo ambiente requieren la prueba física documentada.
- No se instaló ni modificó nada en la tele ni se publicó un repositorio.

## Actualización 1.2

- Compilación local, Android Lint y nueve pruebas unitarias: aprobados.
- Se agregó una prueba previa para la selección de SmartTube; falló antes de implementar la nueva política.
- Cinco pruebas nuevas cubren SmartTube instalado después de guardar YouTube oficial, elección de beta, app desinstalada, ausencia de alternativas y ausencia de reproductores.
- Registro HOME y acceso al selector de inicio añadidos; funcionamiento como launcher en la Panavox pendiente de prueba física.
- Firma estable configurada como secreto cifrado del repositorio, con autorización explícita. No se imprimió ni se incorporó al código la clave privada.

## Actualización 1.3

- Quince pruebas unitarias aprobadas: 4 de fecha, 5 de reproductor, 6 de la política de regreso entre inicios.
- Una prueba previa falló con la política sin implementar, y pasó al implementar la comparación limitada a la actividad principal de Android TV Home.
- Android Lint: sin errores; 14 advertencias de compatibilidad/internacionalización y componentes de interfaz.
- En la Panavox: tuerca alcanzada con flechas y OK, abre com.android.tv.settings/.MainSettings.
- Con autorización explícita se habilitó Regreso a Amanda. El sistema reporta capabilities=0, solo eventos TYPE_WINDOW_STATE_CHANGED y paquete com.google.android.tvlauncher; sin servicios caídos.
- Android TV Home quedó habilitado otra vez. Inicio vuelve a Amanda mediante el ayudante; el botón Google TV deja el launcher original visible hasta reabrir Amanda. Ambos recorridos comprobados por la actividad enfocada.
- Protector: componente com.google.android.backdrop/.Backdrop, habilitado, espera de 300000 ms. Se verificó el arranque de DreamActivity mediante la actividad exportada DaydreamVoiceAction del sistema. No se capturaron ni copiaron fotos privadas.
- La conexión automática privada dentro del recuadro no está implementada: se usa el protector a pantalla completa. La usuaria rechazó el álbum compartido por enlace.
- Falta verificar el reinicio completo con el nuevo ayudante; el reinicio anterior con launcher original desactivado sí estaba confirmado.
