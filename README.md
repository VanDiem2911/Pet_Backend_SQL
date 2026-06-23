# Pet Backend

Spring Boot API dùng MongoDB cho website Pet's Home.

## Chạy local

Yêu cầu:

- Java 21 trở lên
- MongoDB đang chạy tại `mongodb://localhost:27017`

```powershell
cd D:\HUDI\Pet\backend
.\mvnw.cmd spring-boot:run
```

Ứng dụng chạy tại `http://localhost:8080`.

## Biến môi trường

- `MONGODB_URI`: chuỗi kết nối MongoDB, mặc định `mongodb://localhost:27017/pet_home`
- `SERVER_PORT`: cổng backend, mặc định `8080`
- `CORS_ALLOWED_ORIGINS`: danh sách origin frontend, mặc định `http://localhost:5173,http://127.0.0.1:5173`
- `SEED_DATA`: tự seed sản phẩm mẫu khi database trống, mặc định `true`

## API chính

- `GET /api/health`
- `GET /api/products`
- `GET /api/products?section=featured`
- `GET /api/products?section=bestSeller`
- `GET /api/products?section=flashSale`
- `POST /api/products`
- `PUT /api/products/{id}`
- `DELETE /api/products/{id}`
- `GET /api/carts/{sessionId}`
- `POST /api/carts/{sessionId}/items`
- `PATCH /api/carts/{sessionId}/items/{productId}?quantity=2`
- `DELETE /api/carts/{sessionId}/items/{productId}`
- `POST /api/newsletter`
- `POST /api/orders`
