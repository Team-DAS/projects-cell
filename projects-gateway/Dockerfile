# 1. Usar una imagen base ligera de Node.js
FROM node:18-alpine

# 2. Establecer el directorio de trabajo
WORKDIR /app

# 3. Copiar archivos de dependencias e instalar
COPY package.json ./
RUN npm install --production

# 4. Copiar el código de la aplicación
COPY index.js .

# 6. Comando para arrancar el servicio
CMD ["node", "index.js"]