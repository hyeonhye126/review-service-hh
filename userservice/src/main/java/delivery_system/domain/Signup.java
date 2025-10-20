package delivery_system.domain;
import lombok.*;
import delivery_system.infra.AbstractEvent;

import java.util.UUID;

//<<< DDD / Domain Event
@Data
@ToString
public class Signup extends AbstractEvent {
    private String user_id;
    private String password;
    private String address;
    private UserRole role;

    public Signup(User user) {
        this.user_id = user.getId();
        this.password = user.getPassword();
        //this.address = user.getAddress();
        this.role = user.getRole();
    }
    public Signup() {
        super();
    }
}
//>>> DDD / Domain Event

//>>> DDD / Domain Event
