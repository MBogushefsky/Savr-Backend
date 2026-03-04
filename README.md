# Savr Backend

A Spring Boot REST API that aggregates product prices across Amazon, Walmart, eBay, and Google Shopping, tracks stock and crypto portfolios, and syncs bank transactions via Plaid — a unified personal finance and smart shopping platform.

## Why It's Valuable

Savr unifies four distinct financial data domains into a single backend: real-time price comparison across major retailers (via official APIs and web scraping), live stock and cryptocurrency tracking (Binance, MarketStack), bank account aggregation via Plaid with background sync jobs, and savings goal management. The breadth of third-party integrations — each with its own dedicated configuration, service, and controller layer — makes this an unusually complete personal finance engine for a single Spring Boot application.

## Tech Stack

- **Framework**: Spring Boot 2.4.3
- **Language**: Java 11
- **ORM**: Hibernate 5.4 + Spring Data JPA
- **Database**: MySQL / compatible RDBMS
- **HTTP Client**: Retrofit 2 + Spring RestTemplate
- **Web Scraping**: Jsoup
- **Banking**: Plaid API (OAuth link, account sync, transaction sync)
- **Crypto**: Binance API (live prices, WebSocket alerts)
- **Stocks**: MarketStack API
- **Shopping**: Amazon Product Advertising API, Walmart Open API, eBay API, ValueSerp (Google Shopping)
- **SMS Alerts**: Twilio
- **Server**: Embedded Tomcat 9 (WAR packaging)
- **Build**: Maven

## Key Features

- **Multi-retailer price comparison** — search products across Amazon, Walmart, eBay, and Google Shopping in one call; retrieve full product specs by ID
- **Plaid bank integration** — OAuth link flow, account listing, transaction sync with scheduled background sync jobs
- **Savings goals** — create and track financial goals against bank account balances
- **Bank statistics** — spending analytics derived from Plaid transaction history
- **Crypto portfolio** — real-time Binance prices, portfolio tracking, configurable price-threshold alerts via Binance WebSocket
- **Stock portfolio** — MarketStack integration for market data and portfolio performance
- **SMS notifications** — Twilio alerts for price or portfolio threshold triggers
- **User management** — registration, authentication, profile management, avatar file upload
- **CORS configured** — ready for SPA frontend integration

## Architecture Overview

```
src/main/java/com/bogaware/savr/
├── Application.java
├── configurations/
│   ├── bank/          # Plaid API client configuration
│   ├── exchanges/     # Binance + MarketStack config + alert scheduling
│   ├── shopping/      # Amazon, Walmart, eBay, ValueSerp, web scraping
│   ├── user/          # Twilio, file upload, admin user setup
│   └── core/          # RestTemplate, CORS
├── controllers/
│   ├── bank/          # PlaidToken, PlaidAccount, PlaidTransaction, Goal, BankStatistics
│   ├── exchanges/     # Binance, MarketStack, Exchange aggregator
│   ├── shopping/      # Amazon, Walmart, eBay, GoogleShopping
│   └── user/          # Security, Profile, TwilioMessage
├── services/          # Business logic mirroring controller structure
├── repositories/      # Spring Data JPA repositories
├── models/            # JPA entity classes
└── enums/             # ProductCondition, SearchSortBy
```

Each third-party integration has its own `Configuration`, `Controller`, and `Service`, keeping concerns cleanly separated and easy to extend.

## Getting Started

### Prerequisites

- Java 11+
- Maven 3.6+
- MySQL database running locally or remote
- API keys for desired integrations (see Environment Variables)

### Installation

```bash
mvn clean install
mvn spring-boot:run
```

Or build a WAR and deploy to an external Tomcat:
```bash
mvn package
cp target/Savr-*.war $TOMCAT_HOME/webapps/
```

API starts on port `8080` by default.

## Environment Variables

Set in `src/main/resources/application.properties` or as system environment variables:

| Variable | Description |
|---|---|
| `SPRING_DATASOURCE_URL` | JDBC connection string |
| `SPRING_DATASOURCE_USERNAME` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | Database password |
| `PLAID_CLIENT_ID` | Plaid API client ID |
| `PLAID_SECRET` | Plaid API secret |
| `PLAID_ENV` | Plaid environment (`sandbox`, `development`, `production`) |
| `BINANCE_API_KEY` | Binance API key |
| `BINANCE_SECRET_KEY` | Binance API secret |
| `MARKETSTACK_API_KEY` | MarketStack stock data API key |
| `AMAZON_API_KEY` | Amazon Product Advertising API key |
| `WALMART_API_KEY` | Walmart Open API key |
| `EBAY_API_KEY` | eBay Developer API key |
| `VALUESERP_API_KEY` | ValueSerp (Google Shopping) API key |
| `TWILIO_ACCOUNT_SID` | Twilio account SID |
| `TWILIO_AUTH_TOKEN` | Twilio auth token |
| `TWILIO_PHONE_NUMBER` | Twilio sender phone number |
| `ADMIN_USERNAME` | Default administrator username |
| `ADMIN_PASSWORD` | Default administrator password |
