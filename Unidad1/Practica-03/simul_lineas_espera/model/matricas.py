class Metricas:
    def __init__(self, rho, L, Lq, W, Wq,
                 tiempos_espera=None, traza_cola=None, eventos=None):
        self.rho = rho
        self.L = L
        self.Lq = Lq
        self.W = W
        self.Wq = Wq

        self.tiempos_espera = tiempos_espera if tiempos_espera is not None else []
        self.traza_cola = traza_cola if traza_cola is not None else []
        # Registro de lo que pasó: lista de (tiempo, tipo, número_de_cliente)
        # tipo puede ser "llegada", "inicio" (empieza servicio) o "salida"
        self.eventos = eventos if eventos is not None else []
