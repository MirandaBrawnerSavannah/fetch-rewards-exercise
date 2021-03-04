package fetch.rewards.model;

import java.util.Objects;

public class PointBalance {
	private String payer;
	private int points;
	
	public PointBalance() {}

	public PointBalance(String payer, int points) {
		this.payer = payer;
		this.points = points;
	}

	public String getPayer() {
		return payer;
	}

	public void setPayer(String payer) {
		this.payer = payer;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!(obj instanceof PointBalance))
			return false;
		PointBalance other = (PointBalance) obj;
		return Objects.equals(payer, other.payer) && points == other.points;
	}
	
	
}
