package delivery_system.cart.infra;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;
import delivery_system.domain.*;

@Component
public class OrderHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<Order>> {

    @Override
    public EntityModel<Order> process(EntityModel<Order> model) {
        return model;
    }
}
