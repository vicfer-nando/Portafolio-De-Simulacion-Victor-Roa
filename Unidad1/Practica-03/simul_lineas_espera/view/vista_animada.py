import os
import sys
import time
from collections import deque

import matplotlib
matplotlib.use("Agg")           
import matplotlib.pyplot as plt
import numpy as np

from view.vista_consola import VistaConsola

NEGRITA = "\033[1m"
CYAN = "\033[96m"
VERDE = "\033[92m"
AMARILLO = "\033[93m"
GRIS = "\033[90m"
RESET = "\033[0m"

BARRAS = "▁▂▃▄▅▆▇█"      
HUECOS = 8               


class VistaAnimada(VistaConsola):
   
    def __init__(self, ventana=40, seg_por_unidad=0.25, carpeta="graficas"):
        self.ventana = ventana                  
        self.seg_por_unidad = seg_por_unidad    
        self.carpeta = carpeta
        os.makedirs(carpeta, exist_ok=True)    

    def mostrar_escenario(self, esc, t, s):
        try:
            input(f"\nPresiona Enter para ver la animación del escenario "
                  f"{esc.numero} (Ctrl+C la salta)... ")
        except EOFError:
            pass
        cuadros = self.armar_cuadros(s.eventos)
        self.reproducir(esc, cuadros)
        super().mostrar_escenario(esc, t, s)    

    # ------------------------------------------------------------------
    # 1) Convertir los eventos del modelo en "cuadros" (fotos de la cola)
    # ------------------------------------------------------------------
    def armar_cuadros(self, eventos):
        cola = deque()
        llegada_de = {}
        est = {"servidor": None, "llegadas": 0, "atendidos": 0,
               "iniciados": 0, "espera_total": 0.0, "cola_max": 0}

        cuadros = [self.tomar_cuadro(0.0, "Inicia la simulación", cola, est)]

        i = 0
        while i < len(eventos) and eventos[i][0] <= self.ventana:
            tiempo = eventos[i][0]
            mensajes = []
            
            while i < len(eventos) and eventos[i][0] == tiempo:
                _, tipo, cid = eventos[i]
                if tipo == "llegada":
                    cola.append(cid)
                    llegada_de[cid] = tiempo
                    est["llegadas"] += 1
                    mensajes.append(f"Llega el cliente #{cid}")
                elif tipo == "inicio":
                    cola.remove(cid)
                    est["servidor"] = cid
                    espera = tiempo - llegada_de[cid]
                    est["iniciados"] += 1
                    est["espera_total"] += espera
                    mensajes.append(f"#{cid} pasa al servidor (esperó {espera:.2f})")
                else:  
                    est["servidor"] = None
                    est["atendidos"] += 1
                    mensajes.append(f"#{cid} termina y sale")
                i += 1
            est["cola_max"] = max(est["cola_max"], len(cola))
            cuadros.append(self.tomar_cuadro(tiempo, "  →  ".join(mensajes), cola, est))

        cuadros.append(self.tomar_cuadro(self.ventana, "Fin de la animación", cola, est))
        return cuadros

    def tomar_cuadro(self, tiempo, mensaje, cola, est):
        prom = est["espera_total"] / est["iniciados"] if est["iniciados"] else 0.0
        return {"tiempo": tiempo, "mensaje": mensaje, "cola": list(cola),
                "servidor": est["servidor"], "llegadas": est["llegadas"],
                "atendidos": est["atendidos"], "cola_max": est["cola_max"],
                "espera_prom": prom}

    def armar_serie(self, cuadros, muestras=50):
        
        paso = self.ventana / muestras
        serie = []
        j = 0
        for k in range(muestras):
            instante = k * paso
            while j + 1 < len(cuadros) and cuadros[j + 1]["tiempo"] <= instante:
                j += 1
            serie.append(len(cuadros[j]["cola"]))
        return serie

    # ------------------------------------------------------------------
    # 2) Mostrar los cuadros uno tras otro
    # ------------------------------------------------------------------
    def reproducir(self, esc, cuadros):
        serie = self.armar_serie(cuadros)
        sys.stdout.write("\033[?25l")                 
        try:
            for i, cuadro in enumerate(cuadros):
                lineas = self.dibujar(esc, cuadro, serie)
                if i > 0:
                    sys.stdout.write(f"\033[{len(lineas)}A")   
                sys.stdout.write("".join(l + "\033[K\n" for l in lineas))
                sys.stdout.flush()
                if i + 1 < len(cuadros):
                    pausa = (cuadros[i + 1]["tiempo"] - cuadro["tiempo"]) * self.seg_por_unidad
                    time.sleep(min(pausa, 1.5))
        except KeyboardInterrupt:
            print("\nAnimación detenida.")
        finally:
            sys.stdout.write("\033[?25h")             

    def dibujar(self, esc, c, serie):
     
        avance = int(c["tiempo"] / self.ventana * 30)
        barra = "█" * avance + "░" * (30 - avance)

        frente = c["cola"][:HUECOS]
        extra = len(c["cola"]) - len(frente)
        prefijo = f"{'+' + str(extra) if extra else '':>5}"
        casillas = [" " * 5] * (HUECOS - len(frente))
        casillas += [f"{CYAN}[{n:>3}]{RESET}" for n in reversed(frente)]
        tubo = "─" * (5 + HUECOS * 5)

        if c["servidor"] is None:
            servidor = f"{GRIS}[ libre ]{RESET}"
        else:
            servidor = f"{VERDE}[ #{c['servidor']:<3}]{RESET}"

        en_sistema = len(c["cola"]) + (0 if c["servidor"] is None else 1)

        n = min(len(serie), int(c["tiempo"] / self.ventana * len(serie)) + 1)
        maximo = max(max(serie), 1)
        grafico = "".join(BARRAS[round(q / maximo * 7)] for q in serie[:n])

        return [
            f"{NEGRITA}ESCENARIO {esc.numero}  |  λ={esc.lam}  μ={esc.mu}  "
            f"ρ={esc.rho():.2f}  |  {esc.condicion}{RESET}",
            f"  Tiempo: {c['tiempo']:6.2f} / {self.ventana:.0f}  {AMARILLO}{barra}{RESET}",
            "",
            "  LLEGADAS ──▶   COLA (FIFO)                          SERVIDOR",
            f"  ┌{tubo}┐",
            f"  │{prefijo}{''.join(casillas)}│ ▶ {servidor}",
            f"  └{tubo}┘",
            "",
            f"  En cola: {len(c['cola']):<3} En sistema: {en_sistema:<3} "
            f"Llegadas: {c['llegadas']:<4} Atendidos: {c['atendidos']:<4} "
            f"Cola máx: {c['cola_max']}",
            f"  Espera promedio hasta ahora: {c['espera_prom']:.2f}",
            f"  Cola vs tiempo: {grafico}{' ' * (len(serie) - n)} (máx {max(serie)})",
            f"  Evento: {c['mensaje'][:66]}",
        ]

    # ------------------------------------------------------------------
    # 3) Gráficas: solo se guardan en la carpeta, no se muestran
    # ------------------------------------------------------------------
    def mostrar_tabla_comparativa(self, resultados):
        super().mostrar_tabla_comparativa(resultados)
        if not resultados:
            return
        self.grafica_cola(resultados)
        self.grafica_esperas(resultados)
        self.grafica_comparacion(resultados)
        print("\nGráficas guardadas en la carpeta:", self.carpeta)

    def guardar(self, fig, nombre):
        fig.tight_layout()
        fig.savefig(os.path.join(self.carpeta, nombre), dpi=120)
        plt.close(fig)                          

    def grafica_cola(self, resultados, ventana=100):
        fig, ejes = plt.subplots(len(resultados), 1, figsize=(10, 8),
                                 sharex=True, squeeze=False)
        for eje, (esc, t, s) in zip(ejes.flatten(), resultados):
            tiempos = [p[0] for p in s.traza_cola if p[0] <= ventana]
            colas = [p[1] for p in s.traza_cola if p[0] <= ventana]
            eje.step(tiempos, colas, where="post", color="tab:blue")
            eje.axhline(t.Lq, color="red", linestyle="--",
                        label=f"Lq teórico = {t.Lq:.2f}")
            eje.axhline(s.Lq, color="green", linestyle=":",
                        label=f"Lq simulado = {s.Lq:.2f}")
            eje.set_title(f"Escenario {esc.numero}: λ={esc.lam}, μ={esc.mu} "
                          f"(ρ={esc.rho():.2f})")
            eje.set_ylabel("Clientes en cola")
            eje.legend(loc="upper left")
        ejes.flatten()[-1].set_xlabel("Tiempo")
        fig.suptitle(f"Tamaño de la cola en el tiempo (primeras {ventana} unidades)")
        self.guardar(fig, "1_tamano_cola.png")

    def grafica_esperas(self, resultados, cantidad=300):
        fig, ejes = plt.subplots(len(resultados), 1, figsize=(10, 8),
                                 sharex=True, squeeze=False)
        for eje, (esc, t, s) in zip(ejes.flatten(), resultados):
            esperas = s.tiempos_espera[:cantidad]
            eje.plot(range(1, len(esperas) + 1), esperas,
                     color="tab:orange", linewidth=0.9)
            eje.axhline(t.Wq, color="red", linestyle="--",
                        label=f"Wq teórico = {t.Wq:.2f}")
            eje.axhline(s.Wq, color="green", linestyle=":",
                        label=f"Wq simulado = {s.Wq:.2f}")
            eje.set_title(f"Escenario {esc.numero}: λ={esc.lam}, μ={esc.mu}")
            eje.set_ylabel("Espera en cola")
            eje.legend(loc="upper right")
        ejes.flatten()[-1].set_xlabel("Número de cliente")
        fig.suptitle(f"Tiempo de espera de los primeros {cantidad} clientes")
        self.guardar(fig, "2_tiempos_espera.png")

    def grafica_comparacion(self, resultados):
        """Barras teórico vs simulado para L, Lq, W y Wq."""
        medidas = ["L", "Lq", "W", "Wq"]
        etiquetas = [f"Esc {esc.numero}" for esc, t, s in resultados]
        posiciones = np.arange(len(resultados))
        ancho = 0.35

        fig, ejes = plt.subplots(2, 2, figsize=(10, 8))
        for eje, medida in zip(ejes.flatten(), medidas):
            teoricos = [getattr(t, medida) for esc, t, s in resultados]
            simulados = [getattr(s, medida) for esc, t, s in resultados]
            eje.bar(posiciones - ancho / 2, teoricos, ancho,
                    label="Teórico", color="tab:red")
            eje.bar(posiciones + ancho / 2, simulados, ancho,
                    label="Simulado", color="tab:green")
            eje.set_xticks(posiciones)
            eje.set_xticklabels(etiquetas)
            eje.set_title(medida)
            eje.legend()
        fig.suptitle("Comparación teórico vs simulado")
        self.guardar(fig, "3_comparacion.png")
