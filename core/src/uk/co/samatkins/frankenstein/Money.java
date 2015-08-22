package uk.co.samatkins.frankenstein;

/**
 * £1 = 20s,
 * 1s = 12d,
 * ergo £1 = 240d
 */
public class Money {
	private int totalPence;

	public Money(int pounds, int shillings, int pence) {
		this.totalPence = pence + (shillings * 12) + (pounds * 240);
	}

	public void add(Money other) {
		this.totalPence += other.totalPence;
	}

	public void subtract(Money other) {
		this.totalPence -= other.totalPence;
	}

	public void add(int pounds, int shillings, int pence) {
		this.totalPence += pence + (shillings * 12) + (pounds * 240);
	}

	public void subtract(int pounds, int shillings, int pence) {
		this.totalPence -= pence + (shillings * 12) + (pounds * 240);
	}

	public void setTotalPence(int totalPence) {
		this.totalPence = totalPence;
	}

	@Override
	public String toString() {
		int pounds = totalPence / 240,
			shillings = (totalPence / 12) % 20,
			pence = totalPence % 12;
		return String.format("\u00A3%1$d.%2$02ds.%3$02dd", pounds, shillings, pence);
	}
}
