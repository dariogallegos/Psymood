# Psymood

> Psymood es un proyecto que se centra en la extracción y almacenamiento de información del usuario con el fin de ayudar a futuros proyectos de predicción del estado de ánimo.

```
    VERSIONES MÍNIMAS
  -----------------------
  | Android >=  6.0     |
  -----------------------
```

## Instalación

### Requisitos
Está desarrollado con Android Studio para dispositivos con el mismo sistema operativo.  Para poder trabajar con este proyecto es necesario realizar una serie de instalaciones previas.

* [Java](https://www.java.com/es/download/)  
Es importante actualizarse a la última versión de Java (version 1.8). En entornos como _Eclipse_ se puede seleccionar la versión de Java que se desea utilizar, así que no debería haber ningún problema si necesitamos una versión inferior para otro proyecto.

* [Git](https://git-scm.com/book/es/v1/Empezando-Instalando-Git)  
Dependiendo de nuestro sistema operativo, la instalación seguirá diferentes pasos.

  La instalación en Windows incluye una consola de comandos y una interfaz gráfica. Existen otras interfaces como [GitKraken](https://www.gitkraken.com/) que también son válidas.


### Descarga del código fuente  

Para descargar el código de esta aplicación basta con introducir el siguiente comando en nuestra consola de comandos o interfaz gráfica de git

    git clone https://github.com/dariogallegos/Psymood.git

Se descargará un proyecto Android  listo para ser compilado.

## Ejecución en dispositivo

Debido a posibles incompatibilidades con la versión de *Gradle* que utilizan plataformas como *Cordova* o *Ionic* con respecto de las librerias que utilizamos, la ejecución en dispositivo debe realizarse desde el entorno de desarrollo de cada plataforma.
Para Android, utilizaremos Android Studio, abriendo desde ahí el archivo *build.gradle* en la carpeta raíz del proyecto de Android:

<CARPETA_DEL_PROYECTO>/build.gradle


## Repositorio

Se van a utilizar tres ramas principales, siguiendo la metodología [git-flow][git-flow]:


* develop 
  _Rama de desarrollo_.  
  En esta rama deben ir únicamente los desarrollos y cambios _estables_. La aplicación debe poder cargarse desde esta rama a un dispositivo y ser completamente funcional; para los cambios inestables o en desarrollo, deberán utilizarse cada una de las ramas que se abran para los diversos desarrollos y nuevos servicios (las ramas _feature_).

* release  
  _Rama de testing para la versión candidata_.  
  En esta rama debe estar el código de una nueva versión, con todos los cambios que fuere a incluir y que esté preparada para subir a _Google Play_.  
  Esta rama solo se actualizará desde la rama _dev_ o bien para corregir errores y bugs _localizados_. En el caso de que la versión que se esté probando no sea estable o presente numerosos errores de varios desarrollos, se deberá corregir desde la rama _dev_.  
  Siguiendo la metodología [git-flow][git-flow], esta rama se deberá crear única y exclusivamente cuando se tenga una versión candidata para subir a tienda y se utilizará para actualizar tanto las ramas _dev_ como _master_ cuando obtenga el visto bueno definitivo, momento en el cual se procederá a su eliminación. Puede haber varias ramas release al mismo tiempo.  
  La nomenclatura de esta rama deberá seguir la siguiente plantilla:  

        release/version-fecha

    _**Importante:** No se deben hacer desarrollos sobre la rama release. La rama release presenta la versión candidata después de haber sido testeada con el código que se enucentre en_ dev _y/o en las ramas de_ feature _. Si hubiera que hacer un desarrollo grande para la rama release, deberá devolverse esta a_ dev _o realizar ese desarrollo desde una rama feature._

* master  
  _Rama de producción_.  
  Esta rama se actualizará **_exclusivamente_** desde las ramas _reseale_. Contiene el código de tienda y servirá para poder depurar la aplicación en producción o para tener un acceso rápido al código estable.  
  También se deberá aplicar las correspondientes etiquetas de versión a cada uno de los commits de esta rama.
  acceso.

### Ramas de funcionalidades

A la hora de realizar un nuevo desarrollo para una nueva funcionalidad o un evolutivo, se deberá crear una nueva rama desde la rama _dev_.

Se seguirá la máxima _una rama por cada funcionalidad_. Esto quiere decir que en cada una de las ramas solo se deberá incluir las modificaciones de una única característica o servicio. Si, por ejemplo, tuviéramos una petición extensa con varias funcionalidades, se deberán crear tantas ramas por cada una de ellas. Véase [_A successful Git branching model_](http://nvie.com/posts/a-successful-git-branching-model/).


## Descarga el APK

Para poder instalarte la aplicación, basta con acceder al siguiente enlace de google Drive. Dentro, se encuentra el archivo Psymood.apk. Para poder instalarlo es necesario activar la opción **_descargar desde orígenes desconocidos_** en el dispositivo.  Ir [_Psymood apk_](https://drive.google.com/drive/u/0/folders/1alD8T4vmcb-NbdRxcZJtmw0qEl9qhqjz).
