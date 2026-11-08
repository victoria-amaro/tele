# Amanda — pantalla tranquila para Android TV

App nativa en Kotlin para la Panavox de Amanda. Se instala como una app más y ahora también puede elegirse como launcher, si el sistema lo permite. No cambia por sí sola el inicio ni la configuración de la tele. No contiene publicidad, analítica, inicio de sesión ni permisos de red.

## Qué incluye

- «Bienvenida, Amanda», reloj de 24 horas y fecha en español de Uruguay.
- Cita bíblica Reina-Valera 1909 y reflexión breve. OK sobre el devocional abre el texto completo cuando la cita es larga.
- Seis accesos: TV, YouTube, Disney+, Netflix, Prime Video y HBO Max.
- Íconos reales obtenidos de las apps instaladas; el de TV es un símbolo de televisor.
- Navegación con flechas, OK y Atrás; borde dorado para identificar el foco.
- Mantené OK sobre un acceso para elegir qué app abre. Las seis posiciones se conservan aunque falte una app: al abrirla se explica cómo elegirla.
- Fotos familiares que cambian cada 15 segundos y vista completa sin recortar la imagen.
- Importación de imágenes si la tele dispone de selector de archivos. Copia hasta 40 fotos de 15 MB cada una al almacenamiento privado de Amanda. No necesita permiso para leer toda la galería.
- Acceso a los ajustes del protector de pantalla del sistema, cuando la tele ofrece esa pantalla.

## Subir a GitHub desde la PC

1. Descomprimí el ZIP. Creá un repositorio vacío llamado, por ejemplo, `devocional-tv`.
2. Subí **el contenido de esta carpeta**, no el ZIP ni una carpeta contenedora extra. En la raíz del repositorio deben quedar `settings.gradle.kts`, `build.gradle.kts` y las carpetas `app` y `.github`.
3. Windows puede ocultar `.github`: asegurate de incluirla. Si el navegador no la sube, creá el archivo desde GitHub con **Add file → Create new file**, nombre `.github/workflows/build.yml`, y copiá el contenido del archivo del mismo nombre.
4. Hacé commit en `main` o `master`.
5. Abrí **Actions → Compilar APK**. El proceso ejecuta pruebas unitarias, Android Lint y compilación; solo publica si esos pasos pasan.
6. Cuando termine en verde, abrí **Releases**, entrá en la versión más reciente y descargá `amanda-tv.apk`. También queda como artefacto `amanda-tv` en Actions.

Para un repositorio público, el enlace de descarga será:

`https://github.com/victoria-amaro/tele/releases/latest/download/amanda-tv.apk`

Un repositorio privado requiere autenticación para descargar; el enlace anterior no funciona como enlace público desde Downloader. No subas fotos familiares a un repositorio público: se incorporan al APK y también pueden quedar en el historial de Git. Para mantener las fotos privadas, importalas desde la tele o usá un repositorio privado con un método de instalación autenticado.

## Google Fotos y el protector que ya tiene la tele

La conexión de Google Fotos del **modo ambiente** pertenece al sistema. Esta app no puede reutilizarla para acceder a tu cuenta o a esos álbumes.

En Amanda: **OK sobre el recuadro de fotos → Google Fotos: ajustar protector de la tele**. Abre los ajustes de Daydream/protector si el fabricante los expone. Si no, muestra la ruta manual. En Google TV suele ser **Ajustes → Sistema → Modo ambiente → Google Fotos**; verificá la disponibilidad en tu Panavox.

Elegí tu álbum desde los ajustes oficiales de la tele. Amanda no mantiene la pantalla encendida, así que permite que el sistema active su protector por inactividad según su configuración. No cambia el tiempo de espera ni el álbum. Hay que probar en la Panavox que el protector se active con Amanda abierta.

**El recuadro de fotos de Amanda se alimenta por separado:**

