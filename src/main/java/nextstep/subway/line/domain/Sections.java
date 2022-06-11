package nextstep.subway.line.domain;

import nextstep.subway.station.domain.Station;

import javax.persistence.CascadeType;
import javax.persistence.Embeddable;
import javax.persistence.OneToMany;
import java.util.*;

@Embeddable
public class Sections {
    private static final int ONLY_ONE = 1;

    protected Sections() {
    }

    @OneToMany(mappedBy = "line", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = true)
    private final List<Section> sections = new ArrayList<>();

    public Sections(List<Section> sections) {
        if (Objects.isNull(sections)) {
            throw new IllegalArgumentException("null 을 입력할수 없습니다.");
        }
        this.sections.addAll(sections);
    }

    public void addSection(final Section section) {
        if (!sections.contains(section)) {
            sections.add(section);
        }
    }

    public List<Station> getStations() {
        if (this.sections.isEmpty()) {
            return Collections.emptyList();
        }
        return insertStationBySorted();
    }

    public int isSize() {
        return this.sections.size();
    }

    public Optional<Station> getStartStation() {
        if (sections.isEmpty()) {
            return Optional.empty();
        }
        if (sections.size() == ONLY_ONE) {
           return this.sections.stream().map(Section::getUpStation).findAny();
        }
        return sections.stream().filter(this::isStartStation).map(Section::getUpStation).findAny();
    }

    public List<Section> getSections() {
        return sections;
    }

    public boolean isContains(final Section section) {
        return sections.contains(section);
    }


    private List<Station> insertStationBySorted() {
        List<Station> result = new ArrayList<>();
        Optional<Station> isStartStation = getStartStation();
        while (isStartStation.isPresent()) {
            Station station = isStartStation.get();
            result.add(station);
            isStartStation = findNextStation(station);
        }
        return result;
    }

    private Optional<Station> findNextStation(final Station station) {
        return sections.stream()
                .filter(section -> section.isMatchUpStation(station))
                .map(Section::getDownStation)
                .findAny();
    }

    private boolean isStartStation(final Section source) {
        Optional<Section> isPreSection = sections.stream()
                .filter(section -> !section.isMatchDownStation(source.getUpStation()) &&
                        section.isMatchUpStation(source.getDownStation())).findAny();
        return isPreSection.isPresent();
    }
}
