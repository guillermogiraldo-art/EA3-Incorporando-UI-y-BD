# EA3. Incorporando UI y BD

Actividad de **Programación Orientada a Objetos II** (PREICA2602B020046)
Docente: Jorge Armando Julio
Autor: Guillermo Antonio Giraldo Carbonell
Institución Universitaria Digital de Antioquia

## Descripción
Aplicación de escritorio en Java (Swing + JDBC, sin frameworks externos) que permite ingresar
la URL, el usuario y la contraseña de una base de datos y probar la conexión. Muestra el resultado
en un `JTextArea`: verde si la conexión es exitosa y rojo si falla (`SQLException` o `ClassNotFoundException`).
La conexión se cierra con `try-with-resources`.

## Contenido
- `ConexionTester.java`: código fuente.
- `ConexionTester_codigo.csv`: código en formato CSV.
- `EA3_Incorporando_UI_y_BD_GiraldoGuillermo.pdf`: informe.
- `capturas/`: evidencia de ejecución.

## Cómo ejecutar
Se necesita el driver `sqlite-jdbc` (https://github.com/xerial/sqlite-jdbc), por ejemplo la versión 3.36.0.3,
en la misma carpeta que el código.

```
javac -encoding UTF-8 ConexionTester.java
java -cp ".;sqlite-jdbc-3.36.0.3.jar" ConexionTester
```
(En Linux o Mac, usa `:` en lugar de `;`.)
