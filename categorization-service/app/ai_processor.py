# app/ai_processor.py
import httpx
import json
from .config import settings

# --- Directriz 1: Lista de categorías principal ---
# Esta es nuestra lista "maestra" de categorías.
# La IA será forzada a elegir una de estas.
MAIN_CATEGORIES_LIST = [
    "Desarrollo de Software",
    "Diseño Gráfico y Multimedia",
    "Marketing Digital y SEO",
    "Redacción y Traducción",
    "Soporte Administrativo",
    "Consultoría y Negocios",
    "Otro"
]

def analyze_project(description: str, title: str) -> dict:
    """
    Analiza un proyecto usando la API de Groq para
    1. Clasificarlo en una Categoría Principal.
    2. Extraer Etiquetas/Habilidades.
    """
    
    # Preparamos el "prompt" para la IA, dándole contexto y reglas
    # Escapamos las comillas para evitar problemas con el JSON
    safe_title = title.replace('"', '\\"')
    safe_description = description.replace('"', '\\"')
    
    prompt = f"""Eres un asistente experto en clasificación para una plataforma de freelancers.
Tu trabajo es analizar el siguiente proyecto y devolver dos cosas en formato JSON:
1. "main_category": Clasifica el proyecto en UNA de las siguientes categorías: {', '.join(MAIN_CATEGORIES_LIST)}.
2. "tags": Extrae un máximo de 5 habilidades o tecnologías clave mencionadas en el texto.

Texto del Proyecto:
Título: {safe_title}
Descripción: {safe_description}

Responde ÚNICAMENTE con un objeto JSON válido con las claves "main_category" y "tags"."""
    
    # --- Directriz 2: Llamada a la API de Groq ---
    headers = {
        "Authorization": f"Bearer {settings.GROQ_API_KEY}",
        "Content-Type": "application/json"
    }
    
    payload = {
        "model": settings.GROQ_MODEL,
        "temperature": 0.0,  # Queremos respuestas consistentes, no creativas
        "messages": [
            {"role": "system", "content": "Eres un asistente que responde únicamente en formato JSON válido."},
            {"role": "user", "content": prompt}
        ]
        # Nota: response_format puede causar errores en algunos modelos de Groq
        # Si sigues teniendo problemas, descomenta la siguiente línea:
        # "response_format": {"type": "json_object"}
    }
    
    try:
        # Usamos httpx para hacer la llamada síncrona a la API
        with httpx.Client() as client:
            response = client.post(settings.GROQ_API_URL, headers=headers, json=payload, timeout=10.0)
            
            # Si algo falla (ej. API caída), lanzamos un error
            response.raise_for_status() 
            
            data = response.json()
            
            # Extraemos el contenido JSON de la respuesta de la IA
            ai_json_response = json.loads(data['choices'][0]['message']['content'])
            
            # Validación simple de la respuesta
            if "main_category" not in ai_json_response or "tags" not in ai_json_response:
                raise ValueError("La respuesta de la IA no tiene el formato esperado.")
            
            # Nos aseguramos de que la categoría sea una de las válidas
            if ai_json_response["main_category"] not in MAIN_CATEGORIES_LIST:
                ai_json_response["main_category"] = "Otro" # Fallback
                
            return ai_json_response
            
    except httpx.HTTPStatusError as http_err:
        print(f"Error HTTP llamando a Groq: {http_err}")
    except (json.JSONDecodeError, ValueError, KeyError) as e:
        print(f"Error procesando respuesta de Groq: {e}")
    except Exception as e:
        print(f"Error inesperado en AI Processor: {e}")

    # En caso de cualquier error, devolvemos un "fallback"
    return {
        "main_category": "Otro",
        "tags": []
    }
