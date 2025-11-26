# bx-postgresql

```
|:------------------------------------------------------:|
| ⚡︎ B o x L a n g ⚡︎
| Dynamic : Modular : Productive
|:------------------------------------------------------:|
```

<blockquote>
	Copyright Since 2023 by Ortus Solutions, Corp
	<br>
	<a href="https://www.boxlang.io">www.boxlang.io</a> |
	<a href="https://www.ortussolutions.com">www.ortussolutions.com</a>
</blockquote>

<p>&nbsp;</p>

This module provides a BoxLang JDBC driver for PostgreSQL databases, enabling seamless integration between BoxLang applications and PostgreSQL for enterprise-grade database operations.

## Features

- 🚀 **High Performance**: Built on the proven `org.postgresql:postgresql` driver
- 💪 **Enterprise Ready**: Full support for PostgreSQL's advanced features
- 🔄 **BoxLang Integration**: Native support for BoxLang's `queryExecute()` and datasource management
- 🔒 **Security**: Built-in SSL/TLS support for secure connections
- ⚡ **Zero Configuration**: Works out of the box with minimal setup
- 🎯 **Production Ready**: Battle-tested driver for mission-critical applications

## Installation

### Via CommandBox (Recommended)

```bash
box install bx-postgresql
```

### Via BoxLang Module Installer

```bash
# Into the BoxLang HOME
install-bx-module bx-postgresql

# Or a local folder
install-bx-module bx-postgresql --local
```

## Quick Start

Once installed, you can immediately start using PostgreSQL databases in your BoxLang applications:

```javascript
// Define a PostgreSQL datasource
this.datasources[ "myDB" ] = {
    "driver": "postgresql",
    "host": "localhost",
    "port": 5432,
    "database": "myapp",
    "username": "postgres",
    "password": "password"
};

// Use it in your code
result = queryExecute("SELECT * FROM users WHERE id = ?", [1], {"datasource": "myDB"});
```

## Configuration Examples