- **En la tele:** OK sobre fotos → Agregar fotos a esta app. Solo funciona si hay un selector/proveedor de archivos compatible. No se presupone que Google Fotos esté instalado o aparezca en ese selector.
- **Desde la PC:** descargá las imágenes elegidas de Google Fotos y agregalas a `app/src/main/assets/photos/` como JPG, JPEG, PNG o WEBP; luego recompilá. Reducí antes las fotos a unos 1920 × 1080 para ahorrar espacio. No hay sincronización automática con el álbum.

Si no hay fotos, se muestra una tarjeta con instrucciones; la familia de la maqueta era ilustrativa y no está incluida en el APK. No se incorporó OAuth ni la API de Google Fotos: para fotos en espera se aprovecha la función nativa que ya tiene la tele.

Fuentes: [Google TV: protector de pantalla](https://support.google.com/googletv/answer/10070821?hl=es-419), [cambios de las API de Google Fotos](https://developers.google.com/photos/support/updates), [selector de archivos Android](https://developer.android.com/training/data-storage/shared/documents-files).

## YouTube y SmartTube

Si están instalados, el acceso de YouTube busca primero SmartTube estable, luego beta/F-Droid y finalmente YouTube oficial. Si antes estaba guardado YouTube oficial y luego instalás SmartTube, ahora se prioriza SmartTube automáticamente. Mantené OK para elegir otro reproductor. El acceso muestra el ícono real de la app que abre: con SmartTube aparecerá su ícono, no el logo de YouTube.

Amanda no instala SmartTube ni modifica YouTube. No bloquea anuncios de otra aplicación. Si solo está YouTube oficial, pide confirmación cada vez antes de abrirlo y explica que puede mostrar anuncios. Las demás plataformas siguen requiriendo sus cuentas/suscripciones y pueden mostrar los anuncios de sus propios planes.

SmartTube es un cliente no oficial: su reproducción y compatibilidad con esta Panavox todavía deben probarse. Si decidís instalarlo, usá únicamente sus [publicaciones oficiales](https://github.com/yuliskov/SmartTube/releases), comprobá los avisos actuales del mantenedor y elegí después la app desde Amanda. No se incluye ningún APK de terceros en este proyecto.

Referencias: [código de SmartTube y nombres de paquete](https://github.com/yuliskov/SmartTube/blob/master/smarttubetv/build.gradle), [YouTube y bloqueadores](https://support.google.com/youtube/answer/14129599?hl=es).

## Instalación y reversión en la tele

- **Antes:** anotá las apps instaladas y tomá fotos de los ajustes actuales. Eso es un registro de configuración, no una copia completa del sistema.
- **Cambio:** instalar Amanda agrega una app. No sustituye Inicio sin que lo elijas. Instalarla no requiere root, ADB ni USB; cambiar el launcher en ciertos modelos puede requerir diagnóstico por ADB.
- **Riesgo:** es una compilación de prueba; falta comprobarla en la Panavox. No cambies el launcher para probarla.
- **Instalar:** transferí/descargá `amanda-tv.apk` por Wi-Fi. Si usás Downloader, habilitá temporalmente «Instalar apps desconocidas» solo para esa app e inhabilitalo después.
- **Comprobar:** abrí Amanda y completá `PRUEBAS-EN-TV.md`.
- **Revertir:** Ajustes → Aplicaciones → Amanda → Desinstalar. Borra sus fotos importadas y preferencias; conserva los originales y las otras apps. No hace falta restablecer la tele.
- **Protector:** si cambiás el álbum o el tiempo de espera desde ajustes del sistema, anotá el valor anterior y restauralo allí. Desinstalar Amanda no revierte esos cambios que vos hayas hecho en el sistema.

## Contenido

`app/src/main/assets/devocional.json` contiene 10 entradas, que se repiten cada 10 días. Las citas se cotejaron con RV 1909; las reflexiones son textos originales. Se conservan las oraciones del archivo original como datos, pero no se muestran en este diseño. No son 365 devocionales. Fuentes por versículo en `FUENTES.md`.

## Compilar en una PC con herramientas Android

Java 17, SDK Android 34 y Gradle 8.7:

```powershell
gradle testDebugUnitTest lintDebug assembleDebug --no-daemon
```

No se incluye Gradle Wrapper: Actions instala la versión indicada. Android Studio puede importar el proyecto usando Gradle 8.7.

## Firma y futuras actualizaciones

El APK es **debug**, apropiado para esta prueba doméstica. Cada entorno nuevo puede generar una firma debug distinta. Android no permite actualizar una instalación con otra firma: sin una clave estable, puede ser necesario desinstalar Amanda primero, perdiendo sus fotos importadas y preferencias. Conservá el APK anterior para revertir la versión.

Para que las compilaciones futuras de Actions usen la misma clave, guardá el contenido Base64 de tu `debug.keystore` como secreto del repositorio **DEBUG_KEYSTORE_BASE64**. No subas el archivo de clave al repositorio ni pegues su contenido en chats. Si no configurás este secreto, las compilaciones igualmente funcionan, con la limitación anterior. Para distribuir públicamente debe prepararse una firma de producción.

Se usa AndroidX ExifInterface 1.4.2 para leer la orientación de las fotos, siguiendo la recomendación de Android Lint: https://developer.android.com/jetpack/androidx/releases/exifinterface .


## Amanda 1.2: inicio de la tele y lentitud

Esta versión registra Amanda como aplicación HOME y agrega **Inicio y ajustes → Elegir pantalla de inicio**. Si Android ofrece su selector, elegí Amanda. Probá tanto el botón Inicio del control como un reinicio completo. La compatibilidad al encender o salir de espera depende del fabricante; no se garantiza en la Panavox hasta probarlo.

**Preferencias del dispositivo → Pantalla de inicio** con opciones como «Personalizar canales», «Vista previa de videos», «Reorganizar apps» y «Android TV Home» es la personalización del launcher original, no un selector de launcher predeterminado. Abrir «Android TV Home» ahí puede mostrar información y licencias.

Si no existe el selector de inicio, no desactives Android TV Home ni Servicios principales de Android TV. Primero hay que comprobar si la tele ofrece depuración inalámbrica y obtener la información del launcher actual. No se ejecutan scripts para quitar paquetes, el regreso mediante accesibilidad de la versión 1.3 es opcional y requiere habilitación explícita y no se agregan servicios de arranque que Android pueda bloquear.

**Reversión del inicio:** desde el selector del sistema volvé a elegir Android TV Home o Google TV. Si se usó otro método, registrá antes el componente anterior y conservá su procedimiento de reversión. Esta versión no desactiva paquetes.

**Lentitud:** antes de quitar apps, anotá el espacio libre y observá si la lentitud ocurre en toda la tele o solo dentro de una app. Desactivar las vistas previas de video/audio reduce la reproducción automática del launcher, pero no garantiza resolver el problema. Reiniciá desde Ajustes → Sistema → Reiniciar y comprobá si mejora. Revisá las apps instaladas y quitá solo las apps de usuario que realmente no se usan, conservando sus datos importantes. No borres datos de cuentas ni componentes del sistema. No se incluye una lista genérica de paquetes a desactivar: falta inventariar esta Panavox.

**Firma:** se configuró `DEBUG_KEYSTORE_BASE64` como secreto cifrado del repositorio con autorización de su propietaria. Las versiones nuevas usan una firma de prueba estable. La primera versión publicada usó una firma temporal distinta: actualizar esa instalación puede fallar. Antes de desinstalar, conservá los originales de las fotos importadas y anotá los accesos elegidos; desinstalar borra esas copias y preferencias.

Referencias: [categoría HOME de Android](https://developer.android.com/reference/android/content/Intent#CATEGORY_HOME), [ajustes de inicio](https://developer.android.com/reference/android/provider/Settings#ACTION_HOME_SETTINGS), [diagnóstico de lentitud de Google TV](https://support.google.com/googletv/answer/12364830?hl=es).


## Amanda 1.3: ajustes visibles y dos inicios

- **⚙ Ajustes** abre directamente la configuración general de la tele. Mantené OK sobre este botón para abrir las opciones de Amanda (tipo de inicio, apps, reproductor y protector).
- **Inicio Google TV** abre el launcher original. Requiere que Android TV Home esté habilitado; si está desactivado, muestra una explicación y abre su ficha para habilitarlo. La app no puede habilitar por sí sola un paquete del sistema.
- Mantené OK sobre Inicio Google TV para elegir el **Tipo de inicio**: Amanda automática o Google TV habitual.
- La navegación con flechas une los dos botones con el devocional y las fotos.

**Si Amanda es el único inicio habilitado, no la desinstales antes de habilitar Android TV Home.** Actualizar un APK con la misma firma conserva los datos. La versión 1.3 usa la firma estable ya configurada.

### Regreso automático opcional

En esta Panavox, Android TV Home tiene prioridad mayor que Amanda. Para mantener los dos habilitados y seguir mostrando Amanda al encender, se incluye el servicio opcional **Regreso a Amanda**. No se habilita automáticamente al instalar la app.

Primero explicá su funcionamiento y pedí autorización. Luego, en Tipo de inicio → Configurar regreso automático a Amanda, se abre Ajustes → Accesibilidad. El sistema muestra su aviso general y el usuario decide si habilitar el servicio. Una app instalada por fuera de Play Store puede requerir habilitar ajustes restringidos en su ficha; no se presupone que el fabricante ofrezca esa opción.

La configuración limita los eventos a cambios de ventana de `com.google.android.tvlauncher`. El código solo compara paquete y nombre de la actividad principal: no recupera el árbol de pantalla, no lee textos, fotos ni contraseñas, no filtra teclas, no pulsa controles y no usa internet. Se declara expresamente que no es una herramienta de accesibilidad para discapacidades.

Al detectar la pantalla principal de Google TV en modo automático abre Amanda. Al pulsar Inicio Google TV desde Amanda, deja visible el inicio normal hasta que abras Amanda otra vez. No intercepta Netflix, SmartTube, Ajustes ni el protector. Puede verse brevemente la pantalla original antes del regreso. Hay que probar este mecanismo en la Panavox antes de afirmar que funciona al encender.

Para revertir: elegir Google TV como inicio habitual, mantener Android TV Home habilitado y desactivar Regreso a Amanda en Accesibilidad. También podés dejar el servicio desactivado permanentemente y usar Amanda como una app normal.

### Fotos del protector y fotos de Amanda

Son dos presentaciones diferentes. Los álbumes elegidos para el protector de Google no se importan al recuadro de Amanda. Esta versión permite abrir los ajustes del protector y no mantiene la pantalla encendida por la fuerza. Se verificó en esta Panavox que el protector está habilitado, usa com.google.android.backdrop/.Backdrop y tiene un tiempo de espera de cinco minutos. No se cambió ni se borró el álbum.

Referencias técnicas: [servicios de accesibilidad](https://developer.android.com/guide/topics/ui/accessibility/service), [modo ambiente y pantalla encendida](https://developer.android.com/training/tv/playback/ambient-mode).


### Google Fotos privado: acceso nativo verificado en la Panavox

La usuaria eligió conservar privado su álbum y rechazó compartirlo mediante enlace. No se creó una conexión web, no se agregó permiso INTERNET y no se copiaron fotos de Google al recuadro.

Sin fotos locales cargadas, el recuadro muestra **Google Fotos · Álbum privado / OK para ver a pantalla completa**. Al pulsar OK envía la acción `com.google.android.pano.action.SLEEP` al paquete de ajustes de la tele. Esa actividad es pública/exportada en esta Panavox y abre el protector nativo ya configurado. Las imágenes permanecen bajo control del sistema de Google; Amanda no recibe sus archivos ni sus credenciales. Es una presentación a pantalla completa, no fotos incrustadas dentro del recuadro.

Para configurar el protector se usa la acción observada `com.google.android.tv.settings.ambient`, con alternativa a `android.settings.DREAM_SETTINGS` y después los ajustes generales. Esta Panavox no resuelve los accesos estándar DREAM_SETTINGS ni DISPLAY_SETTINGS, por eso se agregó el acceso específico comprobado. En otros modelos puede variar.

Mantener OK sobre el recuadro conserva las opciones para fotos locales y configuración del protector. Al usar Google Fotos privado, no hay necesidad de añadir fotos al repositorio.

