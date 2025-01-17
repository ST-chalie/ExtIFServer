
public class CheckParameter {

    public int checkParameter(RequestDto requestDto) {
        Boolean result = true;

        if (requestDto.getStatusCode() == 0 && requestDto.getStatus() == null) {
            result = false;
        }
        return result ? requestDto.getStatusCode() : 0;
    }
}
