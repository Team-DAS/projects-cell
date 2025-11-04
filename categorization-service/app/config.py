# app/config.py
from pydantic import BaseSettings

class Settings(BaseSettings):
    """
    Configuración de la aplicación, leída desde variables de entorno.
    Pydantic se encarga de leerlas automáticamente.
    """
    
    # --- RabbitMQ ---
    # Dirección del servidor RabbitMQ (ej. 'rabbitmq-service' en Kubernetes)
    RABBITMQ_HOST: str = "localhost"
    
    # El exchange que tu servicio Java está usando
    PROJECT_EXCHANGE: str = "project.events.exchange"
    
    # Las routing keys que vamos a ESCUCHAR
    ROUTING_KEY_CREATED: str = "project.created"
    ROUTING_KEY_UPDATED: str = "project.updated"
    
    # La routing key que vamos a PUBLICAR
    ROUTING_KEY_CATEGORIZED: str = "project.categorized"
    
    # El nombre de la cola para ESTE servicio
    CATEGORIZATION_QUEUE: str = "categorization_queue"
    
    # --- Groq (IA API) ---
    GROQ_API_KEY: str 
    GROQ_API_URL: str = "https://api.groq.com/openai/v1/chat/completions"
    GROQ_MODEL: str = "llama-3.1-8b-instant" # Modelo corregido: nota el guión y versión 3.1

    class Config:
        # Esto le dice a Pydantic que busque variables de entorno
        # (ej. si ve GROQ_API_KEY, buscará la variable de entorno GROQ_API_KEY)
        case_sensitive = True

# Creamos una instancia única de la configuración para importar en otros archivos
settings = Settings()