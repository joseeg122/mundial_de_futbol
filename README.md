# Mundial de Fútbol en Consola

Taller **Arreglos y Matrices** – Lógica de programación (Java).

## Equipo
- Integrantes:
- Jeronimo Arcila Ceballos
- Emmanuel Rios Molina
- Jose Estrada
- 
## Banderas implementadas:
- Croacia, Qatar, Uruguay, Senegal, Nueva Zelanda, Australia y Países Bajos

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
| `BanderasBase.java` | Las 7 banderas en pixel art (matriz 18x24) |
| `Bandera.java` | Algoritmo de escalado (4 tamaños), dibujo en consola y lectura/escritura del CSV |
| `TablaPosiciones.java` | Matriz 48 equipos x (PJ, PG, PE, PP, GF, GC, DG, TA, TR, Pts), editable y paginada |
| `Fixture.java` | Matriz de partidos: consulta por grupo y detalle de un partido |
| `ConsoleColors.java` / `ConsoleInput.java` | Colores ANSI y lectura por teclado (helpers) |
| `recursos/info_banderas.csv` | Banderas (nombre;filas;columnas + una fila de dígitos por línea) |
| `recursos/equipos.txt` | Los 48 equipos y su grupo |
| `recursos/partidos.txt` | Calendario de los 72 partidos de fase de grupos y resultados |
| `recursos/tabla_posiciones.txt` | Estado guardado de la tabla |

## Pasos del taller

**Paso 1 – Matrix 2 Console.** Cada bandera es una `int[][]` de 18x24 con los códigos de color del enunciado
(1 rojo, 2 azul, 3 blanco, 4 amarillo, 5 verde, 6 morado, 7 cyan, 8 negro, 9 gris; 0 transparente).
`Bandera.escalar(base, factor)` recibe el factor de escala, divide la matriz en bloques de `factor x factor`
y deja en cada celda el color dominante del bloque:

| Tamaño | Factor | Resolución |
|---|---|---|
| Grande | 1 | 18 x 24 |
| Mediano | 2 | 9 x 12 |
| Pequeño | 3 | 6 x 8 |
| Ícono | 6 | 3 x 4 |

Colores sustitutos: el granate de Qatar se dibuja en rojo; el escudo de Croacia y el Sol de Mayo de Uruguay son aproximaciones.

**Paso 2 – Tabla de posiciones.** Matriz `int[48][10]`. Se puede editar (PG, PE, PP, GF, GC, TA, TR) y PJ, DG y Pts se recalculan solos.
Se imprime formateada y **paginada** (12 equipos por página), por grupos o por puntos.

**Paso 3 – Fixture.** Matriz `String[72][8]`. Con el menú por teclado (`ConsoleInput`) se ven los partidos por grupo
y la hora e integrantes de un partido específico. También se puede registrar un resultado, que actualiza la tabla.

**Paso 4 – Colaboración global.** Todo se guarda en archivos planos (`recursos/`). La opción 8 del menú importa el CSV de banderas
de otro equipo. El menú usa ASCII art.

## Nota sobre datos
Los grupos y equipos corresponden al sorteo del Mundial 2026. Las fechas por jornada siguen el calendario oficial de cada grupo;
las **horas son de ejemplo** y se pueden editar directamente en `recursos/partidos.txt`.
