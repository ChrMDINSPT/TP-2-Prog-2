# Burger King Backend

Backend para un sistema de gestión de un restaurante de comidas rápidas, basado en el TP original de Programación II y llevado a una arquitectura más completa con API REST, persistencia en PostgreSQL y separación por capas.

## Objetivo

El sistema busca administrar:

- Usuarios del sistema.
- Empleados y roles diarios.
- Ingredientes.
- Ítems del menú.
- Pedidos.
- Ítems concretos dentro de cada pedido.
- Personalización de ingredientes por pedido.
- Estados del pedido.
- Ventas y consultas para inspectores.

La idea es conservar la lógica del TP original, pero migrarla a una arquitectura cliente-servidor.

## Stack

### Backend

- Java
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- Spring Security
- OAuth2 / OpenID Connect
- Maven

### Base de datos

- PostgreSQL
- Neon

### Frontend

Pendiente de definición final.

La idea inicial es utilizar un frontend web liviano que consuma la API REST.

## Arquitectura

El backend sigue una arquitectura por capas:

```text
HTTP Request
    ↓
Controller
    ↓
Service
    ↓
Repository
    ↓
PostgreSQL / Neon
```

### Responsabilidades

- `controller`: recibe requests HTTP y devuelve responses.
- `service`: contiene la lógica de negocio y validaciones.
- `repository`: acceso a datos mediante Spring Data JPA.
- `entity`: mapeo de entidades JPA contra las tablas.
- `dto`: contratos de entrada y salida de la API.
- `config`: configuración general, especialmente seguridad.
- `exception`: manejo centralizado de errores.

## Estructura de paquetes

```text
com.burgerking.backend
├── controller
├── dto
├── entity
├── exception
├── config
├── repository
├── service
└── BurgerkingBackendApplication.java
```

## Modelo de datos

### users

Representa a todos los usuarios del sistema.

Campos principales:

```text
id
external_subject
name
user_type
```

Tipos de usuario:

```text
MANAGER
INSPECTOR
EMPLOYEE
```

### employees

Información específica de empleados.

Campos:

```text
user_id
daily_role
```

Roles diarios:

```text
SELLER
COOK
UNASSIGNED
```

El rol diario puede ser modificado por un gerente.

### ingredients

Ingredientes disponibles.

Campos:

```text
id
name
```

### items

Productos del menú.

Campos:

```text
id
name
price
active
```

`active = false` funciona como baja lógica para evitar romper referencias históricas.

### item_ingredients

Relación entre un ítem y su receta base.

```text
item_id
ingredient_id
```

### orders

Pedidos realizados.

Campos principales:

```text
id
seller_id
cook_id
status
created_at
delivered_at
```

Estados:

```text
CREATED
RECEIVED
IN_PREPARATION
READY
DELIVERED
CANCELLED
```

### order_items

Representa un producto concreto dentro de un pedido.

Campos:

```text
id
order_id
item_id
item_name
unit_price
```

`item_name` y `unit_price` se guardan como snapshot histórico.

Esto evita que un pedido viejo cambie si posteriormente se modifica el nombre o precio del producto original.

### order_item_ingredients

Ingredientes concretos de un `OrderItem`.

```text
order_item_id
ingredient_id
```

Permite personalizar pedidos sin modificar la receta base del menú.

Ejemplo:

```text
Whopper base:
- Pan
- Carne
- Cebolla
- Lechuga
- Tomate

Whopper pedido:
- Pan
- Carne
- Lechuga
- Tomate
- Queso
```

## Entidades implementadas

- [x] Ingredient
- [x] Item
- [x] User
- [x] Employee
- [x] UserType
- [x] DailyRole
- [ ] Order
- [ ] OrderItem
- [ ] OrderStatus

## DTOs implementados

- [x] CreateItemRequest
- [x] CreateUserRequest
- [x] ChangeRoleRequest
- [ ] CreateOrderRequest
- [ ] CreateOrderItemRequest
- [ ] OrderResponse
- [ ] SalesSummaryResponse

## Endpoints implementados

### Ingredients

```text
GET    /api/ingredients
GET    /api/ingredients/{id}
POST   /api/ingredients
PUT    /api/ingredients/{id}
DELETE /api/ingredients/{id}
```

Estado:

- [x] GET todos
- [x] GET por ID
- [x] POST
- [x] PUT
- [x] DELETE

### Items

```text
GET    /api/items
GET    /api/items/{id}
POST   /api/items
PUT    /api/items/{id}
DELETE /api/items/{id}
```

Estado:

- [x] GET todos
- [x] GET por ID
- [x] POST
- [x] PUT
- [x] DELETE lógico mediante `active = false`

### Users

```text
GET    /api/users
GET    /api/users/{id}
POST   /api/users
PUT    /api/users/{id}
DELETE /api/users/{id}
```

Estado:

- [x] Entity
- [x] Repository
- [x] Service
- [x] Controller

### Employees

```text
GET   /api/employees
GET   /api/employees/{id}
GET   /api/employees/role/{role}
PATCH /api/employees/{id}/role
```

Estado:

- [x] Entity
- [x] Repository
- [x] Service
- [x] Controller
- [x] Cambio de rol diario

## Roadmap

### Etapa 1 - Base del backend

