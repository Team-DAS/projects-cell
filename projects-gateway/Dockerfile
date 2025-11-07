# --- 1. Imagen base ligera ---
FROM node:20-alpine

# --- 2. Directorio de trabajo ---
WORKDIR /app

# --- 3. Copiar archivos de dependencias ---
COPY package.json package-lock.json ./

# --- 4. Instalar dependencias reproducibles ---
RUN npm ci --only=production

# --- 5. Copiar el código de la aplicación ---
COPY index.js ./

# --- 6. Comando de inicio ---
CMD ["node", "index.js"]
