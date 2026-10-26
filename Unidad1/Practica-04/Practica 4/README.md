# Simulación SIR

Implementación en Java de una simulación epidemiológica SIR (Susceptibles-Infectados-Recuperados) basada en agentes, con exportación de resultados a CSV y un modelo equivalente en NetLogo para comparar resultados.

## Características

- Simulación discreta por pasos de tiempo sobre una población de individuos.
- Cada individuo tiene un estado: `SUSCEPTIBLE`, `INFECTADO` o `RECUPERADO`.
- Actualización síncrona: los cambios de un paso se calculan a partir del estado del paso anterior.
- Generador aleatorio con semilla configurable, lo que permite reproducir exactamente una ejecución.
- Validaciones de consistencia de la población, transiciones de estado y monotonicidad de S y R.
- Exportación del historial completo a un archivo CSV.
- Resumen por consola con estado final, pico de infectados y tiempo de cálculo.
- Modelo NetLogo equivalente en `netlogo/modelo_sir_netlogo.nlogox.nlogox`.

## Requisitos

- Java 17 o superior.
- Maven 3.8 o superior (solo para compilar y empaquetar).
- NetLogo 7 (opcional, para abrir el modelo).

## Estructura del proyecto

```
.
├── pom.xml
├── src/main/java/sir/
│   ├── Main.java                      # Punto de entrada y argumentos de línea de comandos
│   ├── controller/
│   │   ├── SimulacionController.java  # Orquesta ejecución y exportación
│   │   └── ExportadorCSV.java         # Escritura del CSV
│   ├── model/
│   │   ├── EstadoSalud.java           # Estados S, I, R
│   │   ├── Individuo.java
│   │   ├── Poblacion.java             # Población inmutable y validaciones
│   │   ├── ParametrosSimulacion.java  # Parámetros y valores predeterminados
│   │   ├── RegistroPaso.java          # Conteo S/I/R en un instante
│   │   └── ResultadoSimulacion.java
│   ├── random/
│   │   ├── GeneradorAleatorio.java    # Interfaz del generador
│   │   └── GeneradorJava.java         # Adaptador sobre java.util.Random
│   └── simulation/
│       └── MotorSimulacionSIR.java    # Inicialización y evolución del modelo
├── netlogo/
│   ├── modelo_sir_netlogo.nlogox.nlogox
│   └── resultados_netlogo.csv
└── resultados/
    └── resultados_java.csv
```

## Compilación y ejecución

Compilar y generar el JAR:

```powershell
mvn clean package
```

Ejecutar con la salida predeterminada (`resultados/resultados_java.csv`):

```powershell
java -jar target/sir-simulacion.jar
```

Especificar otro archivo de salida:

```powershell
java -jar target/sir-simulacion.jar --salida resultados/mi_simulacion.csv
```

Mostrar la ayuda:

```powershell
java -jar target/sir-simulacion.jar --help
```

Notas:

- Si el archivo de salida ya existe, se sobrescribe.
- Se crean automáticamente las carpetas necesarias para la ruta indicada.
- `--help` no ejecuta la simulación ni crea archivos.

## Parámetros

Los parámetros se definen en código en `src/main/java/sir/model/ParametrosSimulacion.java`; si se modifican hay que recompilar.

| Parámetro | Constante | Valor predeterminado | Descripción |
| --- | --- | --- | --- |
| Población | `POBLACION` | `100` | Número total de individuos. |
| Infectados iniciales | `INFECTADOS_INICIALES` | `5` | Individuos infectados al inicio. |
| Beta | `BETA` | `0.30` | Probabilidad de contagio por contacto con un susceptible. |
| Gamma | `GAMMA` | `0.10` | Probabilidad de recuperación de un infectado en cada paso. |
| Tiempo de simulación | `TIEMPO_SIMULACION` | `100` | Pasos simulados (se generan T+1 registros). |
| Semilla | `SEMILLA` | `42` | Semilla del generador aleatorio. |

Validaciones aplicadas:

- `poblacion > 0`.
- `0 <= infectadosIniciales <= poblacion`.
- `beta` y `gamma` finitos en `[0, 1]`.
- `tiempoSimulacion >= 0`.

## Funcionamiento del modelo

1. Se crea la población y se eligen aleatoriamente los individuos infectados iniciales.
2. En cada paso, cada individuo infectado realiza un contacto con un individuo aleatorio.
   - Si el contacto es susceptible, se infecta con probabilidad `beta`.
   - El individuo infectado se recupera con probabilidad `gamma`.
3. Todos los cambios se calculan sobre una copia y se aplican de forma síncrona.
4. Se registra el conteo de S, I y R del paso y se continúa hasta `tiempoSimulacion`.
5. Al finalizar se valida el resultado y se exporta el historial.

La misma semilla produce la misma secuencia de resultados.

## Formato de salida CSV

El archivo generado tiene cabecera y una fila por instante:

```csv
tiempo,susceptibles,infectados,recuperados
0,95,5,0
1,94,5,1
...
```

| Columna | Descripción |
| --- | --- |
| `tiempo` | Paso de simulación (desde 0). |
| `susceptibles` | Cantidad de individuos susceptibles. |
| `infectados` | Cantidad de individuos infectados. |
| `recuperados` | Cantidad de individuos recuperados. |

En cada fila se cumple `susceptibles + infectados + recuperados = poblacion`.

## Resumen por consola

Al finalizar, el programa muestra:

- Parámetros utilizados.
- Número de pasos y de registros.
- Estado final S, I y R.
- Máximo de infectados y el primer instante en que ocurre.
- Tiempo de cálculo en nanosegundos.
- Ruta absoluta del CSV generado.

## Modelo NetLogo

El archivo `netlogo/modelo_sir_netlogo.nlogox.nlogox` contiene un modelo equivalente con los mismos parámetros (`beta = 0.30`, `gamma = 0.10`). Los resultados exportados por NetLogo se guardan en `netlogo/resultados_netlogo.csv` y los de Java en `resultados/resultados_java.csv`, lo que permite comparar ambas implementaciones instante a instante.

## Pruebas

El proyecto incluye JUnit 5 como dependencia de pruebas. Puedes ejecutar:

```powershell
mvn test
```
