package si.fri.turizem.util;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.util.Collection;

public class RestUtils {

    public Response response(Collection<? extends Object> returnedEntites, Response.Status status) {
        return Response
                .status(status)
                .entity(returnedEntites)
                .header("X-Total-Count", returnedEntites.size())
                .build();
    }

    public Response response(Object returnedEntity, Response.Status status) {

        if (returnedEntity == null && status == Response.Status.OK) {
            status = Response.Status.NO_CONTENT;
        }

        return Response
                .status(status)
                .entity(returnedEntity)
                .header("X-Total-Count", 1)
                .build();
    }

    public Response response(Response.Status status) {
        return Response
                .status(status)
                .build();
    }

    public Response response(StreamingOutput streamingOutput, String filename, int length) {
        return Response
                .ok(streamingOutput, "application/octet-stream")
                .header("Content-Disposition", "filename=" + filename)
                .header("Content-Length", length)
                .build();
    }
}
