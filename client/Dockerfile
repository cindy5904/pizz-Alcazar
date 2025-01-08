# Étape 1 : Builder
FROM node:18-slim AS build

WORKDIR /app

COPY package*.json ./
RUN npm install

COPY . .

# Étape 2 : Image finale
FROM node:18-slim

WORKDIR /app
COPY --from=build /app ./

EXPOSE 3000
CMD ["npm", "start"]
