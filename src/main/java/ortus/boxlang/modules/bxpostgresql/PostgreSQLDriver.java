/**
 * [BoxLang]
 *
 * Copyright [2023] [Ortus Solutions, Corp]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ortus.boxlang.modules.bxpostgresql;

import ortus.boxlang.runtime.config.segments.DatasourceConfig;
import ortus.boxlang.runtime.dynamic.casters.StringCaster;
import ortus.boxlang.runtime.jdbc.drivers.DatabaseDriverType;
import ortus.boxlang.runtime.jdbc.drivers.GenericJDBCDriver;
import ortus.boxlang.runtime.scopes.Key;
import ortus.boxlang.runtime.types.IStruct;
import ortus.boxlang.runtime.types.Struct;

/**
 * The PostgreSQL JDBC Driver
 * https://jdbc.postgresql.org/documentation/use/
 */
public class PostgreSQLDriver extends GenericJDBCDriver {

	protected static final String	DEFAULT_CLASSNAME			= "org.postgresql.Driver";
	protected static final String	DEFAULT_PORT				= "5430";
	protected static final String	DEFAULT_HOST				= "localhost";
	protected static final String	DEFAULT_DELIMITER			= "&";
	protected static final IStruct	DEFAULT_HIKARI_PROPERTIES	= Struct.of();
	protected static final IStruct	DEFAULT_CUSTOM_PARAMS		= Struct.of();

	/**
	 * Constructor
	 */
	public PostgreSQLDriver() {
		super();
		this.name					= new Key( "PostgreSQL" );
		this.type					= DatabaseDriverType.POSTGRESSQL;
		this.driverClassName		= DEFAULT_CLASSNAME;
		this.defaultDelimiter		= DEFAULT_DELIMITER;
		this.defaultCustomParams	= DEFAULT_CUSTOM_PARAMS;
		this.defaultProperties		= DEFAULT_HIKARI_PROPERTIES;
	}

	@Override
	public String buildConnectionURL( DatasourceConfig config ) {
		// Validate the database
		String database = ( String ) config.properties.getOrDefault( "database", "" );
		if ( database.isEmpty() ) {
			throw new IllegalArgumentException( "The database property is required for the PostgreSQL JDBC Driver" );
		}

		// Validate the host
		String host = ( String ) config.properties.getOrDefault( "host", DEFAULT_HOST );
		if ( host.isEmpty() ) {
			host = DEFAULT_HOST;
		}

		// Port
		String port = StringCaster.cast( config.properties.getOrDefault( "port", DEFAULT_PORT ) );
		if ( port.isEmpty() || port.equals( "0" ) ) {
			port = DEFAULT_PORT;
		}

		// Build the connection URL with no host info
		return String.format(
		    "jdbc:postgresql://%s:%s/%s?%s",
		    host,
		    port,
		    database,
		    customParamsToQueryString( config )
		);
	}

}
