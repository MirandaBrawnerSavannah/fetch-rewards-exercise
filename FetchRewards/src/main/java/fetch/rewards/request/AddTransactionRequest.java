package fetch.rewards.request;

import java.io.Serializable;
import java.util.Date;

public class AddTransactionRequest implements Serializable {

	private static final long serialVersionUID = -4028817846194922728L;
	private String payer;
	private Integer points;
	private Date timestamp;
	
	public AddTransactionRequest() {}

	public AddTransactionRequest(String payer, Integer points, Date timestamp) {
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

	public Integer getPoints() {
		return points;
	}

	public void setPoints(Integer points) {
		this.points = points;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
