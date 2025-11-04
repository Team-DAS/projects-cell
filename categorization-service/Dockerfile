# Dockerfile

# 1. Imagen Base
# Usamos una imagen oficial de Python, ligera ("slim")
FROM python:3.10-slim

# 2. Variables de Entorno
# Define el directorio de trabajo DENTRO del contenedor
WORKDIR /app
# Evita que Python escriba archivos .pyc
ENV PYTHONDONTWRITEBYTECODE 1
# Le dice a Python que corra en modo "sin buffer" (mejor para logs)
ENV PYTHONUNBUFFERED 1

# 3. Instalación de Dependencias
# Copiamos SÓLO el archivo de requisitos primero.
COPY requirements.txt .

# Instalamos las dependencias. Al copiar requirements.txt por separado,
# Docker puede usar el caché de esta capa si no cambiamos las dependencias,
# haciendo que las futuras 'builds' sean mucho más rápidas.
RUN pip install --no-cache-dir -r requirements.txt

# 4. Copia del Código de la Aplicación
# Copiamos todo el contenido de nuestra carpeta 'app' local
# a la carpeta 'app' DENTRO del contenedor
COPY ./app ./app

# 5. Exponer el Puerto
# Informamos a Docker que la aplicación escuchará en el puerto 8080
# (Usaremos 8080 como estándar de microservicio)
EXPOSE 8080

# 6. Comando de Ejecución (CMD)
# Este es el comando que se ejecuta cuando el contenedor arranca.
# Inicia el servidor Uvicorn, le dice dónde encontrar nuestra app FastAPI
# (el objeto 'app' en el archivo 'app.main') y en qué puerto escuchar.
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "8080"]