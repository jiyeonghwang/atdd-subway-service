package nextstep.subway.path;

import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import nextstep.subway.AcceptanceTest;
import nextstep.subway.line.acceptance.LineAcceptanceTest;
import nextstep.subway.line.acceptance.LineSectionAcceptanceTest;
import nextstep.subway.line.dto.LineRequest;
import nextstep.subway.line.dto.LineResponse;
import nextstep.subway.path.dto.PathRequest;
import nextstep.subway.path.dto.PathResponse;
import nextstep.subway.station.StationAcceptanceTest;
import nextstep.subway.station.dto.StationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("지하철 경로 조회")
public class PathAcceptanceTest extends AcceptanceTest {

    private LineResponse 신분당선;
    private LineResponse 이호선;
    private LineResponse 삼호선;
    private StationResponse 강남역;
    private StationResponse 양재역;
    private StationResponse 교대역;
    private StationResponse 남부터미널역;

    /**
     * 교대역    --- *2호선* ---   강남역
     * |                        |
     * *3호선*                   *신분당선*
     * |                        |
     * 남부터미널역  --- *3호선* ---   양재
     */
    @BeforeEach
    public void setUp() {
        super.setUp();

        강남역 = 지하철역_등록되어_있음("강남역");
        양재역 = 지하철역_등록되어_있음("양재역");
        교대역 = 지하철역_등록되어_있음("교대역");
        남부터미널역 = 지하철역_등록되어_있음("남부터미널역");

        신분당선 = 지하철_노선_등록되어_있음("신분당선", "bg-red-600", 강남역, 양재역, 10);
        이호선 = 지하철_노선_등록되어_있음("이호선", "bg-red-600", 교대역, 강남역, 10);
        삼호선 = 지하철_노선_등록되어_있음("삼호선", "bg-red-600", 교대역, 양재역, 5);

        지하철_노선에_지하철역_등록되어_있음(삼호선, 교대역, 남부터미널역, 3);
    }

    @DisplayName("출발역과 도착역의 최단 경로 조회1")
    @Test
    void shortestPath1() {
        // given
        PathRequest pathRequest = new PathRequest(강남역.getId(), 교대역.getId());
        PathResponse pathResponse = PathResponse.of(Arrays.asList(강남역, 교대역), 10);

        // when
        ExtractableResponse<Response> response = 최단_경로_조회(pathRequest);

        // then
        최단_경로_조회됨(response, pathResponse);
    }

    @DisplayName("출발역과 도착역의 최단 경로 조회2")
    @Test
    void shortestPath2() {
        // given
        PathRequest pathRequest = new PathRequest(강남역.getId(), 남부터미널역.getId());
        PathResponse pathResponse = PathResponse.of(Arrays.asList(강남역, 양재역, 남부터미널역), 12);

        // when
        ExtractableResponse<Response> response = 최단_경로_조회(pathRequest);

        // then
        최단_경로_조회됨(response, pathResponse);
    }

    private ExtractableResponse<Response> 최단_경로_조회(PathRequest pathRequest) {
        Map<String, Long> queryParams = new HashMap<>();
        queryParams.put("source", pathRequest.getSource());
        queryParams.put("target", pathRequest.getTarget());

        return RestAssured
                .given().log().all()
                .accept(MediaType.APPLICATION_JSON_VALUE)
                .queryParams(queryParams)
                .when().get("/paths")
                .then().log().all()
                .extract();
    }

    private StationResponse 지하철역_등록되어_있음(String name) {
        return StationAcceptanceTest.지하철역_등록되어_있음(name).as(StationResponse.class);
    }

    private LineResponse 지하철_노선_등록되어_있음(String name, String color, StationResponse upStation, StationResponse downStation, int distance) {
        LineRequest params = new LineRequest(name, color, upStation.getId(), downStation.getId(), distance);
        return LineAcceptanceTest.지하철_노선_생성_요청(params).as(LineResponse.class);
    }

    private void 지하철_노선에_지하철역_등록되어_있음(LineResponse line, StationResponse upStation, StationResponse downStation, int distance) {
        LineSectionAcceptanceTest.지하철_노선에_지하철역_등록_요청(line, upStation, downStation, distance);
    }

    private void 최단_경로_조회됨(ExtractableResponse<Response> response, PathResponse expect) {
        assertAll(
                () -> assertThat(response.statusCode()).isEqualTo(HttpStatus.OK.value())
                , () -> assertThat(response.as(PathResponse.class)).isEqualTo(expect)
        );
    }
}
