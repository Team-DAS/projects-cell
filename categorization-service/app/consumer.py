# app/consumer.py
import pika
import json
import time
import logging
from pika.exchange_type import ExchangeType

# Importamos nuestra configuración y nuestro procesador de IA
from .config import settings
from .ai_processor import analyze_project

# Configuración de logging para ver qué está pasando
logging.basicConfig(level=logging.INFO)
log = logging.getLogger(__name__)

class RabbitMQConsumer:
    """
    Una clase que encapsula toda la lógica de consumo de RabbitMQ.
    """
    def __init__(self):
        self.connection = None
        self.channel = None
        log.info("Inicializando consumidor...")

    def connect(self):
        """
        Intenta conectarse a RabbitMQ con reintentos.
        """
        while True:
            try:
                # 1. Crear conexión
                log.info(f"Conectando a RabbitMQ en {settings.RABBITMQ_HOST}...")
                params = pika.ConnectionParameters(host=settings.RABBITMQ_HOST)
                self.connection = pika.BlockingConnection(params)
                
                # 2. Crear un canal
                self.channel = self.connection.channel()
                log.info("✅ ¡Conectado exitosamente a RabbitMQ!")
                
                # 3. Configurar el "Oído" (Queues y Exchange)
                self.setup_queues()
                break # Salir del bucle si la conexión es exitosa
            except pika.exceptions.AMQPConnectionError:
                log.warning("❌ No se pudo conectar a RabbitMQ. Reintentando en 5 segundos...")
                time.sleep(5)

    def setup_queues(self):
        """
        Declara el exchange, la cola y los bindings.
        Esto es "idempotente": si ya existen, no hace nada.
        """
        log.info("Configurando exchange y colas...")
        # 1. Declarar el exchange (debe coincidir con el de Java)
        self.channel.exchange_declare(
            exchange=settings.PROJECT_EXCHANGE, 
            exchange_type=ExchangeType.topic, 
            durable=True
        )
        
        # 2. Declarar nuestra cola durable (donde se almacenan los mensajes)
        self.channel.queue_declare(
            queue=settings.CATEGORIZATION_QUEUE, 
            durable=True
        )
        
        # 3. "Enlazar" (Bind) nuestra cola al exchange para los eventos que nos importan
        self.channel.queue_bind(
            exchange=settings.PROJECT_EXCHANGE,
            queue=settings.CATEGORIZATION_QUEUE,
            routing_key=settings.ROUTING_KEY_CREATED
        )
        self.channel.queue_bind(
            exchange=settings.PROJECT_EXCHANGE,
            queue=settings.CATEGORIZATION_QUEUE,
            routing_key=settings.ROUTING_KEY_UPDATED
        )
        log.info("Exchange y colas configurados.")

    def publish_event(self, routing_key: str, body: dict):
        """
        Publica un nuevo evento (el categorizado) de vuelta al exchange.
        """
        self.channel.basic_publish(
            exchange=settings.PROJECT_EXCHANGE,
            routing_key=routing_key,
            body=json.dumps(body),
            properties=pika.BasicProperties(delivery_mode=2) # Mensaje persistente
        )
        log.info(f"⬆️ Evento '{routing_key}' publicado para {body.get('projectId')}")

    def on_message_callback(self, ch, method, properties, body):
        """
        Esta es la función que se ejecuta CADA VEZ que llega un mensaje.
        """
        routing_key = method.routing_key
        log.info(f"\n⬇️ Mensaje recibido con routing key: {routing_key}")
        
        try:
            # 1. Decodificar el mensaje (el ProjectEventDTO de Java)
            event_data = json.loads(body.decode('utf-8'))
            project_id = event_data.get('projectId')
            description = event_data.get('description', '')
            title = event_data.get('title', '')
            
            if not project_id or not description:
                log.warning("Mensaje sin projectId o description. Descartando.")
                # Confirmar (ACK) el mensaje para sacarlo de la cola
                ch.basic_ack(delivery_tag=method.delivery_tag)
                return

            # 2. "Pensar" (Llamar a nuestra IA)
            log.info(f"Procesando con IA (Groq) para proyecto: {project_id}...")
            ai_results = analyze_project(description, title)
            
            # 3. Preparar el NUEVO evento enriquecido
            categorized_event = {
                "projectId": project_id,
                "mainCategory": ai_results["main_category"],
                "tags": ai_results["tags"],
                "categorizedAt": time.time(),
                "originalEventType": event_data.get('eventType') # ej. "CREATED" o "UPDATED"
            }
            
            # 4. "Hablar" (Publicar el nuevo evento)
            self.publish_event(
                routing_key=settings.ROUTING_KEY_CATEGORIZED,
                body=categorized_event
            )

            # 5. Confirmar a RabbitMQ que procesamos el mensaje (ACK)
            ch.basic_ack(delivery_tag=method.delivery_tag)
            log.info(f"✅ Mensaje procesado y confirmado (ACK) para {project_id}.")

        except json.JSONDecodeError as e:
            log.error(f"🔥 Error de JSON: {e}. Mensaje descartado (NACK).")
            ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False) # No re-encolar
        except Exception as e:
            log.error(f"🔥 Error inesperado: {e}. Mensaje descartado (NACK).")
            # No re-encolar (requeue=False) para evitar un "bucle de envenenamiento"
            # donde un mensaje fallido se procesa una y otra vez.
            ch.basic_nack(delivery_tag=method.delivery_tag, requeue=False)

    def start_consuming(self):
        """
        Inicia el bucle de consumo.
        """
        try:
            self.connect() # Asegurarse de estar conectado
            
            # Define el "prefetch count". Le dice a RabbitMQ:
            # "No me envíes más de 1 mensaje a la vez".
            # Esto evita que el consumidor se sature si la IA es lenta.
            self.channel.basic_qos(prefetch_count=1)
            
            self.channel.basic_consume(
                queue=settings.CATEGORIZATION_QUEUE,
                on_message_callback=self.on_message_callback
                # auto_ack=False (por defecto) significa que debemos hacer ACK manual.
            )
            log.info("[*] Esperando mensajes. Para salir presiona CTRL+C")
            self.channel.start_consuming()
        except KeyboardInterrupt:
            log.info("Cerrando consumidor...")
            self.connection.close()
        except Exception as e:
            log.error(f"Error crítico en el consumidor: {e}")
            if self.connection and self.connection.is_open:
                self.connection.close()

# Este es el objeto que importaremos en main.py
consumer_instance = RabbitMQConsumer()