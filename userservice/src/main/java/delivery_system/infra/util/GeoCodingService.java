package delivery_system.infra.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import org.json.JSONObject;
import org.json.JSONArray;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
// ❌ URLEncoder는 이제 UriComponentsBuilder가 처리하므로 삭제 가능
// import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

// ✅ [추가] JTS/Spatial 라이브러리 임포트
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;

@Service
public class GeoCodingService {

    @Value("${kakao.api.key}")
    private String kakaoApiKey;

    // ❌ [삭제] KAKAO_ADDRESS_URL, KAKAO_KEYWORD_URL (UriComponentsBuilder 사용)

    // ✅ [추가] JTS GeometryFactory (매번 생성할 필요 없이 재사용)
    private final GeometryFactory geometryFactory = new GeometryFactory();

    @PostConstruct
    public void checkKey() {
        System.out.println("✅ Loaded Kakao API Key: " + kakaoApiKey);
    }

    /**
     * ✅ [수정]
     * 주소 문자열을 PostGIS 'Point' 객체로 변환
     * UserService에서 이 메서드를 호출합니다.
     */
    public Point getCoordinateAsPoint(String address) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            URI uri = UriComponentsBuilder
                    .fromHttpUrl("https://dapi.kakao.com/v2/local/search/address.json")
                    .queryParam("query", address)
                    .build(false)
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            ResponseEntity<String> response = restTemplate.exchange(
                    uri, HttpMethod.GET, entity, String.class);

            // System.out.println("🔍 카카오 응답 코드: " + response.getStatusCode());
            // System.out.println("📦 카카오 응답 내용: " + response.getBody());

            JSONObject json = new JSONObject(response.getBody());
            JSONArray documents = json.getJSONArray("documents");

            if (documents.isEmpty()) {
                System.out.println("⚠️ 주소 검색 결과 없음, 키워드로 재검색: " + address);
                // ✅ 키워드 검색 (Point 반환)
                return getCoordinateByKeywordAsPoint(address);
            }

            JSONObject doc = documents.getJSONObject(0);
            double lat = doc.getDouble("y"); // 위도
            double lon = doc.getDouble("x"); // 경도

            // ✅ [수정] "lat,lon" String 대신 Point 객체 생성
            return createPoint(lon, lat); // ⬅️ (경도, 위도) 순서로 전달

        } catch (Exception e) {
            throw new RuntimeException("카카오 주소 변환 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ [수정]
     * 주소 검색 결과가 없을 때, 키워드 기반 보조 검색 (Point 반환)
     */
    private Point getCoordinateByKeywordAsPoint(String address) {
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "KakaoAK " + kakaoApiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            // ✅ [수정] UriComponentsBuilder 사용
            URI uri = UriComponentsBuilder
                    .fromHttpUrl("https://dapi.kakao.com/v2/local/search/keyword.json")
                    .queryParam("query", address)
                    .build(false)
                    .encode(StandardCharsets.UTF_8)
                    .toUri();

            ResponseEntity<String> response = restTemplate.exchange(
                    uri, HttpMethod.GET, entity, String.class);

            // System.out.println("🔍 [키워드검색] 카카오 응답 코드: " + response.getStatusCode());
            // System.out.println("📦 [키워드검색] 카카오 응답 내용: " + response.getBody());

            JSONObject json = new JSONObject(response.getBody());
            JSONArray docs = json.getJSONArray("documents");

            if (docs.isEmpty()) {
                System.out.println("❌ 키워드 검색 결과 없음: " + address);
                return createPoint(0, 0); // ⬅️ (0,0) Point 반환
            }

            JSONObject doc = docs.getJSONObject(0);
            double lat = doc.getDouble("y");
            double lon = doc.getDouble("x");

            System.out.println("✅ 키워드 변환 성공: Point 객체 반환");
            return createPoint(lon, lat); // ⬅️ (경도, 위도) 순서

        } catch (Exception e) {
            throw new RuntimeException("카카오 키워드 변환 API 호출 실패: " + e.getMessage(), e);
        }
    }

    /**
     * ✅ [추가]
     * (경도, 위도)로 Point 객체를 생성하는 헬퍼 메서드
     */
    private Point createPoint(double lon, double lat) {
        // ❗ [주의] JTS/PostGIS는 (경도, 위도) 순서 (x, y)
        Point point = geometryFactory.createPoint(new Coordinate(lon, lat));
        point.setSRID(4326); // WGS84 (geography) SRID
        return point;
    }

}