package nextstep.subway.line.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;
import nextstep.subway.station.domain.Station;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("노선 도메인 테스트")
public class LineTest {

    private final Station 서울역 = new Station("서울역");
    private final Station 용산역 = new Station("용산역");
    final Line line = Line.of("1호선", "blue", 서울역, 용산역, 10);

    @Test
    @DisplayName("라인에 포함된 역목록 조회")
    void getStations() {
        final Station 남영역 = new Station("남영역");
        final Station 노량진역 = new Station("노량진역");

        line.addLineStation(Section.create(서울역, 남영역, Distance.valueOf(5)));
        line.addLineStation(Section.create(용산역, 노량진역, Distance.valueOf(8)));

        //when
        List<Station> stations = line.getStations();

        //then
        assertThat(stations).extracting(Station::getName).containsExactly("서울역", "남영역", "용산역", "노량진역");
    }

    @Test
    @DisplayName("라인에 구간 추가")
    void addLineStation() {
        final Station 남영역 = new Station("남영역");

        //when
        line.addLineStation(Section.create(서울역, 남영역, Distance.valueOf(5)));

        //then
        assertThat(line.getStations()).extracting(Station::getName).containsExactly("서울역", "남영역", "용산역");

    }

    @Test
    @DisplayName("구간이 없는 라인에 구간 추가")
    void noSectionLineAddStation() {
        final Line line = Line.of("1호선", "blue");

        line.addLineStation(Section.create(서울역, 용산역, Distance.valueOf(5)));

        //then
        assertThat(line.getStations()).extracting(Station::getName).containsExactly("서울역", "용산역");
    }

    @Test
    @DisplayName("이미 포함된 구간 추가시 실패")
    void addLineStationExistsFail() {

        assertThatThrownBy(
            () -> line.addLineStation(Section.create(서울역, 용산역, Distance.valueOf(12))))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("이미 등록된 구간 입니다.");
    }

    @Test
    @DisplayName("연결된 역이 없는 구간 추가시 실패")
    void addLineStationNoIncludeFail() {
        final Station 강남역 = new Station("강남역");
        final Station 역삼역 = new Station("역삼역");

        assertThatThrownBy(
            () -> line.addLineStation(Section.create(강남역, 역삼역, Distance.valueOf(10))))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("등록할 수 없는 구간 입니다.");
    }

    @Test
    @DisplayName("노선에서 구간 삭제")
    void removeLineStation() {

        final Station 남영역 = new Station("남영역");

        line.addLineStation(Section.create(서울역, 남영역, Distance.valueOf(5)));

        //when
        line.removeLineStation(남영역);

        //then
        assertThat(line.getStations()).extracting(Station::getName).containsExactly("서울역", "용산역");


    }

    @Test
    @DisplayName("노선에서 구간 삭제 시 구간이 하나인 경우 실패")
    void removeLineStationMinFail() {

        assertThatThrownBy(() -> line.removeLineStation(서울역))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("구간이 하나는 존재해야 합니다.");
    }

    @Test
    @DisplayName("노선에 포함되지 않은 역 삭제 시 실패")
    void removeLineStationNonIncludeFail() {
        final Station 남영역 = new Station("남영역");
        final Station 강남역 = new Station("강남역");
        line.addLineStation(Section.create(서울역, 남영역, Distance.valueOf(5)));

        assertThatThrownBy(() -> line.removeLineStation(강남역))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("역이 포함된 구간이 없습니다.");
    }

    @Test
    @DisplayName("노선 생성시 추가요금 넣기")
    void createLineWithAdditionalFare() {
        Line line = Line.of("1호선_추가", "blue", 100);
        assertThat(line.getAdditionalFare()).isEqualTo(Fare.valueOf(100));
    }

    @Test
    @DisplayName("노선에 추가운임 수정")
    void updateLineAdditionalFare() {
        assertThat(line.getAdditionalFare()).isEqualTo(Fare.valueOf(0));

        line.update(Line.of("1호선_수정", "blue", 200));

        assertThat(line.getAdditionalFare()).isEqualTo(Fare.valueOf(200));
    }
}
