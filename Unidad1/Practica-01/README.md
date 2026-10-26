# Práctica 01: Modelado matemático del comportamiento atmosférico

## Objetivo

Desarrollar y ejecutar un modelo matemático que permita analizar las condiciones atmosféricas durante un día y estimar la posibilidad de lluvia en diferentes horas. Para ello, se utilizan la humedad, la nubosidad y un factor asociado a la temperatura.

La práctica también permite estudiar cómo cambia la predicción al modificar la importancia relativa de cada variable.

## Fundamento del modelo

El cálculo se realiza mediante un índice ponderado:

\[
I = W_H H + W_N N + W_{TF} Tf
\]

Donde:

- `H`: humedad normalizada entre 0 y 1.
- `N`: nubosidad normalizada entre 0 y 1.
- `Tf`: factor de temperatura obtenido de la tabla definida en la guía.
- `W_H`, `W_N` y `W_TF`: pesos asignados a cada variable, cuya suma es 1.

El resultado se interpreta mediante cuatro categorías:

| Intervalo del índice | Predicción |
|---|---|
| `I < 0.40` | Sin lluvia |
| `0.40 ≤ I < 0.60` | Baja posibilidad |
| `0.60 ≤ I < 0.75` | Lluvia probable |
| `I ≥ 0.75` | Lluvia |

### Comparación de configuraciones

Se utilizan dos conjuntos de pesos para evaluar el efecto de cambiar los parámetros del modelo.

| Configuración | Humedad | Nubosidad | Temperatura |
|---|---:|---:|---:|
| Original | 0.5 | 0.3 | 0.2 |
| Modificada | 0.4 | 0.3 | 0.3 |

La segunda configuración reduce la influencia de la humedad y aumenta la de la temperatura. La fórmula y las variables permanecen iguales.

## Herramientas y dependencias

- **Python 3.12:** implementación del programa.
- **NumPy 1.26:** operaciones numéricas.
- **Matplotlib 3.9:** generación de visualizaciones.

## Organización del código

La solución utiliza una estructura basada en el patrón Modelo-Vista-Controlador (MVC).

| Archivo | Responsabilidad |
|---|---|
| `model.py` | Cálculo del índice, consulta del factor de temperatura y clasificación del resultado. |
| `controller.py` | Administración de los datos atmosféricos y coordinación del proceso. |
| `view.py` | Presentación de tablas y generación de gráficas. |
| `main.py` | Inicio y ejecución de la simulación. |

El directorio `reporte/` contiene los recursos del informe técnico, mientras que `Reporte_Practica_01.pdf` corresponde al reporte en formato PDF.

## Instalación y ejecución

Desde la raíz del repositorio, prepara el entorno de Python:

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r requirements.txt
```

Después, ejecuta la práctica:

```bash
cd Unidad1/practica01
python3 main.py
```

El programa presenta los resultados en consola y genera las siguientes visualizaciones:

| Gráfica | Contenido |
|---|---|
| `indice_lluvia.png` | Comparación de los índices original y modificado durante el día. |
| `factor_temperatura.png` | Relación entre la temperatura y el factor `Tf`. |
| `variables_entrada.png` | Evolución de la humedad, nubosidad y factor de temperatura. |
| `contribucion_variables.png` | Aporte de cada variable al índice calculado. |

## Resultados e interpretación

En los resultados registrados se observan índices elevados durante las horas de la tarde. Por ejemplo, a las 18:00 el modelo original obtiene un índice de `0.89`, frente a `0.86` con la configuración modificada; ambos resultados corresponden a la categoría de lluvia.

La comparación muestra que cambiar los pesos puede modificar el valor numérico del índice sin necesariamente alterar su clasificación. En este caso, la predicción depende de la combinación de las condiciones atmosféricas y de los parámetros seleccionados.

## Consideraciones

- Para temperaturas que no aparecen directamente en la tabla, se utiliza el siguiente valor de temperatura igual o superior, según la implementación descrita.
- La guía contempla también una versión en Java; esta carpeta contiene la implementación en Python.
- No se necesitan variables de entorno para ejecutar la práctica.

Consulta `Reporte_Practica_01.pdf` para revisar el análisis ampliado y las preguntas de control.