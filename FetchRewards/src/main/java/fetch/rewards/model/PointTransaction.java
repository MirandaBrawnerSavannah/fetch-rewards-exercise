package fetch.rewards.model;

import java.util.Date;

public class PointTransaction implements Comparable<PointTransaction> {
	
	private String payer;
	private int points;
	private Date timestamp;
	
	public PointTransaction() {}

	public PointTransaction(String payer, int points, Date timestamp) {
		this.payer = payer;
		this.points = points;
		this.timestamp = timestamp;
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

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public int compareTo(PointTransaction next) {
		return timestamp.compareTo(next.timestamp);
	}
	
	
}
