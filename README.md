# Stock Market Simulator

Simplified stock market service implemented as a Spring Boot REST API.

The system contains:

- wallets that can own stocks
- a bank that stores available stock quantities and acts as the only liquidity provider
- an audit log for successful wallet buy/sell operations

Stock price is fixed at `1`. Wallet balance is not tracked. Buy and sell operations are executed immediately.

## Requirements

- Docker

## Run

Linux/macOS:

```sh
sh start.sh 8080
```

Windows PowerShell:

```powershell
.\start.ps1 8080
```

The API will be available at:

```text
http://localhost:8080
```

The port is passed as the first argument, so a different port can be used:

```sh
./start.sh 9090
```

## High Availability

The service runs three application instances behind HAProxy. All instances share the same PostgreSQL database.

<p align="center">
  <img src="https://github.com/user-attachments/assets/ac056c3f-f7b1-4678-a5d7-3d7723cfecf2" width="524" />
</p>

Calling:

```http
POST /chaos
```

kills the application instance that handled the request. HAProxy routes following requests to the remaining healthy instances, so the product stays available after one instance is killed.

Docker Compose also uses restart policies, so stopped containers can be restarted by Docker.

## Example Flow

Set available bank stocks:

```sh
curl -i -X POST http://localhost:8080/stocks \
  -H "Content-Type: application/json" \
  -d '{"stocks":[{"name":"stock1","quantity":2},{"name":"stock2","quantity":1}]}'
```

```http
HTTP/1.1 200 OK
```

Buy one stock:

```sh
curl -i -X POST http://localhost:8080/wallets/wallet-1/stocks/stock1 \
  -H "Content-Type: application/json" \
  -d '{"type":"buy"}'
```

```http
HTTP/1.1 200 OK
```

Check wallet state:

```sh
curl http://localhost:8080/wallets/wallet-1
```

```json
{
  "id": "wallet-1",
  "stocks": [
    {
      "name": "stock1",
      "quantity": 1
    }
  ]
}
```

Check bank state:

```sh
curl http://localhost:8080/stocks
```

```json
{
  "stocks": [
    {
      "name": "stock1",
      "quantity": 1
    },
    {
      "name": "stock2",
      "quantity": 1
    }
  ]
}
```

Sell one stock:

```sh
curl -i -X POST http://localhost:8080/wallets/wallet-1/stocks/stock1 \
  -H "Content-Type: application/json" \
  -d '{"type":"sell"}'
```

```http
HTTP/1.1 200 OK
```

Check audit log:

```sh
curl http://localhost:8080/log
```

```json
{
  "log": [
    {
      "type": "buy",
      "wallet_id": "wallet-1",
      "stock_name": "stock1"
    },
    {
      "type": "sell",
      "wallet_id": "wallet-1",
      "stock_name": "stock1"
    }
  ]
}
```

## Technology Stack

- Java 25
- Spring Boot
- Hibernate
- PostgreSQL
- Flyway
- Docker
- HAProxy
- JUnit
- GitHub Actions