- [x] Crear proyecto Spring Boot.
- [x] Configurar Maven.
- [x] Conectar Spring Boot con Neon.
- [x] Crear esquema inicial en PostgreSQL.
- [x] Agregar constraints y checks.
- [x] Configurar estructura controller/service/repository/entity/dto.

### Etapa 2 - Menú

- [x] CRUD de Ingredient.
- [x] CRUD de Item.
- [x] Relación Item ↔ Ingredient.
- [x] Baja lógica de Item.

### Etapa 3 - Usuarios

- [x] User.
- [x] Employee.
- [x] UserType.
- [x] DailyRole.
- [x] CRUD de usuarios.
- [x] Consulta de empleados.
- [x] Cambio de rol diario.

### Etapa 4 - Pedidos

- [ ] Crear `OrderStatus`.
- [ ] Crear entity `Order`.
- [ ] Crear entity `OrderItem`.
- [ ] Crear repositories.
- [ ] Crear DTOs de creación.
- [ ] Crear `OrderService`.
- [ ] Crear `OrderController`.
- [ ] Crear pedido.
- [ ] Agregar ítems.
- [ ] Personalizar ingredientes.
- [ ] Asignar cocinero.
- [ ] Iniciar preparación.
- [ ] Marcar pedido como listo.
- [ ] Entregar pedido.
- [ ] Cancelar pedido.

Endpoints previstos:

```text
POST  /api/orders
GET   /api/orders
GET   /api/orders/{id}

PATCH /api/orders/{id}/assign-cook
PATCH /api/orders/{id}/start
PATCH /api/orders/{id}/ready
PATCH /api/orders/{id}/deliver
PATCH /api/orders/{id}/cancel
```

### Etapa 5 - Ventas

- [ ] Listado de ventas.
- [ ] Resumen de ventas.
- [ ] Consulta por vendedor.
- [ ] Consulta por rango de fechas.

Endpoints previstos:

```text
GET /api/sales
GET /api/sales/summary
```

### Etapa 6 - Seguridad

- [ ] Configurar Spring Security.
- [ ] Integrar OAuth2 / OpenID Connect.
- [ ] Asociar `external_subject` con el usuario autenticado.
- [ ] Proteger endpoints por tipo de usuario.
- [ ] Validar roles diarios en operaciones de negocio.

Separación conceptual:

```text
OIDC
→ determina quién es el usuario

Base de datos
→ determina qué tipo de usuario es
→ determina qué rol diario tiene
```

### Etapa 7 - Frontend

- [ ] Definir tecnología final.
- [ ] Login.
- [ ] Vista de gerente.
- [ ] Vista de vendedor.
- [ ] Vista de cocinero.
- [ ] Vista de inspector.
- [ ] Gestión de productos.
- [ ] Creación y personalización de pedidos.
- [ ] Gestión de estados.
- [ ] Consulta de ventas.

## Reglas de negocio principales

### Usuarios

- Un usuario puede ser:
  - `MANAGER`
  - `INSPECTOR`
  - `EMPLOYEE`

- Solo los empleados tienen `daily_role`.

### Roles diarios

Un empleado puede tener:

```text
SELLER
COOK
UNASSIGNED
```

### Ítems

- Los ítems tienen una receta base.
- Los ítems pueden ser desactivados.
- No se eliminan físicamente si pueden existir pedidos históricos que los referencien.

### Pedidos

Flujo previsto:

```text
CREATED
→ RECEIVED
→ IN_PREPARATION
→ READY
→ DELIVERED
```

Alternativamente:

```text
CREATED / RECEIVED / IN_PREPARATION / READY
→ CANCELLED
```

### Personalización

Modificar ingredientes de un pedido no debe modificar el producto original del menú.

Por eso:

```text
Item
→ receta base actual

OrderItem
→ snapshot concreto del producto vendido
```

## Base de datos

La base actual se encuentra en PostgreSQL / Neon.

Tablas:

```text
users
employees
ingredients
items
item_ingredients
orders
order_items
order_item_ingredients
```

La estructura fue creada manualmente y Spring utiliza JPA para mapear las entidades.

Durante desarrollo se recomienda validar el schema con:

```properties
spring.jpa.hibernate.ddl-auto=validate
```

## Ejecución local

Desde WSL:

```bash
./mvnw test
```

Para levantar el backend:

```bash
./mvnw spring-boot:run
```

Por defecto la API queda disponible en:

```text
http://localhost:8080
```

Ejemplo:

```bash
curl http://localhost:8080/api/ingredients
```

## Git workflow

Se trabaja con ramas por funcionalidad.

Ejemplos:

```text
feature/ingredient-crud
feature/item-crud
feature/users-employees
feature/orders
feature/auth
```

Flujo recomendado:

```bash
git switch master
git pull origin master
git switch -c feature/nombre-feature

# realizar cambios

git add .
git commit -m "Descripcion del cambio"
git push -u origin feature/nombre-feature
```

Luego abrir un Pull Request:

```text
base: master
compare: feature/nombre-feature
```

## Notas

- No exponer secretos de Neon en el repositorio.
- No usar las entities JPA como contrato HTTP cuando la entidad tenga relaciones o campos internos sensibles.
- Usar DTOs para requests y responses complejos.
- Evitar consultas masivas si no son necesarias.
- Usar queries específicas mediante Spring Data JPA.
- Mantener controllers livianos.
- Concentrar reglas de negocio en services.
