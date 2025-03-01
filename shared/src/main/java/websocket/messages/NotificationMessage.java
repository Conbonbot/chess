package websocket.messages;

public class NotificationMessage extends ServerMessage{
    private String message;

    public NotificationMessage(ServerMessageType type, String message){
        super(type);
        this.message = message;
    }

    public String getNotification(){
        return message;
    }
}
