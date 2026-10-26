# Práctica 02: Análisis comparativo de modelos de crecimiento poblacional

## Introducción

Esta práctica estudia la evolución de una población mediante cuatro enfoques de simulación: determinístico, discreto, estocástico y continuo. Los resultados de cada enfoque se comparan con una serie de datos observados para examinar sus diferencias y evaluar su capacidad de ajuste.

La serie utilizada como referencia es:

```python
[1000, 1100, 1250, 1400, 1600, 1850, 2100]
```

El ejercicio permite observar cómo influyen la forma de representar el tiempo, la aleatoriedad y los parámetros de crecimiento en las proyecciones obtenidas.

## Modelos implementados

| Enfoque | Representación | Descripción |
|---|---|---|
| Determinístico | \(P(t)=P_0e^{rt}\) | Calcula la evolución sin incorporar variaciones aleatorias. |
| Discreto | \(P(t+1)=P(t)+rP(t)\) | Actualiza la población en períodos sucesivos. |
| Estocástico | \(P(t+1)=P(t)+rP(t)+\varepsilon(t)\) | Incorpora una perturbación aleatoria con distribución normal. |
| Continuo | \(\frac{dP}{dt}=rP\) | Representa el crecimiento continuo y utiliza el método de Euler para aproximarlo numéricamente. |

En el modelo estocástico, el término aleatorio se representa mediante una variable normal con desviación estándar `σ`. Si una actualización produce una población negativa, el resultado se limita a cero.

## Parámetros de entrada

Los parámetros se obtienen a partir de la serie observada y de las operaciones realizadas por el programa.

| Parámetro | Valor registrado | Interpretación |
|---|---:|---|
| `P0` | 1000 | Población inicial. |
| `r` | 0.1318 | Tasa de crecimiento promedio por período. |
| `σ` | 24.50 | Desviación estándar estimada a partir de los residuos del modelo discreto. |
| Períodos | 6 | Número de intervalos entre las siete observaciones. |

La implementación también permite explorar distintos valores de la tasa de crecimiento, la variabilidad aleatoria y el tamaño del paso numérico.

## Entorno de desarrollo

La práctica utiliza las siguientes herramientas:

- **Python 3.12:** desarrollo de los modelos.
- **NumPy 1.26:** cálculos numéricos y generación de valores aleatorios.
- **Matplotlib 3.9:** representación gráfica de los resultados.
- **unittest:** ejecución de pruebas unitarias.

## Estructura de la implementación

El código está organizado mediante el patrón Modelo-Vista-Controlador (MVC).

| Archivo | Función |
|---|---|
| `model.py` | Implementa los modelos, estima parámetros y calcula errores. |
| `controller.py` | Gestiona los datos iniciales y coordina las simulaciones. |
| `view.py` | Presenta los resultados y construye las gráficas. |
| `main.py` | Ejecuta el programa y permite introducir datos mediante argumentos. |
| `test_model.py` | Contiene pruebas unitarias para comprobar las fórmulas. |

Los archivos de salida gráfica se guardan junto al código de la práctica. El directorio `reporte/` reúne los recursos del informe técnico y `Reporte_Practica_02.pdf` contiene el documento final.

## Preparación y ejecución

Desde la raíz del repositorio, crea y activa el entorno virtual e instala las dependencias:

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

Ejecuta la simulación con los datos originales:

```bash
cd Unidad1/practica02
python3 main.py
```

También puedes proporcionar otra serie de poblaciones:

```bash
python3 main.py 2100 1900 1750 1600 1400 1300 1150
```

Para ejecutar las pruebas unitarias:

```bash
python3 -m unittest test_model.py
```

La serie de entrada debe contener al menos dos valores y todas las poblaciones deben ser mayores que cero.

## Visualización de resultados

La ejecución genera cuatro gráficas orientadas a analizar diferentes aspectos de la simulación.

| Archivo | Análisis |
|---|---|
| `comparacion_modelos.png` | Contrasta las proyecciones de los cuatro enfoques con las observaciones. |
| `influencia_r.png` | Examina escenarios de crecimiento, equilibrio y decrecimiento. |
| `efecto_sigma.png` | Compara corridas estocásticas con distintos niveles de variabilidad. |
| `efecto_dt.png` | Estudia el efecto del tamaño del paso de Euler frente a la solución exacta. |

## Resultados principales

Para los datos y parámetros registrados, la comparación al sexto período produce los siguientes valores:

| Modelo | Población final | MAE | RMSE |
|---|---:|---:|---:|
| Observada | 2100 | — | — |
| Determinístico | 2204.7 | 65.49 | 73.81 |
| Discreto | 2101.6 | 23.07 | 29.61 |
| Estocástico | 2043.8 | 33.83 | 40.47 |
| Continuo (Euler) | 2193.4 | 60.85 | 68.22 |

El MAE y el RMSE permiten medir la diferencia entre los resultados calculados y los datos observados; en ambos casos, un valor menor indica un ajuste más cercano.

En esta ejecución, el modelo discreto presenta el menor error. Esto es coherente con la estimación de la tasa `r` a partir de cambios relativos entre períodos consecutivos. Por su parte, el modelo determinístico y el continuo producen estimaciones finales superiores a la población observada.

La simulación estocástica permite estudiar la variabilidad entre ejecuciones, mientras que la prueba con diferentes valores de `dt` muestra cómo cambia la aproximación de Euler al modificar el paso de integración.

## Alcance y observaciones

- Los resultados estocásticos dependen de la semilla aleatoria configurada en `controller.py`, lo que permite reproducir una ejecución cuando se mantiene la misma configuración.
- Los parámetros y escenarios pueden modificarse desde los puntos de configuración indicados en el código.
- La guía contempla una visualización adicional mediante NetLogo y `pyNetLogo`; esa integración requiere disponer de NetLogo y no forma parte de la implementación incluida.

El análisis completo y las preguntas de control se encuentran en `Reporte_Practica_02.pdf`.