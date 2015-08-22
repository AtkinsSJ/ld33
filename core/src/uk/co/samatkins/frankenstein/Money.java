package uk.co.samatkins.frankenstein;

/**
 * £1 = 20s,
 * 1s = 12d,
 * ergo £1 = 240d
 */
public class Money {
	public static final Money Zero = new Money(0,0,0);

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

	public boolean isLessThan(Money other) {
		return this.totalPence < other.totalPence;
	}

	@Override
	public String toString() {
		boolean minus = totalPence < 0;

		int pounds = Math.abs(totalPence / 240),
			shillings = Math.abs((totalPence / 12) % 20),
			pence = Math.abs(totalPence % 12);
		return String.format("%1$c\u00A3%2$d.%3$02ds.%4$02dd", minus ? '-' : '\0', pounds, shillings, pence);
	}
}
