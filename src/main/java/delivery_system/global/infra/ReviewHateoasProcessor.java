package delivery_system.global.infra;

import delivery_system.review.domain.entity.ReviewEntityV1;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelProcessor;
import org.springframework.stereotype.Component;

@Component
public class ReviewHateoasProcessor
    implements RepresentationModelProcessor<EntityModel<ReviewEntityV1>> {

    @Override
    public EntityModel<ReviewEntityV1> process(EntityModel<ReviewEntityV1> model) {
        return model;
    }
}
