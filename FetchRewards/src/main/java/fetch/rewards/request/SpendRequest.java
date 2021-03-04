package fetch.rewards.request;

import java.io.Serializable;

public class SpendRequest implements Serializable {
	private static final long serialVersionUID = -3221930640293914783L;
	private Integer points;
	public SpendRequest() {}
	public SpendRequest(Integer points) {
		this.points = points;
	}
	public Integer getPoints() {
		return points;
	}
	public void setPoints(Integer points) {
		this.points = points;
	}
}
