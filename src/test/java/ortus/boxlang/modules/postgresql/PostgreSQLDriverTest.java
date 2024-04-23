package ortus.boxlang.modules.postgresql;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import ortus.boxlang.runtime.config.segments.DatasourceConfig;
import ortus.boxlang.runtime.jdbc.drivers.DatabaseDriverType;
import ortus.boxlang.runtime.scopes.Key;

public class PostgreSQLDriverTest {

	@Test
	@DisplayName( "Test getName()" )
	public void testGetName() {
		PostgreSQLDriver	driver			= new PostgreSQLDriver();
		Key					expectedName	= new Key( "PostgreSQL" );
		assertThat( driver.getName() ).isEqualTo( expectedName );
	}

	@Test
	@DisplayName( "Test getType()" )
	public void testGetType() {
		PostgreSQLDriver	driver			= new PostgreSQLDriver();
		DatabaseDriverType	expectedType	= DatabaseDriverType.POSTGRESSQL;
		assertThat( driver.getType() ).isEqualTo( expectedType );
	}

	@Test
	@DisplayName( "Test buildConnectionURL()" )
	public void testBuildConnectionURL() {
		PostgreSQLDriver	driver	= new PostgreSQLDriver();
		DatasourceConfig	config	= new DatasourceConfig();
		config.properties.put( "driver", "postgresql" );
		config.properties.put( "database", "mydb" );

		String expectedURL = "jdbc:postgresql://localhost:5430/mydb?";
		assertThat( driver.buildConnectionURL( config ) ).isEqualTo( expectedURL );
	}

	@DisplayName( "Throw an exception if the database is not found" )
	@Test
	public void testBuildConnectionURLNoDatabase() {
		PostgreSQLDriver	driver	= new PostgreSQLDriver();
		DatasourceConfig	config	= new DatasourceConfig();

		assertThrows( IllegalArgumentException.class, () -> {
			driver.buildConnectionURL( config );
		} );
	}

}
