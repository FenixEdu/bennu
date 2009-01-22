package module.projects.persistenceTier.exceptions;

public class OracleConnectionException extends RuntimeException {

	private final String key;

	private final String[] args;

	public OracleConnectionException() {
		this(null, (String[]) null);
	}

	public OracleConnectionException(final String key, final String... args) {
		super(key);
		this.key = key;
		this.args = args;
	}

	public OracleConnectionException(final String key, final Throwable cause,
			final String... args) {
		super(key, cause);
		this.key = key;
		this.args = args;
	}

	public String getKey() {
		return key;
	}

	public String[] getArgs() {
		return args;
	}

}
