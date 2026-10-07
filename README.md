# Mundial de Fútbol en Consola

Taller **Arreglos y Matrices** – Lógica de programación (Java).

## Equipo
- Integrantes:
- Jeronimo Arcila Ceballos
- Emmanuel Rios Molina
- Jose Estrada
- 
## Banderas implementadas:
- Las 48 banderas del estándar (ver `recursos/Flags.csv`)

## Cómo ejecutar
Desde la carpeta del proyecto (para que encuentre `recursos/`):

```bash
javac -encoding UTF-8 *.java
java Mundial
```

> Usa una consola con soporte de colores ANSI (terminal de VS Code/IntelliJ, Windows Terminal, Linux/macOS).

## Estructura

| Archivo | Qué hace |
|---|---|
| `Mundial.java` | Programa principal, menú con ASCII art y opciones |
| `Bandera.java` | Estándar de banderas: lee `Flags.csv` en una matriz `char[480][15]`, switch de 48 casos con la fila de cada país, dibujo en 4 tamaños |
| `TablaPosiciones.java` | Matriz 48 equipos x (PJ, PG, PE, PP, GF, GC, DG, TA, TR, Pts), editable y paginada |
| `Fixture.java` | Matriz de partidos: consulta por grupo y detalle de un partido |
| `ConsoleColors.java` / `ConsoleInput.java` | Colores ANSI y lectura por teclado (helpers) |
| `recursos/Flags.csv` | Las 48 banderas del estándar (480 filas x 15 columnas, 10 filas por bandera) |
| `recursos/Estándar para banderas.pdf` | Documento del estándar de banderas |
| `recursos/equipos.txt` | Los 48 equipos y su grupo |
| `recursos/partidos.txt` | Calendario de los 72 partidos de fase de grupos y resultados |
| `recursos/tabla_posiciones.txt` | Estado guardado de la tabla |

## Pasos del taller

**Paso 1 – Matrix 2 Console.** Las banderas siguen el *Estándar para banderas* (`recursos/Estándar para banderas.pdf`):
todas están en `recursos/Flags.csv`, una matriz de 480 filas x 15 columnas que `Bandera.cargarCSV` lee con
`BufferedReader` en un `char[480][15]`. Cada bandera ocupa 10 filas x 15 columnas (48 banderas) y cada celda es un
dígito de color (1 amarillo, 2 naranja, 3 rojo, 4 morado, 5 azul, 6 verde, 7 blanco, 8 negro, 9 café).
`Bandera.filaInicio(pais)` es el switch de 48 casos con la fila donde empieza cada país, y el dibujo usa dos ciclos
anidados (filas y columnas) pintando cada celda con su color. Para los 4 tamaños, cada celda reducida toma la celda
del centro del bloque que le corresponde en la bandera original:

| Tamaño | Resolución |
|---|---|
| Grande | 10 x 15 |
| Mediano | 6 x 9 |
| Pequeño | 4 x 6 |
| Ícono | 2 x 3 |

**Paso 2 – Tabla de posiciones.** Matriz `int[48][10]`. Se puede editar (PG, PE, PP, GF, GC, TA, TR) y PJ, DG y Pts se recalculan solos.
Se imprime formateada y **paginada** (12 equipos por página), por grupos o por puntos.

**Paso 3 – Fixture.** Matriz `String[72][8]`. Con el menú por teclado (`ConsoleInput`) se ven los partidos por grupo
y la hora e integrantes de un partido específico. También se puede registrar un resultado, que actualiza la tabla.

**Paso 4 – Colaboración global.** Todo se guarda en archivos planos (`recursos/`). La opción 8 del menú importa un `Flags.csv` de banderas
de otro equipo. El menú usa ASCII art.

## Nota sobre datos
Los grupos y equipos corresponden al sorteo del Mundial 2026. Las fechas por jornada siguen el calendario oficial de cada grupo;
las **horas son de ejemplo** y se pueden editar directamente en `recursos/partidos.txt`.
