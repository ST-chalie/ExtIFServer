
class RequestDto {
    public String status;
    public int statusCode;
    public String statusMessage;
    public String authId;
    public String userOId;
    public String userId;
    public String edgeId;

}

class ResponseDto {
    public int returnCode;
    public String resMessage;

}

class MessageDto {
    public String text;
}

class MappingJsonDto {
    public int statusCode;
    public int returnCode;
    public String messageName;

}
