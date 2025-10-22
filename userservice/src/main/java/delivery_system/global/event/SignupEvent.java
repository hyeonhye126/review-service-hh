package delivery_system.global.event;

import delivery_system.user.domain.entity.User;
import delivery_system.user.domain.entity.UserRole;
import lombok.Data;
import lombok.ToString;

//<<< DDD / Domain Event
@Data
@ToString
public class SignupEvent extends AbstractEvent {
    private String user_id;
    private String password;
    private String address;
    private UserRole role;

    public SignupEvent(User user) {
        this.user_id = user.getId();
        this.password = user.getPassword();
        //this.address = user.getAddress();
        this.role = user.getRole();
    }
    public SignupEvent() {
        super();
    }
}
//>>> DDD / Domain Event

//>>> DDD / Domain Event
