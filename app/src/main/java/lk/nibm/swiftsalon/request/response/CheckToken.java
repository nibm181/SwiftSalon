package lk.nibm.swiftsalon.request.response;

public class CheckToken {

    protected static boolean isTokenValid(GenericListResponse response) {
        return response.getStatus() != 2;
    }

    protected static boolean isTokenValid(GenericObjectResponse response) {
        return response.getStatus() != 2;
    }

}
