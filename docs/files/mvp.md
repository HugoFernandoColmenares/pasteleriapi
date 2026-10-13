# What is missing for an MVP

Today the project is a **working prototype**, not a minimum viable product. It proves that Spring Boot can expose CRUD endpoints for one entity and persist them in SQLite. An MVP would let a pastry shop complete one real job: **keep ingredient stock and bake a recipe without guessing whether there is enough inventory**.

## Current state

| Area | Status |
| --- | --- |
| Ingredient CRUD (`/api/ingredients`) | Implemented |
| OpenAPI / Swagger UI | Implemented |
| Docker | Implemented |
| Stock quantity and unit of measure | Missing |
| Recipes / products | Missing |
| Production workflow (check stock → consume stock) | Missing |
| Input validation and consistent errors | Missing |
| Automated tests | Missing |
| Seed / demo data | Missing |
| Frontend or CORS for a client | Missing |
| Authentication | Out of scope for the first MVP |

Without recipes and stock, the API cannot answer the only question that matters in a pastry shop: *can we bake this today?*

## MVP definition

A first usable version should support this path end to end:

1. Register ingredients with **name, brand, unit, unit price, quantity on hand, and availability**.
2. Create a **recipe** (for example a cake) made of ingredients and amounts.
3. Ask whether a recipe **can be produced** with current stock.
4. **Produce** a batch: subtract the used quantities (or reject the request if stock is insufficient).
5. List remaining stock, including a simple **low-stock** flag.

That loop is enough to demonstrate value. Customers, payments, suppliers, and user accounts can wait.

## Features required for the MVP

### 1. Complete the ingredient model

Extend ingredients beyond name/brand/price/status/notes:

- `unit` (kg, g, L, ml, unit)
- `quantity` (current stock)
- `minimumQuantity` (low-stock threshold)
- `updatedAt` (last change)

Keep `status` as an explicit in-stock / discontinued flag. Reject create/update when name is blank, price is negative, or quantity is negative.

### 2. Recipes

Add a `Recipe` resource:

- name, description, servings, active flag
- list of lines: ingredient id + quantity required per batch
- CRUD plus `GET /api/recipes/{id}` with resolved ingredient names

A recipe must not reference a missing ingredient.

### 3. Production (the core use case)

Add a small production endpoint, for example `POST /api/recipes/{id}/produce` with `{ "batches": 1 }`.

Behavior:

- Calculate required vs available stock
- Return **409** if any ingredient is short, listing what is missing
- On success, decrement stock and return the updated quantities

This is the feature that turns CRUD into a product.

### 4. Querying stock

Operators will not scroll a raw list forever. The MVP needs:

- Filter ingredients by name and availability
- Optional `lowStock=true` query
- Pagination (`page`, `size`) on list endpoints

### 5. API quality (non-negotiable for “viable”)

The current controller maps failures to generic `RuntimeException` and has no Bean Validation. For an MVP:

- `@Valid` on request bodies (`@NotBlank`, `@PositiveOrZero`, …)
- A global exception handler with a stable JSON error shape (`timestamp`, `status`, `error`, `message`, `path`)
- Distinct 400 / 404 / 409 responses
- CORS enabled for a local frontend
- `GET /actuator/health` (or equivalent) for Docker checks
- Seed a few ingredients and one sample recipe on first run so Swagger is demoable

### 6. Tests

There are no tests under `src/test`. The MVP should cover:

- Ingredient create/get/update/delete
- Validation failures (400)
- Recipe create with unknown ingredient (404)
- Produce with enough stock (200) and without stock (409)

Slice tests against the web layer are enough; full UI tests are not required yet.

### 7. Minimal client (optional but recommended)

Swagger is enough for developers. For a course MVP, a **single HTML page** (or a tiny frontend) that lists stock, shows a recipe, and calls Produce would make the product visible. If that UI is added, CORS and the error JSON must already be in place.

## Explicitly out of the first MVP

Do not block the first release on these:

- Login, roles, JWT
- Customers, orders, invoicing
- Supplier management and purchase orders
- Costing dashboards and reports
- PostgreSQL / multi-user concurrency at scale
- Image uploads for recipes
- Soft delete and full audit log

They matter later; they are not required to prove the pastry-shop loop.

## Suggested implementation order

1. Validation, error body, health, seed data  
2. Stock fields on ingredients + list filters  
3. Recipes and recipe lines  
4. Produce + low-stock  
5. Tests for the four flows above  
6. Optional one-page UI and README screenshots of the happy path  

After step 5, the API is an MVP. After step 6, it is easy to show.
