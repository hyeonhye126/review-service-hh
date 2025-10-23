package delivery_system.cart.infra;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import delivery_system.cart.domain.repository.ItemRepository;
import delivery_system.cart.presentation.dto.MenuDetailsDto;
import delivery_system.cart.presentation.dto.MenuDetailsDto.OptionGroupDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    @Autowired
    public ItemRepositoryImpl(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
    }

    // PostgreSQL JSONB를 활용한 쿼리
    private static final String FETCH_MENU_DETAILS_QUERY = """
        SELECT 
            m.menu_id, 
            m.store_id, 
            s.store_name, 
            s.delivery_fee, 
            m.menu_name, 
            m.menu_fee,
            COALESCE(
                (json_agg(
                    jsonb_build_object(
                        'menuOptId', mo.menu_opt_id,
                        'menuOptName', mo.menu_opt_name,
                        'values', (
                            SELECT COALESCE(
                                (json_agg(
                                    jsonb_build_object(
                                        'menuOptValueId', mov.menu_opt_value_id,
                                        'valueName', mov.menu_opt_value_name,
                                        'fee', mov.menu_opt_value_fee
                                    )
                                ) FILTER (WHERE mov.menu_opt_value_id IS NOT NULL))::jsonb, -- 🚨 괄호와 ::jsonb로 구문 오류 해결
                                '[]'::jsonb
                            )
                            FROM p_menu_opt_value mov
                            WHERE mov.menu_opt_id = mo.menu_opt_id AND mov.deleted_at IS NULL AND mov.is_active = TRUE
                        )
                    )
                ) FILTER (WHERE mo.menu_opt_id IS NOT NULL))::jsonb, -- 🚨 괄호와 ::jsonb로 구문 오류 해결
                '[]'::jsonb
            ) AS options_json
        FROM p_menu m
        JOIN p_store s ON m.store_id = s.store_id
        LEFT JOIN p_menu_opt_relation mor ON m.menu_id = mor.menu_id AND mor.is_active = TRUE -- mor.deleted_at 제거
        LEFT JOIN p_menu_opt mo ON mor.menu_opt_id = mo.menu_opt_id AND mo.deleted_at IS NULL AND mo.is_active = TRUE
        WHERE m.menu_id = ? AND m.deleted_at IS NULL AND m.is_hidden = FALSE
        GROUP BY m.menu_id, s.store_name, s.delivery_fee
        """;


    @Override
    public Optional<MenuDetailsDto> findMenuDetailsById(UUID menuId) {

        try {
            return Optional.ofNullable(jdbcTemplate.queryForObject(
                    FETCH_MENU_DETAILS_QUERY,
                    new Object[]{menuId},
                    (rs, rowNum) -> {
                        MenuDetailsDto dto = new MenuDetailsDto();
                        dto.setMenuId(UUID.fromString(rs.getString("menu_id")));
                        dto.setStoreId(UUID.fromString(rs.getString("store_id")));
                        dto.setStoreName(rs.getString("store_name"));
                        dto.setDeliveryFee(rs.getInt("delivery_fee"));
                        dto.setMenuName(rs.getString("menu_name"));
                        dto.setMenuFee(rs.getInt("menu_fee"));

                        String optionsJson = rs.getString("options_json");
                        if (optionsJson != null && !optionsJson.equals("[]")) {
                            List<OptionGroupDto> options = null;
                            try {
                                options = objectMapper.readValue(
                                        optionsJson,
                                        objectMapper.getTypeFactory().constructCollectionType(List.class, OptionGroupDto.class)
                                );
                            } catch (JsonProcessingException e) {
                                throw new RuntimeException(e);
                            }
                            dto.setOptions(options);
                        }
                        return dto;
                    }
            ));
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        } catch (Exception e) {
            e.printStackTrace();
            return Optional.empty();
        }
    }

    // ItemRepository 인터페이스의 다른 메서드가 있다면 여기에 구현해야 합니다.
}