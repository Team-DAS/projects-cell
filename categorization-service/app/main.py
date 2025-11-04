# app/main.py
from fastapi import FastAPI
import uvicorn
import threading
import logging

# Importamos la instancia única de nuestro consumidor
from .consumer import consumer_instance

# Configuración de logging
log = logging.getLogger(__name__)

# --- Creación de la Aplicación FastAPI ---
app = FastAPI(
    title="Servicio de Categorización",
    description="Un microservicio que consume eventos de proyectos y los enriquece con IA (Groq).",
    version="1.0.0"
)

# --- Evento de Arranque (Startup) ---
@app.on_event("startup")
async def on_startup():
    """
    Esto se ejecuta UNA VEZ cuando FastAPI arranca.
    """
    log.info("🚀 Aplicación FastAPI iniciada.")
    
    # --- ¡LA PARTE MÁS IMPORTANTE! ---
    # Iniciar el consumidor de RabbitMQ en un hilo separado (un "thread").
    #
    # ¿Por qué? .start_consuming() es un bucle infinito (bloqueante).
    # Si lo ejecutamos directamente aquí, el servidor FastAPI NUNCA terminaría de arrancar
    # y el endpoint /health nunca respondería.
    #
    # Al ponerlo en un hilo, FastAPI puede continuar y nuestro consumidor
    # puede hacer su trabajo en segundo plano.
    
    log.info("Iniciando el consumidor de RabbitMQ en un hilo de segundo plano...")
    
    consumer_thread = threading.Thread(
        target=consumer_instance.start_consuming,
        daemon=True  # 'daemon=True' asegura que el hilo se cierre si la app principal muere
    )
    consumer_thread.start()
    
    log.info("✅ Consumidor de RabbitMQ corriendo en segundo plano.")

# --- Endpoint de Health Check ---
@app.get("/health", tags=["Monitoring"])
def health_check():
    """
    Endpoint de 'health check' (chequeo de salud).
    Si el servidor FastAPI está corriendo, devolverá 200 OK.
    Kubernetes usará esto para saber si el contenedor está "vivo".
    """
    return {"status": "healthy", "service": "categorization-service"}

# --- (Opcional) Para correr localmente sin Docker ---
if __name__ == "__main__":
    # Esto te permite ejecutar `python app/main.py` para pruebas locales
    log.info("Ejecutando en modo de desarrollo local...")
    uvicorn.run(app, host="0.0.0.0", port=8000)