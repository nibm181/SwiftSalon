package lk.nibm.swiftsalon.request.response;

public class CheckToken {

    protected static boolean isTokenValid(GenericResponse response) {
        return response.getStatus() != -1;
    }

}