See [BoxLang's Defining Datasources](https://boxlang.ortusbooks.com/boxlang-language/syntax/queries#defining-datasources) documentation for full examples on where and how to construct a datasource connection pool.

### Basic Configuration

Standard PostgreSQL connection:

```javascript
this.datasources["mainDB"] = {
    "driver": "postgresql",
    "host": "localhost",
    "port": 5432,
    "database": "myapp",
    "username": "postgres",
    "password": "password"
};
```

### Connection String Format

Alternative using full connection string:

```javascript
this.datasources["mainDB"] = {
    "driver": "postgresql",
    "connectionString": "jdbc:postgresql://localhost:5432/myapp",
    "username": "postgres",
    "password": "password"
};
```

### Advanced Configuration

With SSL and connection pooling options:

```javascript
this.datasources["secureDB"] = {
    "driver": "postgresql",
    "host": "db.example.com",
    "port": 5432,
    "database": "production",
    "username": "appuser",
    "password": "securepass",
    // Optional: Custom connection properties
    "custom": {
        "ssl": "true",
        "sslmode": "require",
        "ApplicationName": "MyBoxLangApp",
        "connectTimeout": "10"
    }
};
```

### Cloud Database Configuration

Example for cloud-hosted PostgreSQL (AWS RDS, Azure, etc.):

```javascript
this.datasources["cloudDB"] = {
    "driver": "postgresql",
    "host": "mydb.region.rds.amazonaws.com",
    "port": 5432,
    "database": "production",
    "username": "admin",
    "password": "cloudpassword",
    "custom": {
        "ssl": "true",
        "sslmode": "require"
    }
};
```

## Usage Examples

### Basic Database Operations

```javascript
// Create a table
queryExecute("
    CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        name VARCHAR(100) NOT NULL,
        email VARCHAR(100) UNIQUE,
        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
    )
", [], {"datasource": "mainDB"});

// Insert data
queryExecute("
    INSERT INTO users (name, email)
    VALUES (?, ?)
", ["John Doe", "john@example.com"], {"datasource": "mainDB"});

// Query data
users = queryExecute("
    SELECT * FROM users
    WHERE email = ?
", ["john@example.com"], {"datasource": "mainDB"});

// Update data
queryExecute("
    UPDATE users
    SET name = ?
    WHERE id = ?
", ["John Smith", 1], {"datasource": "mainDB"});
```

### Working with Transactions

```javascript
try {
    // Begin transaction
    transaction action="begin" {
        // Multiple operations
        queryExecute("INSERT INTO users (name, email) VALUES (?, ?)",
                    ["User 1", "user1@test.com"], {"datasource": "mainDB"});
        queryExecute("INSERT INTO users (name, email) VALUES (?, ?)",
                    ["User 2", "user2@test.com"], {"datasource": "mainDB"});
        
        // Commit transaction
        transaction action="commit";
    }
} catch (any e) {
    // Rollback on error
    transaction action="rollback";
    rethrow;
}
```

### Using PostgreSQL-Specific Features

```javascript
// JSONB queries
queryExecute("
    SELECT * FROM products
    WHERE metadata @> ?::jsonb
", ['{"category": "electronics"}'], {"datasource": "mainDB"});

// Array types
queryExecute("
    INSERT INTO tags (name, keywords)
    VALUES (?, ARRAY[?, ?, ?])
", ["Product A", "tag1", "tag2", "tag3"], {"datasource": "mainDB"});

// Full-text search
queryExecute("
    SELECT * FROM articles
    WHERE to_tsvector('english', content) @@ to_tsquery('english', ?)
", ["search term"], {"datasource": "mainDB"});
```

### Testing with PostgreSQL

```javascript
// Test setup
this.datasources["testDB"] = {
    "driver": "postgresql",
    "host": "localhost",
    "port": 5432,
    "database": "test_db",
    "username": "testuser",
    "password": "testpass"
};

function beforeTests() {
    // Create test schema
    queryExecute("
        CREATE TABLE IF NOT EXISTS products (
            id SERIAL PRIMARY KEY,
            name VARCHAR(100),
            price DECIMAL(10,2)
        )
    ", [], {"datasource": "testDB"});

    // Insert test data
    queryExecute("
        INSERT INTO products (name, price) VALUES
        ('Product A', 10.50),
        ('Product B', 25.00)
    ", [], {"datasource": "testDB"});
}

function testProductQuery() {
    var result = queryExecute("
        SELECT COUNT(*) as total FROM products
    ", [], {"datasource": "testDB"});

    expect(result.total).toBe(2);
}
```

## Development

### Prerequisites

- Java 21+
- BoxLang Runtime 1.4.0+
- Gradle (wrapper included)
- PostgreSQL 12+ for testing

### Building from Source

```bash
# Clone the repository
git clone https://github.com/ortus-solutions-private/bx-postgresql.git
cd bx-postgresql

# Build the module
./gradlew build

# Run tests
./gradlew test

# Create module structure for local testing
./gradlew createModuleStructure
```

### Project Structure

```
bx-postgresql/
├── src/
│   ├── main/
│   │   ├── bx/
│   │   │   └── ModuleConfig.bx          # Module configuration
│   │   ├── java/
│   │   │   └── ortus/boxlang/modules/
│   │   │       └── postgresql/
│   │   │           └── PostgreSQLDriver.java # JDBC driver implementation
│   │   └── resources/
│   └── test/
│       ├── java/                        # Unit and integration tests
│       └── resources/
├── build.gradle                         # Build configuration
├── box.json                            # ForgeBox module manifest
└── readme.md                           # This file
```

### Testing

The module includes comprehensive tests:

- **Unit Tests**: Test the PostgreSQL driver implementation directly
- **Integration Tests**: Test the module within the full BoxLang runtime
- **End-to-End Tests**: Verify database operations work correctly

```bash
# Run all tests
./gradlew test

# Run with verbose output
./gradlew test --info

# Run specific test class
./gradlew test --tests "PostgreSQLDriverTest"
```

### Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Add tests for your changes
5. Ensure all tests pass (`./gradlew test`)
6. Format your code (`./gradlew spotlessApply`)
7. Commit your changes (`git commit -m 'Add amazing feature'`)
8. Push to the branch (`git push origin feature/amazing-feature`)
9. Open a Pull Request

## Compatibility

| Module Version | BoxLang Version | PostgreSQL JDBC Version |
| -------------- | --------------- | ----------------------- |
| 1.1.x          | 1.4.0+          | 42.7.4                  |
| 1.0.x          | 1.3.0+          | 42.7.1                  |

## Troubleshooting

### Common Issues

#### Connection refused

```
Ensure PostgreSQL is running and accessible:
sudo systemctl status postgresql
# Check if port 5432 is open
netstat -an | grep 5432
```

#### Authentication failed

```
Verify credentials in pg_hba.conf:
# PostgreSQL Client Authentication Configuration File
# TYPE  DATABASE        USER            ADDRESS                 METHOD
host    all             all             127.0.0.1/32            md5
```

#### SSL connection errors

```
Ensure SSL is properly configured in your datasource:
"custom": {
    "ssl": "true",
    "sslmode": "require"  // or "verify-full" for strict validation
}
```

#### Testing issues

```
Integration tests require the module to be built first:
./gradlew createModuleStructure
```

### Debug Mode

Enable debug logging in your BoxLang application:

```javascript
// In your Application.bx
this.datasources["debugDB"] = {
    "driver": "postgresql",
    "host": "localhost",
    "port": 5432,
    "database": "mydb",
    "username": "user",
    "password": "pass",
    "logSql": true,
    "logLevel": "DEBUG"
};
```

## Resources

- **Documentation**: [BoxLang Database Guide](https://boxlang.ortusbooks.com/boxlang-language/syntax/queries)
- **PostgreSQL Documentation**: [https://www.postgresql.org/docs/](https://www.postgresql.org/docs/)
- **Issues & Support**: [GitHub Issues](https://github.com/ortus-solutions-private/bx-postgresql/issues)
- **ForgeBox**: [bx-postgresql Package](https://forgebox.io/view/bx-postgresql)

## Changelog

See [CHANGELOG.md](https://github.com/ortus-solutions-private/bx-postgresql/blob/development/changelog.md) for a complete list of changes and version history.

## License

Licensed under the Apache License, Version 2.0. See [LICENSE](https://www.apache.org/licenses/LICENSE-2.0) for details.

## Ortus Sponsors

BoxLang is a professional open-source project and it is completely funded by the [community](https://patreon.com/ortussolutions) and [Ortus Solutions, Corp](https://www.ortussolutions.com). Ortus Patreons get many benefits like a cfcasts account, a FORGEBOX Pro account and so much more. If you are interested in becoming a sponsor, please visit our patronage page: [https://patreon.com/ortussolutions](https://patreon.com/ortussolutions)

### THE DAILY BREAD

> "I am the way, and the truth, and the life; no one comes to the Father, but by me (JESUS)" Jn 14:1-12
