
public class CheckParameter {

    public int checkParameter(RequestDto requestDto) {
        Boolean result = true;

        if (requestDto.statusCode == 0 && requestDto.status == null) {
            result = false;
        }
        return result == true ? requestDto.statusCode : 0;
    }
}
