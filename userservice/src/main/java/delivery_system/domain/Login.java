package delivery_system.domain;

import lombok.*;
import delivery_system.infra.AbstractEvent;

//<<< DDD / Domain Event
@Data
@ToString
public class Login extends AbstractEvent {

    private String username;
    private String password;

    public Login() {
        super();
    }
}
//>>> DDD / Domain Event
