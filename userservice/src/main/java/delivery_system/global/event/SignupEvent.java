package delivery_system.global.event;

import delivery_system.user.domain.entity.UserEntity;
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

    public SignupEvent(UserEntity userEntity) {
        this.user_id = userEntity.getId();
        this.password = userEntity.getPassword();
        //this.address = user.getAddress();
        this.role = userEntity.getRole();
    }
    public SignupEvent() {
        super();
    }
}
//>>> DDD / Domain Event

//>>> DDD / Domain Event
