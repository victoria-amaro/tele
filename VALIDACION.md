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
