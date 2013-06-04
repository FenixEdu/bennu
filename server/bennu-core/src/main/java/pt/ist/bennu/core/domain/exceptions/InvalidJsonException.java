package pt.ist.bennu.core.domain.exceptions;

import java.util.Arrays;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.gson.JsonElement;

public class InvalidJsonException extends WebApplicationException {

    private static final long serialVersionUID = 1L;

    public InvalidJsonException(JsonElement jsonElement, String... missingAttributes) {
        super(Response
                .status(Status.BAD_REQUEST)
                .entity(String.format("Invalid Json Input:%s\nMissing Attributes:%s", jsonElement.toString(),
                        Arrays.toString(missingAttributes))).build());
    }
}
