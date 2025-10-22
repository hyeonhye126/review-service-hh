package delivery_system.global.infra;

import delivery_system.store.domain.entity.StoreEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class StoreHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<StoreEntity>> {

    @Override
    public EntityModel<StoreEntity> process(EntityModel<StoreEntity> model) {
        return model;
    }
}
