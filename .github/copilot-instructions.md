# BoxLang PostgreSQL Module - AI Coding Agent Instructions

## Project Overview

This is a **BoxLang JDBC driver module** that provides PostgreSQL database connectivity. BoxLang modules are hybrid Java/BoxLang applications that extend the BoxLang runtime with new capabilities through Java service provider interfaces (SPI).

### Architecture

- **Java Driver Layer** (`src/main/java/ortus/boxlang/modules/postgresql/PostgreSQLDriver.java`): Extends `GenericJDBCDriver` and implements `IJDBCDriver` interface
- **BoxLang Module Config** (`src/main/bx/ModuleConfig.bx`): BoxLang module descriptor with metadata and lifecycle hooks
- **Service Loader Pattern**: Uses Java ServiceLoader with META-INF/services auto-generation via Gradle plugin
- **Shadow JAR**: All dependencies (PostgreSQL JDBC driver) are bundled into a single fat JAR

### Key Architectural Decisions

1. **Driver extends GenericJDBCDriver**: Inherits base JDBC functionality, only needs to override `buildConnectionURL()` for PostgreSQL-specific URL format
2. **ServiceLoader Registration**: The `serviceLoader` Gradle plugin auto-generates META-INF/services files for 7 different BoxLang SPIs (drivers, BIFs, components, schedulers, cache providers, interceptors, services)
3. **Dual Build Output**: Produces both a JAR (`shadowJar`) and module structure (`createModuleStructure`) for different deployment scenarios

## Critical Developer Workflows

### Build & Test Commands

```bash
# Full build with tests and module structure
./gradlew build

# Run tests only (uses JUnit 5 with Truth assertions)
./gradlew test

# Create module structure in build/module/ (required for integration tests)
./gradlew createModuleStructure

# Format Java code using Eclipse formatter
./gradlew spotlessApply

# Check formatting without applying
./gradlew spotlessCheck

# Version bumping
./gradlew bumpPatchVersion  # 1.0.0 -> 1.0.1
./gradlew bumpMinorVersion  # 1.0.0 -> 1.1.0
./gradlew bumpMajorVersion  # 1.0.0 -> 2.0.0
```

### Build Dependency Chain

1. `compileJava` → generates classes
2. `serviceLoaderBuild` → generates META-INF/services files (happens automatically)
3. `shadowJar` → creates fat JAR with PostgreSQL driver bundled
4. `createModuleStructure` → copies JAR + BoxLang files to build/module/
5. `zipModuleStructure` → creates distributable zip

**Important**: `compileTestJava` depends on `compileJava` and `serviceLoaderBuild`, and is finalized by `shadowJar`

### BoxLang Runtime Integration

The module expects BoxLang runtime JAR in one of two locations:
1. `../boxlang/build/libs/boxlang-{version}.jar` (sibling project)
2. `src/test/resources/libs/boxlang-{version}.jar` (downloaded via `downloadBoxLang` task)

Use `./gradlew downloadBoxLang` to fetch the required BoxLang version specified in `gradle.properties`.

## Project-Specific Conventions

### Java Code Patterns

1. **Driver Implementation Pattern**:
   ```java
   public class PostgreSQLDriver extends GenericJDBCDriver {
       protected static final String DEFAULT_PORT = "5430";

       public PostgreSQLDriver() {
           super();
           this.name = new Key("PostgreSQL");
           this.type = DatabaseDriverType.POSTGRESSQL;
           this.driverClassName = "org.postgresql.Driver";
       }

       @Override
       public String buildConnectionURL(DatasourceConfig config) {
           // Validate required properties
           // Build JDBC URL: jdbc:postgresql://host:port/database?params
       }
   }
   ```

2. **Testing Pattern**: Use Google Truth assertions (`assertThat()`) not JUnit assertions
   ```java
   PostgreSQLDriver driver = new PostgreSQLDriver();
   assertThat(driver.getName()).isEqualTo(new Key("PostgreSQL"));
   ```

3. **Error Handling**: Throw `IllegalArgumentException` for missing required config properties

### BoxLang Module Conventions

1. **Token Replacement**: Module files use `@build.version@` and `@build.number@` tokens replaced during build
2. **Module Mapping**: Each module has a BoxLang mapping prefix `bxModules.{mapping}` (e.g., `bxModules.bxpostgresql`)
3. **Version Format**:
   - Development branch: `1.2.0-snapshot` (build.gradle auto-appends)
   - Release branches: `1.2.0+123` (BUILD_ID injected)

### Code Formatting

- **Java**: Uses Eclipse formatter with `.ortus-java-style.xml`
- **BoxLang**: Uses `.cfformat.json` with CommandBox formatting
- **Toggle Comments**: Supports `// @formatter:off` and `// @formatter:on`

## Integration Points

### Service Provider Interfaces (SPIs)

The module registers for these BoxLang SPIs (though only IJDBCDriver is used in this module):
- `ortus.boxlang.runtime.jdbc.drivers.IJDBCDriver` ⭐ (actively implemented)
- `ortus.boxlang.runtime.bifs.BIF`
- `ortus.boxlang.runtime.components.Component`
- `ortus.boxlang.runtime.async.tasks.IScheduler`
- `ortus.boxlang.runtime.cache.providers.ICacheProvider`
- `ortus.boxlang.runtime.events.IInterceptor`
- `ortus.boxlang.runtime.services.IService`

### External Dependencies

- **PostgreSQL JDBC**: `org.postgresql:postgresql:42.7.8` (bundled via Shadow)
- **BoxLang Runtime**: Compile-only dependency, must be present at runtime
- **Test Dependencies**: JUnit 5, Mockito, Google Truth

## Version & Branch Strategy

- **Development branch**: All PRs target `development`, versions auto-suffixed with `-snapshot`
- **Release branches**: Named `releases/v*`, use semantic versioning with build numbers
- **Master/Main**: Snapshot of latest stable, no direct commits
- **Java Version**: JDK 21 (specified in `gradle.properties`)
- **BoxLang Version**: Currently `1.7.0` (in `gradle.properties`)

## Testing Considerations

1. **Test Classpath Filtering**: Test classpath excludes `build/resources` to avoid conflicts
2. **Module Structure Required**: Integration tests need `createModuleStructure` to run first
3. **Standard Streams**: Test output shows standard streams (`showStandardStreams = true`)

## Common Pitfalls

1. **Don't manually edit META-INF/services**: Generated by serviceLoader plugin
2. **Resources excluded from IDE**: `sourceSets.main.resources.srcDirs = []` prevents classpath conflicts
3. **Token replacement**: Don't commit files with unresolved `@build.version@` tokens
4. **Port number**: PostgreSQL default is 5430 in this module (check if intentional vs standard 5432)
5. **Clean task**: Automatically wipes `~/.boxlang/classes` on clean

## Documentation References

- BoxLang Datasources: https://boxlang.ortusbooks.com/boxlang-language/syntax/queries#defining-datasources
- PostgreSQL JDBC Docs: https://jdbc.postgresql.org/documentation/
- Service Loader Plugin: https://github.com/harbby/gradle-serviceloader
