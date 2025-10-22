package delivery_system.global.event;

import lombok.*;

//<<< DDD / Domain Event
@Data
@ToString
public class LoginEvent extends AbstractEvent {

    private String username;
    private String password;

    public LoginEvent() {
        super();
    }
}
//>>> DDD / Domain Event
