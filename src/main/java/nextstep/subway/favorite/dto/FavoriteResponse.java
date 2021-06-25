package nextstep.subway.favorite.dto;

import java.util.List;
import java.util.stream.Collectors;

import nextstep.subway.favorite.domain.Favorite;
import nextstep.subway.station.dto.StationResponse;

public class FavoriteResponse {
	private Long id;
	private StationResponse source;
	private StationResponse target;

	public FavoriteResponse() {
	}

	public FavoriteResponse(long id, StationResponse source, StationResponse target) {
		this.id = id;
		this.source = source;
		this.target = target;
	}

	public static FavoriteResponse of(Favorite favorite) {
		StationResponse source = StationResponse.of(favorite.getSource());
		StationResponse target = StationResponse.of(favorite.getTarget());
		return new FavoriteResponse(favorite.getId(), source, target);
	}

	public static List<FavoriteResponse> listOf(List<Favorite> favorites) {
		return favorites.stream()
			.map(FavoriteResponse::of)
			.collect(Collectors.toList());
	}

	public Long getId() {
		return id;
	}

	public StationResponse getSource() {
		return source;
	}

	public StationResponse getTarget() {
		return target;
	}
}