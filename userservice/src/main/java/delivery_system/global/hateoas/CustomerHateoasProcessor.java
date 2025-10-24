package delivery_system.global.hateoas;

import delivery_system.user.domain.entity.UserEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class CustomerHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<UserEntity>> {

    @Override
    public EntityModel<UserEntity> process(EntityModel<UserEntity> model) {
        return model;
    }
}
